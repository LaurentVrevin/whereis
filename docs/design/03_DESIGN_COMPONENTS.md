# WHERIS --- 03 DESIGN COMPONENTS

> Canonical component specification for the Wheris Android-first design
> system.
>
> This document translates `docs/design/01_DESIGN_FOUNDATIONS.md` and
> `docs/design/02_DESIGN_TOKENS.md` into reusable UI components and
> patterns shared by Figma and Jetpack Compose.
>
> It defines component responsibility, anatomy, variants, states,
> behavior, accessibility and naming. It does **not** define complete
> screen composition; that belongs to
> `docs/design/07_SCREEN_SPECIFICATIONS.md`.
>
> When a future monetization component is in scope, commercial semantics
> come from `docs/reference/WHERIS_BUSINESS_REFERENCE.md`; this
> component specification must not invent or own prices, free limits or
> entitlements.

------------------------------------------------------------------------

## 1. Component philosophy

A Wheris component exists when a visual/interaction pattern is reusable,
carries a stable semantic role, or must remain consistent across several
screens.

Do not componentize every layout fragment. Do not duplicate a reusable
component locally merely because one screen needs a small variation.

Components should be: - understandable; - stateless where practical; -
token-driven; - accessible; - theme-aware; - adaptable to user-generated
content; - suitable for Figma instances and Compose APIs.

------------------------------------------------------------------------

## 2. Source hierarchy

Components consume the foundations and tokens. Monetization-aware
components additionally consume approved commercial semantics from
`docs/reference/WHERIS_BUSINESS_REFERENCE.md` without owning those
business rules.

Canonical relationship:

`Product/Business → Foundations → Tokens → Components → Screens → Flows`

A component must not redefine global color, typography, spacing or shape
rules locally.

If a missing reusable value is discovered, update
`docs/design/02_DESIGN_TOKENS.md` intentionally.

------------------------------------------------------------------------

## 3. Figma ↔ Compose naming

Use recognizable cross-platform names.

  Figma component              Compose target
  ---------------------------- ------------------------------
  `Button / Wheris`            `WherisButton`
  `Card / Base`                `WherisCard`
  `Card / Place`               `WherisPinCard`
  `Chip / Category`            `WherisCategoryChip`
  `Chip / Filter`              `WherisFilterChip`
  `Input / TextField`          `WherisTextField`
  `Input / Search`             `WherisSearchField`
  `Navigation / TopBar`        `WherisTopBar`
  `Navigation / BottomBar`     `WherisBottomBar`
  `Navigation / AddAction`     `WherisFloatingActionButton`
  `Feedback / Loading`         `WherisLoadingIndicator`
  `Feedback / Empty`           `WherisEmptyState`
  `Feedback / Error`           `WherisErrorState`
  `Feedback / Offline`         `WherisOfflineState`
  `Location / AccuracyBadge`   `WherisAccuracyBadge`
  `Location / Distance`        `WherisDistanceIndicator`
  `Location / Direction`       `WherisDirectionIndicator`
  `Category / Icon`            `WherisCategoryIcon`
  `Category / Card`            `WherisCategoryCard`
  `Category / Selector`        `WherisCategorySelector`
  `Place / Marker`             `WherisPinMarker`
  `Place / Summary`            `WherisPinSummary`

Internal code may retain `Pin`; user-facing copy uses `lieu`.

------------------------------------------------------------------------

## 4. Shared component rules

Every reusable interactive component should: - accept a Compose
`Modifier`; - expose semantic callbacks rather than navigation/business
objects; - use Wheris tokens; - support relevant
enabled/disabled/loading/selected states; - avoid embedded business
strings; - expose accessibility semantics; - support Light and Dark; -
tolerate text expansion; - preserve a minimum 48dp touch target.

Figma components should use Auto Layout and meaningful
variants/properties.

------------------------------------------------------------------------

# BUTTONS

## 5. WherisButton

Primary reusable button family.

### Anatomy

-   container;
-   optional leading icon;
-   label;
-   optional trailing icon;
-   optional loading indicator.

### Variants

-   `Primary`
-   `Secondary`
-   `Tertiary`
-   `Destructive`

