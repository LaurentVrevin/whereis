# WHERIS --- 07 SCREEN SPECIFICATIONS

> Detailed semantic contract for the canonical Wheris screens and major
> transient surfaces.
>
> This document is the final screen-level layer of the Wheris design
> specification. It translates the approved foundations, tokens,
> components, catalog and user flows into implementable UI contracts
> shared by Figma and Android.
>
> It intentionally does **not** prescribe arbitrary pixel values already
> owned by `docs/design/02_DESIGN_TOKENS.md`, nor duplicate full
> component anatomy owned by `docs/design/03_DESIGN_COMPONENTS.md`.

------------------------------------------------------------------------

# 1. DOCUMENT ROLE

For each screen, this document defines:

-   stable ID and canonical name;
-   purpose;
-   entry conditions;
-   information hierarchy;
-   layout regions;
-   reusable components;
-   content;
-   states;
-   actions and transitions;
-   data rules;
-   loading/error/offline behavior;
-   accessibility;
-   Figma notes;
-   Android notes.

A screen specification describes **responsibility and composition**. It
does not turn every state into a navigation route.

------------------------------------------------------------------------

# 2. SOURCE HIERARCHY

This document must remain consistent with:

``` text
WHERIS_MASTER.md
WHERIS_BUSINESS_REFERENCE.md
01_DESIGN_FOUNDATIONS.md
02_DESIGN_TOKENS.md
03_DESIGN_COMPONENTS.md
04_SCREEN_CATALOG.md
05_USER_FLOWS.md
06_FIGMA_BUILD_RULES.md
```

If a screen requirement exposes a contradiction, fix the owning source
rather than hiding the contradiction here. Monetization pricing, free
limits, plan roles and entitlements are owned by
`docs/reference/WHERIS_BUSINESS_REFERENCE.md`, not by this screen
specification.

------------------------------------------------------------------------

# 3. GLOBAL SCREEN RULES

All screens must:

-   use Wheris design tokens;
-   compose approved reusable components;
-   support Light and Dark;
-   respect Android system insets;
-   support font scaling and content expansion;
-   use French user-facing terminology;
-   avoid color-only meaning;
-   preserve local functionality when network/map services fail where
    applicable;
-   expose loading/error states only when meaningful;
-   avoid technical/provider jargon.

------------------------------------------------------------------------

# 4. GLOBAL TERMINOLOGY

Canonical user-facing wording:

``` text
Carte
Lieux
Plus
Ajouter un lieu
Mes lieux
Type de lieu
Créer une catégorie
Nouvelle catégorie
Lieu enregistré !
Naviguer
Détails
Lancer la navigation
Modifier
Supprimer
Annuler
Par défaut
```

Do not expose `Pin`, `épingle`, `PinEntity`, Mapbox or Room terminology
to the user.

`Plus` in the canonical navigation terminology refers to `PLUS_001`, the
bottom-navigation destination. It is distinct from the future commercial
offer **Wheris Plus** defined in
`docs/reference/WHERIS_BUSINESS_REFERENCE.md`.

------------------------------------------------------------------------

# 5. GLOBAL RESPONSIVE MODEL

Wheris is phone-first.

General composition:

-   full-width responsive containers;
-   approved horizontal screen padding;
-   edge-to-edge map where appropriate;
-   scroll ownership explicit;
-   bottom actions remain reachable;
-   keyboard-visible layouts considered for forms;
-   no critical layout depends on one exact phone height.

Tablet-specific architecture is outside current scope.

------------------------------------------------------------------------

# 6. GLOBAL ACCESSIBILITY CONTRACT

Every interactive screen must account for:

-   minimum approved touch target;
-   logical focus/read order;
-   meaningful content descriptions for icon-only actions;
-   visible and semantic selected states;
-   disabled/loading semantics;
-   sufficient contrast;
-   text scaling;
-   no category/error/accuracy meaning conveyed only through color.

------------------------------------------------------------------------

# LAUNCH

# 7. LAUNCH_001 --- Splash

**FLOW:** FLOW-01\
**TYPE:** Full screen

## PURPOSE

Provide a short branded startup transition while application startup
state is resolved.

## ENTRY CONDITIONS

App process launches into the normal Android startup path.

## LAYOUT

Single centered brand composition.

Recommended hierarchy:

1.  Wheris logo/mark;
2.  Wheris wordmark if part of approved identity;
3.  optional signature only if it does not lengthen or clutter startup.

## COMPONENTS

Prefer platform splash primitives and minimal Wheris branding rather
than a bespoke interactive screen.

## STATES

-   startup;
-   transition out.

No error state belongs on Splash.

## ACTIONS

None required.

## EXIT

``` text
First launch → ONB_001
Returning user → MAP_001
```

## ACCESSIBILITY

Brand asset must remain recognizable at system-supported splash sizes.

## FIGMA NOTES

Represent the intended visual appearance, but do not imply a long custom
animation.

## ANDROID NOTES

Use Android SplashScreen behavior appropriately. Startup routing should
not depend on a fake timer.

------------------------------------------------------------------------

# ONBOARDING

# 8. ONB_001 --- Save Quickly

**FLOW:** FLOW-01

## PURPOSE

Communicate the first and strongest product benefit.

## PRIMARY COPY

`Enregistre un lieu en quelques secondes`

## LAYOUT

1.  illustration/visual;
2.  headline;
3.  short supporting copy if required;
4.  progress indication;
5.  primary Continue action.

## COMPONENTS

-   onboarding illustration;
-   Wheris button;
-   progress/page indicator if approved.

## ACTIONS

`Continuer` → `ONB_002`

## CONTENT RULE

Keep copy concise. Do not explain architecture, GPS providers or account
concepts.

## ACCESSIBILITY

Illustration is decorative unless it conveys information not present in
text.

## FIGMA NOTES

Use the same onboarding composition template across all three onboarding
screens.

## ANDROID NOTES

Screen content should be stateless; onboarding completion is not
persisted yet.

------------------------------------------------------------------------

# 9. ONB_002 --- Personal Geographic Memory

**FLOW:** FLOW-01

## PURPOSE

Explain that Wheris keeps the user's meaningful places available for
retrieval.

## PRIMARY COPY

`Retrouve tous tes lieux`

## LAYOUT / COMPONENTS

Same structural pattern as `ONB_001`.

## ACTIONS

Back → `ONB_001`\
`Continuer` → `ONB_003`

## FIGMA NOTES

Do not introduce a different visual system between onboarding pages.

## ANDROID NOTES

No business data needs to be loaded for this screen.

------------------------------------------------------------------------

# 10. ONB_003 --- Return Easily

**FLOW:** FLOW-01

## PURPOSE

Complete the promise: save now, find later.

## PRIMARY COPY

`Reviens-y facilement`

## ACTIONS

Back → `ONB_002`\
Continue → `PERM_001`

## CONTENT

Supporting copy may introduce the local/private character of Wheris if
approved, but should not make unverified absolute privacy claims.

## ANDROID NOTES

Do not request Android permission directly from a stateless visual
component.

------------------------------------------------------------------------

# PERMISSION

# 11. PERM_001 --- Location Permission Education

**FLOW:** FLOW-01, FLOW-05

