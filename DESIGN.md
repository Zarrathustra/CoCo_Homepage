---
name: CoCo Project
description: Calm academic brand system for the CoCo cryo-EM module hub.
colors:
  paper: "oklch(97.5% 0.008 99)"
  paper-soft: "oklch(96% 0.01 101)"
  paper-warm: "oklch(93.6% 0.013 102)"
  ink: "oklch(32.3% 0.027 161)"
  ink-muted: "oklch(42% 0.024 155)"
  ink-soft: "oklch(51.6% 0.02 145)"
  line: "oklch(88% 0.014 110)"
  line-strong: "oklch(74% 0.024 115)"
  coconut: "oklch(43.7% 0.071 61)"
  coconut-deep: "oklch(32% 0.052 57)"
  coconut-soft: "oklch(90% 0.03 75)"
  leaf: "oklch(48% 0.075 152)"
  leaf-deep: "oklch(40.3% 0.061 152)"
  leaf-soft: "oklch(92.5% 0.03 150)"
  slate: "oklch(48% 0.071 214)"
  slate-soft: "oklch(92% 0.025 214)"
  focus: "oklch(52% 0.11 152)"
typography:
  display:
    fontFamily: "Charter, Bitstream Charter, CoCo Serif, Cambria, serif"
    fontSize: "clamp(2.2rem, 4.5vw, 3.6rem)"
    fontWeight: 400
    lineHeight: 1.12
    letterSpacing: "-0.012em"
  headline:
    fontFamily: "Charter, Bitstream Charter, CoCo Serif, Cambria, serif"
    fontSize: "clamp(1.5rem, 2.5vw, 2.2rem)"
    fontWeight: 400
    lineHeight: 1.12
    letterSpacing: "-0.012em"
  title:
    fontFamily: "Nunito Sans, ui-sans-serif, system-ui, -apple-system, BlinkMacSystemFont, Segoe UI, sans-serif"
    fontSize: "clamp(1.25rem, 1.6vw, 1.45rem)"
    fontWeight: 600
    lineHeight: 1.12
    letterSpacing: "-0.012em"
  body:
    fontFamily: "Nunito Sans, ui-sans-serif, system-ui, -apple-system, BlinkMacSystemFont, Segoe UI, sans-serif"
    fontSize: "16px"
    fontWeight: 400
    lineHeight: 1.6
  label:
    fontFamily: "Nunito Sans, ui-sans-serif, system-ui, -apple-system, BlinkMacSystemFont, Segoe UI, sans-serif"
    fontSize: "0.78rem"
    fontWeight: 700
    lineHeight: 1
    letterSpacing: "0.12em"
rounded:
  sm: "10px"
  md: "16px"
  lg: "24px"
  xl: "34px"
  pill: "999px"
spacing:
  page: "clamp(1rem, 4vw, 4rem)"
  section: "clamp(1.6rem, 4.5vw, 4rem)"
  hero-gap: "clamp(1.6rem, 5vw, 4.2rem)"
  component-gap-sm: "0.5rem"
  component-gap-md: "0.85rem"
  component-gap-lg: "1.4rem"
components:
  button-primary:
    backgroundColor: "{colors.leaf-deep}"
    textColor: "{colors.paper}"
    rounded: "{rounded.pill}"
    minHeight: "48px"
    padding: "0.75rem 1.15rem"
    fontSize: "0.95rem"
    fontWeight: 600
    lineHeight: 1.25
  button-primary-hover:
    backgroundColor: "{colors.leaf}"
    textColor: "{colors.paper}"
    rounded: "{rounded.pill}"
    padding: "0.75rem 1.15rem"
  button-secondary:
    backgroundColor: "{colors.paper-soft}"
    textColor: "{colors.coconut-deep}"
    rounded: "{rounded.pill}"
    minHeight: "48px"
    padding: "0.75rem 1.15rem"
  chip:
    backgroundColor: "{colors.paper-soft}"
    textColor: "{colors.ink-muted}"
    rounded: "{rounded.pill}"
    padding: "0.45rem 0.72rem"
  workbench-card:
    backgroundColor: "{colors.paper-soft}"
    textColor: "{colors.ink}"
    rounded: "{rounded.xl}"
    padding: "0"
  module-hero:
    backgroundColor: "{colors.paper-warm}"
    textColor: "{colors.ink}"
    rounded: "{rounded.xl}"
    padding: "clamp(2rem, 5vw, 4.5rem) clamp(1.4rem, 4vw, 3rem)"
