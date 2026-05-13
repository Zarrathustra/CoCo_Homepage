---
name: CoCo Project
description: Calm academic brand system for the CoCo cryo-EM module hub.
colors:
  paper: "oklch(98.2% 0.012 88)"
  paper-soft: "oklch(96.1% 0.018 92)"
  paper-warm: "oklch(93.7% 0.026 84)"
  ink: "oklch(23% 0.036 71)"
  ink-muted: "oklch(42% 0.03 80)"
  ink-soft: "oklch(55% 0.026 92)"
  line: "oklch(84% 0.033 88)"
  line-strong: "oklch(72% 0.052 82)"
  coconut: "oklch(42% 0.105 58)"
  coconut-deep: "oklch(31% 0.078 52)"
  coconut-soft: "oklch(89% 0.048 72)"
  leaf: "oklch(50% 0.112 144)"
  leaf-deep: "oklch(35% 0.083 145)"
  leaf-soft: "oklch(91% 0.055 140)"
  slate: "oklch(45% 0.065 218)"
  slate-soft: "oklch(91% 0.035 216)"
  ctfa: "oklch(58% 0.055 285)"
  ctfa-soft: "oklch(92% 0.032 292)"
  focus: "oklch(54% 0.15 142)"
typography:
  display:
    fontFamily: "Charter, Bitstream Charter, Sitka Text, Cambria, serif"
    fontSize: "clamp(2.2rem, 4.5vw, 3.6rem)"
    fontWeight: 650
    lineHeight: 1.12
    letterSpacing: "-0.025em"
  headline:
    fontFamily: "Charter, Bitstream Charter, Sitka Text, Cambria, serif"
    fontSize: "clamp(1.5rem, 2.5vw, 2.2rem)"
    fontWeight: 650
    lineHeight: 1.12
    letterSpacing: "-0.025em"
  title:
    fontFamily: "Inter, ui-sans-serif, system-ui, -apple-system, BlinkMacSystemFont, Segoe UI, sans-serif"
    fontSize: "clamp(1.25rem, 1.6vw, 1.45rem)"
    fontWeight: 720
    lineHeight: 1.12
    letterSpacing: "-0.025em"
  body:
    fontFamily: "Inter, ui-sans-serif, system-ui, -apple-system, BlinkMacSystemFont, Segoe UI, sans-serif"
    fontSize: "16px"
    fontWeight: 400
    lineHeight: 1.6
  label:
    fontFamily: "Inter, ui-sans-serif, system-ui, -apple-system, BlinkMacSystemFont, Segoe UI, sans-serif"
    fontSize: "0.78rem"
    fontWeight: 850
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
    padding: "0.78rem 1.1rem"
    typography: "{typography.title}"
  button-primary-hover:
    backgroundColor: "{colors.leaf}"
    textColor: "{colors.paper}"
    rounded: "{rounded.pill}"
    padding: "0.78rem 1.1rem"
  button-secondary:
    backgroundColor: "{colors.paper-soft}"
    textColor: "{colors.coconut-deep}"
    rounded: "{rounded.pill}"
    padding: "0.78rem 1.1rem"
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

CoCo should feel like a carefully annotated atlas for a structural biology lab: warm paper, precise routes, and a central landmark that makes the module family easy to navigate. The coconut mark gives the brand a human and memorable center, while the diagrammatic workbench keeps the page credible for cryo-EM researchers.

The physical scene is a cryo-EM researcher in a bright lab office, glancing between reconstruction runs and a project homepage to understand where to download CoCo and how CryoSieve, CryoPROS, and CTFA relate. That scene requires a light, warm, low-glare interface with calm academic density, not a dark launch screen or a sales-heavy SaaS page.

This system rejects generic research-project template pages, exaggerated SaaS landing-page styling, overly flashy dark tech aesthetics, and dense jargon walls. The interface should be composed, tactile, and navigable, with one clear primary action: Download CoCo.

**Key Characteristics:**

- Warm lab-paper surfaces with subtle green, coconut, slate, and lavender module cues.
- Serif-led headings for academic calm, sans body text for readable instructions.
- Diagrammatic structure over card-grid repetition.
- Ambient tactile depth, never glassy or glossy.
- CoCo is always the hub; module pages extend the system without competing with it.