## PURPOSE

Explain why foreground location is useful before the Android permission
interaction.

## ENTRY CONDITIONS

Wheris needs current location for the user-requested experience and
permission has not been granted.

## INFORMATION HIERARCHY

1.  location icon/illustration;
2.  concise benefit;
3.  privacy-conscious explanation;
4.  primary permission action;
5.  optional defer action if approved by the flow.

## CORE MESSAGE

Explain that location lets Wheris detect the current position when the
user chooses to save/find a place.

Do not imply continuous tracking.

## ACTIONS

Primary action → Android permission request.

Result:

``` text
Granted precise    → originating flow / MAP_001
Granted approximate→ originating flow / MAP_001
Denied             → PERM_002
```

## ACCESSIBILITY

Permission purpose must be understandable without illustration.

## FIGMA NOTES

Do not recreate the Android system permission dialog as a Wheris
component.

## ANDROID NOTES

Permission request belongs at Route/system boundary, not ViewModel or
stateless Screen. No background-location permission.

------------------------------------------------------------------------

# 12. PERM_002 --- Permission Denied Recovery

**FLOW:** FLOW-01, FLOW-05

## PURPOSE

Provide a calm recovery path after location permission denial.

## STATES

### Requestable again

Explain that location permission is needed to detect the current
position.

Possible action: `Réessayer`

### Permanently denied / settings required

Primary recovery: `Ouvrir les paramètres`

## SECONDARY BEHAVIOR

The user must still be able to access existing locally saved places.

## ACTIONS

-   retry permission when Android allows;
-   open App Settings when required;
-   return to Wheris.

## CONTENT RULE

Do not shame, threaten or imply that existing places are lost.

## ANDROID NOTES

Determine denial/permanent-denial behavior from Android permission
state. Re-evaluate state on return from settings.

------------------------------------------------------------------------

# PRIMARY MAP

# 13. MAP_001 --- Carte

**FLOW:** FLOW-02, FLOW-03, FLOW-10, FLOW-23\
**BOTTOM NAV:** Carte

## PURPOSE

Primary geographic workspace for saving and retrieving places.

## INFORMATION HIERARCHY

1.  geographic context;
2.  saved markers/current position;
3.  selected-place context if any;
4.  `Ajouter un lieu`;
5.  map utility controls;
6.  bottom navigation.

## LAYOUT

Full-bleed map canvas when available.

Overlay regions:

-   top safe-area controls/status where needed;
-   map controls;
-   primary Add Place action;
-   bottom navigation;
-   transient marker quick-detail sheet.

## COMPONENTS

-   neutral Wheris map host;
-   `WherisPinMarker`;
-   current-location marker;
-   recenter control;
-   Add Place FAB/button;
-   `WherisBottomBar`;
-   `SURF_MAP_001`.

## PRIMARY ACTION

`Ajouter un lieu` → `ADD_001`

This action must remain visually dominant over secondary map controls.

## MARKER ACTION

Tap saved marker → select marker + open `SURF_MAP_001`.

Tap empty map area while a marker is selected → clear selection +
dismiss `SURF_MAP_001`.

Tap another saved marker → replace selection + refresh `SURF_MAP_001`.

## STATES

### Normal / populated

Map + saved markers.

On entry, when a usable current position becomes available, center the
camera once on it at a useful local/neighborhood zoom unless the user
has already moved the camera. Do not leave the user on a whole-Earth
view when a usable current position exists. If current position is
unavailable but saved places exist, prefer a useful framing of saved
places.

### Empty --- STATE_MAP_001

No saved places.

Suggested prompt: `Quel lieu voulez-vous retrouver ?`

Primary action: `Ajouter un lieu`

### Map unavailable --- STATE_MAP_002

Preserve access to local places and Add Place behavior where GPS is
usable.

### Offline degradation --- STATE_MAP_003

Communicate map/connectivity limitation without presenting saved data as
lost.

### Current location unavailable

Map/place browsing still works where map is available.

## DATA RULES

Markers derive from canonical saved places + category presentation.

Map failure must not delete/hide canonical place data outside the
unavailable renderer.

## ACCESSIBILITY

Map-only information needs accessible alternatives through `Lieux` and
quick/detail surfaces.

Marker identity cannot rely solely on accent color.

## FIGMA NOTES

Test overlays against varied map backgrounds. Preserve required map
attribution space.

## ANDROID NOTES

Mapbox implementation remains isolated behind `:core:map`. No Mapbox SDK
type may escape that module. The feature consumes neutral
models/callbacks.

------------------------------------------------------------------------

# 14. SURF_MAP_001 --- Marker Quick Detail

**FLOW:** FLOW-10\
**TYPE:** Bottom sheet

## PURPOSE

Expose the fastest useful actions after selecting a saved marker.

## CONTENT

-   category icon;
-   place name or approved fallback identity;
-   category context where useful;
-   distance when current position is available.

## ACTIONS

`Naviguer` → FLOW-12 external navigation\
`Détails` → `PLACE_001`

Dismiss → `MAP_001`

Tapping an empty map area is a canonical dismiss action and clears the
selected marker.

## HIERARCHY

`Naviguer` should be immediately visible.

Do not duplicate full place metadata.

## STATES

-   distance available;
-   distance unavailable;
-   long name;
-   favorite if approved for this surface.

## ACCESSIBILITY

Sheet opening and focus behavior must be coherent with modal semantics.

## ANDROID NOTES

Marker selection and sheet visibility belong to presentation state.
External navigation is emitted as an effect/launcher interaction, not
performed by the stateless Screen.

------------------------------------------------------------------------

# ADD PLACE

# 15. ADD_001 --- Add Place / Location

**FLOW:** FLOW-03, FLOW-05, FLOW-06, FLOW-07

## PURPOSE

Acquire and confirm the position that will become the saved place. The
current foreground position is the default candidate; the user may
deliberately adjust that draft point on the map before confirmation.

## ENTRY CONDITIONS

User explicitly chooses to add a place.

## LAYOUT

Preferred composition:

1.  top bar with Back/Cancel;
2.  map occupying the main geographic region when available;
3.  proposed-position marker, initially aligned with the detected
    current position;
4.  bottom status/action card or sheet;
5.  primary/secondary actions according to state.

## COMPONENTS

-   map host;
-   current-location marker;
-   location status;
-   `WherisAccuracyBadge`;
-   Wheris buttons;
-   loading indicator;
-   feedback components.

## STATE --- Searching

**ID:** `STATE_ADD_001`

Copy: `Recherche de votre position…`

Behavior: - contextual progress; - map remains visible when possible; -
no fabricated coordinates.

## STATE --- Found

**ID:** `STATE_ADD_002`

Copy: `Position détectée`

Supporting content: `Précision : ± X m` when known.

Primary action: `Confirmer cette position` → `ADD_002`

Optional interaction: long-press the proposed marker, drag it to another
point, then release. This changes only the draft coordinate. After
manual adjustment, show a clear manually-adjusted state and do not
present the original GPS accuracy as if it described the new point.

## STATE --- Poor Accuracy

**ID:** `STATE_ADD_003`

Copy: `Précision faible`

Show actual accuracy.