### Sizes

-   `Large` --- default important CTA, 56dp baseline.
-   `Medium` --- standard 48dp control.
-   `Compact` --- only where justified; effective touch target remains
    accessible.

### States

-   enabled;
-   pressed;
-   focused where applicable;
-   disabled;
-   loading.

Loading must prevent accidental duplicate submission.

------------------------------------------------------------------------

## 6. Primary button

Use for the single strongest action in a context, for example: -
`Ajouter un lieu`; - `Enregistrer`; - `Créer`; - `Lancer la navigation`.

Uses `color/action/primary`.

Do not place multiple equally prominent primary buttons in the same
decision area unless the flow genuinely has equivalent actions.

------------------------------------------------------------------------

## 7. Secondary button

Use for important alternatives that should not compete with the primary
action.

Examples: - `Voir sur la carte`; - `Attendre une meilleure position`; -
contextual management actions.

Prefer neutral/outlined/container treatment defined by semantic tokens.

------------------------------------------------------------------------

## 8. Tertiary button

Use for low-emphasis actions such as: - `Annuler`; - lightweight
contextual actions; - text-style actions.

Do not use destructive red for cancellation.

------------------------------------------------------------------------

## 9. Destructive button

Use only for destructive operations such as confirmed deletion.

It must use error/destructive semantics and remain visually separated
from primary positive actions.

A destructive button is not itself a replacement for consequence-aware
confirmation.

------------------------------------------------------------------------

# CARDS

## 10. WherisCard

Generic Wheris surface container.

### Anatomy

-   surface;
-   optional border/elevation;
-   content slot.

### Variants

-   `Default`
-   `Outlined`
-   `Elevated`
-   `Interactive`

Default radius target: `radius/lg`.

The generic card contains no business logic.

------------------------------------------------------------------------

## 11. WherisPinCard

Reusable saved-place list card.

### Anatomy

-   category icon;
-   place name;
-   category label where useful;
-   supporting metadata;
-   optional distance;
-   optional favorite indicator;
-   optional thumbnail;
-   optional trailing affordance.

### States

-   default;
-   pressed/interactive;
-   favorite;
-   unavailable metadata gracefully omitted.

Long place names may wrap or truncate according to list density, but
full content remains available in detail.

Do not display raw coordinates by default in a dense place list unless
the screen specification explicitly requires them.

------------------------------------------------------------------------

## 12. WherisPinSummary

Compact place identity block used in quick detail/detail contexts.

May contain: - category icon; - name; - category; - distance; -
direction; - favorite.

It should make a place recognizable immediately without looking like a
database row.

------------------------------------------------------------------------

# CHIPS

## 13. WherisChip

Base chip primitive.

### Anatomy

-   container;
-   optional icon;
-   label;
-   optional trailing indicator.

### States

-   default;
-   selected;
-   disabled;
-   pressed.

Selected state must use more than color alone.

------------------------------------------------------------------------

## 14. WherisCategoryChip

Represents a category in compact contexts.

Contains: - category icon; - category label; - category accent
treatment.

Used in filters, compact selection and place metadata where appropriate.

It must support dynamic user-created categories and must not depend on a
fixed category enum.

------------------------------------------------------------------------

## 15. WherisFilterChip

Represents an active/inactive filter.

### States

-   unselected;
-   selected;
-   disabled.

Optional count may be added only if the relevant screen requires it.

Filter state must remain obvious after selection.

------------------------------------------------------------------------

# INPUTS

## 16. WherisTextField

General Wheris text input.

### Anatomy

-   optional label;
-   input;
-   optional leading icon;
-   optional trailing action;
-   supporting/error text;
-   container/border.

### Variants

-   single-line;
-   multiline.

### States

-   empty;
-   focused;
-   populated;
-   disabled;
-   error.

User-generated content must support international characters and
reasonable length.

Validation errors should be specific and actionable.

------------------------------------------------------------------------

## 17. WherisSearchField

Specialized search input for places/categories where needed.

### Anatomy

-   search icon;
-   query;
-   clear action when non-empty;
-   optional contextual filter affordance only if specified.

### States

