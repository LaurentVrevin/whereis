# WHERIS --- 06 FIGMA BUILD RULES

> Operational rules for constructing the canonical Wheris design file in
> Figma.
>
> This document tells a human designer or Figma-capable AI agent **how
> to build Wheris correctly** from the approved product and design
> specifications.
>
> It does not redefine product behavior. When a visual decision
> conflicts with an approved semantic specification, the conflict must
> be surfaced rather than silently resolved.

------------------------------------------------------------------------

## 1. Mission

Build a Figma file that is:

-   visually coherent;
-   token-driven;
-   component-based;
-   reusable;
-   easy to review;
-   prototype-ready;
-   accessible;
-   maintainable;
-   suitable for Android/Jetpack Compose handoff.

Figma must materialize Wheris. It must not independently reinvent
Wheris.

------------------------------------------------------------------------

## 2. Source-of-truth hierarchy

Use these documents together:

``` text
PRODUCT / WHERIS_MASTER.md
BUSINESS / WHERIS_BUSINESS_REFERENCE.md
        ↓
01_DESIGN_FOUNDATIONS.md
        ↓
02_DESIGN_TOKENS.md
        ↓
03_DESIGN_COMPONENTS.md
        ↓
04_SCREEN_CATALOG.md
        ↓
05_USER_FLOWS.md
        ↓
06_FIGMA_BUILD_RULES.md
        ↓
07_SCREEN_SPECIFICATIONS.md
```

Roles:

-   Product specification = product truth.
-   Business Reference = monetization/business truth; it owns plan roles,
    entitlements and the status of pricing/free-limit hypotheses.
-   Design specification = semantic UI truth.
-   Figma = visual truth.
-   Compose = implementation truth constrained by product + design +
    validated Figma.

Figma may refine visual execution but must not silently change
semantics, flows, scope or data behavior.

------------------------------------------------------------------------

## 3. Mandatory checkpoint workflow

Do not ask Figma to "design the entire app" in one uncontrolled pass.

Active MVP construction excludes free-limit/paywall, Wheris Plus purchase,
Wheris Premium subscription and other billing/entitlement screens. Future
monetization mockups, if retained, belong in Archive/Future exploration and
must not be linked into canonical MVP prototypes. `Plus` in the active bottom
navigation remains the secondary-navigation destination and must not be
confused with the commercial offer `Wheris Plus`.

Build in checkpoints:

``` text
Checkpoint A — File structure
Checkpoint B — Variables / tokens
Checkpoint C — Atomic components
Checkpoint D — Domain components
Checkpoint E — Representative screen states
Checkpoint F — Screens by user flow
Checkpoint G — Prototype
Checkpoint H — Light/Dark QA
Checkpoint I — Accessibility / responsive QA
Checkpoint J — Handoff cleanup
```

Do not proceed to the next checkpoint while the previous one contains
systemic inconsistencies.

------------------------------------------------------------------------

## 4. No invention rule

The Figma agent must not invent:

-   new product features;
-   new navigation destinations;
-   new business rules;
-   new monetization rules;
-   new category semantics;
-   new permission requirements;
-   new provider integrations;
-   new colors outside the token system;
-   arbitrary spacing values;
-   arbitrary typography;
-   duplicate components;
-   speculative cloud/account/social functionality.

When required information is missing, flag it as a design decision
instead of fabricating one. For monetization questions, consult
`WHERIS_BUSINESS_REFERENCE.md`; do not copy hypothesis values into Figma
variables/components as if they were permanent design tokens.

------------------------------------------------------------------------

## 5. North Star protection

Every design choice must protect:

> **Aucune évolution de Wheris ne doit rendre plus difficile l'action
> d'enregistrer immédiatement sa position.**

The fast Add Place journey must remain visually obvious and
operationally short.

Optional fields must look optional.

------------------------------------------------------------------------

# FIGMA FILE STRUCTURE

## 6. Recommended pages

Use a stable page structure:

``` text
00 — Cover
01 — Foundations
02 — Variables
03 — Components
04 — Patterns
05 — Screens — Light
06 — Screens — Dark
07 — Flows & Prototype
08 — QA
09 — Archive
```

If Figma tooling represents variables separately, `02 — Variables` may
contain documentation/examples rather than duplicate variable
definitions.

------------------------------------------------------------------------

## 7. Cover page

Include:

-   Wheris name;
-   signature `Ta mémoire géographique.`;
-   document status/version/date if useful;
-   links or references to the canonical design documents;
-   concise legend for Approved / Draft / Deprecated if such status is
    used.

Do not turn the cover into a marketing poster that obscures file
navigation.

------------------------------------------------------------------------