Actions: - `Attendre une meilleure position`; - `Continuer quand même`.

Both remain legitimate choices.

## STATE --- Location Disabled

**ID:** `STATE_ADD_004`

Explain that device location services are disabled.

Recovery action opens appropriate system settings.

## STATE --- Timeout / No Fix

**ID:** `STATE_ADD_005`

Explain that Wheris could not obtain a usable current position.

Actions: - `Réessayer`; - cancel/back.

## STATE --- Technical Error

**ID:** `STATE_ADD_006`

User-readable error + retry where meaningful.

## STATE --- Map Unavailable, GPS Usable

**ID:** `STATE_ADD_007`

Show: - `Position détectée`; - coordinates; - accuracy; -
`Carte indisponible`.

Primary action remains confirmation.

## UNKNOWN ACCURACY

If position is usable but accuracy metadata is absent:

`Précision inconnue`

Do not fabricate a value.

## STALE POSITION

Never present stale location as fresh without disclosure.

Exact freshness policy belongs to domain/location logic.

## DATA PRESERVATION

Once a valid position is accepted, preserve:

-   coordinates;
-   accuracy;
-   altitude if available;
-   timestamp.

## ACCESSIBILITY

Location status must be textually understandable. Accuracy cannot be
color-only.

## FIGMA NOTES

Build representative frames for Searching, Found, ManuallyAdjusted,
PoorAccuracy, LocationDisabled and MapUnavailable.

## ANDROID NOTES

Use foreground location only. `FusedLocationProviderClient` stays in
`:core:location`. ViewModel consumes neutral location abstractions.
Permission launcher stays outside ViewModel.

------------------------------------------------------------------------

# 16. ADD_002 --- Select Category

**FLOW:** FLOW-03, FLOW-08

## PURPOSE

Assign a meaningful type to the new place.

## ENTRY CONDITIONS

A position draft has been accepted.

## LAYOUT

1.  top bar;
2.  prompt;
3.  scrollable/adaptive category selector;
4.  `Créer une catégorie`;
5.  persistent/reachable primary save action once selected;
6.  lower-emphasis optional-details action once selected.

## PRIMARY COPY

`Quel type de lieu enregistres-tu ?`

## COMPONENTS

-   `WherisCategoryCard` / selector;
-   category icon;
-   create-category action card;
-   bottom action container if needed.

## DATA

Render system + custom categories dynamically.

Never assume a fixed number.

## SELECTED STATE

Must use more than color:

-   accent;
-   selected border/check;
-   semantic state.

## ACTIONS

Select category → update draft and enable the fast-save actions.

Primary `Enregistrer` → persist the draft directly → `ADD_004` on
success.

Secondary `Ajouter des détails` → `ADD_003`.

`Créer une catégorie` → `CAT_002` with Add Place origin.

The screen must never require opening `ADD_003` merely to expose the
save action.

## DIRECT SAVE STATE

While persistence is active from `ADD_002`:

-   prevent duplicate submission;
-   keep the selected category and accepted position visible/preserved;
-   show loading only if perceptible.

If persistence fails, remain on `ADD_002`, explain the failure, and
allow retry. The user must not be routed through `ADD_003` as an
error-recovery workaround.

## RETURN FROM CAT_002

On successful category creation:

-   new category is present;
-   new category is automatically selected;
-   position draft is unchanged.

## EMPTY/ERROR

System defaults should normally ensure at least baseline categories
exist. A data-loading failure must not be disguised as "no categories".

## ACCESSIBILITY

Category item announces name and selected state.

## FIGMA NOTES

Stress test with dozens of custom categories and long names.

## ANDROID NOTES

No enum-based category identity. Observe categories from
repository/domain models. Draft selection uses `CategoryId`.

------------------------------------------------------------------------

# 17. ADD_003 --- Optional Place Details

**FLOW:** FLOW-03, FLOW-04, FLOW-09

## PURPOSE

Allow optional enrichment for users who explicitly choose it, without
being part of the mandatory ultra-fast path.

## LAYOUT

Scrollable content:

1.  top bar;
2.  selected category summary;
3.  optional name;
4.  optional note;
5.  optional photo;
6.  favorite;
7.  persistent/reachable `Enregistrer`.

## COMPONENTS

-   Wheris text fields;
-   category chip/summary;
-   photo selector/thumbnail;
-   favorite control;
-   Wheris button;
-   bottom action container.

## CONTENT

Fields must visibly communicate optionality.

No address field is required.

## NAME

Optional.

If empty, save remains enabled.

## NOTE

Optional multiline input.

## PHOTO

Optional local photo.

States: - none; - selecting/acquiring through system; - preview
available; - failure; - removed.

## FAVORITE

Optional boolean toggle.

## PRIMARY ACTION

`Enregistrer`

Must be available without filling any optional field.

## SAVE STATE

While persistence is active:

-   prevent duplicate submission;
-   show loading only if perceptible;
-   preserve form content.

## SAVE ERROR

Explain failure and offer retry.

Do not clear: - position; - category; - name; - note; - photo draft; -
favorite.

## BACK

Back → `ADD_002` with draft preserved and the selected category still
immediately saveable.

## ACCESSIBILITY

Fields have visible/semantic labels. Favorite exposes checked state.
Photo actions have meaningful labels.

## FIGMA NOTES

Create at least: - empty optional form; - enriched form; -
keyboard-visible representative state; - save-error state.

## ANDROID NOTES

Domain must not expose Android `Uri` as canonical photo type. System
photo interaction belongs at platform boundary; persist neutral local
photo reference/path abstraction.

------------------------------------------------------------------------

# 18. ADD_004 --- Place Saved

**FLOW:** FLOW-03, FLOW-04, FLOW-09

## PURPOSE

Confirm successful persistence and return the user to useful context.

## CONTENT

Success icon/animation.

Headline: `Lieu enregistré !`

## ACTIONS

`Voir sur la carte` → `MAP_001` focused/selected on new place\
`Terminer` → appropriate main context, normally `MAP_001`

## MOTION

Short and calm.

Respect reduced-motion expectations.

## DATA RULE

This screen only appears after canonical local save succeeds.

## FIGMA NOTES

Avoid oversized celebration or marketing content.

## ANDROID NOTES

Do not navigate here optimistically before repository persistence
succeeds.

------------------------------------------------------------------------

# PLACES

# 19. PLACES_001 --- Mes lieux

**FLOW:** FLOW-02, FLOW-11, FLOW-14, conditional FLOW-19\
**BOTTOM NAV:** Lieux

## PURPOSE

Browse and retrieve saved places without depending on the map.

## LAYOUT

1.  top bar/title;
2.  search/filter controls if approved for MVP;
3.  scrollable place list;
4.  bottom navigation.

## COMPONENTS

-   search field;
-   filter chips;
-   place list item/card;
-   favorite/category indicators;
-   empty state;
-   bottom bar.

## NORMAL STATE

Display saved places with enough identity to scan quickly.

Potential metadata: - name/fallback; - category icon/name; - distance
when available; - favorite.

Do not overload list rows with full detail metadata.

## EMPTY

**ID:** `STATE_PLACES_001`

Explain no places exist.

Primary action: `Ajouter un lieu` → `ADD_001`