## 2. Colors

The palette is Lab-warm: tinted paper neutrals carry the page, while coconut brown, growth green, cryo slate, and CTFA lavender identify system roles.

### Primary

- **Growth Green** (`leaf-deep`, `leaf`, `leaf-soft`): The primary action and active module language. Use it for Download CoCo, hover confirmation, CryoPROS, focusable positive actions, and select soft backgrounds.

### Secondary

- **Coconut Brown** (`coconut-deep`, `coconut`, `coconut-soft`): The CoCo identity color and CryoSieve marker. Use it for the hub, brand wordmark accents, numbered relationship steps, citation links, and warm emphasis.

### Tertiary

- **Cryo Slate** (`slate`, `slate-soft`): The cryo-EM technical support color. Use it sparingly in the workflow map and secondary reference interactions.
- **CTFA Lavender** (`ctfa`, `ctfa-soft`): The planned-state color. Use it for CTFA and TBD treatments only, with dashed or muted behavior when the status is unresolved.

### Neutral

- **Paper Cream** (`paper`, `paper-soft`, `paper-warm`): The base surface system. It should look like warmed lab paper, never pure white.
- **Warm Ink** (`ink`, `ink-muted`, `ink-soft`): Text hierarchy. Use full ink for headings, muted ink for explanatory prose, and soft ink for metadata.
- **Pencil Line** (`line`, `line-strong`): Dividers, table rules, card borders, and map strokes. Lines are structural and quiet.

### Named Rules

**The Warm Paper Rule.** Every neutral surface must keep the warm lab-paper tint. Pure black and pure white are forbidden.

**The One CoCo Action Rule.** Growth Green carries primary action energy. Do not let module links, GitHub references, or CTFA states compete with Download CoCo.

**The Module Difference Rule.** CryoSieve uses Coconut Brown, CryoPROS uses Growth Green, CTFA uses CTFA Lavender, and Cryo Slate stays a supporting cryo-EM note.

## 3. Typography

**Display Font:** Charter, with Bitstream Charter, Sitka Text, Cambria, and serif fallbacks.
**Body Font:** Inter, with system sans fallbacks.
**Label/Mono Font:** No mono family. Labels use the body sans in small uppercase.

**Character:** The pairing should feel like a lab note that has been carefully typeset: serif headings for trust and warmth, sans text for practical reading. Inter is already part of the shipped identity here; do not re-litigate the font unless the whole surface is being redesigned.

### Hierarchy

- **Display** (650, fluid display size, 1.12 line-height): Hero titles and module page titles. Keep the measure short and balanced.
- **Headline** (650, fluid headline size, 1.12 line-height): Section headings that explain system relationship and primary action.
- **Title** (720, compact fluid title size, 1.12 line-height): Module names, component headings, and subsection titles.
- **Body** (400, 16px, 1.6 line-height): Explanatory prose and research-facing descriptions. Keep body copy around 65 to 75 characters per line.
- **Label** (850, compact uppercase, tracked): Eyebrows, toolbar labels, table headers, and small status cues. Labels must stay short.

### Named Rules

**The Calm Hierarchy Rule.** Use size, weight, and measure for hierarchy. Do not add decorative type effects, gradient text, or noisy emphasis.

**The No Mono Costume Rule.** Do not use monospace as shorthand for scientific software. This brand is technical through structure, not terminal styling.

## 4. Elevation

Elevation is ambient tactile. Surfaces feel touchable because they cast soft, broad shadows and sit on tinted paper, not because they float behind blur. The workflow card and download panel can have gentle depth; rows, chips, tables, and citations stay mostly border-led.

### Shadow Vocabulary

- **Ambient Surface** (`0 24px 70px oklch(38% 0.058 70 / 0.14)`): Use only for major showcase surfaces like the workbench card.
- **Soft Surface** (`0 12px 38px oklch(38% 0.058 70 / 0.09)`): Use for secondary panels, figures, download surfaces, and embedded media.
- **Primary Button Glow** (`0 14px 28px oklch(35% 0.083 145 / 0.22)`): Use for the primary CTA at rest.
- **Primary Button Hover Glow** (`0 18px 34px oklch(35% 0.083 145 / 0.26)`): Use only on primary CTA hover.
- **Logo Drop** (`drop-shadow(0 8px 16px oklch(36% 0.08 58 / 0.15))`): Use for the coconut logo and footer mark.