## 8. Foundations page

Use `01 — Foundations` to visually document:

-   product visual direction;
-   typography specimens;
-   spacing rhythm;
-   shapes;
-   elevation examples;
-   icon principles;
-   accessibility examples;
-   map-overlay contrast examples.

This page demonstrates rules; it does not replace Variables or
Components.

------------------------------------------------------------------------

## 9. Variables page

Use `02 — Variables` to document the variable architecture and show
swatches/specimens.

Never manually maintain a disconnected "color palette" that differs from
actual Figma Variables.

------------------------------------------------------------------------

## 10. Components page

Organize by semantic family:

``` text
Buttons
Cards
Chips
Inputs
Navigation
Feedback
Location
Categories
Places
Map
Dialogs
Settings
```

Use component sets where anatomy is shared.

------------------------------------------------------------------------

## 11. Patterns page

Patterns are compositions, not giant generic components.

Examples:

-   marker quick-detail sheet;
-   onboarding page composition;
-   place list composition;
-   category management composition;
-   delete-used-category reassignment composition.

Patterns should be built from canonical component instances.

------------------------------------------------------------------------

## 12. Screen pages

`05 — Screens — Light` contains approved Light frames.

`06 — Screens — Dark` contains Dark equivalents for review.

Do not maintain separate unrelated layouts for themes.

Both must use the same screen IDs.

------------------------------------------------------------------------

## 13. Flow page

`07 — Flows & Prototype` contains:

-   ordered flow lanes;
-   representative state frames;
-   prototype links;
-   branch labels;
-   concise annotations.

Do not duplicate every design frame if an instance/reference can be used
cleanly.

------------------------------------------------------------------------

## 14. QA page

Use `08 — QA` for deliberate stress tests:

-   long names;
-   many categories;
-   missing optional metadata;
-   large font scenarios;
-   Light/Dark comparisons;
-   map contrast;
-   offline/map-unavailable states;
-   selected/unselected states;
-   error/recovery comparisons.

QA frames are not canonical product screens.

------------------------------------------------------------------------

## 15. Archive page

Deprecated explorations go to `09 — Archive`.

Rules:

-   clearly mark as deprecated;
-   remove prototype links from canonical flows;
-   do not use archived components in approved screens;
-   never leave "final v2 copy latest" ambiguity in active pages.

------------------------------------------------------------------------

## 15.1 Product-validation checkpoint

Before expanding the canonical file to every secondary screen, prototype and review the validation slice end to end:

`MAP_001 → ADD_001 → ADD_002 → Enregistrer → ADD_004 → MAP_001/PLACES_001 → PLACE_001 → external navigation`.

The prototype must demonstrate that optional details are genuinely optional: `ADD_003` is reached through `Ajouter des détails`, not as a mandatory continuation after category selection.

Use usability evidence to refine hierarchy and interaction cost, but do not invent new product semantics. The existing target is a straightforward save close to under 10 seconds when location is available.

------------------------------------------------------------------------

# VARIABLES

## 16. Variables are mandatory

Use Figma Variables for reusable design tokens wherever supported.

Do not hardcode routine values directly into individual screens.

At minimum cover:

-   semantic colors;
-   category accent colors;
-   spacing;
-   radii;
-   relevant sizing primitives.

Typography should use shared text styles and/or the most appropriate
supported Figma token mechanism.

------------------------------------------------------------------------

## 17. Variable naming

Names must map cleanly to the token specification.

Examples:

``` text
color/primary
color/on-primary
color/background
color/surface
color/text/primary
color/text/secondary
color/border/default
color/error
color/success

spacing/xs
spacing/sm
spacing/md
spacing/lg

radius/sm
radius/md
radius/lg
```

Do not create:

``` text
orange2
orange-final
card-grey
spacing-home
radius-new
```

------------------------------------------------------------------------

## 18. Modes

Use variable modes for theme semantics where practical:

``` text
Mode: Light
Mode: Dark
```

A component should normally switch theme through variables rather than
through a separate manually recolored component.

------------------------------------------------------------------------

## 19. Primitive vs semantic colors

Prefer semantic consumption.

Components should consume:

``` text
color/action/primary
color/surface/default
color/text/primary
```

rather than arbitrary primitive hex references.

Primitive palette values may exist as foundations, but semantic tokens
own component meaning.

------------------------------------------------------------------------

## 20. Category accents

Use the curated category palette defined in `DESIGN/03_DESIGN_COMPONENTS.md`.

Canonical keys:

``` text
category/orange
category/amber
category/yellow
category/green
category/teal
category/blue
category/indigo
category/purple
category/pink
category/red
category/brown
category/slate
```