## SEARCH RESULTS

**ID:** `STATE_PLACES_002`

Conditional on search being approved.

## NO SEARCH RESULT

**ID:** `STATE_PLACES_003`

Explain that no saved place matches current query/filter.

Offer clear/reset action.

Do not use the global empty-place message.

## ACTIONS

Tap place → `PLACE_001`

Favorite/filter/search interactions according to approved scope.

## OFFLINE

Local list remains usable.

## ACCESSIBILITY

List items need clear accessible identity and state.

## FIGMA NOTES

Test: - zero places; - few places; - long list; - long names; - mixed
favorites/categories; - distance unavailable.

## ANDROID NOTES

List should observe local canonical data via Flow. Search/filter/sort
rules belong to presentation/domain as appropriate, not Composable-local
business logic.

------------------------------------------------------------------------

# PLACE DETAIL

# 20. PLACE_001 --- Place Detail

**FLOW:** FLOW-10, FLOW-11, FLOW-12, FLOW-13, FLOW-14, FLOW-15, FLOW-16

## PURPOSE

Provide complete useful information and primary actions for one saved
place.

## LAYOUT

Scrollable content with clear hierarchy:

1.  top bar;
2.  place identity/category/favorite;
3.  map preview when available;
4.  primary navigation action;
5.  distance/direction;
6.  saved metadata;
7.  note;
8.  photo;
9.  edit/delete actions.

Exact order may be visually refined while preserving primary-action
prominence.

## DATA

Potential fields:

-   name;
-   category;
-   category icon/color;
-   favorite;
-   coordinates;
-   distance;
-   cardinal direction;
-   recorded accuracy;
-   altitude;
-   created date;
-   note;
-   local photo.

## OPTIONAL DATA

Absence must collapse gracefully.

Do not show meaningless placeholders such as `Altitude: —` everywhere
unless the final content system explicitly prefers them.

## PRIMARY ACTION

`Lancer la navigation` → FLOW-12.

## SECONDARY ACTION

`Modifier` → `PLACE_002`

## DESTRUCTIVE ACTION

`Supprimer` → `SURF_PLACE_001`

## NO CURRENT LOCATION

Place data remains visible.

Distance/direction may be unavailable with concise explanation where
needed.

## MAP UNAVAILABLE

**ID:** `STATE_PLACE_001`

Replace map area with useful fallback without hiding other local data.

## OFFLINE

Local metadata remains available.

External navigation may still be attempted through compatible apps;
Wheris does not guarantee the external app's offline capability.

## ACCESSIBILITY

Coordinates and geographic indicators need readable text equivalents.

## FIGMA NOTES

Create complete + sparse-data + map-unavailable examples.

## ANDROID NOTES

Distance/bearing/cardinal calculations use pure domain logic. Map
rendering remains in `:core:map`. External navigation launcher is
platform implementation triggered by UI effect.

------------------------------------------------------------------------

# 21. PLACE_002 --- Edit Place

**FLOW:** FLOW-15

## PURPOSE

Edit approved mutable metadata of an existing place.

## EDITABLE

-   name;
-   category;
-   note;
-   favorite;
-   local photo.

## NOT CURRENTLY EDITABLE

-   coordinates of an already saved place;
-   manual repositioning of an already saved marker;
-   recorded creation location after persistence.

Do not invent post-save position editing. The approved `ADD_001` draft
marker adjustment before first save is a separate interaction.

## LAYOUT

Similar field language to `ADD_003` to reduce cognitive load.

## ACTIONS

`Enregistrer` → update → `PLACE_001`\
`Annuler` / Back → `PLACE_001`

Delete may remain owned by detail rather than duplicated here unless
explicitly approved.

## SAVE ERROR

Preserve edits and offer retry.

## ACCESSIBILITY

Same form requirements as Add Details.

## ANDROID NOTES

Update only intended fields. Preserve immutable/unmodified location
metadata. ViewModel should not depend on Room Entity directly.

------------------------------------------------------------------------

# 22. SURF_PLACE_001 --- Delete Place Confirmation

**FLOW:** FLOW-16\
**TYPE:** Dialog or modal sheet

## PURPOSE

Prevent accidental destructive deletion.

## CONTENT

Concise confirmation identifying the place where useful.

## ACTIONS

`Annuler`\
`Supprimer`

Destructive action uses approved destructive treatment.

## SUCCESS

Close surface → delete → return to previous valid context.

## FAILURE

Do not dismiss into a fake-success state.

## ACCESSIBILITY

Focus is trapped appropriately while modal. Destructive action is
explicitly labelled.

## ANDROID NOTES

Photo cleanup and place deletion must preserve data integrity. Do not
claim deletion succeeded before repository operation completes.

------------------------------------------------------------------------

# CATEGORIES

# 23. CAT_001 --- Category Management

**FLOW:** FLOW-17, FLOW-18

## PURPOSE

View and manage system/custom categories.

## LAYOUT

1.  top bar;
2.  create action;
3.  system categories;
4.  custom categories;
5.  scrollable content.

## COMPONENTS

-   category rows/cards;
-   `Par défaut` badge;
-   create-category action;
-   edit affordance for custom categories.

## SYSTEM CATEGORIES

-   visible;
-   stable;
-   not deletable.

## CUSTOM CATEGORIES

-   editable;
-   deletable through safe flow.

## ACTIONS

`Créer une catégorie` → `CAT_002`\
Custom category → `CAT_003`

## SCALABILITY

Must support potentially hundreds of categories through scrolling and
efficient layout assumptions.

## OFFLINE

Fully usable from local data.

## ACCESSIBILITY

System/custom status must not be conveyed solely through color.

## FIGMA NOTES

Stress-test many categories and long names.

## ANDROID NOTES

Observe category models dynamically. Stable system identity uses key/ID,
not French label.

------------------------------------------------------------------------

# 24. CAT_002 --- Nouvelle catégorie

**FLOW:** FLOW-08, FLOW-17

## PURPOSE

Create a custom category.

## ENTRY CONTEXTS

-   from `ADD_002`;
-   from `CAT_001`.

Origin affects return behavior, not category data model.

## LAYOUT

1.  top bar;
2.  name field;
3.  icon selector;
4.  color selector;
5.  live preview;
6.  create action.

## COMPONENTS

-   Wheris text field;
-   icon picker items;
-   curated color swatches;
-   category preview;
-   Wheris button.

## NAME

Required to create a meaningful custom category.

Validation should be clear and local.

Exact uniqueness/case rules belong to product/domain validation if
defined.

## ICON

Select from curated icon catalog.

## COLOR

Select from curated category palette.

No arbitrary custom color picker in MVP.

## PREVIEW

Updates in real time and uses the same category visual language as the
app.

## ACTIONS

`Créer` → persist.

If origin = Add Place: → `ADD_002`, new category auto-selected.

If origin = Category Management: → `CAT_001`.

`Annuler` → origin screen.

## FAILURE

Preserve entered name/icon/color and offer retry.

## ACCESSIBILITY

Color swatches need accessible names/selection state; selection cannot
be color-only.

## FIGMA NOTES

Use real component instances in preview.

## ANDROID NOTES

