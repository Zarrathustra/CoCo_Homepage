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
- Module subpages and the install guide are embedded in `index.html` and selected through `data-view` / hash navigation. The active views are `home`, `cryosieve`, `cryopros`, `ctfa`, and `install`.
- The homepage currently includes: hero copy, a relationship-step panel, a CoCo GUI preview, module ledger rows, a v0.1.0 download panel, code/publication references, and legal/contact footer content.
- The CoCo GUI preview uses `img/North_Star_light_mode.png` and `img/North_Star_dark_mode.png`, with an inline JavaScript toggle controlled by `data-coco-gui-mode` and `aria-pressed`.
- Inline JavaScript in `index.html` manages the module dropdown, GUI preview mode toggle, hash/deep-link view switching, browser history, focus movement, install-page internal anchors, anchor routing back to home, and keyboard activation for clickable module rows.
- The visual system uses OKLCH design tokens in `:root`: warm paper surfaces, muted ink text, coconut accents for CryoSieve, leaf accents for CryoPROS, slate/ctfa accents for other module states, and shared radius/shadow/spacing tokens.
- Assets live at the repository root (`coco_logo.png`, `coco.tar.gz`) and under `img/`. The Maven resource configuration copies `index.html`, `styles.css`, `coco_logo.png`, `coco.tar.gz`, and `img/**` into Spring Boot static resources.
- `src/main/java/org/cocoproject/homepage/CocoHomepageApplication.java` is only the Spring Boot wrapper used to serve the static site as a runnable JAR. Business/UI changes usually belong in `index.html`, `styles.css`, and assets, not Java.
- `src/main/resources/application.properties` currently only names the Spring application.

## Current content state

- CoCo is presented as the only primary download target. The `v0.1.0` release downloads from `coco.tar.gz`; do not validate or unpack this archive unless the user explicitly asks.
- The full installation guide is a designed `#install` view in `index.html`, not a public raw `INSTALL.md` link. Source material came from `/Users/hmx/Development/wdm3-v0.1-release-fixes/INSTALL.md`.
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
