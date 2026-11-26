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
- Module subpages, the change log directory, release notes, and all five versioned installation pages are embedded in `index.html` and selected through `data-view` / hash navigation. The active views are `home`, `cryosieve`, `cryopros`, `emodelg`, `cocofold-cocoflex`, `structure-validation`, `changelog`, `changelogs`, `changelog-v0-3-5`, `changelog-v0-3-2`, `changelog-v0-2-0`, `install-v0-3-10`, `install-v0-3-5`, `install-v0-3-2`, `install-v0-2-0`, and `install-v0-1-0`.
- The homepage currently includes: a first screen (eyebrow, three-line title, lede, Download CoCo pill with version note, Explore modules link, and a decorative density-map gallery), a headed CoCo GUI preview, module ledger rows, a v0.3.10 download panel, publication references, and legal/contact footer content. The first screen shares the page container and uses grid, `clamp()`, and rem; its sizes stop at their 1440px values. The gallery is `img/hero-density-gallery-863.webp` / `-1726.webp` via `srcset`. Its Download CoCo pill keeps linking to `#download`.
- The CoCo GUI preview (`#workspace`, with its own section heading) uses eight screenshots for Light/Dark and Structure / Job graph / 2D classes / EModelG, defaulting to Light + Structure. A live caption below the image describes the current view, and Light/Dark is a small secondary toggle on the right of the controls bar. They are served from `img/gui/` as WebP at 1600w and 2560w through `srcset`/`sizes`; the original 5K PNGs are source masters only. The displayed image is lazy-loaded without high fetch priority. Do not preload all eight on page load: when the card nears the viewport and the browser is idle, the script warms the other views of the current theme; hovering or focusing a control warms that control's image; nothing is warmed under Save-Data or 2G. A single displayed image preserves its 16:9 ratio. Inline JavaScript coordinates the independent theme/view controls through `data-coco-gui-theme`, `data-coco-gui-layout`, and `aria-pressed`, updating the preview's accessible description. Below 36rem, the view buttons use a 2×2 grid. The eight screenshots predate the v0.3.10 interface (Workspaces, the side-by-side job editor, and panel-based Overviews); retaking them is deferred until requested.
- Every view shares one header: brand (CoCo / Project with the uppercase Cryo-EM Copilot tagline), right-aligned Overview / Modules / References links, and one Download CoCo pill. Overview points to `#workspace`; `#overview` remains on the first screen for old links. Below 30rem the pill shows "Download" and keeps "CoCo v0.3.10" in its accessible name. Mobile navigation wraps without clipping the Modules disclosure, and coarse-pointer navigation targets are at least 48px tall. The disclosure uses explicit click/keyboard state, `hidden`, and `aria-expanded`; do not reopen it with CSS hover/focus selectors. A ResizeObserver keeps the root scroll padding at the actual sticky-header height plus 16px for homepage and installation anchors.
- Inline JavaScript in `index.html` manages the module dropdown, GUI preview mode toggle, hash/deep-link view switching, browser history, focus movement, install-page internal anchors, anchor routing back to home, and keyboard activation for clickable module rows.
- The visual system uses OKLCH design tokens in `:root` (the Sage Paper palette from `DESIGN.md`): tinted paper surfaces, green-grey ink text, low-chroma coconut accents for CryoSieve details, shared leaf accents for CryoPROS and EModelG details, slate supporting accents, and shared radius/shadow/spacing tokens. All homepage module rows share neutral styling. Sans text uses self-hosted Nunito Sans on every platform; serif headings use Charter with a self-hosted CoCo Serif fallback (a renamed Charis SIL subset). Font files and OFL licenses live in `fonts/`; the Nunito Sans file is preloaded. Sans weights are limited to 400, 500, 600, and 700, and serif headings use 400.
- Site assets live at the repository root (`coco_logo.png`, the source logo) and under `img/` and `fonts/`. Pages use `img/coco-logo-144.webp` for the header and footer marks. `pom.xml` packages `img/**` and `fonts/**` but excludes the eight 5K GUI PNG masters and the CryoPROS featured PNG master. CoCo release archives live outside the JAR in the directory configured by `COCO_DOWNLOAD_DIR`, which defaults to `./downloads`.
- `/downloads/**` serves the external download directory. `/emodelg/**` and `/cocofold/**` serve its `emodelg/` and `cocofold/` subdirectories for CoCo resource catalog compatibility, with GET, HEAD, and byte-range support. Auxiliary resources are not CoCo releases and must not increase release download totals.
- CoCoFold resource version `protenix-1.0.2-r1` uses the unchanged `cocofold-models-protenix-1.0.2-r1.tar.gz` and `cocofold-common-protenix-1.0.2-r1.tar.gz` archives from `/Users/hmx/Protenix_Resources/cocofold/`, hosted under `COCO_DOWNLOAD_DIR/cocofold/` with `LICENSE-Protenix.txt`. Include `SHA256SUMS` and `assets-manifest.json` as manual references; runtime trust comes from catalogs embedded in CoCo and CoCoShed. Keep resource files outside Git and the website JAR. CoCo v0.3.10 downloads these archives through its CoCoFold Resources check (default base URL `https://cocoproject.org.cn/cocofold`), and the v0.3.10 installation guide links `/cocofold/LICENSE-Protenix.txt`. The local folder currently lacks `cocofold-models-protenix-1.0.2-r1.tar.gz`; confirm the server copy before release.
- Spring Boot serves the static site and external downloads, and owns the persistent SQLite metrics service. The database path is configured by `COCO_METRICS_DB` and defaults to `./data/coco-metrics.db`.
- Download totals are stored per CoCo version with an all-release rollup. Page traffic is stored as daily anonymous UV by hash view; raw IP addresses and raw visitor cookies are not persisted.
- `src/main/resources/application.properties` configures downloads, metrics storage, time zone, the release catalog, and the optional internal metrics token.