Creation uses stable generated `CategoryId`, neutral `iconKey`, color
token/ARGB representation, `isSystem=false`, creation timestamp. Do not
store localized system semantics here.

------------------------------------------------------------------------

# 25. CAT_003 --- Edit Category

**FLOW:** FLOW-17, FLOW-18

## PURPOSE

Edit a custom category.

## LAYOUT

Same core editor language as `CAT_002`.

## EDITABLE

-   name;
-   icon;
-   accent color.

## ACTIONS

`Enregistrer` → update → `CAT_001`\
`Annuler` → `CAT_001`\
`Supprimer` → usage-aware delete flow.

## SYSTEM CATEGORY RULE

Do not route system categories into an unsupported destructive editor.

## UPDATE EFFECT

All places referencing the category should reflect its updated
presentation without modifying each place individually.

## FAILURE

Preserve edit draft and retry.

## ANDROID NOTES

Relationship remains by `CategoryId`; do not denormalize mutable
category name/icon into every Pin as canonical truth.

------------------------------------------------------------------------

# 26. SURF_CAT_001 --- Delete Unused Category

**FLOW:** FLOW-18

## CONDITION

Selected custom category is referenced by zero places.

## CONTENT

Explain category deletion.

## ACTIONS

`Annuler`\
`Supprimer`

## RULE

No place data changes.

------------------------------------------------------------------------

# 27. SURF_CAT_002 --- Delete Used Category / Reassign

**FLOW:** FLOW-18

## CONDITION

One or more places reference the custom category.

## PURPOSE

Delete the category while explicitly preserving its places.

## CONTENT

-   consequence explanation;
-   affected-place count where available/useful;
-   replacement-category selector;
-   stable `Autre` option;
-   confirmation.

Suggested core wording:

`Cette catégorie est utilisée par plusieurs lieux.`

Exact singular/plural copy should adapt to count.

## ACTIONS

Choose replacement → confirm.

Transaction:

``` text
reassign places
→ delete category
```

Cancel → no change.

## CRITICAL RULE

> Deleting a category never deletes its places.

## ACCESSIBILITY

Replacement selector exposes selected category semantically.

## FIGMA NOTES

Do not visually imply a cascade deletion.

## ANDROID NOTES

Implement as a transaction in data/database layer. FK strategy must
prevent accidental cascade deletion.

------------------------------------------------------------------------

# 28. SURF_CAT_003 --- Icon Selector

**TYPE:** Embedded selector or modal surface

## PURPOSE

Select one icon from the finite Wheris category icon catalog.

## RULES

-   searchable/browsable only if scale justifies it;
-   selected state explicit;
-   icons use stable semantic keys;
-   no arbitrary uploaded icons in MVP.

------------------------------------------------------------------------

# 29. SURF_CAT_004 --- Color Selector

**TYPE:** Embedded selector or modal surface

## PURPOSE

Select one approved category accent.

## RULES

-   curated palette only;
-   selected state uses check/border in addition to color;
-   accessible color name/semantic label;
-   Light/Dark preview compatibility.

------------------------------------------------------------------------

# EXTERNAL NAVIGATION

# 30. NAVEXT_001 --- Navigation Handoff Contract

**FLOW:** FLOW-12

## PURPOSE

Define the Wheris-owned experience around handing a destination to
another navigation application.

This does not have to be a full-screen route in Android.

## INPUT

-   destination coordinates;
-   optional place label;
-   preferred navigation-app setting if present.

## BEHAVIOR

If one valid preferred app can be launched: → hand off directly.

If user choice is needed: → `SURF_NAVEXT_001`.

If none: → `STATE_NAVEXT_001`.

## CONTENT RULE

Do not imply Wheris will calculate or display a route.

## ANDROID NOTES

Use compatible Android intents/URI mechanisms behind an
`ExternalNavigationLauncher` abstraction. ViewModel emits effect; no
`Context` in ViewModel.

------------------------------------------------------------------------

# 31. SURF_NAVEXT_001 --- Navigation App Chooser

**FLOW:** FLOW-12

## PURPOSE

Choose among actually compatible available external navigation apps.

## CONTENT

For each runtime-supported option:

-   app icon if safely available;
-   app name.

## ACTION

Select app → external handoff.

Optional preference behavior may allow "remember choice" only if product
settings support it.

## RULE

Do not display apps that are not actually available merely because a
mockup includes them.

------------------------------------------------------------------------

# 32. STATE_NAVEXT_001 --- No Compatible Navigation App

## PURPOSE

Explain that Wheris cannot currently hand the destination to a
compatible navigation app.

## CONTENT

-   concise explanation;
-   coordinates remain accessible;
-   return to place detail.

No place data changes.

------------------------------------------------------------------------

# PLUS

# 33. PLUS_001 --- Plus

**FLOW:** FLOW-02, FLOW-17, FLOW-20, FLOW-21, FLOW-22\
**BOTTOM NAV:** Plus

## PURPOSE

Provide secondary destinations without cluttering Carte/Lieux.

## APPROVED ENTRIES

-   Catégories;
-   Paramètres;
-   Confidentialité/information if dedicated;
-   À propos.

## TERMINOLOGY / BUSINESS BOUNDARY

`PLUS_001` is the navigation area **Plus**, not the commercial plan
**Wheris Plus**. Its existence does not imply entitlement, purchase or
subscription state.

Monetization/paywall is not part of the current MVP flow.

## LAYOUT

Simple grouped list/card navigation.

## COMPONENTS

-   settings/navigation rows;
-   section labels where needed;
-   bottom navigation.

## ACCESSIBILITY

Rows have clear labels and sufficiently large targets.

## ANDROID NOTES

Only implemented destinations should appear.

------------------------------------------------------------------------

# 34. SETTINGS_001 --- Paramètres

**FLOW:** FLOW-20

## PURPOSE

Configure implemented user preferences.

## CANDIDATE GROUPS

### Apparence

Light / Dark.

System-following remains an open product decision.

### Unités

Required conceptually by user stories, but exact options remain open.

### Navigation

Preferred external navigation app if implemented.

## RULE

Do not show inactive/future settings just to make the screen look
complete.

## ACTIONS

Preference changes should give immediate or clearly predictable effect.

## DATA

Preferences belong in DataStore, not Room business tables.

## ACCESSIBILITY

Rows/toggles/selectors expose state semantically.

## ANDROID NOTES

Use neutral preference models. DataStore handles persisted settings.

------------------------------------------------------------------------

# 35. SETTINGS_002 --- Apparence

**FLOW:** FLOW-20

## PURPOSE

Choose the implemented Wheris theme mode.

## REQUIRED OPTIONS

-   `Clair`;
-   `Sombre`.

A `Système` option remains an open product decision and must not be
shown as canonical until approved.

## ACTIONS

Select mode → apply immediately and persist. Back → `SETTINGS_001`.

## ANDROID NOTES

Persist the preference in DataStore. Theme rendering remains
semantic-token driven.

------------------------------------------------------------------------

# 36. SETTINGS_003 --- Unités

**FLOW:** FLOW-20

## PURPOSE

Choose the unit system used consistently for displayed distances.

## CONTENT RULE