-   empty;
-   active;
-   populated;
-   disabled.

Search should feel lightweight and should not visually dominate the
place library.

------------------------------------------------------------------------

# NAVIGATION

## 18. WherisTopBar

Top app bar for Wheris screens.

### Variants

-   root title;
-   back navigation;
-   optional one or two contextual actions.

### Anatomy

-   navigation affordance if needed;
-   title;
-   optional actions.

Do not overload top bars with many actions.

Screen title should tolerate localization and user-generated context
where applicable.

------------------------------------------------------------------------

## 19. WherisBottomBar

Primary navigation component.

Destinations: - `Carte` - `Lieux` - `Plus`

Each item contains: - icon; - visible text label; - selected/unselected
state.

Selection must not rely on color alone.

The component respects system navigation/gesture insets.

The current MVP must not add extra destinations casually.

------------------------------------------------------------------------

## 20. WherisFloatingActionButton

Primary Add Place action on map-oriented contexts.

### Variants

-   extended with `Ajouter un lieu`;
-   compact/icon form only when the screen specification explicitly
    permits it.

The extended form is preferred when clarity matters.

### States

-   default;
-   pressed;
-   disabled if genuinely unavailable.

The action should remain visually dominant without covering essential
map content.

------------------------------------------------------------------------

# FEEDBACK

## 21. WherisLoadingIndicator

Reusable loading feedback.

### Variants

-   inline;
-   contained;
-   contextual/map acquisition.

It must not imply numeric progress when none exists.

For GPS searching, pair progress with clear text and preserve map
context where specified.

------------------------------------------------------------------------

## 22. WherisEmptyState

Reusable empty-state pattern.

### Anatomy

-   optional illustration/icon;
-   title;
-   supporting text;
-   optional primary action;
-   optional secondary action.

The state must explain the next useful action.

Illustration is secondary to message and action.

------------------------------------------------------------------------

## 23. WherisErrorState

Reusable recoverable-error pattern.

### Anatomy

-   semantic icon;
-   title;
-   explanation;
-   primary recovery action when available;
-   optional secondary action.

The component must answer: - what happened; - what remains possible; -
what to do next.

Do not expose technical exception text.

------------------------------------------------------------------------

## 24. WherisOfflineState

Specialized degraded-network state.

It is not synonymous with general error.

### Anatomy

-   offline/network icon;
-   concise message;
-   explanation of what remains available;
-   retry action when meaningful.

Saved local places should not appear unavailable merely because
map/network data is unavailable.

------------------------------------------------------------------------

## 25. WherisSuccessFeedback

Short-lived success pattern for meaningful completion such as saving a
place.

May include: - check icon/animation; - `Lieu enregistré !`; - onward
actions.

Motion is optional enhancement; meaning must remain without animation.

Avoid long success interstitials.

------------------------------------------------------------------------

# LOCATION

## 26. WherisAccuracyBadge

Displays location accuracy in human-readable form.

### Inputs

-   qualitative level;
-   optional numeric accuracy text.

### Visual states

-   excellent;
-   good;
-   medium;
-   poor;
-   unknown.

Every state combines text/iconography with semantic visual treatment.

Poor accuracy is cautionary, not necessarily an error.

Domain thresholds are not defined by this component.

------------------------------------------------------------------------

## 27. Accuracy color mapping

Approved initial semantic treatment:

-   `Excellent` → success family.
-   `Good` → success/positive family with lower visual emphasis.
-   `Medium` → warm caution treatment.
-   `Poor` → stronger caution treatment.
-   `Unknown` → neutral/information treatment.

The exact caution swatches must be contrast-validated during Figma
component construction before being promoted to global tokens.

Do not use destructive red merely because GPS accuracy is poor.

------------------------------------------------------------------------

## 28. WherisDistanceIndicator

Displays user-friendly distance to a saved place.

### Anatomy

-   optional distance icon;
-   formatted distance;
-   optional supporting label.

Formatting rules belong to product/domain presentation logic, not the
Design System.

The component must not calculate geographic distance.

------------------------------------------------------------------------

## 29. WherisDirectionIndicator

Displays cardinal direction/bearing context.