Do not introduce an arbitrary full-spectrum color picker for MVP.

------------------------------------------------------------------------

## 21. Category color QA

Before final approval, render all category accents in:

-   category icon;
-   category card;
-   category chip;
-   default marker;
-   selected marker;
-   Light;
-   Dark.

If one fails, fix the shared category token, not isolated screen
instances.

------------------------------------------------------------------------

## 22. Spacing rule

Every routine gap/padding must come from the approved spacing scale.

If a design repeatedly needs a value not present in the scale:

1.  verify it is genuinely needed;
2.  update the token specification;
3.  add the variable;
4.  use it consistently.

Do not solve token gaps with dozens of local exceptions.

------------------------------------------------------------------------

## 23. Shape rule

Use canonical radius variables.

Do not visually "improve" individual cards by giving each a different
corner radius.

Hierarchy should come from semantics, spacing, typography and container
treatment---not random rounding.

------------------------------------------------------------------------

# TYPOGRAPHY

## 24. Text styles

Create canonical text styles corresponding to the approved typography
roles.

Use semantic names, for example:

``` text
Typography/Display
Typography/Headline/Large
Typography/Headline/Medium
Typography/Title/Large
Typography/Title/Medium
Typography/Body/Large
Typography/Body/Medium
Typography/Label/Large
Typography/Label/Medium
```

Exact names should match `DESIGN/02_DESIGN_TOKENS.md`.

------------------------------------------------------------------------

## 25. No local typography invention

Do not manually change font size/weight/line height on a screen merely
to make one mockup "fit".

If a new semantic text role is truly required, update Tokens first.

------------------------------------------------------------------------

## 26. Text expansion

Frames and components must tolerate:

-   French text;
-   future localization expansion;
-   long custom category names;
-   long user place names;
-   font scaling.

Do not solve overflow by shrinking text below approved typography.

------------------------------------------------------------------------

# ICONS

## 27. Icon source

Use the approved icon system/library consistently.

Avoid mixing unrelated visual icon families.

Custom illustrations are not substitutes for functional icons.

------------------------------------------------------------------------

## 28. Icon naming

Use semantic icon names.

Category persistence is based on stable `iconKey` concepts, not
arbitrary pasted SVG identity.

The visual catalog must be finite and selectable.

------------------------------------------------------------------------

## 29. Icon-only controls

Every icon-only interactive control needs an accessible semantic purpose
documented in the screen/component spec.

The visual alone is not sufficient handoff documentation.

------------------------------------------------------------------------

# COMPONENT CONSTRUCTION

## 30. Reuse before creation

Before creating any component:

1.  search the approved component library;
2.  verify whether a variant/property can express the need;
3.  only create a new component if semantics are genuinely different.

Never duplicate an existing component to make a one-screen tweak.

------------------------------------------------------------------------

## 31. Never detach approved instances

Canonical screens use component instances.

Do not detach an instance simply to change:

-   text;
-   icon;
-   selected state;
-   visibility;
-   category color;
-   size;
-   enabled state.

Expose the required component property instead.

------------------------------------------------------------------------

## 32. Auto Layout mandatory

Use Auto Layout for all appropriate components and layouts.

This includes:

-   buttons;
-   cards;
-   chips;
-   inputs;
-   top/bottom bars;
-   sheets;
-   dialogs;
-   lists;
-   settings rows;
-   screen content stacks.

Absolute positioning is reserved for genuinely layered/spatial elements
such as map overlays, marker internals and illustrations.

------------------------------------------------------------------------

## 33. Component properties

Use appropriate Figma properties:

-   text properties;
-   booleans;
-   instance swaps;
-   variants;
-   variable-bound properties.

Properties should model meaningful differences, not implementation
accidents.

------------------------------------------------------------------------

## 34. Variant discipline

Use variants when states share anatomy.

Examples:

``` text
Button/Wheris
  Type=Primary|Secondary|Tertiary|Destructive
  Size=Large|Medium
  State=Enabled|Pressed|Disabled|Loading
```

Avoid combinatorial explosion.

Use optional properties when a layer can simply be shown/hidden.

------------------------------------------------------------------------

## 35. State naming

Use stable state vocabulary:

``` text
Default
Selected
Pressed
Disabled
Loading
Error
Empty
Offline
Searching
Found
PoorAccuracy
MapUnavailable
```

Do not alternate randomly between synonyms such as `Active`, `On`,
`Chosen`, `Picked` for the same semantic state.

------------------------------------------------------------------------

## 36. Component scope

Do not create a generic mega-component for an entire business flow.

For example:

-   `Category/Card` = component.
-   "Delete used category and reassign" = pattern/surface composition.
-   `Place/Marker` = component.
-   "Map screen with all states" = screen.

