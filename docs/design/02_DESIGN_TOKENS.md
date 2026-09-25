# WHERIS --- 02 DESIGN TOKENS

> Canonical design-token specification for Wheris.
>
> This document translates `DESIGN/01_DESIGN_FOUNDATIONS.md` into reusable
> visual primitives for Figma and Jetpack Compose. It defines the token
> vocabulary and the approved baseline values for the Android-first MVP.
>
> Token names are semantic wherever possible. Figma and Compose must
> express the same concepts even when their platform syntax differs.
>
> This document does **not** define component anatomy or screen layouts.
> Those belong to later design documents.

------------------------------------------------------------------------

## 1. Token architecture

Wheris uses three conceptual levels:

1.  **Primitive values** --- raw palette, numeric spacing, radii and
    dimensions.
2.  **Semantic tokens** --- purpose-based values such as
    `color/background` or `color/action/primary`.
3.  **Component consumption** --- components consume semantic tokens
    rather than raw values whenever possible.

Do not reference raw palette values directly from screens when a
semantic token exists.

------------------------------------------------------------------------

## 2. Naming convention

Canonical documentation names use slash-separated paths:

`category/property/variant`

Examples:

-   `color/action/primary`
-   `color/text/secondary`
-   `spacing/md`
-   `radius/lg`
-   `icon/size/md`

Figma should use these paths as variable/style names where supported.

Compose should expose idiomatic Wheris names rather than literal slash
strings, for example:

-   `color/action/primary` → `WherisColors.actionPrimary`
-   `spacing/md` → `WherisSpacing.md`
-   `radius/lg` → `WherisShapes.large`
-   `icon/size/md` → `WherisIconSize.medium`

The semantic meaning must remain identical.

------------------------------------------------------------------------

## 3. Token modes

Color tokens require at least:

-   `Light`
-   `Dark`

Non-color tokens such as spacing and standard dimensions are generally
shared between modes unless explicitly specified otherwise.

Figma Variables should use Light/Dark modes rather than duplicated
disconnected styles where practical.

------------------------------------------------------------------------

## 4. Brand primitive palette

Established Wheris brand primitives:

  Token                        Value Purpose
  ---------------------- ----------- -----------------------------
  `palette/orange/500`     `#F97316` Core Wheris orange
  `palette/orange/400`     `#FB923C` Secondary orange
  `palette/orange/300`     `#FDBA74` Light orange
  `palette/cream/50`       `#FFF7ED` Warm light background
  `palette/stone/900`      `#1C1917` Main warm dark text
  `palette/stone/600`      `#57534E` Secondary warm text
  `palette/stone/200`      `#E7E5E4` Light border
  `palette/white`          `#FFFFFF` Light surface
  `palette/green/500`      `#22C55E` Success primitive
  `palette/red/600`        `#DC2626` Error/destructive primitive
  `palette/blue/600`       `#2563EB` Information primitive

These are primitives, not permission to hardcode them throughout
screens.

------------------------------------------------------------------------

## 5. Light semantic colors

Baseline Light mode:

  Semantic token                                Value
  --------------------------------------- -----------
  `color/background`                        `#FFF7ED`
  `color/surface`                           `#FFFFFF`
  `color/surface/subtle`                    `#FFF7ED`
  `color/surface/elevated`                  `#FFFFFF`
  `color/text/primary`                      `#1C1917`
  `color/text/secondary`                    `#57534E`
  `color/text/on-primary`                   `#1C1917`
  `color/border/default`                    `#E7E5E4`
  `color/action/primary`                    `#F97316`
  `color/action/primary-hover-emphasis`     `#FB923C`
  `color/action/primary-soft`               `#FFEDD5`
  `color/status/success`                    `#22C55E`
  `color/status/error`                      `#DC2626`
  `color/status/info`                       `#2563EB`

Disabled, pressed, container and content-state colors should be derived
semantically at component-definition time and verified for contrast
rather than created ad hoc per screen.

For the primary orange `#F97316`, canonical button/content text is the warm dark `#1C1917`, not white. This pairing provides materially stronger text contrast and matches the UX/UI blueprint. White may still be used where a different background pairing has been contrast-validated.

------------------------------------------------------------------------

## 6. Dark semantic colors

Dark mode is intentionally designed, not inverted.

