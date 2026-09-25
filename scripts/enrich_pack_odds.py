#!/usr/bin/env python3
"""
Automated extraction, lookup, and enrichment pipeline for trading card Pack Odds / Insertion Ratios.

Usage:
    python3 scripts/enrich_pack_odds.py [--dry-run] [--scrape-limit 10] [--test-filter "Flair Showcase"]
"""

import argparse
import json
import logging
import os
import re
import sys
import time
import urllib.parse
import urllib.request
from html import unescape
from pathlib import Path
from typing import Dict, List, Optional, Set, Tuple

logging.basicConfig(
    level=logging.INFO,
    format="%(asctime)s [%(levelname)s] %(message)s",
    datefmt="%Y-%m-%d %H:%M:%S",
)
logger = logging.getLogger("pack_odds_pipeline")

# Regex patterns for stated pack odds
ODDS_PATTERNS = [
    # Explicit 1:X with optional qualifier: "1:30 Packs", "1:288 Hobby", "1:24"
    re.compile(r"\b1\s*:\s*(\d+)\s*(packs?|hobby(?:\s+packs?)?|retail(?:\s+packs?)?|boxes?|cases?|blasters?)?\b", re.IGNORECASE),
    # "inserted 1 in 30 packs" / "odds 1 in 288"
    re.compile(r"\b(?:inserted|odds|ratio|rate\s+of)\s*(?:of|at)?\s*1\s*(?:in|:)\s*(\d+)\s*(packs?|hobby|retail)?\b", re.IGNORECASE),
    # "stated odds: 1:X"
    re.compile(r"stated\s+odds\s*[:\-]?\s*1\s*:\s*(\d+)\s*(packs?|hobby|retail)?", re.IGNORECASE),
]

def make_set_key(season: Optional[str], brand: Optional[str], theme: Optional[str], variant: Optional[str]) -> str:
    s = (season or "").strip().lower()
    b = (brand or "").strip().lower()
    t = (theme or "").strip().lower()
    v = (variant or "").strip().lower()
    return f"{s}|{b}|{t}|{v}"

def load_json(filepath: Path) -> any:
    if not filepath.exists():
        return None
    with open(filepath, "r", encoding="utf-8") as f:
        return json.load(f)

def save_json(filepath: Path, data: any) -> None:
    filepath.parent.mkdir(parents=True, exist_ok=True)
    with open(filepath, "w", encoding="utf-8") as f:
        json.dump(data, f, indent=2, ensure_ascii=False)
        f.write("\n")

def search_duckduckgo_lite(query: str, delay_seconds: float = 1.0) -> List[str]:
    """
    Queries DuckDuckGo Lite search and extracts text snippets.
    Includes rate limiting and polite request handling.
    """
    url = "https://lite.duckduckgo.com/lite/"
    headers = {
        "User-Agent": "Mozilla/5.0 (Macintosh; Intel Mac OS X 10_15_7) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/128.0.0.0 Safari/537.36",
        "Accept": "text/html,application/xhtml+xml,application/xml;q=0.9,*/*;q=0.8",
        "Accept-Language": "en-US,en;q=0.5",
    }
    data = urllib.parse.urlencode({"q": query}).encode("utf-8")
    req = urllib.request.Request(url, data=data, headers=headers)

    time.sleep(delay_seconds)
    try:
        with urllib.request.urlopen(req, timeout=10) as resp:
            html = resp.read().decode("utf-8", errors="replace")
            # Extract snippets from result-snippet table cells
            snippets = re.findall(r"<td class='result-snippet'>(.*?)</td>", html, re.DOTALL | re.IGNORECASE)
            cleaned = []
            for s in snippets:
                clean_txt = unescape(re.sub(r"<[^>]+>", " ", s)).strip()
                clean_txt = re.sub(r"\s+", " ", clean_txt)
                if clean_txt:
                    cleaned.append(clean_txt)
            return cleaned
    except Exception as e:
        logger.warning(f"Search request failed for '{query}': {e}")
        return []

