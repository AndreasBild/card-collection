#!/usr/bin/env python3
"""
Applies pack odds from pack_odds_lookup.json directly to the MySQL database.
Also maps set variants and brand aliases seamlessly.
"""

import json
import os
import re
import subprocess
import sys
from pathlib import Path

# Paths
LOOKUP_PATH = Path("data/pack_odds_lookup.json")
JAVA_LOOKUP_PATH = Path("../card-collectionJava/content/json/pack-odds-lookup.json")
LOCAL_PROP_PATH = Path("src/main/resources/application-local.properties")

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
                # jdbc:mysql://localhost:3306/card_collection?...
                m = re.search(r"//([^:/]+)(?::(\d+))?/([^?]+)", url)
                if m:
                    host = m.group(1)
                    if m.group(2):
                        port = m.group(2)
                    database = m.group(3)

    return host, port, user, password, database

def run_mysql_query(sql, host, port, user, password, database):
    cmd = ["mysql", "-h", host, "-P", port, "-u", user]
    if password:
        cmd.append(f"-p{password}")
    cmd.extend([database, "-B", "-e", sql])
    res = subprocess.run(cmd, capture_output=True, text=True)
    if res.returncode != 0:
        raise RuntimeError(f"MySQL error: {res.stderr}")
    return res.stdout

def run_mysql_script(sql_content, host, port, user, password, database):
    cmd = ["mysql", "-h", host, "-P", port, "-u", user]
    if password:
        cmd.append(f"-p{password}")
    cmd.append(database)
    res = subprocess.run(cmd, input=sql_content, capture_output=True, text=True)
    if res.returncode != 0:
        raise RuntimeError(f"MySQL error executing script: {res.stderr}")
    return res.stdout

def normalize_name(s: str) -> str:
    return (s or "").strip().lower()

def main():
    host, port, user, password, database = get_mysql_credentials()
    print(f"Connecting to MySQL: {user}@{host}:{port}/{database}")

    with open(LOOKUP_PATH, "r", encoding="utf-8") as f:
        lookup = json.load(f)

    # Fetch cards from DB
    query = """
    SELECT 
      c.id, 
      s.name as season, 
      b.name as brand, 
      t.name as theme, 
      v.name as variant,
      c.pack_odds,
      c.number
    FROM card c
    JOIN season s ON c.season_id = s.id
    JOIN card_brand b ON c.brand_id = b.id
    JOIN card_theme t ON c.theme_id = t.id
    JOIN variant v ON c.variant_id = v.id
    ORDER BY c.id;
    """
    out = run_mysql_query(query, host, port, user, password, database)
    lines = out.strip().split("\n")
    header = lines[0].split("\t")
    cards = [dict(zip(header, l.split("\t"))) for l in lines[1:] if l.strip()]
    print(f"Loaded {len(cards)} cards from database.")

    # Match and generate updates
    updates = []
    updated_cards = []

    for c in cards:
        c_id = c["id"]
        season = c["season"]
        brand = c["brand"]
        theme = c["theme"]
        variant = c["variant"]
        current_odds = c["pack_odds"] if c["pack_odds"] != "NULL" else None

        key = f"{normalize_name(season)}|{normalize_name(brand)}|{normalize_name(theme)}|{normalize_name(variant)}"

        entry = lookup.get(key)

        # Fallback aliases
        if not entry:
            # Check Brand aliases: e.g. "Topps Bowman's Best" -> "Bowman's Best"
            alt_brand = brand.replace("Topps ", "").replace("Fleer ", "")
            key_alt_brand = f"{normalize_name(season)}|{normalize_name(alt_brand)}|{normalize_name(theme)}|{normalize_name(variant)}"
            entry = lookup.get(key_alt_brand)

        if not entry:
            # Check Ultra vs Fleer Ultra
            if brand.lower() == "ultra":
                entry = lookup.get(f"{normalize_name(season)}|fleer ultra|{normalize_name(theme)}|{normalize_name(variant)}")

        if not entry:
            # Check variant aliases: e.g. Ruby -> Star Rubies, 24 K Gold -> 24-Karat Gold
            if variant.lower() == "ruby":
                entry = lookup.get(f"{normalize_name(season)}|{normalize_name(brand)}|{normalize_name(theme)}|star rubies")
            elif "24 k gold" in variant.lower():
                entry = lookup.get(f"{normalize_name(season)}|{normalize_name(brand)}|{normalize_name(theme)}|24-karat gold")

        if entry and entry.get("pack_odds"):
            odds_val = entry["pack_odds"]
            if odds_val not in ["Standard Base", "N/A"]:
                # Also store this exact key into lookup for permanent 1:1 mapping
                if key not in lookup:
                    lookup[key] = {
                        "season": season,
                        "brand": brand,
                        "theme": theme,
                        "variant": variant,
                        "pack_odds": odds_val,
                        "source": "Alias Mapping",
                        "confidence": "AUTOMATED"
                    }

                if current_odds != odds_val:
                    escaped_odds = odds_val.replace("'", "''")
                    updates.append(f"UPDATE card SET pack_odds = '{escaped_odds}' WHERE id = {c_id};")
                    updated_cards.append((c_id, season, brand, theme, variant, c.get("number"), odds_val))

    print(f"Cards with pack odds to update: {len(updates)}")
    if updates:
        sql_batch = "START TRANSACTION;\n" + "\n".join(updates) + "\nCOMMIT;\n"
        run_mysql_script(sql_batch, host, port, user, password, database)
        print(f"Successfully executed {len(updates)} database UPDATE statements!")

        # Save updated lookup
        with open(LOOKUP_PATH, "w", encoding="utf-8") as f:
            json.dump(lookup, f, indent=2, ensure_ascii=False)
            f.write("\n")
        if JAVA_LOOKUP_PATH.parent.exists():
            with open(JAVA_LOOKUP_PATH, "w", encoding="utf-8") as f:
                json.dump(lookup, f, indent=2, ensure_ascii=False)
                f.write("\n")
            print(f"Mirrored lookup table to {JAVA_LOOKUP_PATH}")

        print("\nSample updated cards in database:")
        for u in updated_cards[:10]:
            print(f"  ID {u[0]}: {u[1]} {u[2]} - {u[3]} [{u[4]}] #{u[5]} -> {u[6]}")
    else:
        print("All cards already up to date in database.")

if __name__ == "__main__":
    main()