------------------------------------------------------------------------

# SCREEN CONSTRUCTION

## 37. Stable frame IDs

Every canonical screen frame starts with the ID from
`DESIGN/04_SCREEN_CATALOG.md`.

Examples:

``` text
MAP_001 — Map
ADD_001 — Add Place — Location
ADD_002 — Add Place — Category
ADD_003 — Add Place — Details
PLACE_001 — Place Detail
CAT_002 — New Category
```

------------------------------------------------------------------------

## 38. State frame naming

Use:

``` text
ADD_001 / Theme=Light / State=Searching
ADD_001 / Theme=Light / State=Found
ADD_001 / Theme=Light / State=PoorAccuracy
MAP_001 / Theme=Dark / State=Offline
```

This makes variants searchable and comparable.

------------------------------------------------------------------------

## 39. Screen versus state

Do not create a new canonical screen because visual content changes.

Examples that remain states:

-   GPS searching;
-   position found;
-   poor accuracy;
-   offline map;
-   map unavailable;
-   empty place list;
-   no search result.

Follow `DESIGN/04_SCREEN_CATALOG.md`.

------------------------------------------------------------------------

## 40. Screen composition

Screens should primarily compose approved components.

A screen may contain unique layout structure, but recurring controls
should not be recreated locally.

------------------------------------------------------------------------

## 41. Safe areas and system bars

Respect Android status/navigation/gesture insets.

Do not position critical actions under system UI.

Map screens may visually extend edge-to-edge while controls remain inset
appropriately.

------------------------------------------------------------------------

## 42. Android-first frame baseline

Use an Android-first reference viewport suitable for design review.

Do not assume one exact device dimension is the product.

Canonical screens must be constructed responsively enough to adapt to:

-   compact phones;
-   taller phones;
-   reasonable width variation;
-   larger text.

Do not bake layout logic around a single screenshot size.

------------------------------------------------------------------------

## 43. Vertical overflow

If content can exceed available height, explicitly define scrolling
behavior.

Examples:

-   category selector;
-   place details;
-   category management;
-   settings.

Do not simply crop content in the canonical frame.

------------------------------------------------------------------------

## 44. Keyboard states

For important text-entry screens, verify at least one representative
keyboard-visible layout.

Examples:

-   `ADD_003`;
-   `CAT_002`;
-   `CAT_003`;
-   search in `PLACES_001`.

Primary actions must not become accidentally unreachable.

------------------------------------------------------------------------

# MAP RULES

## 45. Map is contextual, not canonical data

The map visually enriches geographic information.

It must not be the only way to understand a saved place.

Design fallback states for map failure.

------------------------------------------------------------------------

## 46. Real map context in save flow

When proposing to save current position, show the point on a real
map-style context when map rendering is available.

Do not replace the entire save confirmation with an abstract
illustration.

------------------------------------------------------------------------

## 47. Map provider neutrality

Figma should represent Wheris map behavior, not expose Mapbox-specific
UI unless required by attribution/provider constraints.

Do not design:

-   Mapbox Navigation;
-   route lines as an internal Wheris routing feature;
-   provider search UI;
-   Directions controls.

------------------------------------------------------------------------

## 48. Attribution

Reserve compliant attribution placement where required by the map
provider/data license.

Do not cover or remove required attribution in final
implementation-oriented designs.

------------------------------------------------------------------------

## 49. Map controls

Only include approved controls.

Typical MVP control:

-   recenter/current position.

Do not add compass, layers, 3D, route, terrain or search controls merely
because map apps often have them.

------------------------------------------------------------------------

## 50. Marker rules

Saved-place markers:

-   use category icon/accent;
-   remain recognizable on varied map backgrounds;
-   show obvious selected state.

Current-user location must not look like a saved marker.

------------------------------------------------------------------------

## 51. Map-overlay contrast

Test controls, markers and sheets over:

-   light map area;
-   dark map area;
-   dense labels;
-   green/blue terrain;
-   road-heavy area.

A component that only works over one screenshot is not approved.

------------------------------------------------------------------------

# ADD PLACE RULES

## 52. Add action prominence

`Ajouter un lieu` is the dominant action on the main map.

Do not let secondary map controls visually compete with it.

------------------------------------------------------------------------

## 53. Position acquisition

`ADD_001` keeps map context visible when available.

Searching should use calm contextual progress rather than replacing the
whole screen with a spinner.

------------------------------------------------------------------------

## 54. Accuracy

Accuracy must be understandable through:

-   text;
-   value when available;
-   semantic badge/treatment.

Do not communicate accuracy by color alone.

Poor accuracy is caution, not destructive error.

