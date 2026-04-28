---
name: slides
description: Create and edit presentation slide decks (`.pptx`) with PptxGenJS and render/validation utilities. Use when tasks involve building a new PowerPoint deck, recreating slides from screenshots/PDFs/reference decks, modifying slide content while preserving editable output, adding charts/diagrams/visuals, or diagnosing layout issues such as overflow, overlaps, and font substitution.
license: Apache-2.0
metadata:
  short-description: "Create and edit PPTX slide decks"
  author: OpenAI
  source: https://github.com/openai/skills/tree/e6afb0d74cc75d220df2faf3dd6c635c2dc6a108/skills/.curated/slides
---

# Slides

## Overview

Use PptxGenJS for slide authoring. Do not use `python-pptx` for deck generation unless the task is inspection-only; keep editable output in JavaScript and deliver both the `.pptx` and the source `.js`.

Keep work in a task-local directory. Only copy final artifacts to the requested destination after rendering and validation pass.

## Bundled Resources

- `scripts/render_slides.py`: Rasterize a `.pptx` or `.pdf` to per-slide PNGs.
- `scripts/slides_test.py`: Detect content that overflows the slide canvas.
- `scripts/create_montage.py`: Build a contact-sheet style montage of rendered slides.
- `scripts/detect_font.py`: Report missing or substituted fonts as LibreOffice resolves them.

## Workflow

1. Inspect the request and determine whether you are creating a new deck, recreating an existing deck, or editing one.
2. Set the slide size up front. Default to 16:9 (`LAYOUT_WIDE`) unless the source material clearly uses another aspect ratio.
3. Build the deck in JavaScript with PptxGenJS, an explicit theme font, stable spacing, and editable PowerPoint-native elements when practical.
4. Run the bundled scripts from this skill directory or copy the needed ones into the task workspace. Render the result with `render_slides.py`, review the PNGs, and fix layout issues before delivery.
5. Run `slides_test.py` for overflow checks when slide edges are tight or the deck is dense.
6. Deliver the `.pptx`, the authoring `.js`, and any generated assets that are required to rebuild the deck.

## Authoring Rules

- Set theme fonts explicitly. Do not rely on PowerPoint defaults if typography matters.
- Size text boxes deliberately and verify by rendering; do not rely on PptxGenJS `fit` or `autoFit` for final layout.
- Use bullet options, not literal `•` characters.
- Use explicit crop/contain calculations when placing images so aspect ratio and framing are intentional.
- Prefer native PowerPoint charts for simple bar/line/pie/histogram style visuals so reviewers can edit them later.
- For charts or diagrams that PptxGenJS cannot express well, render SVG externally and place the SVG in the slide.
- Fix all unintentional overlap and out-of-bounds issues before delivering. If an overlap is intentional, leave a short code comment near the relevant element.

## Recreate Or Edit Existing Slides

- Render the source deck or reference PDF first so you can compare slide geometry visually.
- Match the original aspect ratio before rebuilding layout.
- Preserve editability where possible: text should stay text, and simple charts should stay native charts.

## Validation Commands

Examples below assume you copied the needed scripts into the working directory. If not, invoke the same script paths relative to this skill folder.

```bash
# Render slides to PNGs for review
python3 scripts/render_slides.py deck.pptx --output_dir rendered

# Build a montage for quick scanning
python3 scripts/create_montage.py --input_dir rendered --output_file montage.png

# Check for overflow beyond the original slide canvas
python3 scripts/slides_test.py deck.pptx

# Detect missing or substituted fonts
python3 scripts/detect_font.py deck.pptx --json
```