Approved baseline:

  Semantic token                                Value
  --------------------------------------- -----------
  `color/background`                        `#12100F`
  `color/surface`                           `#1C1917`
  `color/surface/subtle`                    `#292524`
  `color/surface/elevated`                  `#292524`
  `color/text/primary`                      `#FAFAF9`
  `color/text/secondary`                    `#D6D3D1`
  `color/text/on-primary`                   `#1C1917`
  `color/border/default`                    `#44403C`
  `color/action/primary`                    `#FB923C`
  `color/action/primary-hover-emphasis`     `#FDBA74`
  `color/action/primary-soft`               `#7C2D12`
  `color/status/success`                    `#4ADE80`
  `color/status/error`                      `#F87171`
  `color/status/info`                       `#60A5FA`

These values preserve Wheris's warm character while improving legibility
on dark surfaces.

Final contrast must be verified in real components, because contrast
depends on foreground/background pairing rather than token values in
isolation.

------------------------------------------------------------------------

## 7. Semantic content colors

Components should distinguish content roles:

-   `color/text/primary` --- primary readable content.
-   `color/text/secondary` --- metadata/supporting text.
-   `color/text/on-primary` --- content placed on primary action
    surfaces.
-   `color/icon/primary` --- normally aligned with primary text.
-   `color/icon/secondary` --- normally aligned with secondary text.
-   `color/icon/on-primary` --- icons on primary surfaces.

Figma may alias icon tokens to text tokens initially. Separate them only
when visual requirements genuinely diverge.

------------------------------------------------------------------------

## 8. Action colors

Primary orange is reserved for meaningful emphasis:

-   main CTA;
-   selected/active emphasis;
-   Wheris brand accents;
-   focused geographic action where appropriate.

Do not use primary orange as decoration on every card.

Secondary actions should generally use neutral surfaces/text or outlined
treatments.

Destructive actions use semantic error/destructive colors rather than
orange.

------------------------------------------------------------------------

## 9. Status colors

Status color families:

-   Success --- saved/completed/healthy positive confirmation.
-   Error --- destructive or actual failure.
-   Info --- neutral informational state.
-   Warning/accuracy caution --- must be introduced as a semantic token
    only when the component specification establishes its required
    contrast and usage.

Normal GPS acquisition is not an error.

No status may rely on color alone.

------------------------------------------------------------------------

## 10. Category colors

Custom categories require user-selectable accent colors.

The category palette must be:

-   curated;
-   finite;
-   distinguishable;
-   suitable for small markers/icons;
-   usable in Light and Dark themes;
-   paired with icon/label identity.

The initial category palette should be defined as named accent choices
rather than arbitrary unrestricted color input.

Recommended semantic keys:

-   `category/orange`
-   `category/amber`
-   `category/yellow`
-   `category/green`
-   `category/teal`
-   `category/blue`
-   `category/indigo`
-   `category/purple`
-   `category/pink`
-   `category/red`
-   `category/brown`
-   `category/slate`

Exact category swatches should be validated visually in the Figma
component checkpoint before being frozen. Category identity must never
depend on these colors alone.

------------------------------------------------------------------------

## 11. Opacity scale

Use opacity sparingly and semantically.

Baseline primitive scale:

  Token                             Value
  ------------------------------ --------
  `opacity/disabled`               `0.38`
  `opacity/secondary-emphasis`     `0.60`
  `opacity/medium-emphasis`        `0.74`
  `opacity/high-emphasis`          `0.87`
  `opacity/full`                   `1.00`

Prefer explicit semantic colors over stacking opacity on complex
backgrounds when contrast becomes unpredictable.

------------------------------------------------------------------------

## 12. Typography foundation

Typography must remain Android-native, legible and
localization-friendly.

Default family:

`Roboto` / Android system sans-serif fallback.

Do not require a downloadable/custom font for the MVP unless a later
explicit brand decision justifies the operational cost.

Compose should map the typography scale into `WherisTypography` /
Material 3 typography roles.

------------------------------------------------------------------------

## 13. Typography scale