def extract_odds_from_text(text: str) -> Optional[str]:
    """Applies regex heuristics to extract stated pack odds."""
    for pattern in ODDS_PATTERNS:
        match = pattern.search(text)
        if match:
            denominator = match.group(1)
            qualifier = match.group(2) if len(match.groups()) > 1 and match.group(2) else "Packs"
            qualifier_clean = qualifier.strip().title()
            if not qualifier_clean.endswith("Packs") and "Pack" not in qualifier_clean and "Box" not in qualifier_clean and "Case" not in qualifier_clean:
                qualifier_clean = f"{qualifier_clean} Packs"
            return f"1:{denominator} {qualifier_clean}".strip()
    return None

def is_standard_base_set(theme: str, variant: str) -> bool:
    v = (variant or "").strip().lower()
    t = (theme or "").strip().lower()
    return v in ["", "base"] and t in ["", "base", "base set"]

def enrich_pipeline(
    cards_path: Path,
    lookup_path: Path,
    dry_run: bool = False,
    scrape_limit: int = 0,
    test_filter: Optional[str] = None,
    sql_output_path: Optional[Path] = None,
) -> Tuple[int, int, int]:
    """
    Main enrichment routine:
    1. Aggregates unique sets
    2. Uses lookup cache + web scrapers
    3. Enriches cards.json
    4. Writes back outputs
    """
    if not cards_path.exists():
        logger.error(f"Cards JSON dataset not found at {cards_path}")
        return (0, 0, 0)

    cards = load_json(cards_path)
    if not isinstance(cards, list):
        logger.error(f"Invalid card list in {cards_path}")
        return (0, 0, 0)

    lookup = load_json(lookup_path) or {}
    logger.info(f"Loaded {len(cards)} cards from {cards_path}")
    logger.info(f"Loaded {len(lookup)} cached set entries from {lookup_path}")

    # 1. Aggregate unique sets
    sets_map: Dict[str, Dict[str, str]] = {}
    for c in cards:
        season = c.get("season")
        brand = c.get("brand")
        theme = c.get("theme")
        variant = c.get("variant")
        key = make_set_key(season, brand, theme, variant)

        if test_filter:
            full_str = f"{season} {brand} {theme} {variant}".lower()
            if test_filter.lower() not in full_str:
                continue

        if key not in sets_map:
            sets_map[key] = {
                "season": season or "",
                "brand": brand or "",
                "theme": theme or "",
                "variant": variant or "",
            }

    logger.info(f"Identified {len(sets_map)} target unique sets to evaluate.")

    # 2. Lookup & Scrape
    scraped_count = 0
    resolved_count = 0

    for key, set_info in sets_map.items():
        if key in lookup and lookup[key].get("pack_odds"):
            resolved_count += 1
            continue

        season = set_info["season"]
        brand = set_info["brand"]
        theme = set_info["theme"]
        variant = set_info["variant"]

        # Check standard base fallback
        if is_standard_base_set(theme, variant):
            lookup[key] = {
                "season": season,
                "brand": brand,
                "theme": theme,
                "variant": variant,
                "pack_odds": "Standard Base",
                "source": "Standard Base Definition",
                "confidence": "DEFAULT",
            }
            resolved_count += 1
            continue

        # Check scrape limit
        if scrape_limit > 0 and scraped_count >= scrape_limit:
            continue

        # Scrape DuckDuckGo for stated odds
        query = f'"{season}" "{brand}" "{variant}" odds OR pack'
        logger.info(f"Scraping odds for: {season} {brand} - {theme} [{variant}] (Query: {query})")
        snippets = search_duckduckgo_lite(query)
        scraped_count += 1

        odds_found = None
        for snippet in snippets:
            odds_found = extract_odds_from_text(snippet)
            if odds_found:
                break

        if odds_found:
            logger.info(f"-> Discovered odds for {key}: '{odds_found}'")
            lookup[key] = {
                "season": season,
                "brand": brand,
                "theme": theme,
                "variant": variant,
                "pack_odds": odds_found,
                "source": "Automated Web Scrape",
                "confidence": "AUTOMATED",
            }
            resolved_count += 1
        else:
            # Check secondary search without variant quotes if variant is complex
            if variant and variant.lower() != "base":
                query_fallback = f'"{season}" "{brand}" {theme} odds "1:"'
                snippets_fb = search_duckduckgo_lite(query_fallback)
                scraped_count += 1
                for snippet in snippets_fb:
                    odds_found = extract_odds_from_text(snippet)
                    if odds_found:
                        break

            if odds_found:
                logger.info(f"-> Discovered fallback odds for {key}: '{odds_found}'")
                lookup[key] = {
                    "season": season,
                    "brand": brand,
                    "theme": theme,
                    "variant": variant,
                    "pack_odds": odds_found,
                    "source": "Automated Fallback Scrape",
                    "confidence": "AUTOMATED",
                }
                resolved_count += 1
            else:
                logger.debug(f"No stated odds found for {key}. Marking N/A.")
                lookup[key] = {
                    "season": season,
                    "brand": brand,
                    "theme": theme,
                    "variant": variant,
                    "pack_odds": "N/A",
                    "source": "Heuristic Default",
                    "confidence": "UNVERIFIED",
                }

    # 3. Enrich cards
    enriched_cards_count = 0
    sql_updates = []

    for c in cards:
        season = c.get("season")
        brand = c.get("brand")
        theme = c.get("theme")
        variant = c.get("variant")
        key = make_set_key(season, brand, theme, variant)

        entry = lookup.get(key)
        if entry and entry.get("pack_odds"):
            odds_val = entry["pack_odds"]
            if odds_val not in ["Standard Base", "N/A"]:
                c["packOdds"] = odds_val
                enriched_cards_count += 1
            else:
                c["packOdds"] = None

            # Generate SQL update if ID exists
            card_id = c.get("id")
            if card_id and odds_val not in ["Standard Base", "N/A"]:
                sql_odds = odds_val.replace("'", "''")
                sql_updates.append(f"-- Card: {card_id}\nUPDATE card SET pack_odds = '{sql_odds}' WHERE id = (SELECT c.id FROM card c WHERE ...);")

    logger.info(f"Enriched {enriched_cards_count} / {len(cards)} cards with specific pack odds.")

    # 4. Save results if not dry-run
    if not dry_run:
        save_json(lookup_path, lookup)
        save_json(cards_path, cards)
        logger.info(f"Successfully saved updated lookup to {lookup_path}")
        logger.info(f"Successfully saved enriched cards to {cards_path}")

        # Also mirror lookup to card-collectionJava if path exists
        mirror_path = Path("../card-collectionJava/content/json/pack-odds-lookup.json")
        if mirror_path.parent.exists():
            save_json(mirror_path, lookup)
            logger.info(f"Mirrored lookup to {mirror_path}")

        if sql_output_path and sql_updates:
            sql_output_path.parent.mkdir(parents=True, exist_ok=True)
            with open(sql_output_path, "w", encoding="utf-8") as f:
                f.write("\n".join(sql_updates) + "\n")
            logger.info(f"Saved SQL updates to {sql_output_path}")
    else:
        logger.info("[DRY RUN] No files were modified.")

    return len(cards), enriched_cards_count, len(lookup)

def main():
    parser = argparse.ArgumentParser(description="Enrich Trading Cards dataset with historical Pack Odds / Insertion Ratios.")
    parser.add_argument("--cards-json", type=Path, default=Path("../card-collectionJava/content/json/cards.json"), help="Path to cards.json")
    parser.add_argument("--lookup-json", type=Path, default=Path("data/pack_odds_lookup.json"), help="Path to pack_odds_lookup.json")
    parser.add_argument("--dry-run", action="store_true", help="Perform run without writing changes")
    parser.add_argument("--scrape-limit", type=int, default=0, help="Maximum number of external scrape requests")
    parser.add_argument("--test-filter", type=str, default=None, help="Filter sets by keyword (e.g. 'Flair Showcase')")
    parser.add_argument("--sql-output", type=Path, default=None, help="Optional path to output SQL updates")

    args = parser.parse_args()
    enrich_pipeline(
        cards_path=args.cards_json,
        lookup_path=args.lookup_json,
        dry_run=args.dry_run,
        scrape_limit=args.scrape_limit,
        test_filter=args.test_filter,
        sql_output_path=args.sql_output,
    )

if __name__ == "__main__":
    main()