---

# Design System: CoCo Project

## 1. Overview

**Creative North Star: "Coconut Atlas"**

CoCo should feel like a carefully annotated atlas for a structural biology lab: quiet paper, sage-grey ink, precise routes, and a central landmark that makes the module family easy to navigate. The coconut mark gives the brand a human and memorable center, while the diagrammatic workbench keeps the page credible for cryo-EM researchers.

The physical scene is a cryo-EM researcher in a bright lab office, glancing between reconstruction runs and a project homepage to understand where to download CoCo and how CryoSieve, CryoPROS, and EModelG relate. That scene requires a light, low-glare interface with calm academic density, not a dark launch screen or a sales-heavy SaaS page.

This system rejects generic research-project template pages, exaggerated SaaS landing-page styling, overly flashy dark tech aesthetics, and dense jargon walls. The interface should be composed, tactile, and navigable, with one clear primary action: Download CoCo.

**Key Characteristics:**

- Quiet paper surfaces and sage-grey ink, with low-chroma green, coconut, and slate module cues.
- Regular-weight serif headings for academic calm, Nunito Sans for readable instructions.
- Diagrammatic structure over card-grid repetition.
- Ambient tactile depth, never glassy or glossy.
- CoCo is always the hub; module pages extend the system without competing with it.

## 2. Colors

The palette is Sage Paper: faintly tinted paper neutrals and green-grey ink carry the page, while low-chroma coconut brown, growth green, and cryo slate identify system roles. The first screen introduced this palette; every view now shares it.

### Primary

- **Growth Green** (`leaf-deep`, `leaf`, `leaf-soft`): The primary action and active module language. Use it for Download CoCo, hover confirmation, CryoPROS, focusable positive actions, and select soft backgrounds.

### Secondary

- **Coconut Brown** (`coconut-deep`, `coconut`, `coconut-soft`): The CoCo identity color and CryoSieve marker. Use it for the hub, brand wordmark accents, numbered relationship steps, citation links, and warm emphasis.

### Tertiary

- **Cryo Slate** (`slate`, `slate-soft`): The cryo-EM technical support color. Use it sparingly in the workflow map and secondary reference interactions.

### Neutral

- **Sage Paper** (`paper`, `paper-soft`, `paper-warm`): The base surface system. It should look like quiet lab paper, never pure white.
- **Sage Ink** (`ink`, `ink-muted`, `ink-soft`): Green-grey text hierarchy. Use full ink for headings, muted ink for explanatory prose, and soft ink for metadata.
- **Pencil Line** (`line`, `line-strong`): Dividers, table rules, card borders, and map strokes. Lines are structural and quiet.

### Named Rules

**The Tinted Paper Rule.** Every neutral surface keeps a faint paper tint and every ink keeps its green-grey cast. Pure black and pure white are forbidden.

**The One CoCo Action Rule.** Growth Green carries primary action energy. Do not let module links or GitHub references compete with Download CoCo.

**The Module Color Rule.** CryoSieve uses Coconut Brown; CryoPROS and EModelG share Growth Green; and Cryo Slate stays a supporting cryo-EM note. All homepage module rows use the same paper surfaces and ink text.

## 3. Typography

**Display Font:** Charter, then Bitstream Charter, then self-hosted CoCo Serif, then Cambria and serif. CoCo Serif is a Latin subset of Charis SIL, renamed to respect its OFL reserved font name; it loads only where Charter is missing.
**Body Font:** Nunito Sans, self-hosted as one variable WOFF2 (weights 400 to 700) and used on every platform, including macOS and iOS.
**Label/Mono Font:** No mono family. Labels use the body sans in small uppercase.

**Character:** The pairing should feel like a lab note that has been carefully typeset: regular-weight serif headings for trust and calm, a soft humanist sans for practical reading. Font files and their OFL licenses live in `fonts/`.

**Weight roles:** 400 for body and serif headings, 500 for navigation and quiet metadata, 600 for titles, buttons, and emphasis, 700 for small uppercase labels and the brand name. Do not introduce other sans weights.