### Anatomy

-   directional icon;
-   cardinal label;
-   optional numeric bearing when explicitly needed.

The component presents data; it does not calculate bearing.

Direction must remain understandable without color.

------------------------------------------------------------------------

## 30. WherisLocationStatusCard

Reusable geographic-status surface for save flows.

### Variants

-   searching;
-   found;
-   poor accuracy;
-   disabled/unavailable;
-   error where appropriate.

### Anatomy

-   state icon/progress;
-   title;
-   supporting text;
-   accuracy badge when available;
-   relevant actions.

It may be displayed over/alongside the map.

The component should be compact enough to preserve spatial context.

------------------------------------------------------------------------

# CATEGORIES

## 31. WherisCategoryIcon

Canonical rendering of a category identity.

### Inputs

-   `iconKey`;
-   accent color;
-   size;
-   selected state where relevant.

### Sizes

-   compact;
-   standard;
-   large;
-   marker-specific via marker component.

The icon must maintain sufficient contrast against its container.

Custom and system categories use the same rendering model.

------------------------------------------------------------------------

## 32. Category accent palette --- approved baseline

The initial curated accent choices are:

  Key                   Light accent   Dark-theme accent
  ------------------- -------------- -------------------
  `category/orange`        `#F97316`           `#FB923C`
  `category/amber`         `#D97706`           `#F59E0B`
  `category/yellow`        `#CA8A04`           `#EAB308`
  `category/green`         `#16A34A`           `#4ADE80`
  `category/teal`          `#0F766E`           `#2DD4BF`
  `category/blue`          `#2563EB`           `#60A5FA`
  `category/indigo`        `#4F46E5`           `#818CF8`
  `category/purple`        `#7E22CE`           `#C084FC`
  `category/pink`          `#DB2777`           `#F472B6`
  `category/red`           `#DC2626`           `#F87171`
  `category/brown`         `#92400E`           `#D6A36A`
  `category/slate`         `#475569`           `#94A3B8`

These values are category accents, not guaranteed text colors.

Icons/text placed on or near these accents must use component-defined
contrast-safe containers/content.

This table is synchronized with `docs/design/02_DESIGN_TOKENS.md`.
`docs/design/02_DESIGN_TOKENS.md` owns the canonical values; this
component document owns how components consume them.

------------------------------------------------------------------------

## 33. WherisCategoryCard

Selectable category representation for category-selection screens.

### Anatomy

-   category icon;
-   category name;
-   optional system/custom metadata only in management contexts;
-   selected indicator.

### States

-   default;
-   selected;
-   pressed;
-   disabled if applicable.

The selected state should combine border/container treatment with a
check/selection cue.

Cards must support long custom names.

------------------------------------------------------------------------

## 34. Create-category card

Special action card displayed after/among available categories where
specified.

### Anatomy

-   plus icon;
-   `Créer une catégorie`.

It is an action, not a fake category.

It should be visually distinct enough to communicate creation while
remaining part of the selector rhythm.

------------------------------------------------------------------------

## 35. WherisCategorySelector

Higher-level reusable category-selection pattern.

Responsibilities: - render a scalable collection of categories; - expose
selection; - expose `Créer une catégorie`; - support scrolling; -
support a newly created category becoming selected.

It must not assume a fixed number of system categories.

Search may be introduced when scale justifies it, but should not be
mandatory for a small collection.

------------------------------------------------------------------------

## 36. Category preview

Creation/edit screens need a live preview.

### Anatomy

-   selected icon;
-   selected accent color;
-   current category name or placeholder;
-   representative container.

Preview updates immediately as name/icon/color changes.

It must represent actual component styling rather than a decorative mock
unrelated to final category rendering.

------------------------------------------------------------------------

## 37. Category management row

Used in category management lists.

### Anatomy

-   category icon;
-   name;
-   `Par défaut` badge for system category when applicable;
-   optional usage count if product later specifies it;
-   trailing edit affordance for custom categories.

System categories must not expose delete affordance.

Custom categories can expose edit/delete through the approved
interaction pattern.

------------------------------------------------------------------------

# PLACES / MAP

## 38. WherisPinMarker