------------------------------------------------------------------------

## 55. Poor accuracy decision

Both actions must be visually understandable:

-   `Attendre une meilleure position`;
-   `Continuer quand même`.

The design must not make continuation look forbidden.

------------------------------------------------------------------------

## 56. Optional details

`ADD_003` must make it visually obvious that:

-   name is optional;
-   note is optional;
-   photo is optional;
-   favorite is optional.

`Enregistrer` must remain easy to find without completing these fields.

------------------------------------------------------------------------

## 57. Save success

`ADD_004` is intentionally brief.

Do not turn success into a large marketing interruption.

------------------------------------------------------------------------

# CATEGORY RULES

## 58. Dynamic category count

Never design category layouts assuming exactly 6, 8 or 12 categories.

Custom categories can grow substantially.

Use scrolling/adaptive layout.

------------------------------------------------------------------------

## 59. System and custom categories

Both use the same fundamental visual identity model:

-   name;
-   icon;
-   accent.

Management may add `Par défaut` for system categories.

Do not create two incompatible visual systems.

------------------------------------------------------------------------

## 60. Create-category action

`Créer une catégorie` is an action card/control, not a fake persisted
category.

It must remain discoverable in `ADD_002` and `CAT_001`.

------------------------------------------------------------------------

## 61. Live preview

`CAT_002` and `CAT_003` show a preview that uses the actual category
visual language.

Do not create a decorative preview that cannot map to real components.

------------------------------------------------------------------------

## 62. Category deletion

Design deletion consequences explicitly.

If a category is used:

-   explain that places use it;
-   allow reassignment;
-   preserve places.

Never show wording implying that deleting the category deletes the
places.

------------------------------------------------------------------------

# PLACE RULES

## 63. Place identity

A place must remain visually identifiable even when optional data is
absent.

Do not design cards/details that collapse because:

-   name is absent;
-   photo is absent;
-   note is absent;
-   altitude is absent;
-   distance is unavailable.

------------------------------------------------------------------------

## 64. Quick detail

Marker quick detail prioritizes:

1.  place identity;
2.  distance if available;
3.  `Naviguer`;
4.  `Détails`.

Do not copy the full detail screen into the sheet.

------------------------------------------------------------------------

## 65. Full detail

`PLACE_001` supports rich metadata but should retain hierarchy.

Primary action:

`Lancer la navigation`

Secondary/destructive actions remain clearly differentiated.

------------------------------------------------------------------------

# OFFLINE / FAILURE RULES

## 66. Offline is not generic error

Use offline-specific language only when connectivity is actually
relevant.

GPS may work offline.

Local Room data works offline.

------------------------------------------------------------------------

## 67. Map unavailable

When map is unavailable, preserve useful local content.

Do not show a giant error panel that pushes
coordinates/distance/note/photo out of reach.

------------------------------------------------------------------------

## 68. Error recovery

Every recoverable error design must show the recovery action.

Do not design dead-end error screens unless the user genuinely cannot
proceed.

------------------------------------------------------------------------

## 69. Preserve draft visually

Recovery mockups must reflect real preservation requirements.

Example:

-   user typed a note;
-   photo selection fails;
-   returned `ADD_003` still contains the note.

Do not reset frames simply because it is easier to prototype.

------------------------------------------------------------------------

# LIGHT / DARK

## 70. Dark theme is designed, not inverted

Dark theme must preserve:

-   hierarchy;
-   contrast;
-   warmth;
-   category recognition;
-   map-overlay legibility;
-   elevation/surface separation.

Do not mechanically invert Light colors.

------------------------------------------------------------------------

## 71. Theme parity

Every core screen/state must be viable in both themes.

At minimum visually QA:

-   main map;
-   Add Place found;
-   poor accuracy;
-   category selection;
-   place list;
-   place detail;
-   category creation;
-   offline/map unavailable;
-   dialogs/sheets.

------------------------------------------------------------------------

# ACCESSIBILITY

## 72. Touch targets

Interactive targets should meet the Wheris/Android minimum target
defined in Tokens, normally 48dp.

Visual icons may be smaller inside a larger hit area.

------------------------------------------------------------------------

## 73. Contrast

Text and meaningful UI must meet the approved accessibility contrast
targets.

Category accent colors are not automatically valid text colors.

------------------------------------------------------------------------

## 74. Color independence

Do not encode:

-   category identity;
-   selection;
-   GPS quality;
-   errors;
-   favorites

using color alone.

Use icon, shape, label, border, fill or state changes as appropriate.

------------------------------------------------------------------------

## 75. Text scaling

QA representative screens with substantially larger text.

Layouts should reflow/scroll rather than clip essential content.

