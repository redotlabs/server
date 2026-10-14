#!/usr/bin/env python3
"""Run original/unlocked/fixed sources in disposable copies, retaining raw evidence.

Requires JDK 21, Docker (cgroup v2), Python 3. No production URLs or credentials.
Run from this checkout: python3 scripts/inquiry-experiment.py --output /absolute/path
"""
import argparse
import hashlib
import json
import shutil
import subprocess
import tempfile
from pathlib import Path

BASE = 'c373b2053b7f15c684ee8ffae2ba1665e63acf2d'
GEN = 'src/main/java/redot/redot_server/domain/cms/inquiry/service/InquiryNumberGenerator.java'
REPO = 'src/main/java/redot/redot_server/domain/cms/inquiry/repository/InquirySequenceRepository.java'


def main():
    parser = argparse.ArgumentParser()
    parser.add_argument('--output', type=Path, required=True)
    args = parser.parse_args()
    root = Path(__file__).resolve().parents[1]
    output = args.output.resolve()
    output.mkdir(parents=True, exist_ok=False)
    files = subprocess.check_output(['git', 'ls-files', '-z', '--cached', '--others', '--exclude-standard'], cwd=root).decode().split('\0')
    original = subprocess.check_output(['git', 'show', f'{BASE}:{GEN}'], cwd=root).decode()
    # Only date acquisition is adapted so all variants use the same deterministic test clock.
    original = original.replace('import java.time.LocalDate;', 'import java.time.LocalDate;\nimport java.time.Clock;')
    original = original.replace('private final InquirySequenceRepository inquirySequenceRepository;',
                                'private final InquirySequenceRepository inquirySequenceRepository;\n    private final Clock inquiryClock;')
    original = original.replace('LocalDate.now()', 'LocalDate.now(inquiryClock)')
    manifest = {'base': BASE, 'source_head': subprocess.check_output(['git','rev-parse','HEAD'],cwd=root).decode().strip(),
                'clock_adaptation': 'original/unlocked only LocalDate.now -> injected test Clock', 'variants': {}}
    for variant in ['original', 'unlocked', 'fixed']:
        destination = output / variant
        destination.mkdir()
        with tempfile.TemporaryDirectory(prefix=f'redot-{variant}-') as tmp:
            checkout = Path(tmp)
            for name in files:
                if not name or name.startswith(('docs/', 'artifacts/')):
                    continue
                source = root / name
                if source.is_file():
                    target = checkout / name
                    target.parent.mkdir(parents=True, exist_ok=True)
                    shutil.copy2(source, target)
            if variant != 'fixed':
                (checkout / GEN).write_text(original)
            if variant == 'unlocked':
                repo = (checkout / REPO).read_text()
                assert repo.count('@Lock(LockModeType.PESSIMISTIC_WRITE)') == 1
                (checkout / REPO).write_text(repo.replace('@Lock(LockModeType.PESSIMISTIC_WRITE)', ''))
            (destination / 'InquiryNumberGenerator.java.txt').write_text((checkout / GEN).read_text())
            (destination / 'InquirySequenceRepository.java.txt').write_text((checkout / REPO).read_text())
            manifest['variants'][variant] = {p: hashlib.sha256((checkout / p).read_bytes()).hexdigest() for p in [GEN, REPO]}
            commands = [
                ['./gradlew', 'test', '--tests', '*InquiryConcurrencyTest', '--no-daemon'],
                ['./gradlew', 'inquiryExperiment', f'-PexperimentOutput={destination}', f'-PexperimentVariant={variant}', '--no-daemon'],
            ]
            for label, command in zip(['regression', 'load'], commands):
                print(f'{variant}: {label}', flush=True)
                with (destination / f'{label}.log').open('w') as log:
                    result = subprocess.run(command, cwd=checkout, stdout=log, stderr=subprocess.STDOUT, timeout=900)
                manifest['variants'][variant][f'{label}_exit'] = result.returncode
                reports = checkout / 'build/test-results' / ('test' if label == 'regression' else 'inquiryExperiment')
                if reports.exists():
                    shutil.copytree(reports, destination / f'{label}-xml', ignore=shutil.ignore_patterns('binary'))
                (output / 'manifest.json').write_text(json.dumps(manifest, indent=2))
                # Expected regression failures in control variants are retained, not hidden.
                if label == 'load' and result.returncode != 0 or variant == 'fixed' and result.returncode != 0:
                    raise RuntimeError(f'{variant} {label} failed; inspect {destination}')
    print(f'Evidence saved: {output}', flush=True)


if __name__ == '__main__':
    main()