Canonical saved-place map marker.

### Anatomy

-   marker container;
-   category accent;
-   category icon;
-   selection treatment.

### States

-   default;
-   selected.

Baseline: - default \~40dp visual container; - selected \~48dp; -
internal icon \~20--24dp.

Marker identity combines shape/icon/color.

Mapbox implementation details are not part of this component
specification.

------------------------------------------------------------------------

## 39. Marker selection

Selected marker should become obvious through at least two cues, for
example: - increased size; - emphasized border/halo; - elevation; -
selection indicator.

Do not use only a slight hue change.

Selection animation should be short and non-essential.

------------------------------------------------------------------------

## 39.1 Add-location candidate marker interaction

Within `ADD_001` only, the proposed save-position marker supports an
intentional long-press + drag gesture. The gesture moves the draft
coordinate that will be confirmed; it does not move the live
user-location representation and does not edit an already-saved place.

Interaction requirements:

-   default detected position remains immediately confirmable;
-   long press clearly enters a dragging state before movement is
    accepted;
-   dragging must preserve a usable touch target and visual feedback;
-   release commits the new draft candidate, not the persisted place;
-   after manual adjustment, GPS accuracy from the original fix must not
    be shown as if it described the manually selected point;
-   accessibility must provide an equivalent non-color indication that
    the position was adjusted manually.

------------------------------------------------------------------------

## 40. User-location marker

The current user location is a separate map primitive.

It should use a familiar location-dot language: - central location
dot; - contrasting outline; - optional accuracy halo.

It must never look like a saved category marker.

The accuracy halo is not a saved area and must not imply exact
certainty.

------------------------------------------------------------------------

## 41. WherisMapControl

Reusable map overlay control.

Examples: - recenter/current location; - other map-only actions approved
by a screen.

### Anatomy

-   48dp minimum touch container;
-   24dp icon;
-   high-contrast surface;
-   subtle border/elevation.

Use accessible content descriptions.

Do not add map controls simply because the provider supports them.

------------------------------------------------------------------------

## 42. Marker quick-detail sheet

Reusable bottom-sheet pattern shown after tapping a place marker.

The sheet is transient selection context: tapping an empty map area
dismisses it and clears the selected marker; tapping another marker
replaces the selection.

### Anatomy

-   drag handle if platform pattern uses it;
-   `WherisPinSummary`;
-   distance where available;
-   primary `Naviguer`;
-   secondary `Détails`.

The sheet must preserve useful map context.

Target: marker → navigate in two interactions.

It should not reproduce the entire full-detail screen.

------------------------------------------------------------------------

## 43. Place photo

Reusable local-photo presentation.

### Variants

-   thumbnail;
-   detail;
-   add/replace placeholder.

Photo UI should use controlled crop/radius and accessible labels where
meaningful.

Do not expose filesystem paths or implementation-specific URI text.

------------------------------------------------------------------------

## 44. Favorite control

Reusable favorite toggle.

### Anatomy

-   recognizable favorite icon;
-   selected/unselected state;
-   accessible state description.

Selection must not rely on color alone; filled/outlined icon state is
appropriate.

It should not compete visually with the primary CTA.

------------------------------------------------------------------------

# BADGES AND METADATA

## 45. WherisBadge

Small semantic label.

Possible uses: - `Par défaut`; - compact non-critical status.

Badges use label typography and compact padding.

Avoid badge proliferation.

------------------------------------------------------------------------

## 46. Coordinate display

Coordinates are metadata, not a generic Design System business component
unless repeated behavior justifies one.

When shown: - use readable body/metadata typography; - preserve
sufficient precision according to product logic; - optionally expose
explicit copy action; - do not automatically copy to clipboard.

A dedicated component may be promoted later if multiple screens require
identical behavior.

------------------------------------------------------------------------

## 47. Metadata row

A reusable lightweight pattern may present: - icon; - label; - value.

Examples: - date saved; - altitude; - accuracy; - coordinates.

Use it where repeated in place detail.

Do not turn every metadata item into an elevated card.

------------------------------------------------------------------------

# DIALOGS / SHEETS

## 48. WherisConfirmationDialog

