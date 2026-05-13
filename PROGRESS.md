# Progress

## Current goal

Build a production-ready Web homepage for **CoCo Project**.

The homepage is a brand surface for structural biology laboratories. It should explain that **CoCo is the unified GUI interface** and that **CryoSieve, CryoPROS, and CTFA are modules wrapped inside CoCo**.

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

Generated probe artifacts:

- `.impeccable-artifacts/probe-1-academic-workbench.png`
- `.impeccable-artifacts/probe-2-specimen-atlas.png`
- `.impeccable-artifacts/probe-3-gui-lab-console.png`
- `.impeccable-artifacts/shape-probes.json`

## Coconut / CoConut brand element

The CoCo theme should retain a recognizable **Coconut / CoConut** identity.

Known asset:

- `coco_logo.png`

Notes:

- Current logo uses a coconut-like shape with green leaves.
- Use coconut identity cues subtly.
- Do not turn the page into a tropical, fruit, cartoon, or travel-style visual.
- Combine the coconut mark with the Probe 1 academic workbench direction.
- Good cues: coconut-shell curves, leaf green accents, warm paper tones, central hub mark.

## Module content gathered

CryoSieve:

- Source checked from `mxhulab/cryosieve` GitHub README.
- CryoSieve is for particle sorting / sieving in single-particle cryo-EM analysis.
- It removes unnecessary particles from final stacks and improves downstream reconstruction quality.
- Latest release observed: `v1.3.0`.

CryoPROS:

- Source checked from `mxhulab/cryopros` GitHub README.
- CryoPROS addresses misalignment errors caused by preferred orientation in single-particle cryo-EM.
- It uses AI-generated auxiliary particles and co-refinement of synthesized and experimental data.
- Latest release observed: `v1.0.1`.

CTFA:

- Placeholder / TBD for now.

## Files created or changed

Created:

- `PRODUCT.md`: product and brand context for Impeccable workflow.
- `index.html`: homepage structure and content.
- `styles.css`: homepage styling.
- `PROGRESS.md`: this progress summary.

Existing assets used:

- `coco_logo.png`

Generated artifacts:

- `.impeccable-artifacts/probe-1-academic-workbench.png`
- `.impeccable-artifacts/probe-2-specimen-atlas.png`
- `.impeccable-artifacts/probe-3-gui-lab-console.png`
- `.impeccable-artifacts/homepage-mobile.png`
- `.impeccable-artifacts/homepage-tablet.png`
- `.impeccable-artifacts/homepage-desktop.png`
- `.impeccable-artifacts/inspect-homepage.mjs`

## Implementation status

Homepage implementation has started and appears to include:

- Static `index.html` homepage.
- `styles.css` styling.
- Responsive inspection artifacts for mobile, tablet, and desktop.
- CoCo as the central product and only primary download CTA.
- Module relationship framing for CryoSieve, CryoPROS, and CTFA.

## Important constraints for continuing

- Follow `/impeccable` design rules.
- Use OKLCH colors, not raw black / white defaults.
- No gradient text.
- No decorative glassmorphism by default.
- No side-stripe accent borders.
- No repeated generic identical card grid as the main design idea.
- No em dashes in UI copy.
- Keep copy concise and academic.
- Keep the homepage focused on clarity, module relationships, and CoCo download confidence.

## Suggested next step

Inspect the current `index.html` and `styles.css`, compare against the preferred Probe 1 direction and the generated viewport screenshots, then polish any remaining visual, responsive, or content issues.
