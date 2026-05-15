# Progress

## Current goal

Build a production-ready Web homepage for **CoCo Project**, with three module sub-pages (CryoSieve, CryoPROS, CTFA) navigable via seamless view-switching.

The homepage is a brand surface for structural biology laboratories. It explains that **CoCo is the unified GUI interface** and that **CryoSieve, CryoPROS, and CTFA are modules wrapped inside CoCo**.

Primary conversion target: **Download CoCo** only.

## Confirmed product context

- Target users: structural biology laboratories, especially cryo-EM researchers, graduate students, PIs, and scientific software users.
- Main goals:
  - Help visitors understand what CoCo does.
  - Make the relationship between CoCo, CryoSieve, CryoPROS, and CTFA clear.
  - Drive visitors toward downloading CoCo.
- Visual tone: gentle, professional, academic.
- Avoid: generic research-project template, exaggerated SaaS marketing style, overly dark futuristic interface, excessive jargon.
- Download CTA is currently a placeholder.
- CTFA is currently TBD / placeholder.
- CTFA is **not** a primary download target.
- CryoSieve, CryoPROS, and CTFA should not compete with CoCo as download actions.

## Confirmed visual direction

Preferred visual probe: **Probe 1: Academic Workbench**.

Direction:
- Calm academic workbench / scientific workflow map.
- CoCo appears as the central unified GUI hub.
- CryoSieve, CryoPROS, and CTFA connect around CoCo as wrapped modules.
- Use subtle cryo-EM density-map contour texture.
- Use gentle lab-paper warmth, soft mineral green / blue, precise annotations, and clear module relationships.
- Keep the first screen explanatory, not just atmospheric.

## Coconut / CoConut brand element

- Current logo uses a coconut-like shape with green leaves.
- Use coconut identity cues subtly.
- Combine the coconut mark with the Probe 1 academic workbench direction.
- Good cues: coconut-shell curves, leaf green accents, warm paper tones, central hub mark.

## Module content

**CryoSieve:**
- Source: `mxhulab/cryosieve` GitHub README and JoVE paper.
- Particle sorting / sieving in single-particle cryo-EM analysis.
- Removes unnecessary particles from final stacks, improves downstream reconstruction quality.
- Latest release: `v1.3.0`.
- JoVE video: <https://www.jove.com/t/66617/enhancing-density-maps-removing-majority-particles-single-particle>
- Nature Communications paper: <https://www.nature.com/articles/s41467-023-43555-x>
- 8 validated demo cases from EMPIAR.
- Local assets: `img/featured_image.jpg` (181KB).

**CryoPROS:**
- Source: `mxhulab/cryopros` GitHub README.
- Preferred-orientation correction framework using AI-generated auxiliary particles and co-refinement.
- Latest release: `v1.0.1`.
- Local assets: `img/cryopros_main_figure.jpg` (238KB, 800x1040, sRGB).

**CTFA:**
- Placeholder / TBD. No GitHub repo, no paper, no assets.
- Marked TBD with dashed version badge, no GitHub link in back nav.

## Files

| File | Lines | Purpose |
|------|-------|---------|
| `index.html` | 665 | Homepage + 3 sub-page views + inline JS |
| `styles.css` | 1485 | All styling (5 cascade layers: reset/base/components/layout/responsive) |
| `img/featured_image.jpg` | 181KB | CryoSieve hero image |
| `img/cryopros_main_figure.jpg` | 238KB | CryoPROS main figure |
| `coco_logo.png` | — | Brand logo |
| `PRODUCT.md` | — | Product and brand context |
| `PROGRESS.md` | — | This file |

## Architecture

Single-file SPA with JS view-switching (`index.html` only, zero dependencies):

```
index.html
  <header>                    — always visible, sticky
  <main data-view="home">     — homepage (5 sections)
  <div data-view="cryosieve"> — CryoSieve sub-page (hidden)
  <div data-view="cryopros">  — CryoPROS sub-page (hidden)
  <div data-view="ctfa">      — CTFA sub-page (hidden)
  <footer>                    — always visible
```