Reusable confirmation pattern.

### Anatomy

-   title;
-   explanatory text;
-   confirm action;
-   cancel action.

### Variants

-   standard;
-   destructive.

The confirm label should name the action where possible (`Supprimer`)
rather than generic `OK`.

------------------------------------------------------------------------

## 49. Delete-place dialog

Specialization of destructive confirmation.

It must make clear that the selected place will be deleted.

Actions: - `Annuler`; - `Supprimer`.

Avoid unnecessary extra text if consequence is straightforward.

------------------------------------------------------------------------

## 50. Delete-category dialog/sheet

This is consequence-aware, not a simple yes/no dialog when places use
the category.

When unused: - confirm category deletion.

When used: - explain that the category is used by places; - allow
selection of a replacement category or the product-defined `Autre`
reassignment path; - make clear that places themselves are not deleted.

The UI must never suggest cascade deletion.

------------------------------------------------------------------------

## 51. Navigation app chooser

When Wheris presents its own chooser rather than relying entirely on
Android resolution:

### Anatomy

-   title;
-   compatible app options;
-   cancel.

Only display apps actually available/compatible according to
implementation.

Do not fabricate provider availability in static runtime UI.

------------------------------------------------------------------------

# ONBOARDING / EDUCATION

## 52. WherisIllustrationBlock

Reusable composition for onboarding/empty education.

### Anatomy

-   illustration;
-   optional decorative background treatment.

It must scale vertically on small screens and remain secondary to
title/body/action.

------------------------------------------------------------------------

## 53. Onboarding page pattern

Each onboarding page may use: - illustration; - headline; - short
supporting copy; - pagination indicator; - primary progression action; -
skip only if product flow specifies it.

Existing messages: - `Enregistre un lieu en quelques secondes` -
`Retrouve tous tes lieux` - `Reviens-y facilement`

The three pages should feel like one component family.

------------------------------------------------------------------------

## 54. Permission education card/screen

Explains why location is useful before/around the Android permission
request.

It must: - use product language; - avoid legalistic copy; - avoid
implying permission is already granted; - provide a clear action; -
support denial/recovery states separately.

------------------------------------------------------------------------

# LIST / SEARCH PATTERNS

## 55. Place list pattern

The place list composes: - optional search; - optional filters; -
`WherisPinCard` items; - empty state when appropriate.

The list must scale and remain readable with photos absent/present and
long names.

Do not invent a separate card design for each category.

------------------------------------------------------------------------

## 56. Category list pattern

Category management uses category rows/cards with clear distinction
between: - system categories; - custom categories; - create-category
action.

Potentially many categories require scrolling and may later justify
search.

------------------------------------------------------------------------

# SETTINGS

## 57. Settings row

Reusable settings item.

### Anatomy

-   optional leading icon;
-   title;
-   optional supporting text;
-   optional trailing value/control/chevron.

Rows should use standard Android expectations.

Do not make every settings row a separate elevated card unless grouping
requires it.

------------------------------------------------------------------------

## 58. Settings section

Groups related settings with: - section heading where useful; - rows; -
spacing/dividers as needed.

Potential groups include appearance, navigation preferences, categories
and information.

Only implemented settings should be shown.

------------------------------------------------------------------------

# FUTURE MONETIZATION PRESENTATION --- OUTSIDE ACTIVE MVP

## 59. Monetization feature row

If a future approved monetization scope is activated, a reusable benefit
row may present an approved Wheris Plus or Wheris Premium capability
using: - feature icon; - title; - short explanation.

The row is presentation-only. It must not decide which plan owns a
feature, compute entitlement state, or embed price/free-limit values.
Those semantics belong to `docs/reference/WHERIS_BUSINESS_REFERENCE.md`
and the owning feature layer.

The navigation destination `Plus` and the commercial offer `Wheris Plus`
are different concepts. Component naming, examples and copy must keep
that distinction explicit.

Monetization components must remain separate from core place-saving and
retrieval controls.

------------------------------------------------------------------------

## 60. Free-limit state

If a free active-place limit is activated, the state must use `lieu`
terminology and explain the actual active business rule.

