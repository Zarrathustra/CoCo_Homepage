# CLAUDE.md

This file provides guidance to Claude Code (claude.ai/code) when working with code in this repository.

## Project overview

This repository contains the CoCo Project homepage: a mostly static single-page site for a cryo-EM workflow GUI covering data preparation, reconstruction, and analysis, with CryoSieve, CryoPROS, and EModelG as featured modules. The product direction is documented in `PRODUCT.md`, and the detailed visual system is documented in `DESIGN.md`.

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
- Module subpages, the change log directory, release notes, and all four versioned installation pages are embedded in `index.html` and selected through `data-view` / hash navigation. The active views are `home`, `cryosieve`, `cryopros`, `emodelg`, `changelog`, `changelogs`, `changelog-v0-3-2`, `changelog-v0-2-0`, `install-v0-3-5`, `install-v0-3-2`, `install-v0-2-0`, and `install-v0-1-0`.
- The homepage currently includes: hero copy, a relationship-step panel, a CoCo GUI preview, module ledger rows, a v0.3.5 download panel, publication references, and legal/contact footer content.
- The CoCo GUI preview uses four `North_Star` images for the Light/Dark and Cards/DAG combinations. Inline JavaScript coordinates the two segmented controls through `data-coco-gui-theme`, `data-coco-gui-layout`, and `aria-pressed`.
- Inline JavaScript in `index.html` manages the module dropdown, GUI preview mode toggle, hash/deep-link view switching, browser history, focus movement, install-page internal anchors, anchor routing back to home, and keyboard activation for clickable module rows.
- The visual system uses OKLCH design tokens in `:root`: warm paper surfaces, muted ink text, coconut accents for CryoSieve details, shared leaf accents for CryoPROS and EModelG details, slate supporting accents, and shared radius/shadow/spacing tokens. All homepage module rows share neutral styling.
- Site assets live at the repository root (`coco_logo.png`) and under `img/`. CoCo release archives live outside the JAR in the directory configured by `COCO_DOWNLOAD_DIR`, which defaults to `./downloads`.
- `/downloads/**` serves the external download directory. `/emodelg/**` serves its `emodelg/` subdirectory for CoCo Environment Check compatibility, with GET, HEAD, and byte-range support. Auxiliary resources are not CoCo releases and must not increase release download totals.
- Spring Boot serves the static site and external downloads, and owns the persistent SQLite metrics service. The database path is configured by `COCO_METRICS_DB` and defaults to `./data/coco-metrics.db`.
- Download totals are stored per CoCo version with an all-release rollup. Page traffic is stored as daily anonymous UV by hash view; raw IP addresses and raw visitor cookies are not persisted.
- `src/main/resources/application.properties` configures downloads, metrics storage, time zone, the release catalog, and the optional internal metrics token.

## Current content state

- CoCo is presented as the only primary download target. The `v0.3.5` release downloads from `/downloads/coco-0.3.5.tar.gz`; do not validate or unpack this archive unless the user explicitly asks.
- The homepage shows the all-release CoCo download total as quiet gray supporting text. Detailed daily UV and per-release download metrics are available through the Bearer-token-protected internal JSON endpoint.
- Installation pages are independent views at `#install-v0-3-5`, `#install-v0-3-2`, `#install-v0-2-0`, and `#install-v0-1-0`, with a version switcher in each guide. Each release change log links to its matching guide. The homepage opens the complete v0.3.5 guide; `#install` and legacy `#install-*` chapter links remain aliases for the latest guide. Legacy install chapter links resolve to matching latest-guide chapters, falling back to its page top when a chapter is absent, including same-view navigation. Explicit historical chapter links retain their original targets. Chapter IDs and ARIA references use version prefixes. Keep historical `install` metrics while recording new visits under versioned views.
- The v0.3.2 and v0.2.0 guides follow their respective CoCo release tags, with pinned source commits in README.md and HTML comments. v0.3.2 requires Linux x86_64 and host Python 3, uses a versioned runtime with CoCoShed, v1 APIs and `/login`, supports SLURM/LSF, and does not require RELION executables. v0.2.0 retains its unversioned runtime, CryoSPARC/RELION setup, SLURM examples, legacy APIs and `/login-verification`. Do not copy current installation details into historical guides.
- The `#changelog` view shows the v0.3.5 placeholder. The archived `#changelog-v0-3-2` view explains v0.3.2 task dependencies and automatic continuation, batch operations, separate DFR/SGD reconstruction options, and the independent CryoSPARC export job. The Workflow continuity introduction uses `img/coco-v0.3.2-deferred-job.png` at its natural aspect ratio. The batch operations section uses Figure 1 from the release document, stored at `img/coco-v0.3.2-batch-operations.png`; the original document Figure 2 is excluded. Both captions are descriptive, without figure numbers.
- The `#changelogs` directory lists v0.3.5, v0.3.2, and v0.2.0, newest first. The complete v0.2.0 notes and both original screenshots remain at `#changelog-v0-2-0`. All release action panels link to all change logs and the current CoCo download.
- CryoSieve is a wrapped module for particle sorting and sieving. The page shows version `v1.3.0`, GitHub/publication links, a featured image, a main figure, and the EMPIAR validation table.
- CryoPROS is a wrapped module for preferred-orientation correction. The page shows version `v1.0.1`, GitHub/publication links, and a featured image.
- CTFA is temporarily hidden because it is not yet visible in the CoCo application. Its content is retained in an inert template, outside active hash routing. Keep historical `ctfa` metrics, but do not expose public entries or product claims until requested.
- The homepage features three analysis modules without limiting CoCo to three capabilities. CryoSieve uses bundled DFR reconstruction and postprocessing. CryoPROS uses Prep, Train, and Generate jobs plus a separate CryoSPARC export job.
- Keep the primary download at v0.3.5 until a release change is requested. The v0.3.5 change log intentionally uses a placeholder. Its complete installation guide follows source snapshot `11bab54eaaa8514589de9314d92be2ff5d607e6f` (backend version 0.3.5), including Local GPU, SLURM/LSF, EModelG resources/Phenix, and state-preserving upgrades. Do not call this snapshot a release tag or overwrite historical guide bodies.
- EModelG covers automated glycan and protein backbone modeling. Its product page uses the v0.3.5 EModelG document and original example/workflow images, with the main Science citation DOI `10.1126/science.aef4958` verified against CoCo source. CryoSeek DOI `10.65215/bkvrt910` is the research example source. Do not add an unconfirmed standalone version, GitHub link, or primary download; hosted resource routes remain supported.
- The homepage Publications section contains the CryoSieve, CryoPROS, and EModelG papers only. Module detail pages keep their existing GitHub links.
- Footer content includes copyright, ICP/public security registration text, phone, address, and email.

## Design and content guidance

- Follow `PRODUCT.md` for audience, purpose, tone, and accessibility direction.
- Follow `DESIGN.md` for the current "Coconut Atlas" / Academic Workbench visual system.
- Keep copy academically clear and practical; avoid SaaS-style hype or dense jargon.
- Maintain the warm paper / research atlas visual style when importing content from other cases or references.
- Preserve the single primary action hierarchy: Download CoCo is the primary goal, while module pages, GitHub links, and citations remain supporting content.
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
