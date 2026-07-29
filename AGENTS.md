# CLAUDE.md

This file provides guidance to Claude Code (claude.ai/code) when working with code in this repository.

## Project overview

This repository contains the CoCo Project homepage: a mostly static single-page site for a cryo-EM workflow GUI that wraps CryoSieve, CryoPROS, and CTFA. The product direction is documented in `PRODUCT.md`, and the detailed visual system is documented in `DESIGN.md`.

Keep the experience gentle, professional, academic, and focused on explaining CoCo as the unified GUI hub and cryo-EM workflow copilot.

## Development commands

- Run the Spring Boot app locally: `mvn spring-boot:run`
- Build the runnable JAR: `mvn package`
- Run tests: `mvn test`
- Run one test class, if tests are added: `mvn -Dtest=ClassName test`
- Serve the static page without Java, for quick layout checks: `python3 -m http.server 8000`
- Check working tree before/after edits: `git status --short`

The Maven build requires Java 17. There are currently no frontend package-manager commands; the site is plain HTML/CSS plus inline JavaScript.

## Architecture

- `index.html` is the primary application surface. It contains the sticky header, homepage, embedded module subpages, footer, and inline JavaScript.
- `styles.css` contains all styling, organized with cascade layers: `reset`, `base`, `components`, `layout`, and `responsive`. Prefer extending these existing layers rather than creating new stylesheet files.
- Module subpages, the change log, and both install guides are embedded in `index.html` and selected through `data-view` / hash navigation. The active views are `home`, `cryosieve`, `cryopros`, `ctfa`, `changelog`, `install`, and `install-v0-1-0`.
- The homepage currently includes: hero copy, a relationship-step panel, a CoCo GUI preview, module ledger rows, a v0.2.0 download panel, code/publication references, and legal/contact footer content.
- The CoCo GUI preview uses four `North_Star` images for the Light/Dark and Cards/DAG combinations. Inline JavaScript coordinates the two segmented controls through `data-coco-gui-theme`, `data-coco-gui-layout`, and `aria-pressed`.
- Inline JavaScript in `index.html` manages the module dropdown, GUI preview mode toggle, hash/deep-link view switching, browser history, focus movement, install-page internal anchors, anchor routing back to home, and keyboard activation for clickable module rows.
- The visual system uses OKLCH design tokens in `:root`: warm paper surfaces, muted ink text, coconut accents for CryoSieve, leaf accents for CryoPROS, slate/ctfa accents for other module states, and shared radius/shadow/spacing tokens.
- Site assets live at the repository root (`coco_logo.png`) and under `img/`. CoCo release archives live outside the JAR in the directory configured by `COCO_DOWNLOAD_DIR`, which defaults to `./downloads`.
- Spring Boot serves the static site and external downloads, and owns the persistent SQLite metrics service. The database path is configured by `COCO_METRICS_DB` and defaults to `./data/coco-metrics.db`.
- Download totals are stored per CoCo version with an all-release rollup. Page traffic is stored as daily anonymous UV by hash view; raw IP addresses and raw visitor cookies are not persisted.
- `src/main/resources/application.properties` configures downloads, metrics storage, time zone, the release catalog, and the optional internal metrics token.

## Current content state

- CoCo is presented as the only primary download target. The `v0.2.0` release downloads from `/downloads/coco-0.2.6.tar.gz`; do not validate or unpack this archive unless the user explicitly asks.
- The homepage shows the all-release CoCo download total as quiet gray supporting text. Detailed daily UV and per-release download metrics are available through the Bearer-token-protected internal JSON endpoint.
- The current installation guide is a designed `#install` view based on the CoCo repository's `INSTALL.md`, not a public raw-document link. The previous v0.1.0 guide remains available as the archived `#install-v0-1-0` view from the v0.2.0 change log.
- The `#changelog` view explains the v0.2.0 data job graph, multi-user project access, standardized logs, CryoSPARC configuration, and CryoPROS metric improvements using two release screenshots under `img/`.
- CryoSieve is a wrapped module for particle sorting and sieving. The page shows version `v1.3.0`, GitHub/publication links, a featured image, a main figure, and the EMPIAR validation table.
- CryoPROS is a wrapped module for preferred-orientation correction. The page shows version `v1.0.1`, GitHub/publication links, and a featured image.
- CTFA is a planned/TBD module. Do not add a GitHub link, standalone download, or release claim unless the project provides public details.
- Footer content includes copyright, ICP/public security registration text, phone, address, and email.

## Design and content guidance

- Follow `PRODUCT.md` for audience, purpose, tone, and accessibility direction.
- Follow `DESIGN.md` for the current "Coconut Atlas" / Academic Workbench visual system.
- Keep copy academically clear and practical; avoid SaaS-style hype or dense jargon.
- Maintain the warm paper / research atlas visual style when importing content from other cases or references.
- Preserve the single primary action hierarchy: Download CoCo is the primary goal, while module pages, GitHub links, citations, and CTFA status remain supporting content.
- Preserve accessibility patterns already in the page: skip link, keyboard navigation, semantic landmarks, semantic labels, alt text for meaningful images, `aria-hidden` for decorative images, visible focus states, and reduced-motion support.
- Avoid adding frontend dependencies, build steps, or new stylesheet files unless there is a clear project need.
- Keep `data/` and release archives outside Git and the JAR. Website/JAR upgrades must reuse the configured SQLite path so historical metrics survive.
- When registering a future CoCo release, add its version-to-artifact mapping without removing older release catalog entries; old per-version totals must remain queryable.

## gpt-image-2 Image Generation

Use the `bbdinoapi.top` image generation endpoint for `gpt-image-2`.

- Store the API key in the local `.env` file at `.env` as `API_KEY=...`.
- Load the key from `.env` before calling the API.
- Do not hard-code the key in commands that are meant to be saved or shared.

Example call:

```bash
set -a
. ./.env
set +a

curl https://bbdinoapi.top/v1/images/generations \
  -H "Authorization: Bearer ${API_KEY}" \
  -H "Content-Type: application/json" \
  -d '{
    "model": "gpt-image-2",
    "prompt": "description",
    "size": "1024x1024",
    "quality": "high",
    "n": 1
  }'
```

Request fields:

- `model` string, required: fixed to `gpt-image-2`.
- `prompt` string, required: describes the image to generate.
- `size` string, optional: `1024x1024`, `1792x1024`, `1024x1792`, `2560x1440`, or `2160x3840`.
- `quality` string, optional: `low`, `medium`, or `high`.
- `n` number, optional: number of images to generate; currently prefer `1` to `4`.