The capability is required by the reference user stories, but the exact
option set is still an explicit product decision. Do not invent a
canonical option list in Figma or Compose.

## ACTIONS

Select approved unit mode → persist and apply consistently. Back →
`SETTINGS_001`.

## ANDROID NOTES

Persist in DataStore; formatting belongs to presentation/domain
formatting logic rather than individual components.

------------------------------------------------------------------------

# 37. SETTINGS_004 --- Application de navigation

**FLOW:** FLOW-20, FLOW-12

## PURPOSE

Manage the preferred compatible external navigation application.

## CONTENT

Render only compatible applications actually available on the device
plus the approved no-preference/default behavior.

## ACTIONS

Select/change/remove preference. Back → `SETTINGS_001`.

## FAILURE

If a preferred app later disappears, Wheris falls back to the runtime
chooser/recovery path rather than failing silently.

## ANDROID NOTES

Installed-app discovery and launching remain platform concerns; persist
only the neutral preference required to resolve the user's choice.

------------------------------------------------------------------------

# 38. ABOUT_001 --- À propos

**FLOW:** FLOW-22

## PURPOSE

Explain Wheris and expose relevant app/legal/provider information.

## CONTENT

-   concise Wheris description;
-   app version;
-   required legal/provider attribution links;
-   privacy entry where appropriate.

Possible product copy:

Wheris permet d'enregistrer et de retrouver les lieux qui comptent : une
voiture garée, une tente de festival, un bivouac, un restaurant, un
point de vue ou tout autre endroit à mémoriser.

## RULES

Do not invent: - social networks; - support channels; - changelog; -
rating CTA.

## ANDROID NOTES

Version information should come from application metadata rather than
duplicated hardcoded UI text where practical.

------------------------------------------------------------------------

# PRIVACY

# 39. PRIVACY_001 --- Confidentialité

**STATUS:** Canonical destination synchronized with
`docs/design/04_SCREEN_CATALOG.md`.\
**FLOW:** FLOW-21

## PURPOSE

Explain Wheris's local-first privacy behavior accurately.

## CONTENT

Explain:

-   no account required for local MVP;
-   saved places are stored locally by Wheris;
-   Wheris does not require permanent movement tracking;
-   foreground location is used for user-requested geographic actions;
-   map/external providers may have their own network behavior;
-   backup behavior must not be described more strongly than actual
    Android configuration allows.

## ACTIONS

Informational links where approved.

## CONTENT RULE

Preferred product promise:

`Tes lieux restent sur ton téléphone.`

Before release, ensure this wording remains accurate relative to Android
backup and any provider/network behavior. Avoid stronger absolute claims
such as "aucune donnée ne quitte jamais l'appareil" unless technically
verified.

## ACCESSIBILITY

Plain language, readable sections, links with descriptive labels.

## ANDROID NOTES

Security/privacy implementation must match
`docs/product/SECURITY_PRIVACY.md`. This screen does not itself enforce
privacy; it describes actual behavior.

------------------------------------------------------------------------

# CONDITIONAL SEARCH/FILTER SURFACE

# 40. SURF_PLACES_001 --- Filters

**STATUS:** Conditional pending MVP scope confirmation.

## PURPOSE

Filter `PLACES_001` without creating a separate product area.

## CURRENT CANDIDATES

-   category;
-   favorites.

## ACTIONS

-   apply;
-   clear/reset;
-   dismiss.

## RULE

Do not add speculative filters such as rating, sharing status, cloud
state or arbitrary map metadata.

------------------------------------------------------------------------

# CROSS-CUTTING FEEDBACK

# 38. STATE_GLOBAL_001 --- Generic Technical Error

Use only when no more specific subsystem state applies.

## CONTENT MODEL

1.  human-readable problem;
2.  what remains safe;
3.  recovery action if available.

Avoid stack traces/error codes/provider names.

------------------------------------------------------------------------

# 39. STATE_GLOBAL_002 --- Offline

Offline is a contextual state/pattern, not a universal blocking screen.

## RULE

Preserve local capabilities.

Do not imply: `Pas de réseau = GPS indisponible`.

------------------------------------------------------------------------

# 40. STATE_GLOBAL_003 --- Loading

Prefer local/contextual loading.

Avoid blank full-screen loading when existing content can remain
visible.

------------------------------------------------------------------------

# PRODUCT-VALIDATION CONTRACT

# 40.1 Fast-save evidence

The canonical design must be testable as behavior, not only reviewed as
static frames.

For the validation slice, record at minimum:

-   save completion from `ADD_002` without optional fields;
-   median and p90 time from usable-position state to persisted success;
-   abandonment before persistence;
-   persistence error / retry success;
-   later retrieval through Carte or Lieux;
-   D30 / D90 and `1 → 3 → 10` usage outside the UI specification when
    analytics are activated under privacy rules.

Do not add hidden tracking or sensitive geographic analytics merely to
measure these outcomes.

------------------------------------------------------------------------

# SCREEN DATA CONTINUITY

# 41. Add Place Draft Contract

The Add Place flow carries a draft containing conceptually:

``` text
position
accuracy
altitude
timestamp
selectedCategoryId
name?
note?
photoReference?
isFavorite
```

Transitions among `ADD_001`, `ADD_002`, `CAT_002`, and `ADD_003` must
preserve applicable values. A direct save from `ADD_002` consumes the
same canonical draft/persistence path as a save from `ADD_003`; the
product must not maintain two divergent creation models.

------------------------------------------------------------------------

# 42. Category Detour Contract

When:

``` text
ADD_002
→ CAT_002
→ create category
→ ADD_002
```

the result must preserve the place draft and return with the new
category selected.

This is a product invariant, not merely prototype polish.

------------------------------------------------------------------------

# 43. Failure Preservation Contract

Recoverable persistence/photo/location detours must not erase unrelated
valid user input.

A failed photo selection does not erase a note.

A failed save does not erase the selected category.

A failed category save does not erase its editor fields.

------------------------------------------------------------------------

# EMPTY / MISSING DATA

# 44. Missing Place Name

Name is optional.

Detailed UI must use an approved fallback identity based on
category/context rather than rendering an empty heading.

**Open content decision:** exact fallback wording.

Do not resolve it inconsistently per screen.

------------------------------------------------------------------------

# 45. Missing Distance

If current position is unavailable, do not show a fake distance.

Either omit the value or use approved unavailable copy.

------------------------------------------------------------------------

# 46. Missing Altitude

Altitude is optional.

Its absence is normal and should not be treated as an error.

------------------------------------------------------------------------

# 47. Missing Photo / Note

Do not reserve large empty visual blocks for absent optional content
unless the screen provides a purposeful add/edit affordance.

------------------------------------------------------------------------

# 48. Unknown Accuracy

Use explicit unknown semantics rather than invented numbers.

------------------------------------------------------------------------

# DARK THEME

# 49. Theme Contract

Every canonical screen uses semantic Light/Dark tokens.

Do not maintain screen-specific dark colors.

Map imagery itself may vary independently; overlays must remain legible
in both themes.

------------------------------------------------------------------------

# 50. Dark QA Screens

At minimum inspect:

``` text
MAP_001
ADD_001 / Found
ADD_001 / PoorAccuracy
ADD_002
ADD_003
PLACES_001
PLACE_001
CAT_002
SURF_CAT_002
PERM_002
```