### Hierarchy

- **Display** (400, fluid display size, 1.12 line-height): Hero titles and module page titles. Keep the measure short and balanced. The home hero pairs the regular title with a bold-italic two-tone CoCo wordmark (slate, then coconut).
- **Headline** (400, fluid headline size, 1.12 line-height): Section headings that explain system relationship and primary action.
- **Title** (600, compact fluid title size, 1.12 line-height): Module names, component headings, and subsection titles.
- **Body** (400, 16px, 1.6 line-height): Explanatory prose and research-facing descriptions. Keep body copy around 65 to 75 characters per line.
- **Label** (700, compact uppercase, tracked): Eyebrows, toolbar labels, table headers, and small status cues. Labels must stay short.

### Named Rules

**The Calm Hierarchy Rule.** Use size, weight, and measure for hierarchy. Do not add decorative type effects, gradient text, or noisy emphasis.

**The No Mono Costume Rule.** Do not use monospace as shorthand for scientific software. This brand is technical through structure, not terminal styling.

## 4. Elevation

Elevation is ambient tactile. Surfaces feel touchable because they cast soft, broad shadows and sit on tinted paper, not because they float behind blur. The workflow card and download panel can have gentle depth; rows, chips, tables, and citations stay mostly border-led.

### Shadow Vocabulary

- **Ambient Surface** (`0 24px 70px oklch(32% 0.026 155 / 0.12)`): Use only for major showcase surfaces like the workbench card.
- **Soft Surface** (`0 12px 38px oklch(32% 0.026 155 / 0.08)`): Use for secondary panels, figures, download surfaces, and embedded media.
- **Primary Button Glow** (`0 6px 16px oklch(35% 0.0606 152 / 0.14)`): Use for the primary CTA at rest.
- **Primary Button Hover Glow** (`0 8px 20px oklch(35% 0.0606 152 / 0.2)`): Use only on primary CTA hover.
- **Logo Drop** (`drop-shadow(0 8px 16px oklch(36% 0.0544 61 / 0.15))`): Use for the coconut logo and footer mark.

### Named Rules

**The Ambient Not Glass Rule.** Depth comes from soft shadow, tonal layering, and quiet borders. Decorative blur, frosted panels, and glassmorphism are forbidden.

**The Lift on Intent Rule.** Interactive lift should be slight: one pixel for hover, small scale on active press. Large floating cards are not part of this system.

## 5. Components

### Buttons

- **Shape:** Fully rounded pills (`pill`). Buttons should feel reassuring and practical, not flashy.
- **Size:** Content action buttons share a 48px minimum height, 0.95rem text, and 1.25 line height. Keep navigation compact and preserve 48px touch targets. Grid rows must not stretch buttons vertically.
- **Grouping:** Paired actions share a baseline and a 12px gap. Keep 24px between the download heading and its actions. Below 36rem, stack content buttons at equal full width. Release-directory links sit below the main release actions as aligned text links.
- **Primary:** Growth Green background, paper text, 600 weight, compact horizontal padding. This is for Download CoCo and no other equal-priority action.
- **Hover / Focus:** Hover shifts to brighter Growth Green with a slightly stronger glow. Focus uses the visible green ring from `focus`.
- **Secondary:** Paper fill, Pencil Line border, Coconut Brown text. Use for lower-commitment navigation like View modules.
- **Disabled:** Reduce opacity, remove shadow, and prevent pointer interaction. Use disabled buttons only for unavailable actions; the current release download is an active primary link.

### Chips

- **Style:** Small pill badges with paper fill, quiet borders, and semibold text.
- **Module Color:** Homepage module chips share neutral paper fills. Detail-page chips use coconut tints for CryoSieve and leaf tints for CryoPROS and EModelG.
- **Use:** Use chips for attributes and status. Do not turn every metadata phrase into a chip.

### Cards / Containers

- **Corner Style:** Generous radii for major surfaces (`xl`), medium radii for tables, citations, and figures (`md`).
- **Background:** Paper gradients and OKLCH tints. Major panels may use subtle radial color fields.
- **Shadow Strategy:** Use Ambient Surface for the workbench only, Soft Surface for important panels, and borders for lists.
- **Border:** Quiet Pencil Line borders. No thick side stripes.
- **Internal Padding:** Use fluid padding for major panels and compact fixed padding for rows, tags, and citations.