Do not freeze a numeric limit in the Design System. The limit is
supplied by product/business configuration according to
`docs/reference/WHERIS_BUSINESS_REFERENCE.md`.

The state must not visually imply that existing places are deleted,
locked or lost. It may block creation of an additional place according
to the approved business rule while preserving access to already-saved
local data.

This is a screen/business state, not a global component token.

------------------------------------------------------------------------

# ACCESSIBILITY

## 61. Component accessibility contract

Every component specification must consider: - minimum touch target; -
screen-reader name/role/state; - selected/disabled state; -
non-color-only meaning; - text scaling; - contrast; - focus order when
interactive.

Compose implementation should merge/clear semantics intentionally only
when it improves the accessible representation.

------------------------------------------------------------------------

## 62. Icon-only actions

Icon-only buttons require a meaningful accessible label.

Examples: - favorite; - recenter map; - close; - back; - edit where
represented only by icon.

Decorative icons should not create redundant announcements.

------------------------------------------------------------------------

## 63. Dynamic content

Components displaying user content must not assume: - fixed line
length; - ASCII; - one-line category names; - a photo exists; - distance
is always available; - current location is always available.

Missing optional data should produce graceful composition, not
placeholder junk.

------------------------------------------------------------------------

# FIGMA CONSTRUCTION

## 64. Component sets

In Figma, use Component Sets for variants that share anatomy.

Examples: - `Button / Wheris`: Type × Size × State. - `Category / Card`:
State. - `Chip / Filter`: Selected × Enabled. - `Place / Marker`:
Selected. - `Location / AccuracyBadge`: Level.

Avoid creating hundreds of combinatorial variants when
properties/optional layers can express the difference more cleanly.

------------------------------------------------------------------------

## 65. Figma properties

Prefer meaningful component properties: - text; - boolean visibility; -
instance swap for icons; - variant state/type; - category accent
variable where workflow permits.

Do not detach components to alter routine content.

------------------------------------------------------------------------

## 66. Auto Layout

All applicable components use Auto Layout.

Use: - content-driven width/height where appropriate; - fill container
for screen-level actions; - min-size constraints; - token spacing; -
correct alignment.

Map markers and layered illustrations may use intentional
absolute/layered positioning internally.

------------------------------------------------------------------------

## 67. Figma naming

Use hierarchical names matching this document.

Examples:

``` text
Button/Wheris
Card/Base
Card/Place
Chip/Category
Input/TextField
Navigation/BottomBar
Feedback/Offline
Location/AccuracyBadge
Category/Card
Place/Marker
Map/Control
```

Variants should use clean properties such as:

``` text
Type=Primary
Size=Large
State=Enabled
```

Avoid names such as `Button final 2`, `Card copy`, or screen-specific
duplicated masters.

------------------------------------------------------------------------

# COMPOSE CONSTRUCTION

## 68. Compose API principles

Reusable components should expose UI data and callbacks, not
ViewModels/repositories.

Conceptual example:

``` kotlin
@Composable
fun WherisCategoryCard(
    name: String,
    iconKey: String,
    colorArgb: Long,
    selected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
)
```

The exact API may evolve, but ownership principles do not.

------------------------------------------------------------------------

## 69. Stateless screens and components

Components should generally be stateless.

Local state is acceptable for purely visual/interaction implementation
details that do not represent business state.

Selection, saved status, category identity, favorite state and location
state should come from the owning UI state.

------------------------------------------------------------------------

## 70. Business-free Design System

`:core:designsystem` must not know: - Room; - DAO; - repository; -
Koin; - Mapbox; - `FusedLocationProviderClient`; - navigation
destinations; - business persistence.

Domain-oriented display components may receive neutral display data, but
must not perform business operations.

------------------------------------------------------------------------

## 71. Component preview matrix

Each significant component should have previews covering the states that
materially affect appearance.

Examples: - button primary/secondary/destructive + loading/disabled; -
category card default/selected + long name; - place card with/without
photo and distance; - accuracy levels; - offline/error/empty; -
Light/Dark.

Do not generate a preview for every trivial combinatorial permutation.

------------------------------------------------------------------------