## Current content state

- CoCo is presented as the only primary download target. The `v0.3.10` release downloads from `/downloads/coco-0.3.10.tar.gz`; do not validate or unpack this archive unless the user explicitly asks. The release catalog keeps v0.2.0, v0.3.2, and v0.3.5 alongside v0.3.10.
- The homepage shows the all-release CoCo download total as quiet gray supporting text. Detailed daily UV and per-release download metrics are available through the Bearer-token-protected internal JSON endpoint.
- Installation pages are independent views at `#install-v0-3-10`, `#install-v0-3-5`, `#install-v0-3-2`, `#install-v0-2-0`, and `#install-v0-1-0`, with a version switcher in each guide. Each release change log links to its matching guide. The homepage opens the complete v0.3.10 guide; the v0.3.5 guide is archived with its body unchanged; `#install` and legacy `#install-*` chapter links remain aliases for the latest guide. Legacy install chapter links resolve to matching latest-guide chapters, falling back to its page top when a chapter is absent, including same-view navigation. Explicit historical chapter links retain their original targets. Chapter IDs and ARIA references use version prefixes. Keep historical `install` metrics while recording new visits under versioned views.
- The v0.3.2 and v0.2.0 guides follow their respective CoCo release tags, with pinned source commits in README.md and HTML comments. v0.3.2 requires Linux x86_64 and host Python 3, uses a versioned runtime with CoCoShed, v1 APIs and `/login`, supports SLURM/LSF, and does not require RELION executables. v0.2.0 retains its unversioned runtime, CryoSPARC/RELION setup, SLURM examples, legacy APIs and `/login-verification`. Do not copy current installation details into historical guides.
- The `#changelog` view presents v0.3.10, written from source at beta snapshot `4dbafa9b051a758ecbe580e6039fa96fe47a40df` (no brochure): nineteen newly visible job types in six sections — preprocessing from movies to 2D classes; the Atomic Model group (Import Sequence, Structure Prediction, 3D Alignment, CoCoFold Refine, CoCoFlex, Structure Relaxation, Structure Validation); maps, masks, and references (EMDB import, Generate Mask and Auto Mask, Create Templates, WIT); Workspaces and the side-by-side job editor; panel-based results and settings; and upgrade notes. The preprocessing chain, including CTFA, is described only there. It reuses the Residue Findings screenshot and adds no new images. The archived `#changelog-v0-3-5` view keeps the v0.3.5 brochure notes: six newly visible job types, supported workflow saving/loading, result inspection, CryoSieve Auto Mask, CPU configuration, and preferences. The archived `#changelog-v0-3-2` view explains v0.3.2 task dependencies and automatic continuation, batch operations, separate DFR/SGD reconstruction options, and the independent CryoSPARC export job. The Workflow continuity introduction uses `img/coco-v0.3.2-deferred-job.png` at its natural aspect ratio. The batch operations section uses Figure 1 from the release document, stored at `img/coco-v0.3.2-batch-operations.png`; the original document Figure 2 is excluded. Both captions are descriptive, without figure numbers.
- The `#changelogs` directory lists v0.3.10, v0.3.5, v0.3.2, and v0.2.0, newest first. The complete v0.2.0 notes and both original screenshots remain at `#changelog-v0-2-0`. All release action panels link to all change logs and the current CoCo download.
- All five module pages share one structure: a Back to modules link (plus GitHub for CryoSieve/CryoPROS and the setup guide for EModelG), a hero with the "CoCo analysis module" kicker, an at-a-glance fact list, module content, a "Use … in CoCo" step panel where its steps are documented, publications, and a closing panel with Download CoCo, an installation or setup guide link (`#install-v0-3-10`, or its EModelG / CoCoFold chapters), and previous/next module links. Module pages show no version information: no module versions, no CoCo release labels, and no preview status. Facts and steps use only statements already supported by page content; omit a field rather than inventing it. Attribute chips were removed. Each view sets `document.title` from its `h1`.
- CryoSieve is a wrapped module for particle sorting and sieving. The page shows GitHub/publication links, a featured image, the EMPIAR validation table, four sieving-round steps, and the main figure with a descriptive caption.
- CryoPROS is a wrapped module for preferred-orientation correction. The page shows GitHub/publication links and a featured image served as WebP (`img/cryopros-featured-960.webp` / `-1920.webp`); the PNG is a source master excluded from the JAR. `img/cryopros_main_figure.png` is intentionally unused. In v0.3.10, CryoPROS Prep is created from an accepted 3D Alignment of particles with a volume or structure, as its use steps describe.
- CTFA is visible in CoCo v0.3.10, but by decision the website describes it only as one step of the preprocessing chain in the v0.3.10 change log. The old CTFA page stays in an inert template, outside active hash routing. Keep historical `ctfa` metrics, but do not add a CTFA module page, ledger row, or navigation entry until requested.
- The homepage features five analysis modules. Structure Validation at `#structure-validation` shows a cropped Residue Findings screenshot (`img/structure-validation/residue-findings-640.webp` / `-1280.webp`, linking to the full-size master), four assessment groups with their required inputs, and a narrated walkthrough with native audio controls at normal speed; the walkthrough has no captions yet because no transcript exists. CoCoFold & CoCoFlex share one page at `#cocofold-cocoflex`, with a CoCoFold section and silent CoCoFlex movies whose figcaptions show a visible title and description; their custom controls show elapsed and total time and default to 1.5×. The `materials/v0.3.10/` asset paths are file locations only, not page copy. CryoSieve uses bundled DFR reconstruction and postprocessing. CryoPROS uses Prep, Train, and Generate jobs plus a separate CryoSPARC export job.
- CoCoFold & CoCoFlex and Structure Validation are released v0.3.10 modules, treated as accepted; do not add validation caveats. Keep their facts aligned with the v0.3.10 source: CoCoFold refines Protenix 1.0.2 predictions (Base or Mini model) after an accepted 3D Alignment with posed particles, only for standard protein-only targets, in Whole or Grouped mode; CoCoFlex continues from a Mini + Whole refinement, and CoCo plays back ensembles but does not export movies. The CAHRA, HER2, and TRPV1 movies are research examples. The shipped implementation follows CoCoFold2 on Protenix, not AlphaFold. Structure Validation accepts models from Import Structure, EModelG, or Structure Relaxation, reports values without pass/fail grades, computes the MolProbity score with bundled cctbx (no Phenix), and its Map group gives unmasked/masked/corrected FSC plus 13-direction conical FSC curves (no directional-resolution or 3DFSC claims).
- The primary download is v0.3.10. Its change log and complete installation guide follow beta snapshot `4dbafa9b051a758ecbe580e6039fa96fe47a40df` (backend version 0.3.10, not a release tag), compared with tag `v0.3.5`. The guide covers glibc 2.28+, CUDA 12.6, the Python 3.11 runtime, the master-directory CryoSPARC profile (no passwords, sessions, Remote URL, or path mappings), CryoSeek WIT, PDB & EMDB connectivity, EModelG resources/Phenix, CoCoFold Resources and the Protenix MSA Service, Default CPUs per GPU and run locations, Local GPU / SLURM / LSF, built-in modules by Builder group, and upgrades from v0.3.5. Beta's INSTALL.md still contains stale CryoSPARC, `bookmarks/`, and export-script passages; trust the code. Later source changes require a new comparison before the pages are updated.
- The archived v0.3.5 change log follows `CoCo_v0.3.5_brochure.pdf` and its visibility review against beta snapshot `87e70ed7770eb7d8837a18f54700255f76c275ba`; only exposed user features are described. Its complete installation guide follows source snapshot `11bab54eaaa8514589de9314d92be2ff5d607e6f` (backend version 0.3.5), including Local GPU, SLURM/LSF, EModelG resources/Phenix, and state-preserving upgrades. Do not call these snapshots release tags or overwrite historical guide bodies.
- EModelG covers automated glycan and protein backbone modeling. Its product page follows pages 2–4 of the v0.3.5 brochure, with three original research images, accessible HTML workflow steps, research metrics with their evaluation conditions, three modeling modes, and setup links to `#install-v0-3-10-emodelg`; its use steps also mention EMDB import and Structure Validation from v0.3.10, with the main Science citation DOI `10.1126/science.aef4958` verified against CoCo source. CryoSeek DOI `10.65215/bkvrt910` is the research example source. Do not add an unconfirmed standalone version, GitHub link, or primary download; hosted resource routes remain supported.
- EModelG metrics retain their study scope: 0.643 Å mean all-atom RMSD is from a separate protein-free glycan case; 0.891 AUPRC uses a 4 Å site-matching threshold; PLL-1 has a 2.87 Å map and a 0.87 TM-score against AlphaFold. These are research results, not version-to-version performance comparisons. Workflow reuse is limited to eligible completed particle-import / 2D DFR / Select 2D / Average Spectrum flows, loaded as drafts with class review repeated.
- The homepage Publications section groups the CryoSieve, CryoPROS, EModelG, CoCoFold, and CoCoFold2 papers by module. The combined module page groups its publications under CoCoFold and CoCoFlex, reserving the CoCoFlex citation until its URL is supplied. Module detail pages keep their existing GitHub links.
- Footer content includes copyright, ICP/public security registration text, phone, address, and email.

## Design and content guidance

- Follow `PRODUCT.md` for audience, purpose, tone, and accessibility direction.
- Follow `DESIGN.md` for the current "Coconut Atlas" / Academic Workbench visual system.
- Keep copy academically clear and practical; avoid SaaS-style hype or dense jargon.
- Maintain the Sage Paper / research atlas visual style when importing content from other cases or references.
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