------------------------------------------------------------------------

## 76. Screen-reader handoff notes

Figma cannot fully implement Android semantics, but important semantic
expectations should be annotated where visual structure is insufficient.

Examples:

-   favorite selected state;
-   map recenter label;
-   selected category;
-   accuracy meaning;
-   destructive action.

------------------------------------------------------------------------

# CONTENT RULES

## 77. User-facing terminology

Use:

-   `lieu`;
-   `lieux`;
-   `Ajouter un lieu`;
-   `Mes lieux`;
-   `Type de lieu`;
-   `Lieu enregistré`.

Do not expose `pin`/`épingle` in user-facing French copy.

Technical documentation/code may retain `Pin`.

------------------------------------------------------------------------

## 78. French-first

Canonical product mockups are French-first.

Do not mix English placeholder UI into approved French screens.

Stable Figma layer/component names may remain technical/English where
useful.

------------------------------------------------------------------------

## 79. Realistic content

Use representative Wheris content:

-   `Ma voiture`;
-   `Tente festival`;
-   `Restaurant Le Panorama`;
-   `Spot coucher de soleil`;
-   `Bivouac du lac`.

Custom categories can include:

-   `Champignons`;
-   `Balades du chien`;
-   `Drone`;
-   `Couchers de soleil`.

Avoid meaningless `Lorem ipsum` in final review screens.

------------------------------------------------------------------------

## 80. Coordinates and privacy

Use clearly fictional/demo coordinates in design examples when exact
coordinates are displayed.

Do not paste real private user locations into canonical Figma mockups.

------------------------------------------------------------------------

# PROTOTYPING

## 81. Prototype priority

Prototype the behavior defined in `DESIGN/05_USER_FLOWS.md`, not decorative
transitions.

Mandatory primary prototypes:

-   first launch;
-   fast Add Place;
-   enriched Add Place;
-   create category during Add;
-   retrieve from map;
-   retrieve from Lieux;
-   external navigation handoff;
-   edit/delete place;
-   category delete/reassign.

------------------------------------------------------------------------

## 82. Prototype branches

Representative recovery branches include:

-   permission denied;
-   GPS poor accuracy;
-   GPS disabled;
-   map unavailable;
-   save failure;
-   no compatible navigation app.

------------------------------------------------------------------------

## 83. Prototype naming

Name interactions/flow starting points clearly.

Examples:

``` text
FLOW-03 — Fast Add
FLOW-08 — Create Category During Add
FLOW-10 — Retrieve From Map
FLOW-18 — Delete & Reassign Category
```

------------------------------------------------------------------------

## 84. Smart Animate restraint

Use motion only when it clarifies continuity.

Do not use elaborate transitions that Android implementation is unlikely
to reproduce or that slow the task.

------------------------------------------------------------------------

## 85. Draft continuity in prototype

When a flow detours and returns, prototype frames should preserve
visible input.

This is mandatory for category creation during Add.

------------------------------------------------------------------------

# RESPONSIVE / ADAPTIVE

## 86. Compact-first

Optimize first for ordinary Android phone widths.

Then verify adaptation rather than assuming the reference viewport is
universal.

------------------------------------------------------------------------

## 87. Width behavior

Prefer:

-   fill-container controls where appropriate;
-   constrained readable text blocks;
-   adaptive grids/lists;
-   flexible cards.

Avoid fixed-width elements that break on narrower devices.

------------------------------------------------------------------------

## 88. Height behavior

Critical actions should remain reachable on shorter screens.

Scrollable content must have explicit scrolling ownership.

Bottom actions must account for navigation/gesture insets.

------------------------------------------------------------------------

## 89. Larger screens

MVP is phone-first.

Do not invent tablet-specific product architecture yet.

Components should nevertheless avoid needless assumptions that make
future adaptation impossible.

------------------------------------------------------------------------

# HANDOFF TO ANDROID

## 90. Figma is not generated Compose

Android implementation should reproduce semantics and validated visuals,
not blindly translate Figma layers one-to-one.

Figma layer structure should still be clean enough to make
implementation intent obvious.

------------------------------------------------------------------------

## 91. Naming parity

Where possible:

``` text
Figma                         Compose
Button / Wheris               WherisButton
Category / Card               WherisCategoryCard
Place / Marker                WherisPinMarker
Location / AccuracyBadge      WherisAccuracyBadge
Navigation / BottomBar        WherisBottomBar
```

This reduces interpretation during implementation.

------------------------------------------------------------------------

## 92. Token parity

Document mapping such as:

``` text
Figma variable          Compose
color/primary           WherisColors.primary
spacing/md              WherisSpacing.md
radius/lg               WherisShapes.large
```

