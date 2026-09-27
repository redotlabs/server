#!/usr/bin/env python3
"""Check packaging boundaries; never print configuration contents."""
from pathlib import Path
import zipfile
root = Path(__file__).resolve().parents[1]
expected = {"application.yml", "application-homelab.yml"}
expected |= {str(p.relative_to(root / "src/main/resources")) for p in (root / "src/main/resources/db/migration").glob("*.sql")}
with zipfile.ZipFile(root / "build/kubernetes/app.jar") as jar:
    actual = {n.removeprefix("BOOT-INF/classes/") for n in jar.namelist()
              if n.startswith("BOOT-INF/classes/") and not n.endswith(("/", ".class"))}
    assert actual == expected, "JAR resource allowlist mismatch"
    for name in expected:
        assert jar.read("BOOT-INF/classes/" + name) == (root / "src/main/resources" / name).read_bytes(), "Packaged resource differs from public source"
print("JAR public resource allowlist and source match: PASS")