Baseline Wheris scale:

  --------------------------------------------------------------------------------------------
  Token                               Size     Line height          Weight Typical role
  ------------------------ --------------- --------------- --------------- -------------------
  `type/display/small`                36sp            44sp             600 Rare
                                                                           hero/onboarding
                                                                           emphasis

  `type/headline/large`               32sp            40sp             600 Major
                                                                           onboarding/title

  `type/headline/medium`              28sp            36sp             600 Strong screen
                                                                           emphasis

  `type/headline/small`               24sp            32sp             600 Screen/section
                                                                           emphasis

  `type/title/large`                  22sp            28sp             600 App bar/screen
                                                                           title

  `type/title/medium`                 16sp            24sp             600 Card/section title

  `type/title/small`                  14sp            20sp             600 Compact title

  `type/body/large`                   16sp            24sp             400 Main body

  `type/body/medium`                  14sp            20sp             400 Standard
                                                                           metadata/body

  `type/body/small`                   12sp            16sp             400 Secondary metadata

  `type/label/large`                  14sp            20sp             600 Buttons/prominent
                                                                           labels

  `type/label/medium`                 12sp            16sp             600 Chips/compact
                                                                           controls

  `type/label/small`                  11sp            16sp             600 Small badges
  --------------------------------------------------------------------------------------------

Use `sp` in Compose and equivalent typographic sizing in Figma.

Do not hardcode text size inside individual screens when an approved
role fits.

------------------------------------------------------------------------

## 14. Typography usage

Place names should generally use title/body emphasis according to
context.

Coordinates, timestamps, altitude and accuracy are metadata and should
usually use body/label roles.

Buttons should use label roles.

Avoid excessive weight variation. Wheris primarily uses Regular (400)
and SemiBold (600), with other weights introduced only when justified.

------------------------------------------------------------------------

## 15. Dynamic type / font scaling

Compose text must use `sp` and remain compatible with user font scaling.

Figma screen specifications should be tested conceptually with
longer/multiline content rather than assuming fixed one-line labels.

Do not reduce font sizes to compensate for a fragile layout.

------------------------------------------------------------------------

## 16. Spacing scale

Wheris uses a 4dp-based spacing system.

  Token               Value
  ---------------- --------
  `spacing/none`      `0dp`
  `spacing/xxs`       `2dp`
  `spacing/xs`        `4dp`
  `spacing/sm`        `8dp`
  `spacing/md`       `12dp`
  `spacing/lg`       `16dp`
  `spacing/xl`       `24dp`
  `spacing/2xl`      `32dp`
  `spacing/3xl`      `40dp`
  `spacing/4xl`      `48dp`
  `spacing/5xl`      `64dp`

Prefer this scale over arbitrary values.

`2dp` is reserved for fine visual adjustments, not general layout
rhythm.

------------------------------------------------------------------------

## 17. Screen spacing

Baseline phone screen horizontal content padding:

`spacing/lg = 16dp`

Spacious/hero contexts may intentionally use:

`spacing/xl = 24dp`

This aligns the canonical tokens with the approved UX/UI blueprint. The exact choice belongs to the screen/component specification and must remain consistent within a flow.

Large vertical section separation generally uses `24–32dp`.

------------------------------------------------------------------------

## 18. Touch targets

Minimum interactive touch target:

`48dp × 48dp`

This applies even when the visible icon is smaller.

Primary controls may be taller where visual prominence/ergonomics
require it.

Do not shrink map controls, favorite icons or category controls below
accessible interaction size merely to fit more content.

------------------------------------------------------------------------

## 19. Control heights

Baseline dimensions:

  Token                    Value
  --------------------- --------
  `control/height/sm`     `40dp`
  `control/height/md`     `48dp`
  `control/height/lg`     `56dp`

Interactive controls below 48dp visual height must still provide an
effective 48dp touch target where required.

Primary Wheris buttons should generally use `56dp` unless a compact
context explicitly calls for another approved variant.

------------------------------------------------------------------------

## 20. Icon sizes

  Token               Value
  ---------------- --------
  `icon/size/xs`     `16dp`
  `icon/size/sm`     `20dp`
  `icon/size/md`     `24dp`
  `icon/size/lg`     `32dp`
  `icon/size/xl`     `40dp`

`24dp` is the standard UI icon size.

Category/marker icons may use specialized dimensions defined by their
components.

------------------------------------------------------------------------

## 21. Corner radius scale