### First Screen

- **Structure:** Eyebrow, three-line display title, two-line lede, the Download CoCo pill with its Linux mark and version note, then an Explore modules text link. A decorative density-map gallery (`aria-hidden`, WebP at 1x and 2x) sits beside the copy.
- **Layout:** The first screen shares the page container, so its left edge matches the header and every section. From 58rem it is two columns (copy 0.9fr, gallery 1.1fr); below that the gallery follows the copy at no more than 34rem wide.
- **Scale:** Sizes grow with the viewport and stop at their 1440px values, because the container stops growing there. Use `clamp()` and rem, never artboard units or absolute positioning.

### Navigation

- **Style:** One sticky header for every view: solid paper background, a single line divider, and the same content width as the page sections.
- **Layout:** Brand on the left, text links right-aligned, then the Download CoCo pill. The brand reads "CoCo" (600) and "Project" (400), with an uppercase "Cryo-EM Copilot" tagline whose Cryo and Co are tinted slate and coconut.
- **Typography:** Nunito Sans 500 for links, without pill backgrounds.
- **Dropdown:** Rounded panel, soft shadow, paper fill. Module options are grouped as title plus concise descriptor.
- **States:** Hover turns link text leaf-deep; dropdown items use leaf-soft backgrounds. Keyboard focus must remain visible.
- **Mobile:** Links wrap to a second row instead of collapsing into a modal. Below 30rem the header CTA shows "Download" and keeps the version in its accessible name. Keep touch targets at least 48px on coarse pointers.

### Module Ledger

- **Style:** A ruled ledger, not an identical card grid. Each module row has an index, description, and tags.
- **Interaction:** Rows are clickable with subtle background change and visible focus outline.
- **Status:** CTFA is deferred until it is visible in CoCo. Public rows and navigation feature CryoSieve, CryoPROS, and EModelG. Retain the neutral row treatment and existing module accents.

### GUI Preview

- **Style:** A headed section with one large workbench card: the CoCo screenshot at 16:9, then a controls bar. No extra window chrome; the screenshots already include the browser toolbar.
- **Controls:** A dark segmented group switches Structure, Job graph, 2D classes, and EModelG. A one-sentence live caption explains the current view and spells out abbreviations such as DAG. Light/Dark is a small paper-toned toggle at the end of the bar, visibly secondary to the view group.
- **Rule:** The preview shows what CoCo looks like in use. Captions describe only what the screenshot shows.

### Figures and Tables

- **Figures:** Use bordered, softly shadowed images with medium radii. Scientific figures should be legible and not cropped aggressively.
- **Tables:** Use compact rows, uppercase headers, paper fill, and horizontal overflow support on small screens.

## 6. Do's and Don'ts

### Do:

- **Do** keep CoCo as the central GUI hub in both copy and layout.
- **Do** use OKLCH tokens from the frontmatter for all new colors.
- **Do** keep the primary CTA singular: Download CoCo should remain the only primary action.
- **Do** use tinted paper neutrals for large surfaces and full Sage Ink for headings.
- **Do** use the module color rule consistently: neutral homepage rows, Coconut Brown for CryoSieve details, and Growth Green for CryoPROS and EModelG details.
- **Do** preserve keyboard navigation, visible focus rings, and reduced-motion behavior.
- **Do** keep scientific copy concise, precise, and readable.

### Don't:

- **Don't** create generic research-project template pages.
- **Don't** use exaggerated SaaS landing-page styling.
- **Don't** use overly flashy dark tech aesthetics.
- **Don't** create dense jargon walls.
- **Don't** use pure black, pure white, gradient text, or decorative glassmorphism.
- **Don't** use thick colored side-stripe borders on cards, rows, callouts, or alerts.
- **Don't** repeat identical icon-card grids for modules. Use ledger rows, diagrams, or content-specific layouts.
- **Don't** make CryoSieve, CryoPROS, EModelG, GitHub, or references look like equal download actions.
- **Don't** use modals as the first solution for navigation or module detail.
- **Don't** add em dash punctuation in interface copy.