Do not force Compose to reverse-engineer arbitrary pixel values from
screenshots.

------------------------------------------------------------------------

## 93. Screen ID parity

Use canonical IDs in Android planning/issues/docs.

Example:

``` text
ADD_001
```

means the same Add Place location responsibility in:

-   Product discussion;
-   Design docs;
-   Figma;
-   Android implementation.

Technical route names may differ, but mapping must remain explicit.

------------------------------------------------------------------------

## 94. Android notes

`DESIGN/07_SCREEN_SPECIFICATIONS.md` should include an `ANDROID NOTES` section
for implementation-sensitive screens.

Figma annotations may reference those notes rather than duplicating
architecture rules.

Examples:

-   permission handled at Route/system boundary;
-   map is provider-neutral UI;
-   external navigation is an effect;
-   no-map fallback must remain functional.

------------------------------------------------------------------------

# AGENT-SPECIFIC RULES

## 95. When an AI agent builds in Figma

The agent must first inspect:

-   available variables;
-   existing components;
-   current page;
-   canonical screen ID;
-   relevant screen specification;
-   relevant user flow.

It must not begin by freely generating a new visual language.

------------------------------------------------------------------------

## 96. Agent batch size

Prefer small, reviewable batches.

Good:

``` text
Build tokens
→ review

Build Button + Card + Chip families
→ review

Build category components
→ review

Build FLOW-03 representative frames
→ review
```

Bad:

``` text
Build all 30+ screens and all states in one pass
```

------------------------------------------------------------------------

## 97. Agent change discipline

When asked to modify a validated component:

1.  modify the master;
2.  inspect affected instances;
3.  preserve intended variants;
4.  review representative screens;
5.  do not patch individual instances to hide a systemic issue.

------------------------------------------------------------------------

## 98. Agent ambiguity rule

If a request conflicts with:

-   product scope;
-   tokens;
-   components;
-   screen catalog;
-   flows;

the agent should surface the conflict.

Do not "make it work" by silently changing the source of truth.

------------------------------------------------------------------------

## 99. Agent destructive-action rule

Never mass-delete or replace validated frames/components merely to
regenerate them.

Preserve approved work.

Archive deprecated explorations when needed.

------------------------------------------------------------------------

# QUALITY GATES

## 100. Gate A --- File structure

Pass when:

-   pages use canonical names;
-   archive is separated;
-   no uncontrolled duplicate pages;
-   cover/reference navigation is understandable.

------------------------------------------------------------------------

## 101. Gate B --- Variables

Pass when:

-   Light/Dark semantic variables exist;
-   approved category accents exist;
-   spacing/radius values are variable-driven;
-   no systemic rogue values are visible.

------------------------------------------------------------------------

## 102. Gate C --- Components

Pass when:

-   core components from `DESIGN/03_DESIGN_COMPONENTS.md` exist as reusable
    masters/sets;
-   instances can express expected states;
-   no major screen requires detaching components.

------------------------------------------------------------------------

## 103. Gate D --- Representative screens

Before building every state, validate at least:

``` text
MAP_001 — populated
ADD_001 — found
ADD_002 — category selected + Enregistrer primary + Ajouter des détails secondary
ADD_004 — saved success
PLACES_001 — populated
PLACE_001 — detail
STATE_MAP_002 — map unavailable / local data preserved
```

This first representative set must prove the `save → retrieve` loop before secondary surface completeness. `ADD_003` and `CAT_002` are the next representative states once the fast path hierarchy is validated.

------------------------------------------------------------------------

## 104. Gate E --- Flow completeness

Pass when primary and critical recovery flows from `DESIGN/05_USER_FLOWS.md`
can be followed without dead ends.

------------------------------------------------------------------------

## 105. Gate F --- Theme

Pass when representative screens work in both Light/Dark without manual
one-off recoloring.

------------------------------------------------------------------------

## 106. Gate G --- Accessibility

Pass when:

-   contrast is acceptable;
-   touch targets are appropriate;
-   selection is not color-only;
-   long text does not break core screens;
-   large text is viable;
-   semantic handoff notes exist where needed.

------------------------------------------------------------------------

## 107. Gate H --- Handoff

Pass when an Android engineer can answer without guessing:

-   which component to use;
-   which token applies;
-   what state is represented;
-   what screen ID is involved;
-   what action transitions where;
-   what happens offline/error;
-   what is optional;
-   what is destructive.

------------------------------------------------------------------------

# DO / DON'T

## 108. Do