Wheris uses rounded geometry without making every object pill-shaped.

  Token               Value
  --------------- ---------
  `radius/xs`         `4dp`
  `radius/sm`         `8dp`
  `radius/md`        `12dp`
  `radius/lg`        `16dp`
  `radius/xl`        `24dp`
  `radius/full`     `999dp`

Typical intent:

-   small badges/details → `sm`;
-   inputs/small cards → `md`;
-   main cards/dialog-like surfaces → `lg`;
-   prominent sheets/large surfaces → `xl`;
-   chips/circular/pill elements → `full`.

Exact component mapping belongs to `DESIGN/03_DESIGN_COMPONENTS.md`.

------------------------------------------------------------------------

## 22. Border widths

  Token                       Value
  ------------------------- -------
  `border/width/none`         `0dp`
  `border/width/default`      `1dp`
  `border/width/emphasis`     `2dp`

Use `2dp` for meaningful focus/selection emphasis where required, not
ordinary card outlines.

------------------------------------------------------------------------

## 23. Elevation scale

Wheris uses subtle elevation.

  Token                  Value
  -------------------- -------
  `elevation/none`       `0dp`
  `elevation/low`        `1dp`
  `elevation/medium`     `3dp`
  `elevation/high`       `6dp`

Avoid heavy shadows. On dark surfaces, hierarchy may rely more on
surface tone/border than shadow.

Figma shadows should visually correspond to these semantic levels rather
than inventing unrelated per-screen effects.

------------------------------------------------------------------------

## 24. Scrim

Modal/sheet scrims should use a semantic scrim token rather than
arbitrary black opacity.

Baseline:

-   Light: black at approximately `32%`
-   Dark: black at approximately `48%`

Exact implementation may align with Material platform behavior when
using native Material components.

------------------------------------------------------------------------

## 25. Divider

Dividers use `color/border/default` at `1dp` where separation is
actually needed.

Prefer spacing/grouping over excessive divider lines.

------------------------------------------------------------------------

## 26. Button token intent

Primary button baseline:

-   height: `56dp`;
-   horizontal padding: `24dp`;
-   icon: `20–24dp`;
-   radius: typically `16dp`;
-   typography: `type/label/large`;
-   container: `color/action/primary`;
-   content: `color/text/on-primary`.

Exact enabled/pressed/loading/disabled behavior belongs to the component
specification.

------------------------------------------------------------------------

## 27. Text field token intent

Baseline text field:

-   minimum height: `56dp`;
-   radius: `12–16dp` depending on final component anatomy;
-   standard horizontal content padding: `16dp`;
-   label/body typography from approved roles;
-   border/content colors from semantic tokens.

Multiline note fields grow vertically and must not be forced into
single-line dimensions.

------------------------------------------------------------------------

## 28. Chip token intent

Chips should remain compact but touch-accessible.

Typical visual height:

`32–40dp`

Effective touch target should remain appropriate.

Use `radius/full` and `type/label/medium` or `label/large` according to
density.

Selected state must not rely only on color.

------------------------------------------------------------------------

## 29. Card token intent

Typical cards:

-   radius: `16dp`;
-   internal padding: `16dp`;
-   surface: semantic surface;
-   elevation: `low` or none + border;
-   item gap: `8–12dp`.

Large hero/location cards may use `24dp` radius when justified.

------------------------------------------------------------------------

## 30. Bottom sheet tokens

Modal/standard bottom sheets should align with Material 3 behavior while
using Wheris surface/color/shape semantics.

Baseline top corner radius:

`24dp`

Content padding typically begins at `24dp`, adjusted by component
anatomy and system insets.

Quick marker details must remain compact enough not to obscure
unnecessary map area.

------------------------------------------------------------------------

## 31. Dialog tokens

Dialogs should use:

-   elevated surface;
-   `24dp` corner radius baseline;
-   clear title/body hierarchy;
-   `24dp` outer content spacing baseline;
-   visually separated destructive actions when applicable.

Use platform-appropriate width constraints rather than fixed screenshot
widths.

------------------------------------------------------------------------

## 32. Bottom navigation dimensions

Bottom navigation must respect Android system insets.

Conceptual content height before system inset:

approximately `64–80dp`, finalized in the component specification.

Each destination must provide an accessible touch area and icon +
visible label.

Destinations are fixed to `Carte`, `Lieux`, `Plus` for the current
product structure.

------------------------------------------------------------------------

## 33. Floating/add action