### Named Rules

**The Ambient Not Glass Rule.** Depth comes from soft shadow, tonal layering, and warm borders. Decorative blur, frosted panels, and glassmorphism are forbidden.

**The Lift on Intent Rule.** Interactive lift should be slight: one pixel for hover, small scale on active press. Large floating cards are not part of this system.

## 5. Components

### Buttons

- **Shape:** Fully rounded pills (`pill`). Buttons should feel reassuring and practical, not flashy.
- **Primary:** Growth Green background, warm paper text, strong weight, compact horizontal padding. This is for Download CoCo and no other equal-priority action.
- **Hover / Focus:** Hover shifts to brighter Growth Green with a slightly stronger glow. Focus uses the visible green ring from `focus`.
- **Secondary:** Warm paper fill, Pencil Line border, Coconut Brown text. Use for lower-commitment navigation like View modules.
- **Disabled:** Reduce opacity, remove shadow, and prevent pointer interaction. The current Download coming soon state uses this intentionally.

### Chips

- **Style:** Small pill badges with warm paper fill, quiet borders, and semibold text.
- **Module Color:** CryoSieve chips use coconut-tinted fills, CryoPROS chips use leaf-tinted fills, CTFA chips use lavender fills.
- **Use:** Use chips for attributes and status. Do not turn every metadata phrase into a chip.

### Cards / Containers

- **Corner Style:** Generous radii for major surfaces (`xl`), medium radii for tables, citations, and figures (`md`).
- **Background:** Warm paper gradients and OKLCH tints. Major panels may use subtle radial color fields.
- **Shadow Strategy:** Use Ambient Surface for the workbench only, Soft Surface for important panels, and borders for lists.
- **Border:** Quiet Pencil Line borders. No thick side stripes.
- **Internal Padding:** Use fluid padding for major panels and compact fixed padding for rows, tags, and citations.

### Navigation

- **Style:** Sticky top navigation with a warm translucent paper background and a single line divider.
- **Typography:** Semibold sans, compact and readable.
- **Dropdown:** Rounded panel, soft shadow, warm paper fill. Module options are grouped as title plus concise descriptor.
- **States:** Hover uses leaf-soft backgrounds and leaf-deep text. Keyboard focus must remain visible.
- **Mobile:** Navigation wraps instead of collapsing into a modal. Keep touch targets at least 48px on coarse pointers.

### Module Ledger

- **Style:** A ruled ledger, not an identical card grid. Each module row has an index, description, and tags.
- **Interaction:** Rows are clickable with subtle background change and visible focus outline.
- **Status:** CTFA remains muted, lavender, and clearly TBD.

### Workbench Map

- **Style:** The signature component. It combines a GUI-like container, topographic contour lines, connector paths, and three module nodes around the CoCo hub.
- **Behavior:** Module nodes are keyboard accessible and scale slightly on hover.
- **Rule:** The map must explain relationship first. It is not decorative wallpaper.

### Figures and Tables

- **Figures:** Use bordered, softly shadowed images with medium radii. Scientific figures should be legible and not cropped aggressively.
- **Tables:** Use compact rows, uppercase headers, warm paper fill, and horizontal overflow support on small screens.

## 6. Do's and Don'ts

### Do:

- **Do** keep CoCo as the central GUI hub in both copy and layout.
- **Do** use OKLCH tokens from the frontmatter for all new colors.
- **Do** keep the primary CTA singular: Download CoCo should remain the only primary action.
- **Do** use warm paper neutrals for large surfaces and full Warm Ink for headings.
- **Do** use the module color rule consistently: Coconut Brown for CryoSieve, Growth Green for CryoPROS, CTFA Lavender for CTFA.
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
- **Don't** make CryoSieve, CryoPROS, CTFA, GitHub, or references look like equal download actions.
- **Don't** use modals as the first solution for navigation or module detail.
- **Don't** add em dash punctuation in interface copy.
