#!/usr/bin/env python3
"""
Syncs the current MySQL database content to src/main/resources/sql/dump/Dump.sql
with exact multiline formatted INSERT tuples.
"""

import argparse
import gzip
import os
import re
import subprocess
from datetime import datetime
from pathlib import Path

REPO_ROOT = Path(__file__).resolve().parent.parent
LOCAL_PROP_PATH = REPO_ROOT / "src/main/resources/application-local.properties"
APP_PROP_PATH = REPO_ROOT / "src/main/resources/application.properties"
DUMP_PATH = REPO_ROOT / "src/main/resources/sql/dump/Dump.sql"
BACKUP_DIR = REPO_ROOT / "backups"

def get_mysql_credentials():
    user = "root"
    password = ""
    host = "localhost"
    port = "3306"
    database = "card_collection"

    # Read from application.properties first as fallback, then application-local.properties for overrides
    for prop_path in [APP_PROP_PATH, LOCAL_PROP_PATH]:
        if prop_path.exists():
            for line in prop_path.read_text(encoding="utf-8").splitlines():
                line = line.strip()
                if line.startswith("#") or not line:
                    continue
                if line.startswith("spring.datasource.username="):
                    val = line.split("=", 1)[1].strip()
                    if val and not val.startswith("${"):
                        user = val
                elif line.startswith("spring.datasource.password="):
                    val = line.split("=", 1)[1].strip()
                    if not val.startswith("${"):
                        password = val
                elif line.startswith("spring.datasource.url="):
                    val = line.split("=", 1)[1].strip()
                    m = re.search(r"//([^:/]+)(?::(\d+))?/([^?]+)", val)
                    if m:
                        host = m.group(1)
                        if m.group(2):
                            port = m.group(2)
                        database = m.group(3)

    return host, port, user, password, database

def format_inserts(text):
    def repl(match):
        prefix = match.group(1)
        values_part = match.group(2)
        formatted_values = re.sub(r"\),\(", "),\n       (", values_part)
        return prefix + formatted_values + ";"

    text = re.sub(r"INSERT INTO (`[^`]+`) VALUES\s*(.*?);", r"INSERT INTO \1\nVALUES \2;", text)
    text = re.sub(r"(INSERT INTO `[^`]+`\nVALUES\s*)(.*?);", repl, text, flags=re.DOTALL)
    return text

def create_rolling_backup(dump_content: str, backup_dir: Path, max_backups: int = 30):
    backup_dir.mkdir(parents=True, exist_ok=True)
    timestamp = datetime.now().strftime("%Y-%m-%d_%H%M%S")
    backup_file = backup_dir / f"dump_{timestamp}.sql.gz"

    with gzip.open(backup_file, "wt", encoding="utf-8") as f:
        f.write(dump_content)

    print(f"Created compressed backup: {backup_file.name} ({backup_file.stat().st_size} bytes)")

    # Prune older backups
    existing_backups = sorted(
        backup_dir.glob("dump_*.sql.gz"),
        key=lambda p: p.stat().st_mtime,
        reverse=True
    )
    if len(existing_backups) > max_backups:
        for old in existing_backups[max_backups:]:
            old.unlink(missing_ok=True)
            print(f"Pruned older backup: {old.name}")

def main():
    parser = argparse.ArgumentParser(description="Sync MySQL database to Dump.sql with rolling backups")
    parser.add_argument("--no-backup", action="store_true", help="Skip creating rolling compressed backup")
    parser.add_argument("--max-backups", type=int, default=30, help="Maximum rolling backups to retain (default: 30)")
    args = parser.parse_args()

    host, port, user, password, database = get_mysql_credentials()
    print(f"Exporting database dump from {user}@{host}:{port}/{database}...")

    cmd = ["mysqldump", "-h", host, "-P", port, "-u", user]
    if password:
        cmd.append(f"-p{password}")
    cmd.extend([
        database,
        "--set-gtid-purged=OFF",
        "--single-transaction",
        "--skip-opt",
        "--add-drop-table",
        "--create-options",
        "--quick",
        "--extended-insert",
        "--set-charset"
    ])

    res = subprocess.run(cmd, capture_output=True, text=True)
    if res.returncode != 0:
        raise RuntimeError(f"mysqldump error: {res.stderr}")

    dump = res.stdout

    header = "CREATE DATABASE IF NOT EXISTS `card_collection` /*!40100 DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci */ /*!80016 DEFAULT ENCRYPTION = 'N' */;\nUSE `card_collection`;\n"
    if "USE `card_collection`;" not in dump:
        dump = header + dump

    formatted = format_inserts(dump)
    DUMP_PATH.parent.mkdir(parents=True, exist_ok=True)
    DUMP_PATH.write_text(formatted, encoding="utf-8")
    print(f"Successfully wrote {len(formatted.splitlines())} lines to {DUMP_PATH}")

    if not args.no_backup:
        create_rolling_backup(formatted, BACKUP_DIR, max_backups=args.max_backups)

if __name__ == "__main__":
    main()