The Add Place action must remain highly visible on the map.

If represented as an extended FAB or equivalent:

-   icon: standard `24dp`;
-   minimum touch dimension: `56dp`;
-   prominent semantic primary color;
-   explicit `Ajouter un lieu` label when the chosen state/layout
    supports it.

Exact collapsed/extended behavior belongs to component/screen specs.

------------------------------------------------------------------------

## 34. Map control dimensions

Map overlay controls should use:

-   visible icon around `24dp`;
-   touch container at least `48dp`;
-   high-contrast surface;
-   rounded geometry consistent with Wheris;
-   subtle elevation/border to remain legible over map content.

Do not use transparent icon-only controls that disappear over varying
map imagery.

------------------------------------------------------------------------

## 35. User-location marker

The current-position marker is not a category marker.

It must have a dedicated visual identity and remain distinguishable from
saved places.

Exact visual construction is defined later, but its minimum visual
footprint should remain legible around `20–24dp` with any accuracy halo
treated separately.

------------------------------------------------------------------------

## 36. Saved-place marker sizing

Baseline marker family should support:

-   default;
-   selected;
-   clustered/future density behavior if later required.

Initial visual target:

-   default marker container approximately `40dp`;
-   selected marker approximately `48dp`;
-   internal category icon approximately `20–24dp`.

These are component baselines, not fixed map-engine pixel assumptions.
Final Mapbox rendering must account for density and visual testing.

------------------------------------------------------------------------

## 37. Category selector dimensions

Category selection should use comfortable icon/card/chip targets.

A category grid/card target should not force the label into unreadably
narrow widths.

When many categories exist, scrolling/search/hierarchy should solve
density rather than reducing targets.

------------------------------------------------------------------------

## 38. Accuracy badge tokens

Accuracy feedback needs semantic visual states:

-   excellent;
-   good;
-   medium;
-   poor;
-   unknown.

The badge must include textual/iconographic information, not color
alone.

Exact state colors should be defined in `DESIGN/03_DESIGN_COMPONENTS.md` after
checking contrast against both themes.

Domain accuracy thresholds remain a product/domain rule, not a visual
token.

------------------------------------------------------------------------

## 39. Distance and direction tokens

Distance and cardinal direction are geographic metadata.

Use standard body/title hierarchy and clear iconography.

Do not create a special decorative font or oversized compass treatment
unless a specific screen requires it.

Cardinal direction must be understandable without color.

------------------------------------------------------------------------

## 40. Image/photo geometry

Place photos should use rounded Wheris geometry and crop behavior
appropriate to context.

Baseline:

-   thumbnails: `64–96dp` depending on list/detail context;
-   detail image: flexible width with stable aspect/crop rules;
-   radius: `12–16dp`.

Exact component behavior belongs to the component spec.

------------------------------------------------------------------------

## 41. Illustration sizing

Onboarding/empty-state illustrations should be responsive rather than
fixed.

Typical phone target: approximately `160–240dp` visual width depending
on screen hierarchy.

Illustrations are secondary to copy/action and must shrink gracefully on
short screens.

------------------------------------------------------------------------

## 42. Layout grid

Wheris uses a simple mobile layout grid rather than a desktop-style
column system.

Primary phone rules:

-   edge padding: usually `24dp`;
-   compact edge padding: `16dp`;
-   internal rhythm based on the 4dp spacing scale;
-   full-bleed map where appropriate;
-   overlays respect safe/system insets.

Do not force map screens into the same inset container as content
screens.

------------------------------------------------------------------------

## 43. Insets

System status/navigation/gesture insets are dynamic, not fixed design
tokens.

Figma should represent safe areas clearly.

Compose must use Android window inset APIs rather than hardcoded
status/navigation bar heights.

------------------------------------------------------------------------

## 44. Breakpoint philosophy

Phone is MVP primary.

Do not encode arbitrary responsive breakpoints in the Design System
before a concrete adaptive layout requires them.

Prefer Compose adaptive/window-size concepts when tablet/foldable
layouts are actually introduced.

Figma foundations should nevertheless avoid fixed-size assumptions.

------------------------------------------------------------------------

## 45. Z-order semantics

Conceptual layering:

1.  background/map;
2.  primary content;
3.  cards/map overlays;
4.  bottom navigation/FAB;
5.  bottom sheet;
6.  dialog;
7.  transient system/product feedback where appropriate.