------------------------------------------------------------------------

# FIGMA FRAME CONTRACT

# 51. Canonical Naming

Use:

``` text
SCREEN_ID / Theme=<mode> / State=<state>
```

Examples:

``` text
ADD_001 / Theme=Light / State=Found
MAP_001 / Theme=Dark / State=Offline
PLACE_001 / Theme=Light / State=Default
```

------------------------------------------------------------------------

# 52. Frame Annotations

Important frames may include non-product annotation outside the device
frame:

-   screen ID;
-   flow ID;
-   state;
-   entry;
-   primary action;
-   special Android note;
-   unresolved decision.

Annotations must never be mistaken for app UI.

------------------------------------------------------------------------

# 53. Component Instances

Canonical frames use approved instances.

Do not detach to make one screen fit.

If a component cannot represent a legitimate screen state, fix the
component specification/master.

------------------------------------------------------------------------

# ANDROID SCREEN CONTRACT

# 54. Route / Screen Separation

Typical feature UI uses:

``` text
Route
  ↓ collects state/effects + system interactions
Screen
  ↓ stateless UI
```

`Screen` should not:

-   resolve DI;
-   request permission;
-   access repository;
-   access Room;
-   use Mapbox SDK types;
-   launch navigation through Context;
-   own business persistence.

------------------------------------------------------------------------

# 55. UiState

UiState represents renderable screen state.

Prefer explicit state models over unrelated boolean collections when
states are mutually exclusive.

Example for location:

``` text
Searching
Available
PoorAccuracy
LocationDisabled
Timeout
Error
```

Exact Kotlin shape belongs to implementation.

------------------------------------------------------------------------

# 56. UiAction

UiAction represents user intent, for example:

``` text
RetryLocation
ConfirmPosition
SelectCategory
SavePlace
ToggleFavorite
DeleteConfirmed
```

Names should describe intent rather than widget mechanics where
practical.

------------------------------------------------------------------------

# 57. UiEffect

Use one-off effects for actions such as:

-   Android permission request trigger coordination;
-   opening settings;
-   external navigation;
-   system photo interaction;
-   transient navigation event where architecture uses effects.

Do not encode durable screen state as a one-off effect.

------------------------------------------------------------------------

# 58. Design System Boundary

Feature screens consume Wheris Design System components.

Design System must not depend on:

-   Room;
-   Koin;
-   Mapbox;
-   FusedLocation;
-   repositories;
-   business ViewModels.

Neutral visual geographic primitives are acceptable where ownership is
clear.

------------------------------------------------------------------------

# SCREEN-TO-FEATURE MAPPING

# 59. Feature Ownership

Recommended ownership:

``` text
LAUNCH_001                  :app / startup
ONB_001–003, PERM_*         :feature:onboarding
MAP_001, SURF_MAP_001       :feature:home
ADD_001–004                 :feature:addpin
PLACES_001                  :feature:pins
PLACE_001–002               :feature:pindetail
CAT_001–003, SURF_CAT_*     :feature:categories
PLUS_001, SETTINGS_001,
ABOUT_001, PRIVACY_001      :feature:settings
```

External-navigation implementation belongs to an appropriate
core/platform abstraction, while entry UI is owned by calling features.

------------------------------------------------------------------------

# 60. Internal Naming

User UI says `lieu`.

Technical code may retain:

``` text
Pin
PinId
PinRepository
PinEntity
AddPin
PinDetail
```

Do not rename technical architecture solely for cosmetic parity if `Pin`
is already the established neutral domain term.

------------------------------------------------------------------------

# PER-SCREEN TEST INTENT

# 61. Onboarding Tests

Verify:

-   correct page progression;
-   permission education before system request where intended;
-   denial recovery;
-   onboarding completion persistence.

------------------------------------------------------------------------

# 62. Map Tests

Verify:

-   empty/populated;
-   marker selection;
-   quick sheet;
-   Add action;
-   map unavailable fallback;
-   local data remains accessible.

Map implementation should be fakeable for feature tests.

------------------------------------------------------------------------

# 63. Add Place Tests

Verify:

-   searching → found;
-   poor accuracy wait/continue;
-   disabled/timeout/error;
-   map unavailable + valid GPS;
-   category selection;
-   direct save from `ADD_002`;
-   secondary `Ajouter des détails` branch to `ADD_003`;
-   custom category detour preservation;
-   optional details;
-   duplicate-save prevention from both save entry points;
-   save failure preservation;
-   success.

------------------------------------------------------------------------

# 64. Places Tests

Verify:

-   empty/populated;
-   open detail;
-   favorites;
-   conditional search/filter behavior if included;
-   offline local rendering.

------------------------------------------------------------------------

# 65. Detail Tests

Verify:

-   optional metadata absence;
-   distance/direction availability;
-   map unavailable;
-   edit;
-   delete;
-   external-navigation effect.

------------------------------------------------------------------------

# 66. Category Tests

Verify:

-   default categories;
-   create custom;
-   edit custom;
-   system deletion blocked;
-   delete unused;
-   delete used + reassign;
-   deletion never deletes places.

------------------------------------------------------------------------

# OPEN DECISIONS

# 67. Photo Source

Still open:

-   system photo picker;
-   camera;
-   both.

This specification defines the screen behavior around a local photo but
does not invent the acquisition policy.

------------------------------------------------------------------------

# 68. Search / Filter / Sort MVP

Still requires scope confirmation.

`PLACES_001` must not be blocked from implementation by speculative
advanced browsing controls.

------------------------------------------------------------------------

# 69. Unit Options

User-selectable units are conceptually requested, but exact choices
remain to be approved.

------------------------------------------------------------------------

# 70. Follow-System Theme

Light and Dark are required.

Automatic system-following remains open.

------------------------------------------------------------------------

# 71. Unnamed Place Fallback

Exact user-facing fallback label remains to be approved and should be
consistent across Map quick detail, list and detail.

------------------------------------------------------------------------

# 72. Privacy Destination Synchronization

`PRIVACY_001 — Confidentialité` is canonical across
`docs/design/04_SCREEN_CATALOG.md`, `docs/design/05_USER_FLOWS.md` and
this specification. Any future change to its ownership or destination
type must update all three documents together.

------------------------------------------------------------------------

# NON-MVP SCREEN GUARD

# 73. Do Not Add

Do not create detailed current-release screen specifications for:

-   login/account;
-   cloud sync;
-   friends/collaboration;
-   sharing;
-   public discovery;
-   internal routing;
-   turn-by-turn;
-   advanced offline-map downloads;
-   AI;
-   widget;
-   Wear OS;
-   iOS;
-   free-limit/paywall;
-   Wheris Plus purchase;
-   Wheris Premium subscription;
-   purchase/subscription restoration or entitlement recovery.

Future monetization work requires separate approved scope and must
derive its commercial rules from
`docs/reference/WHERIS_BUSINESS_REFERENCE.md`. The existence of a
business model does not make these active MVP screen contracts.

------------------------------------------------------------------------

# SCREEN REVIEW MATRIX

# 74. Primary Screens

Must be visually approved before broad implementation:

