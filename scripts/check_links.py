#!/usr/bin/env python3
import sys, re, pathlib

root = pathlib.Path(sys.argv[1] if len(sys.argv) > 1 else ".")
errors = 0

LINK_RE = re.compile(r'\[[^\]]+\]\(([^)]+)\)')
for md in root.rglob("*.md"):
    text = md.read_text(encoding="utf-8", errors="ignore")
    for m in LINK_RE.finditer(text):
        href = m.group(1)
        if href.startswith(("http", "https", "sandbox:")):
            continue
        if href.startswith("#") or href.startswith("mailto:"):
            continue
        target = (md.parent / href).resolve()
        if not target.exists():
            print(f"[BROKEN] {md}: {href}")
            errors += 1
if errors:
    print(f"\nBroken links: {errors}")
    sys.exit(1)
print("All links OK")