Do not invent arbitrary z-index values in design documentation.
Components/platform implementation own exact layering.

------------------------------------------------------------------------

## 46. Motion duration tokens

Baseline motion:

  Token                          Value Intent
  -------------------------- --------- --------------------------
  `motion/duration/fast`       `100ms` Small feedback
  `motion/duration/short`      `200ms` Selection/state change
  `motion/duration/medium`     `300ms` Sheet/content transition
  `motion/duration/long`       `450ms` Rare larger transition

Avoid longer decorative motion in frequent flows.

Platform Material motion may be used where it produces equivalent
intent.

------------------------------------------------------------------------

## 47. Motion easing

Prefer established Material/Compose easing behavior over inventing
custom curves for every component.

Semantic intent:

-   standard --- normal state transition;
-   emphasized --- larger spatial transition;
-   linear --- only for genuinely continuous/progress behavior.

Exact cubic Bézier values need not be duplicated if the implementation
uses stable Material motion primitives.

------------------------------------------------------------------------

## 48. Reduced motion

Animations must not be required to understand state.

Save confirmation, marker selection and sheets must remain
understandable when motion is reduced or effectively absent.

Do not encode meaning exclusively in animation.

------------------------------------------------------------------------

## 49. Accessibility contrast

All final foreground/background pairings must be contrast-checked.

Token approval alone does not guarantee component-level compliance.

Pay special attention to:

-   orange with white/dark text;
-   secondary text on cream/dark surfaces;
-   category accents;
-   disabled states;
-   text over maps/photos;
-   semantic status containers.

When a brand color fails for small text, change the semantic pairing
rather than forcing the brand value.

------------------------------------------------------------------------

## 50. Map overlay contrast

Map imagery is unpredictable.

Text/buttons placed over a map require an opaque or sufficiently
controlled surface, border/elevation and semantic content color.

Do not place essential text directly over arbitrary map imagery without
a readability container.

------------------------------------------------------------------------

## 51. Focus and selection

Selection should use a combination of semantic color and at least one
additional cue such as border, checkmark, icon treatment, size or label.

Keyboard/focus behavior should remain compatible with Android
accessibility/input systems even though touch is primary.

------------------------------------------------------------------------

## 52. Disabled state

Disabled UI should communicate unavailability without becoming
unreadable.

Do not implement disabled state solely by applying one global opacity to
complex content if that harms contrast.

Component specifications should define container/content treatment
semantically.

------------------------------------------------------------------------

## 53. Error/destructive state

Error and destructive are related but not identical.

Use `color/status/error` as the baseline semantic primitive, while
component specs decide whether the state is:

-   destructive action;
-   field validation;
-   blocking error;
-   informational error container.

Do not paint entire screens red for recoverable problems.

------------------------------------------------------------------------

## 54. Success state

Success green is primarily for confirmation/status, not general positive
decoration.

`Lieu enregistré !` may use success iconography while the main onward
action can remain Wheris orange.

This preserves brand hierarchy.

------------------------------------------------------------------------

## 55. Token ownership

Canonical ownership:

-   raw/semantic values → `DESIGN/02_DESIGN_TOKENS.md`;
-   component-specific mapping → `DESIGN/03_DESIGN_COMPONENTS.md`;
-   screen-specific composition → `DESIGN/07_SCREEN_SPECIFICATIONS.md`.

A screen must not create a new global token silently.

------------------------------------------------------------------------

## 56. Figma variable collections

Recommended Figma organization:

### Collection --- Color

Modes: `Light`, `Dark`

Groups: - `color/background` - `color/surface/*` - `color/text/*` -
`color/icon/*` - `color/border/*` - `color/action/*` - `color/status/*`

### Collection --- Dimensions

-   `spacing/*`
-   `radius/*`
-   `border/width/*`
-   `control/height/*`
-   `icon/size/*`
-   relevant stable component-independent dimensions

### Typography

Use Figma text styles or supported variable strategy matching the
canonical type roles.

### Motion

Document as variables/styles only if the active Figma workflow benefits
from them; do not create unusable token bureaucracy.

------------------------------------------------------------------------

## 57. Compose token mapping

Recommended conceptual mapping:

``` kotlin
WherisTheme
WherisColors
WherisTypography
WherisSpacing
WherisShapes
WherisElevation
WherisBorders
WherisIconSize
WherisMotion
```

The exact Kotlin API may evolve during implementation, but semantic
names must remain recognizable.

Material `ColorScheme` can be used underneath/alongside Wheris semantics
where mappings are clean.

Do not force every Wheris semantic into a Material slot if the meaning
does not match.

------------------------------------------------------------------------

## 58. Raw values in Compose

Raw numeric/color values should primarily live in Design System
implementation.

Feature screens should consume Wheris tokens/components.

One-off values are acceptable only for genuinely local geometry that
does not represent a reusable design decision.

Repeated local magic numbers indicate a missing token/component.

------------------------------------------------------------------------

## 59. Raw values in Figma

Designers/agents should apply variables/styles rather than entering hex
values and recurring spacing/radius values manually.

If a required value is missing, stop and determine whether:

1.  an existing token should be used;
2.  a component owns the value;
3.  a new global token is justified.

Do not silently create near-duplicates such as 15dp, 17dp and 23dp.

------------------------------------------------------------------------

## 60. Token change policy

Changing a foundational token can affect many screens.

Before changing an approved token:

1.  identify the design problem;
2.  inspect affected components;
3.  verify Light/Dark impact;
4.  verify accessibility;
5.  update Figma variable/style;
6.  update Compose token;
7.  review affected screens.

Do not locally override a token just to make one screen match a
screenshot.

------------------------------------------------------------------------

## 61. Category palette freeze checkpoint

The general category-color model is approved here, but exact category
swatches are intentionally **not yet frozen**.

They should be finalized while designing `WherisCategoryIcon`,
`WherisCategoryCard`, `WherisCategoryChip` and map markers, because
those surfaces reveal real contrast/recognition constraints.

Once validated, the swatches should be added back to this token document
as canonical values.

This is intentional checkpoint discipline, not an incomplete
specification.

------------------------------------------------------------------------

## 62. Material 3 relationship

Material 3 provides platform foundations for typography behavior,
controls, accessibility and interaction patterns.

Wheris tokens may map to Material 3 where semantics align.

Wheris must not blindly use default Material colors/shapes when they
conflict with the approved Wheris visual language.

Conversely, do not duplicate stable Material behavior merely to appear
custom.

------------------------------------------------------------------------

## 63. Token anti-patterns

Forbidden patterns include:

-   hardcoded hex values across feature screens;
-   arbitrary spacing outside the scale without reason;
-   separate Figma and Compose token vocabularies;
-   category identity based only on color;
-   simple light-to-dark inversion;
-   shrinking touch targets for density;
-   custom font dependency without product justification;
-   opacity used to hide contrast problems;
-   screen-local copies of global semantic colors;
-   silent token overrides to match isolated screenshots.

------------------------------------------------------------------------

## 64. Token validation checklist

Before freezing this checkpoint, verify:

-   Wheris orange remains visually distinctive.
-   Light theme remains warm, not yellow/orange-heavy.
-   Dark theme remains warm and readable.
-   Primary CTA contrast is acceptable in both themes.
-   Secondary text remains readable.
-   Spacing supports both airy map flows and denser lists.
-   Radius system feels coherent rather than excessively pill-shaped.
-   Touch targets remain Android-accessible.
-   Type hierarchy supports long French/user content.
-   Tokens can be represented in both Figma and Compose.
-   No screen-specific behavior has leaked into global tokens.
-   Category accent palette remains explicitly scheduled for
    component-level validation.

------------------------------------------------------------------------

## 65. Checkpoint output

After this document is validated, the next design checkpoint is:

> **`DESIGN/03_DESIGN_COMPONENTS.md`**

That document will map these tokens into actual reusable Wheris
components and variants, including buttons, cards, chips, inputs,
navigation, feedback, location indicators, category components, place
components and map-related UI.

No screen should invent its own replacement component system.

------------------------------------------------------------------------

## 66. Final token principle

Tokens are not a catalog of numbers.

They are the shared vocabulary that keeps:

> **Wheris specification → Figma → Jetpack Compose**

aligned over time.

A good Wheris token has a clear purpose, a stable semantic name and a
reason to exist.

If a value has no reusable meaning, it probably should not become a
global token.