# COMPONENT QA

## 72. Component QA checklist

Before approving the component checkpoint, verify: - token use; - Figma
instance reuse; - Compose naming parity; - Light/Dark; - long text; -
missing optional data; - touch targets; - semantics; - contrast; -
selected/disabled/loading states; - no color-only meaning; - no
business/infrastructure leakage; - no screen-specific duplicated
components.

------------------------------------------------------------------------

## 73. Category palette QA

Before treating the category palette as final, visually test all 12
accents in: - category icon; - category card; - category chip; - default
marker; - selected marker; - Light; - Dark.

If an accent fails recognition/contrast, adjust the palette centrally
and update `docs/design/02_DESIGN_TOKENS.md`.

Do not patch individual categories with local overrides.

------------------------------------------------------------------------

## 74. Map component QA

Test map overlays against visually varied map backgrounds.

Verify: - controls remain readable; - markers remain distinct; -
selected marker is obvious; - user location cannot be confused with a
saved place; - quick detail does not obscure too much map; - map failure
has a non-map alternative.

------------------------------------------------------------------------

## 75. Component promotion rule

A feature-local pattern may be promoted into the Design System when: -
it repeats; - its semantics are stable; - its API can be
business-independent; - consistency provides real value.

Do not prematurely promote experimental one-screen layouts.

------------------------------------------------------------------------

## 76. Deprecated component rule

When replacing a Design System component: 1. define the replacement; 2.
migrate consumers deliberately; 3. remove or deprecate the old API; 4.
update Figma masters; 5. avoid maintaining two nearly identical systems
indefinitely.

------------------------------------------------------------------------

## 77. Initial canonical component inventory

The initial Wheris component inventory is:

``` text
button/
  WherisButton
  WherisButtonDefaults
  WherisButtonSize

card/
  WherisCard
  WherisPinCard

chip/
  WherisChip
  WherisCategoryChip
  WherisFilterChip

input/
  WherisTextField
  WherisSearchField

topbar/
  WherisTopBar

navigation/
  WherisBottomBar
  WherisFloatingActionButton

feedback/
  WherisLoadingIndicator
  WherisEmptyState
  WherisErrorState
  WherisOfflineState
  WherisSuccessFeedback

location/
  WherisAccuracyBadge
  WherisDistanceIndicator
  WherisDirectionIndicator
  WherisLocationStatusCard

category/
  WherisCategoryIcon
  WherisCategoryCard
  WherisCategorySelector
  WherisCategoryPreview

pin/
  WherisPinMarker
  WherisPinCard
  WherisPinSummary

map/
  WherisMapControl

dialog/
  WherisConfirmationDialog

settings/
  WherisSettingsRow
  WherisSettingsSection
```

This is an initial canonical inventory, not a requirement to implement
every component before its first real consumer exists.

------------------------------------------------------------------------

## 78. Patterns versus components

Some structures are better documented as composition patterns rather
than standalone generic components.

Examples: - marker quick-detail sheet; - onboarding page; - place
list; - category management list; - delete-category reassignment flow.

Do not create giant generic composables merely because a pattern is
documented here.

Screen specifications own final composition.

------------------------------------------------------------------------

## 79. Component checkpoint acceptance criteria

This checkpoint is accepted when: - every core recurring Wheris UI
concept has an owner; - category rendering supports custom categories; -
map UI remains provider-neutral at the design/API boundary; - place
retrieval remains fast; - accessibility is built into component
contracts; - Figma and Compose can use matching names; - screens can be
specified mostly by composing these primitives/patterns; - no
unnecessary feature/business behavior has been smuggled into the Design
System.

------------------------------------------------------------------------

## 80. Next checkpoint

After validation, proceed to:

> **`docs/design/04_SCREEN_CATALOG.md`**

That document will establish the canonical inventory of Wheris screens
and states before detailed flows and per-screen specifications are
written.

------------------------------------------------------------------------

## 81. Final component principle

A Wheris component should reduce interpretation.

Figma should know how it looks and varies.

Compose should know how it is represented and interacted with.

The product should know what role it serves.

If each screen has to reinvent those answers, the component system has
failed.
