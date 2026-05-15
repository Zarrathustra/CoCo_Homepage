# CLAUDE.md

This file provides guidance to Claude Code (claude.ai/code) when working with code in this repository.

## Project overview

This repository contains the CoCo Project homepage: a mostly static single-page site for a cryo-EM workflow GUI that wraps CryoSieve, CryoPROS, and CTFA. The product direction is documented in `PRODUCT.md`: keep the experience gentle, professional, academic, and focused on explaining CoCo as the unified GUI hub.

## Development commands

- Run the Spring Boot app locally: `mvn spring-boot:run`
- Build the runnable JAR: `mvn package`
- Run tests: `mvn test`
- Run one test class, if tests are added: `mvn -Dtest=ClassName test`
- Serve the static page without Java, for quick layout checks: `python3 -m http.server 8000`
- Check working tree before/after edits: `git status --short`

The Maven build requires Java 17. There are currently no frontend package-manager commands; the site is plain HTML/CSS plus inline JavaScript.

## Architecture

- `index.html` is the primary application surface. It contains the homepage sections, module subpages, footer, and the inline JavaScript that switches between views.
- `styles.css` contains all styling, organized with cascade layers: `reset`, `base`, `components`, `layout`, and `responsive`. Prefer extending these existing layers rather than creating new stylesheet files.
- Module subpages are embedded in `index.html` and selected through `data-view` / hash navigation. The main views are `home`, `cryosieve`, `cryopros`, and `ctfa`.
- The visual system uses OKLCH design tokens in `:root`: warm paper surfaces, muted ink text, coconut accents for CryoSieve, leaf accents for CryoPROS, slate/ctfa accents for other module states, and shared radius/shadow/spacing tokens.
- Assets live at the repository root (`coco_logo.png`) and under `img/`. The Maven resource configuration copies `index.html`, `styles.css`, `coco_logo.png`, and `img/**` into Spring Boot static resources.
- `src/main/java/org/cocoproject/homepage/CocoHomepageApplication.java` is only the Spring Boot wrapper used to serve the static site as a runnable JAR. Business/UI changes usually belong in `index.html`, `styles.css`, and assets, not Java.
- `src/main/resources/application.properties` currently only names the Spring application.

## Design and content guidance

- Follow `PRODUCT.md` for audience, purpose, tone, and accessibility direction.
- Keep copy academically clear and practical; avoid SaaS-style hype or dense jargon.
- Maintain the current warm paper / research atlas visual style when importing content from other cases or references.
- Preserve accessibility patterns already in the page: keyboard navigation, semantic labels, alt text for meaningful images, `aria-hidden` for decorative images, and reduced-motion support.

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
- `n` number, optional: number of images to generate; currently prefer `1`-`4`.