``` text
MAP_001
ADD_001
ADD_002
ADD_003
ADD_004
PLACES_001
PLACE_001
CAT_001
CAT_002
```

These establish the core Wheris experience.

------------------------------------------------------------------------

# 75. Critical Recovery Screens / States

Must be reviewed before release:

``` text
PERM_002
STATE_ADD_003 PoorAccuracy
STATE_ADD_004 LocationDisabled
STATE_ADD_005 Timeout
STATE_ADD_007 MapUnavailable
STATE_MAP_002 MapUnavailable
STATE_PLACE_001 MapUnavailable
SURF_PLACE_001 Delete
SURF_CAT_002 Reassign
STATE_NAVEXT_001 NoCompatibleApp
```

------------------------------------------------------------------------

# 76. Content Stress Cases

Each relevant screen family should be checked with:

-   long place name;
-   no place name;
-   long category name;
-   many categories;
-   long note;
-   no photo;
-   no altitude;
-   no current location;
-   poor GPS accuracy;
-   large font;
-   Dark theme.

------------------------------------------------------------------------

# 77. Geographic Stress Cases

Map/geographic UI should be visually checked with:

-   dense urban map;
-   rural map;
-   dark terrain;
-   bright map area;
-   several nearby markers;
-   selected marker;
-   current-location marker;
-   map unavailable.

------------------------------------------------------------------------

# DESIGN ACCEPTANCE CHECKLIST

# 78. Product

A screen passes if:

-   its purpose is obvious;
-   it supports the relevant User Flow;
-   it does not invent a feature;
-   the primary action matches Wheris priorities;
-   terminology is correct.

------------------------------------------------------------------------

# 79. Visual System

A screen passes if:

-   approved tokens are used;
-   approved components are reused;
-   hierarchy is clear;
-   no rogue colors/spacing/type styles are introduced;
-   Light/Dark are coherent.

------------------------------------------------------------------------

# 80. State Completeness

A screen passes if relevant states are represented:

-   loading;
-   empty;
-   error;
-   offline;
-   unavailable;
-   selected;
-   disabled;
-   optional-data absence.

Not every screen needs every state.

------------------------------------------------------------------------

# 81. Data Integrity

A screen/flow fails if its design implies:

-   place loss from map failure;
-   place deletion from category deletion;
-   draft loss after recoverable failure;
-   fake location/accuracy;
-   successful save before persistence;
-   required optional metadata.

------------------------------------------------------------------------

# 82. Accessibility

A screen passes if:

-   text remains readable;
-   controls meet touch-target intent;
-   color is not the sole signal;
-   focus/semantic expectations are clear;
-   large text can be accommodated.

------------------------------------------------------------------------

# 83. Android Handoff

A screen passes handoff if an engineer can identify:

-   canonical screen ID;
-   owning feature;
-   components;
-   states;
-   actions;
-   transitions;
-   optional data;
-   failure behavior;
-   platform/system interactions;
-   architectural boundary notes.

------------------------------------------------------------------------

# FINAL SCREEN INVENTORY

# 84. Canonical Full Screens

``` text
LAUNCH_001   Splash

ONB_001      Onboarding — Save Quickly
ONB_002      Onboarding — Personal Map
ONB_003      Onboarding — Return Easily
PERM_001     Location Permission Education
PERM_002     Permission Denied Recovery

MAP_001      Carte

ADD_001      Add Place — Location
ADD_002      Add Place — Category
ADD_003      Add Place — Details
ADD_004      Add Place — Success

PLACES_001   Mes lieux

PLACE_001    Place Detail
PLACE_002    Edit Place

CAT_001      Category Management
CAT_002      New Category
CAT_003      Edit Category

PLUS_001     Plus
SETTINGS_001 Paramètres
SETTINGS_002 Apparence
SETTINGS_003 Unités
SETTINGS_004 Application de navigation
ABOUT_001    À propos
PRIVACY_001  Confidentialité
```

Conditional monetization/paywall screens are excluded from the active
MVP specification.

------------------------------------------------------------------------

# 85. Canonical Major Surfaces

``` text
SURF_MAP_001       Marker Quick Detail
SURF_PLACE_001     Delete Place Confirmation
SURF_CAT_001       Delete Unused Category
SURF_CAT_002       Delete Used Category / Reassign
SURF_CAT_003       Icon Selector
SURF_CAT_004       Color Selector
SURF_NAVEXT_001    Navigation App Chooser
SURF_PLACES_001    Filters [conditional]
```

------------------------------------------------------------------------

# 86. Canonical Important States

``` text
STATE_MAP_001      Empty
STATE_MAP_002      MapUnavailable
STATE_MAP_003      Offline

STATE_ADD_001      Searching
STATE_ADD_002      Found
STATE_ADD_003      PoorAccuracy
STATE_ADD_004      LocationDisabled
STATE_ADD_005      Timeout
STATE_ADD_006      TechnicalError
STATE_ADD_007      MapUnavailable

STATE_PLACES_001   Empty
STATE_PLACES_002   SearchResults [conditional]
STATE_PLACES_003   NoSearchResults [conditional]

STATE_PLACE_001    MapUnavailable

STATE_NAVEXT_001   NoCompatibleApp

STATE_GLOBAL_001   TechnicalError
STATE_GLOBAL_002   Offline
STATE_GLOBAL_003   Loading
```

------------------------------------------------------------------------

# 87. Documentation Freeze Procedure

Before declaring the Design Specification frozen:

1.  review `docs/design/07_SCREEN_SPECIFICATIONS.md`;
2.  resolve or explicitly defer all open decisions;
3.  verify `PRIVACY_001` and `SETTINGS_002–004` remain synchronized
    across Catalog, Flows and Specifications;
4.  ensure `docs/design/05_USER_FLOWS.md` references stable IDs
    consistently;
5.  ensure category palette values are synchronized between Tokens and
    Components;
6.  verify monetization/paywall, Wheris Plus purchase and Wheris Premium
    subscription are excluded from active MVP design and future
    references remain synchronized with
    `docs/reference/WHERIS_BUSINESS_REFERENCE.md`;
7.  verify Figma build rules reference the final inventory;
8.  then build/normalize the canonical Figma file.

------------------------------------------------------------------------

# 88. Recommended Figma Build Sequence After Documentation Approval

``` text
1. Variables
2. Typography
3. Atomic components
4. Category components
5. Place/location/map components
6. MAP_001
7. ADD_001–004
8. CAT_002 detour
9. PLACES_001
10. PLACE_001–002
11. CAT_001–003
12. Onboarding/permission
13. Plus/settings/privacy/about
14. Recovery states
15. Dark theme
16. Prototypes
17. Accessibility/content stress QA
18. Android handoff review
```

The core Add Place flow is validated before secondary screens consume
large design effort.

------------------------------------------------------------------------

# 89. Final Principle

Every Wheris screen must help the user answer one of two questions:

> **Comment est-ce que je mémorise cet endroit maintenant ?**

or:

> **Comment est-ce que je retrouve cet endroit plus tard ?**

Everything else supports those two moments.

The design is complete when those moments are fast, understandable,
resilient and consistent---from specification, to Figma, to Jetpack
Compose.