-   build from variables;
-   reuse components;
-   use Auto Layout;
-   preserve stable screen IDs;
-   design by flow;
-   show real map context;
-   preserve local/offline usability;
-   test long custom content;
-   test Light/Dark;
-   keep `Ajouter un lieu` obvious;
-   annotate implementation-sensitive behavior;
-   validate before scaling.

------------------------------------------------------------------------

## 109. Don't

-   invent features;
-   hardcode rogue colors;
-   use arbitrary spacing;
-   detach component instances;
-   duplicate masters per screen;
-   create a route for every state;
-   hide poor accuracy;
-   block valid saving because map tiles failed;
-   make optional fields look mandatory;
-   assume fixed categories;
-   cascade-delete places visually;
-   design internal turn-by-turn navigation;
-   design current MVP paywalls, Wheris Plus purchase or Wheris Premium
    subscription screens;
-   use exact private user coordinates in mockups.

------------------------------------------------------------------------

# BUILD ORDER

## 110. Phase 1 --- Foundations

Build:

-   page structure;
-   semantic variables;
-   Light/Dark modes;
-   typography styles;
-   spacing/radius documentation;
-   icon baseline.

Validate before components.

------------------------------------------------------------------------

## 111. Phase 2 --- Atomic components

Build:

-   buttons;
-   base cards;
-   chips;
-   text fields;
-   search;
-   top bar;
-   bottom bar;
-   FAB;
-   badges;
-   loading.

Validate variants and accessibility.

------------------------------------------------------------------------

## 112. Phase 3 --- Wheris domain components

Build:

-   category icon/card/selector/preview;
-   place card/summary/marker;
-   accuracy badge;
-   distance/direction indicators;
-   location status;
-   map control;
-   empty/error/offline states;
-   confirmation dialog.

Validate all category accents and map contrast here.

------------------------------------------------------------------------

## 113. Phase 4 --- Primary Add flow

Build representative frames:

``` text
MAP_001
ADD_001 Searching
ADD_001 Found
ADD_001 PoorAccuracy
ADD_002
ADD_003
ADD_004
CAT_002
```

Prototype category creation detour.

This is the highest-priority visual checkpoint.

------------------------------------------------------------------------

## 114. Phase 5 --- Retrieval

Build:

``` text
MAP_001 populated
SURF_MAP_001
PLACES_001
PLACE_001
PLACE_002
SURF_PLACE_001
```

Prototype map retrieval and list retrieval.

------------------------------------------------------------------------

## 115. Phase 6 --- Categories

Build:

``` text
CAT_001
CAT_002
CAT_003
SURF_CAT_001
SURF_CAT_002
```

Stress test many categories and long names.

------------------------------------------------------------------------

## 116. Phase 7 --- Onboarding / secondary

Build:

``` text
LAUNCH_001
ONB_001
ONB_002
ONB_003
PERM_001
PERM_002
PLUS_001
SETTINGS_001
SETTINGS_002
SETTINGS_003
SETTINGS_004
PRIVACY_001
ABOUT_001
```

Do not let onboarding consume more design complexity than the core
product.

------------------------------------------------------------------------

## 117. Phase 8 --- Degraded states

Build representative:

-   offline;
-   map unavailable;
-   GPS disabled;
-   GPS timeout/error;
-   no search result if search is included in the validated MVP scope;
-   no compatible navigation app;
-   persistence failure.

Use existing components/patterns.

------------------------------------------------------------------------

## 118. Phase 9 --- Dark theme

Apply theme modes and review every canonical screen family.

Fix systemic token/component issues centrally.

------------------------------------------------------------------------

## 119. Phase 10 --- Final prototype / QA

Connect flows.

Review:

-   dead ends;
-   back paths;
-   draft preservation;
-   destructive consequences;
-   offline continuity;
-   content overflow;
-   accessibility;
-   naming;
-   unused/deprecated components.

Then prepare handoff.

------------------------------------------------------------------------

# COMPLETION

## 120. Definition of done

The Figma build is ready for Android handoff when:

-   semantic specifications and visuals agree;
-   canonical screen IDs are used;
-   tokens are variable-driven;
-   core UI is componentized;
-   no routine screen relies on detached instances;
-   primary flows are prototyped;
-   recovery states are represented;
-   custom categories scale;
-   map failure has useful fallback;
-   Light/Dark are intentional;
-   accessibility has been reviewed;
-   archive is cleanly separated;
-   Android implementation can map components/tokens/states without
    reverse-engineering screenshots.

------------------------------------------------------------------------

## 121. Final rule

> **Figma matérialise Wheris ; il ne l'invente pas.**

A successful Wheris Figma file is not merely attractive.

It is a structured, inspectable and reusable visual implementation of
the product specification---precise enough that the Android
implementation can reproduce the same system without guessing.