- View transitions: opacity fade (180ms exit / 260ms enter), `cubic-bezier(0.22, 1, 0.36, 1)`
- Browser history: `pushState`/`popstate` with `#cryosieve`/`#cryopros`/`#ctfa` hashes
- Deep linking: direct URL hash loads the matching sub-page
- Scroll-anchor handling: nav links return to homepage then scroll to target
- Keyboard: Enter/Space on module rows and SVG nodes triggers navigation

## Homepage structure (5 sections)

1. **Hero** — title, lede, CTAs, fact list, SVG workflow map with clickable module nodes
2. **Overview** — "System relationship" 3-step numbered list
3. **Modules** — ledger of 3 clickable module rows (CryoSieve, CryoPROS, CTFA)
4. **Download** — primary CTA (placeholder), checklist
5. **References** — GitHub links for CryoSieve and CryoPROS

## Header navigation

- Brand logo (links to homepage view)
- Nav pills: Overview, Modules dropdown, Download, References
- Modules dropdown: 3 items linking to sub-pages via `data-nav`
- Download CTA button (pill, primary style)
- Dropdown: hover/focus-within (CSS) + click toggle (JS), Escape to close

## Sub-page structure (shared template)

Each sub-page (`module-page` + module-specific color class):
- **Back nav**: "Back to CoCo" link + GitHub link (absent for CTFA)
- **Hero**: module-colored radial gradient, kicker, title (serif h1), lede, version badge
- **Body**: single-column sections (what it does, how it works, how it fits in CoCo), figure (CryoPROS), demo table (CryoSieve), publication citation (CryoSieve), tags

## Module color identities

| Module | Accent | Hero gradient | Badge |
|--------|--------|---------------|-------|
| CryoSieve | `--coconut` (warm amber) | oklch(89% 0.048 72) | Solid border, coconut-deep |
| CryoPROS | `--leaf` (green) | oklch(91% 0.055 140) | Solid border, leaf-deep |
| CTFA | `--ctfa` (muted violet) | oklch(92% 0.032 292) | Dashed border, muted |

## CSS architecture

5 cascade layers: `@layer reset, base, components, layout, responsive`

- **reset**: box-sizing, margin reset, `[hidden] { display: none !important }`
- **base**: 23 custom properties (OKLCH), body gradients, typography (Inter + Charter)
- **components**: ~45 component classes (nav, buttons, cards, SVG map, module ledger, dropdown, sub-pages, video embed, demo table, citation)
- **layout**: width constraints, section padding
- **responsive**: 3 breakpoints (42rem, 58rem, 74rem), coarse pointer, hover:none, reduced-motion

## JS (inline, ~190 lines)

Two IIFE-managed concerns:
1. **Dropdown**: click toggle, outside-click close, Escape close
2. **View switching**: showView() with fade transitions, pushState/popstate, click delegation on `[data-nav]`, scroll-anchor handling, keyboard Enter/Space for module rows and SVG nodes

## States covered

- First load (no hash): homepage visible, sub-pages hidden
- First load (with hash): matching sub-page shown instantly
- Home ↔ sub-page: fade transition, history push
- Sub-page ↔ sub-page: cross-fade, history push
- Back/forward button: popstate handler
- CTFA TBD: dashed version badge, no GitHub link, "Details forthcoming"
- Reduced motion: all transitions become instant via global CSS rule
- Keyboard: focus management, module row/SVG node interactivity

## Implementation history

1. `3bf728c` — Initial homepage sync
2. `642d2da` — Polish: interaction states, blur fix, copy, layout details
3. `4b5a470` — Add module dropdown menu and three sub-pages with view-switching

## Important constraints

- Follow `/impeccable` design rules.
- Use OKLCH colors, not raw black/white defaults.
- No gradient text, no glassmorphism by default, no side-stripe borders.
- No repeated generic identical card grids.
- No em dashes in UI copy.
- Keep copy concise and academic.
- Keep the homepage focused on clarity, module relationships, and CoCo download confidence.
- Zero dependencies: no build tools, no frameworks, no npm.
