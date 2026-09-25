#!/usr/bin/env python3
"""
Syncs the current MySQL database content to src/main/resources/sql/dump/Dump.sql
with exact multiline formatted INSERT tuples.
"""

import os
import re
import subprocess
from pathlib import Path

LOCAL_PROP_PATH = Path("src/main/resources/application-local.properties")
DUMP_PATH = Path("src/main/resources/sql/dump/Dump.sql")

def get_mysql_credentials():
    user = "root"
    password = ""
    host = "localhost"
    port = "3306"
    database = "card_collection"

    if LOCAL_PROP_PATH.exists():
        for line in LOCAL_PROP_PATH.read_text().splitlines():
            line = line.strip()
            if line.startswith("spring.datasource.username="):
                user = line.split("=", 1)[1].strip()
            elif line.startswith("spring.datasource.password="):
                password = line.split("=", 1)[1].strip()
            elif line.startswith("spring.datasource.url="):
                url = line.split("=", 1)[1].strip()
                m = re.search(r"//([^:/]+)(?::(\d+))?/([^?]+)", url)
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

def main():
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
    DUMP_PATH.write_text(formatted, encoding="utf-8")
    print(f"Successfully wrote {len(formatted.splitlines())} lines to {DUMP_PATH}")

if __name__ == "__main__":
    main()
