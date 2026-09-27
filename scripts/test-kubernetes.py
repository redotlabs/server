#!/usr/bin/env python3
"""Local tests only: no host ports, production credentials, or Kubernetes access."""
import json
import os
from pathlib import Path
import secrets
import subprocess
import tempfile
import time

ROOT = Path(__file__).resolve().parents[1]
IMAGES = json.loads((ROOT / "docs/runtime-images.json").read_text())
RUN = "redot-it-" + secrets.token_hex(4)
NETWORK = RUN
DB = RUN + "-db"
REDIS = RUN + "-redis"
RUNNER = RUN + "-runner"
DB_PASSWORD = secrets.token_urlsafe(32)
REDIS_PASSWORD = secrets.token_urlsafe(32)
ENV = dict(os.environ, POSTGRES_PASSWORD=DB_PASSWORD,
           REDOT_IT_DB_HOST=DB, REDOT_IT_DB_PASSWORD=DB_PASSWORD,
           REDOT_IT_REDIS_HOST=REDIS, REDOT_IT_REDIS_PASSWORD=REDIS_PASSWORD, REDISCLI_AUTH=REDIS_PASSWORD)
created = []
network_created = False

def execute(args, *, check=True, timeout=600):
    result = subprocess.run(args, cwd=ROOT, env=ENV, capture_output=True, text=True, timeout=timeout)
    if check and result.returncode:
        # Never echo command/env or raw process output containing credentials.
        raise RuntimeError("Test command failed: " + args[0] + " (exit " + str(result.returncode) + ")")
    return result

temporary = tempfile.TemporaryDirectory(prefix="redot-it-")
try:
    tmp = Path(temporary.name)
    config = tmp / "redis.conf"
    config.write_text("bind 0.0.0.0\nprotected-mode yes\nport 6379\nappendonly yes\nappendfsync everysec\ndir /data\nrequirepass " + REDIS_PASSWORD + "\n")
    config.chmod(0o600)
    data = tmp / "data"
    data.mkdir(mode=0o700)
    execute(["docker", "network", "create", NETWORK])
    network_created = True
    execute(["docker", "run", "-d", "--name", DB, "--network", NETWORK,
             "--memory", "512m", "--cpus", "1", "--tmpfs", "/var/lib/postgresql/data:rw,size=268435456",
             "-e", "POSTGRES_DB=redot_test", "-e", "POSTGRES_USER=redot_test", "-e", "POSTGRES_PASSWORD",
             "-e", "POSTGRES_INITDB_ARGS=--encoding=UTF8", "-e", "TZ=UTC", IMAGES["postgresql"]["image"]])
    created.append(DB)
    execute(["docker", "run", "-d", "--name", REDIS, "--network", NETWORK,
             "--memory", "256m", "--cpus", "0.5", "--user", f"{os.getuid()}:{os.getgid()}",
             "-v", f"{config}:/usr/local/etc/redis/redis.conf:ro", "-v", f"{data}:/data",
             IMAGES["redis"]["image"], "redis-server", "/usr/local/etc/redis/redis.conf"])
    created.append(REDIS)
    for _ in range(60):
        if execute(["docker", "exec", DB, "pg_isready", "-U", "redot_test", "-d", "redot_test"], check=False).returncode == 0:
            break
        time.sleep(1)
    else:
        raise RuntimeError("Isolated PostgreSQL did not become ready")
    redis_cli = ["docker", "exec", "-e", "REDISCLI_AUTH", REDIS, "redis-cli", "--raw"]
    for _ in range(60):
        if execute(redis_cli + ["PING"], check=False).stdout.strip() == "PONG":
            break
        time.sleep(1)
    else:
        raise RuntimeError("Isolated Redis did not become ready")
    execute(redis_cli + ["SET", "redot-aof-probe", "persistence-ok", "EX", "120"])
    time.sleep(2)
    execute(["docker", "restart", REDIS])
    for _ in range(60):
        if execute(redis_cli + ["PING"], check=False).stdout.strip() == "PONG":
            break
        time.sleep(1)
    if execute(redis_cli + ["GET", "redot-aof-probe"]).stdout.strip() != "persistence-ok":
        raise RuntimeError("Redis AOF restart persistence failed")
    execute(redis_cli + ["DEL", "redot-aof-probe"])
    print("Redis AOF restart persistence: PASS")
    cache = Path(os.environ.get("REDOT_GRADLE_CACHE", str(Path.home() / ".cache/redot-gradle"))).resolve()
    cache.mkdir(parents=True, exist_ok=True)
    command = ["docker", "run", "--rm", "--name", RUNNER, "--network", NETWORK,
               "--cpus", "2", "--memory", "2g", "--user", f"{os.getuid()}:{os.getgid()}",
               "-e", "GRADLE_USER_HOME=/gradle", "-v", f"{cache}:/gradle", "-v", f"{ROOT}:/work", "-w", "/work"]
    for key in ["REDOT_IT_DB_HOST", "REDOT_IT_DB_PASSWORD", "REDOT_IT_REDIS_HOST", "REDOT_IT_REDIS_PASSWORD"]:
        command.extend(["-e", key])
    command.extend([IMAGES["test_jdk"]["image"], "./gradlew", "--no-daemon", "--max-workers=2", "--continue", "--rerun-tasks",
                    "test", "integrationTest", "kubernetesBootJar"])
    result = execute(command, check=False, timeout=900)
    log = result.stdout + result.stderr
    for value in [DB_PASSWORD, REDIS_PASSWORD]:
        log = log.replace(value, "[REDACTED]")
    # Build reports are local ignored artifacts, never publish them automatically.
    logpath = ROOT / "build/kubernetes-validation.log"
    logpath.parent.mkdir(exist_ok=True)
    fd = os.open(logpath, os.O_WRONLY | os.O_CREAT | os.O_TRUNC, 0o600)
    with os.fdopen(fd, "w") as f:
        f.write(log)
    print("Local Gradle tests/package exit:", result.returncode)
    print("Reports: build/test-results/test and build/test-results/integrationTest")
    if result.returncode:
        raise SystemExit(result.returncode)
finally:
    execute(["docker", "rm", "-f", RUNNER], check=False)
    for name in reversed(created):
        execute(["docker", "rm", "-f", name], check=False)
    if network_created:
        execute(["docker", "network", "rm", NETWORK], check=False)

    temporary.cleanup()
