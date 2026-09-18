# WHERIS --- 05 USER FLOWS

> Canonical UX flow specification for Wheris.
>
> This document connects the stable screen, state and surface IDs
> defined in `DESIGN/04_SCREEN_CATALOG.md` into explicit user journeys.
>
> Functional basis: the Wheris reference user stories. The flows
> preserve their intent while applying the canonical distinction between
> full screens, embedded states, bottom sheets and dialogs established
> by the Screen Catalog.
>
> This document defines transitions, branches, recovery, preserved data
> and completion conditions. Detailed per-screen layout belongs to
> `DESIGN/07_SCREEN_SPECIFICATIONS.md`.
>
> Monetization behavior is not defined by this file. Future Free / Wheris
> Plus / Wheris Premium rules come from `WHERIS_BUSINESS_REFERENCE.md` and
> are translated into flows only when monetization scope is explicitly
> activated.

------------------------------------------------------------------------

## 1. Purpose

The User Flows document answers:

-   where a user enters a task;
-   what they are trying to accomplish;
-   which canonical screens/surfaces participate;
-   which decisions and failures can occur;
-   what must survive a detour or failure;
-   how the user exits;
-   what counts as success.

It is not a navigation implementation file and does not require every
state to become a Navigation Compose destination.

------------------------------------------------------------------------

## 2. Canonical UX North Star

Wheris exists to make two moments extremely simple:

> « Je veux me souvenir de cet endroit. »\
> → « C'est enregistré. »

Then later:

> « Je veux retrouver cet endroit. »\
> → « Le voilà. »

Every flow must preserve this relationship.

------------------------------------------------------------------------

## 3. Global UX invariant

> **Aucune évolution de Wheris ne doit rendre plus difficile l'action
> d'enregistrer immédiatement sa position.**

The primary save path should remain as close as practical to a
sub-10-second interaction once the app is ready and permission/location
conditions allow it.

Optional enrichment must never become mandatory enrichment.

------------------------------------------------------------------------

## 4. Three reference journeys

The flows are organized around three product-defining journeys.

### A --- Ultra-fast save

``` text
Carte
→ Ajouter
→ position exploitable
→ confirmer
→ catégorie
→ enregistrer
→ lieu enregistré
```

No name, note or photo is required.

### B --- Enriched save

``` text
Carte
→ Ajouter
→ position
→ confirmer
→ catégorie
→ détails facultatifs
→ nom/note/photo/favori selon besoin
→ enregistrer
```

### C --- Retrieval

``` text
Carte ou Lieux
→ lieu
→ aperçu ou détails
→ distance/direction si disponibles
→ Naviguer
→ application externe
```

These three journeys have priority over secondary configuration flows.

------------------------------------------------------------------------

## 5. Flow notation

This document uses:

-   `SCREEN` --- full canonical destination;
-   `STATE` --- state owned by a screen;
-   `SURFACE` --- transient UI such as bottom sheet/dialog;
-   `ACTION` --- explicit user action;
-   `SYSTEM` --- Android/provider outcome;
-   `DECISION` --- branch;
-   `SUCCESS` --- successful completion;
-   `EXIT` --- user leaves without completing.

Example:

``` text
MAP_001
  ACTION Ajouter un lieu
    ↓
ADD_001 / STATE_ADD_001 Searching
    ↓ SYSTEM position available
ADD_001 / STATE_ADD_002 Found
```

------------------------------------------------------------------------

## 6. Data-preservation rule

During any in-progress Add Place flow, Wheris must preserve already
acquired/entered draft data across recoverable detours.

Draft may include:

-   detected `GeoPoint`;
-   GPS accuracy;
-   altitude if available;
-   selected category;
-   optional name;
-   optional note;
-   local photo reference;
-   favorite state.

Creating a category, retrying GPS or recovering from a photo failure
must not silently erase valid draft information.

------------------------------------------------------------------------

## 7. Cancellation rule

Before the place is persisted, the user may abandon the Add Place flow.

Cancellation:

-   creates no place;
-   must not leave a partial `Pin` in canonical storage;
-   may require cleanup of temporary photo resources;
-   returns to an appropriate previous product context.

The exact confirmation threshold for abandoning a heavily edited draft
is a later screen-level decision; do not introduce unnecessary
confirmation into the fast path.

------------------------------------------------------------------------

## 8. Back-navigation rule

Back navigation should follow user intent rather than technical route
history.

Examples:

-   `ADD_003` → back to `ADD_002` while preserving draft;
-   `CAT_002` opened from `ADD_002` → cancel returns to `ADD_002`;
-   `CAT_002` opened from `CAT_001` → cancel returns to `CAT_001`;
-   external app handoff does not destroy Wheris place state.

------------------------------------------------------------------------

# FLOW-01 --- FIRST LAUNCH

## 9. Goal

Allow a first-time user to understand Wheris, understand why location is
useful, make a permission choice, and reach the map.

### User stories

`US-ONB-001` through `US-ONB-010`.

------------------------------------------------------------------------

## 10. Entry condition

-   App launched.
-   Onboarding has not been completed.

### Entry

``` text
LAUNCH_001
→ ONB_001
```

------------------------------------------------------------------------

## 11. Main path

``` text
ONB_001 — Save quickly
  ACTION Continuer
    ↓
ONB_002 — Personal geographic memory
  ACTION Continuer
    ↓
ONB_003 — Return easily / privacy context
  ACTION Continuer
    ↓
PERM_001 — Location permission education
  ACTION Autoriser / Continuer
    ↓
SYSTEM — Android foreground location permission
```

The system permission dialog is not a Wheris screen.

------------------------------------------------------------------------

## 12. Permission granted branch

``` text
SYSTEM permission result
  → precise granted
      → onboarding completed
      → MAP_001

  → approximate granted
      → onboarding completed
      → MAP_001
```

Approximate permission does not block entry to Wheris.

Actual accuracy is communicated when location is used.

------------------------------------------------------------------------

## 13. Permission denied branch

``` text
SYSTEM permission denied
    ↓
PERM_002 — Permission denied recovery
```

The user must understand:

-   Wheris remains accessible;
-   existing/local places are not lost;
-   saving the current position requires appropriate location access;
-   permission can be reconsidered later.

The exact CTA depends on whether Android allows another request or
requires App Settings.

------------------------------------------------------------------------

## 14. Permanently denied branch

``` text
PERM_002
  ACTION Ouvrir les paramètres
    ↓
SYSTEM App Settings
    ↓ return
PERM_002 or MAP_001 according to current permission state
```

Do not loop system permission requests aggressively.

------------------------------------------------------------------------

## 15. Completion

Onboarding completion is persisted as a preference.

Subsequent normal launches:

``` text
LAUNCH_001
→ MAP_001
```

The user does not repeat onboarding unless future explicit product
behavior says otherwise.

------------------------------------------------------------------------

# FLOW-02 --- MAIN NAVIGATION

## 16. Goal

Allow predictable movement between the three primary areas.

### User stories

`US-NAV-001` through `US-NAV-004`.

------------------------------------------------------------------------

## 17. Destinations

``` text
Carte  → MAP_001
Lieux  → PLACES_001
Plus   → PLUS_001
```

The current destination remains visually obvious.

------------------------------------------------------------------------

## 18. Navigation behavior

Primary navigation should preserve expected state when reasonable, such
as:

-   map camera/selection within normal lifecycle expectations;
-   current place-list query/filter state while moving briefly between
    tabs where implementation supports it.

Do not make navigation state preservation more important than
correctness or memory simplicity.

------------------------------------------------------------------------

# VALIDATION SLICE

## 18.1 Product-validation sequence

Before secondary surface completeness becomes the priority, validate this end-to-end behavior with real usage:

``` text
MAP_001
→ Ajouter
→ usable position
→ confirm
→ category
→ Enregistrer
→ ADD_004
→ later return via MAP_001 or PLACES_001
→ recognize place
→ Navigate externally when needed
```

Collect at least first-save activation, fast-save median/p90, `1 → 3`, `3 → 10`, D30/D90 and retrieval usage. No fixed target is invented here except the existing sub-10-second fast-save objective under suitable location conditions.

------------------------------------------------------------------------

# FLOW-03 --- ADD PLACE / ULTRA-FAST HAPPY PATH

## 19. Goal

Save the current position with the smallest practical number of
decisions.

### Core stories

`US-MAP-006`, `US-ADD-001`, `US-ADD-002`, `US-LOC-001`, `US-LOC-002`,
`US-POS-001`--`003`, `US-CATSEL-001`--`004`, `US-DETAILS-001`,
`US-SAVE-001`, `US-DONE-001`--`003`.

------------------------------------------------------------------------

## 20. Entry

Primary entry:

``` text
MAP_001
  ACTION Ajouter un lieu
    ↓
ADD_001
```

Other future/contextual Add actions may enter the same flow, but must
not fork into separate creation architectures.

------------------------------------------------------------------------

## 21. Acquisition

``` text
ADD_001 / STATE_ADD_001 Searching
  SYSTEM usable current position
    ↓
ADD_001 / STATE_ADD_002 Found
```

The map remains visible when available.

The user sees:

-   detected position;
-   accuracy if available;
-   clear confirmation action.

------------------------------------------------------------------------

## 22. Confirm position

``` text
ADD_001 / Found
  ACTION Confirmer cette position
    ↓
ADD_002 — Select Category
```

If accuracy is acceptable, do not add extra warnings.

------------------------------------------------------------------------

## 23. Select category

``` text
ADD_002
  ACTION select category
    ↓
category selected
```

System and custom categories participate in the same selector.

Category identity uses name + icon + accent treatment rather than color
alone.

------------------------------------------------------------------------

## 24. Fast-save decision

The user must be able to save without entering the optional-details screen.

Canonical ultra-fast requirement:

``` text
ADD_002
  selected category
  ACTION Enregistrer
    ↓
local save
    ↓
ADD_004 — Place Saved
```

`Enregistrer` is the primary action once a category is selected.

`Ajouter des détails` is a secondary action that opens `ADD_003` for users who want to enrich the draft before saving. Name, note, photo and favorite must never be required to complete the fast path.

Validation must measure median and p90 save time when a usable position is already available, plus abandonment and persistence failure. The target remains approximately under 10 seconds in those conditions.

------------------------------------------------------------------------

## 25. Save success

``` text
save succeeds
    ↓
ADD_004 — Place Saved
```

Message:

`Lieu enregistré !`

Actions:

``` text
Voir sur la carte → MAP_001 focused/selected on saved place
Terminer          → MAP_001 or originating primary context
```

------------------------------------------------------------------------

## 26. Happy-path success condition

Success means:

-   one canonical place exists locally;
-   its detected position is preserved;
-   selected category is preserved;
-   optional fields remain null/empty where omitted;
-   no duplicate place is created by repeated rapid taps;
-   the place appears consistently in Carte and Lieux.

------------------------------------------------------------------------

# FLOW-04 --- ADD PLACE / ENRICHED

## 27. Goal

Allow optional enrichment without changing the essential save model.

### User stories

`US-DETAILS-002` through `US-DETAILS-007`, `US-PHOTO-001` through
`US-PHOTO-004`, `US-SAVE-002`.

------------------------------------------------------------------------

## 28. Path

``` text
MAP_001
→ ADD_001
→ confirm position
→ ADD_002
→ select category
→ ACTION Ajouter des détails
→ ADD_003
```

The enriched path is an explicit secondary branch. Selecting a category alone must not force entry into `ADD_003`.

At `ADD_003`, the user may independently:

-   add a name;
-   add a note;
-   mark favorite;
-   add a local photo.

None is mandatory.

------------------------------------------------------------------------

## 29. Optional name

Name entry:

-   requires no address;
-   may be left empty;
-   supports user-generated text.

If absent, later list/detail UI must still identify the place
intelligibly using category-based presentation rather than a visibly
blank title.

The exact fallback label remains a content decision for screen
specifications.

------------------------------------------------------------------------

## 30. Optional note

A note can be entered without any address or structured place metadata.

Failure elsewhere in the flow must not silently discard the note.

------------------------------------------------------------------------

## 31. Favorite

Favorite can be toggled before saving.

The visual state changes immediately.

The saved `isFavorite` value must appear consistently after persistence.

------------------------------------------------------------------------

## 32. Photo interaction

``` text
ADD_003
  ACTION Ajouter une photo
    ↓
SYSTEM photo acquisition/selection mechanism
    ↓
success → preview local photo in ADD_003
failure → explain failure, preserve draft, allow continue without photo
cancel  → return to ADD_003 unchanged
```

The reference stories intentionally do **not** decide whether MVP uses:

-   camera;
-   system photo picker;
-   both.

Therefore this flow specifies the behavior but does not invent two
Wheris photo screens.

------------------------------------------------------------------------

## 33. Remove photo before save

If a local photo has been selected:

``` text
ADD_003
  ACTION Retirer la photo
    ↓
photo removed from draft
```

Temporary resources must be cleaned according to implementation
ownership.

Other draft data remains unchanged.

------------------------------------------------------------------------

## 34. Save enriched place

``` text
ADD_003
  ACTION Enregistrer
    ↓
saving
    ↓
ADD_004 on success
```

All supplied optional fields are persisted with the place.

------------------------------------------------------------------------

# FLOW-05 --- LOCATION ACQUISITION & RECOVERY

## 35. Goal

Handle foreground-location realities without confusing them with
map/network failures.

### User stories

`US-LOC-001` through `US-LOC-010`.

------------------------------------------------------------------------

## 36. Searching

``` text
ADD_001 / STATE_ADD_001 Searching
```

The user understands that Wheris is actively looking for the current
position.

The state is not a blank spinner.

Cancellation remains possible.

------------------------------------------------------------------------

## 37. Permission required

If permission is not currently sufficient:

``` text
ADD_001
  → permission-required presentation
  → permission education/system request as appropriate
```

After permission is granted, resume the same Add Place task rather than
forcing the user to restart manually.

------------------------------------------------------------------------

## 38. Permission denied

``` text
SYSTEM denial
    ↓
recoverable permission presentation
```

If Android permits re-request, present an appropriate retry path.

If permission is permanently denied, provide App Settings.

Do not label denial as a technical GPS failure.

------------------------------------------------------------------------

## 39. Location services disabled

``` text
ADD_001 / STATE_ADD_004 LocationDisabled
  ACTION system recovery
    ↓
SYSTEM location settings
    ↓ return
re-evaluate provider state
```

Do not claim Internet is required.

------------------------------------------------------------------------

## 40. Timeout

``` text
ADD_001 / STATE_ADD_005 Timeout
```

Actions:

-   retry;
-   exit/cancel.

Retry returns to:

``` text
STATE_ADD_001 Searching
```

No fake fallback coordinate is generated.

------------------------------------------------------------------------

## 41. Technical error

``` text
ADD_001 / STATE_ADD_006 TechnicalError
```

The message is user-readable.

Where recovery is meaningful:

``` text
ACTION Réessayer
→ STATE_ADD_001 Searching
```

------------------------------------------------------------------------

## 42. No position available

When no usable position can be obtained, Wheris explicitly communicates
absence rather than silently substituting unrelated coordinates.

This may share the timeout/no-fix recovery surface when the user action
is identical, but the underlying UI state should preserve the correct
reason.

------------------------------------------------------------------------

## 43. Stale position

Wheris must not silently present an old location as a fresh current
position.

The reference stories require this case to be handled but do not
prescribe its exact visual interface.

Therefore:

-   stale data may assist acquisition internally if technically
    appropriate;
-   if presented to the user as a candidate, its age/staleness must be
    explicit;
-   it must not be labelled simply `Position détectée` as if current.

Exact freshness policy belongs to domain/location specification.

------------------------------------------------------------------------

# FLOW-06 --- LOW / UNKNOWN GPS ACCURACY

## 44. Goal

Keep the user informed while preserving their agency.

### User stories

`US-ACC-001` through `US-ACC-006`.

------------------------------------------------------------------------

## 45. Poor accuracy branch

``` text
ADD_001 receives usable position with poor accuracy
    ↓
ADD_001 / STATE_ADD_003 PoorAccuracy
```

Show the obtained accuracy when available.

Actions:

``` text
Attendre une meilleure position
Continuer quand même
```

------------------------------------------------------------------------

## 46. Wait for improvement

``` text
ACTION Attendre une meilleure position
    ↓
temporary continued acquisition
```

Possible outcomes:

``` text
better fix → STATE_ADD_002 Found
still poor → STATE_ADD_003 updated
timeout/error → appropriate recovery state
```

The app must stop unnecessary location updates when the flow no longer
needs them.

------------------------------------------------------------------------

## 47. Continue anyway

``` text
STATE_ADD_003
  ACTION Continuer quand même
    ↓
ADD_002
```

The actual accuracy is retained with the saved place.

Poor accuracy is not a destructive/error condition.

------------------------------------------------------------------------

## 48. Unknown accuracy

If position is usable but accuracy metadata is unavailable:

-   say that accuracy is unknown;
-   allow the user to continue;
-   do not fabricate an accuracy value.

------------------------------------------------------------------------

# FLOW-07 --- SAVE WITHOUT MAP / OFFLINE

## 49. Goal

Allow saving a place when GPS is usable but cartographic rendering is
unavailable.

### User stories

`US-OFFADD-001` through `US-OFFADD-003`, plus `US-OFF-002`,
`US-OFF-013`.

------------------------------------------------------------------------

## 50. Branch

``` text
ADD_001
  SYSTEM location available
  SYSTEM map unavailable
    ↓
ADD_001 / STATE_ADD_007 MapUnavailableDuringSave
```

The UI communicates two separate facts:

-   position is available;
-   map representation is unavailable.

------------------------------------------------------------------------

## 51. Fallback information

When map cannot be shown, the confirmation experience may expose:

-   position found;
-   latitude;
-   longitude;
-   accuracy;
-   explicit `Carte indisponible`.

The essential action remains confirmation.

------------------------------------------------------------------------

## 52. Continue offline

``` text
STATE_ADD_007
  ACTION Confirmer
    ↓
ADD_002
  → Enregistrer → local save → ADD_004
  → or Ajouter des détails → ADD_003 → Enregistrer → ADD_004
```

No Internet connection is required merely to persist the place, and map unavailability must not force the enriched branch.

------------------------------------------------------------------------

# FLOW-08 --- CREATE CATEGORY DURING ADD PLACE

## 53. Goal

Create a custom category without interrupting or losing the place being
saved.

### User stories

`US-CATSEL-005`, `US-NEWCAT-001` through `US-NEWCAT-007`.

------------------------------------------------------------------------

## 54. Entry

``` text
ADD_002
  ACTION Créer une catégorie
    ↓
CAT_002 — New Category
```

The Add Place draft remains owned/preserved by the flow.

------------------------------------------------------------------------

## 55. Category creation

At `CAT_002`, user may:

-   enter name;
-   choose icon;
-   choose curated accent color;
-   inspect live preview.

``` text
ACTION Créer
    ↓
validate
    ↓
persist custom category
```

------------------------------------------------------------------------

## 56. Successful return

When creation originated from Add Place:

``` text
CAT_002 success
    ↓
ADD_002
```

Required result:

-   new category appears;
-   new category is automatically selected;
-   previously detected position remains;
-   previously entered Add Place information remains.

The user must not repeat a selection that Wheris can infer from the
action just completed.

------------------------------------------------------------------------

## 57. Cancel category creation

``` text
CAT_002
  ACTION Annuler / Back
    ↓
ADD_002
```

No custom category is created.

The place draft remains intact.

------------------------------------------------------------------------

## 58. Category creation failure

If persistence fails:

-   remain in category creation;
-   preserve entered name/icon/color;
-   explain failure;
-   allow retry or cancel;
-   preserve parent Add Place draft.

------------------------------------------------------------------------

# FLOW-09 --- SAVE PERSISTENCE & FAILURE

## 59. Goal

Make local persistence reliable and recoverable.

### User stories

`US-SAVE-001` through `US-SAVE-006`.

------------------------------------------------------------------------

## 60. Save action

The same canonical persistence operation is available from both save entry points:

``` text
ADD_002
  ACTION Enregistrer

or

ADD_003
  ACTION Enregistrer
```

Immediately prevent duplicate submissions while save is active. Do not implement two divergent creation pipelines.

If persistence is effectively instantaneous, avoid flashing unnecessary loading UI.

If it takes perceptible time, show saving feedback on the originating screen.

------------------------------------------------------------------------

## 61. Save success

``` text
local persistence success
→ ADD_004
```

The draft becomes a canonical saved place.

------------------------------------------------------------------------

## 62. Save failure

``` text
local persistence failure
→ recoverable save-error state on the originating ADD_002 or ADD_003 screen
```

Both entry points use the same draft and persistence contract.

Must preserve:

-   position;
-   category;
-   name;
-   note;
-   photo draft/reference as appropriate;
-   favorite.

Actions:

``` text
Réessayer
Annuler / return according to screen behavior
```

The user must not re-enter the whole place.

------------------------------------------------------------------------

# FLOW-10 --- RETRIEVE FROM MAP

## 63. Goal

Retrieve a saved place visually and act on it quickly.

### User stories

`US-MAP-003`, `US-MAP-004`, `US-MAP-008`, `US-MAP-009`, `US-PREVIEW-001`
through `US-PREVIEW-004`.

------------------------------------------------------------------------

## 64. Select marker

``` text
MAP_001
  ACTION tap saved-place marker
    ↓
marker becomes selected
SURF_MAP_001 — Marker Quick Detail opens
```

Selected marker must be visually distinguishable from others.

------------------------------------------------------------------------

## 65. Quick detail

The sheet shows, when available:

-   name/fallback identity;
-   category;
-   category icon;
-   distance.

No current position:

-   sheet still opens;
-   distance may be omitted/unavailable;
-   place remains actionable.

------------------------------------------------------------------------

## 66. Quick navigation

``` text
SURF_MAP_001
  ACTION Naviguer
    ↓
FLOW-12 External Navigation
```

Target remains:

> marker → navigation in two interactions.

------------------------------------------------------------------------

## 67. Open full detail

``` text
SURF_MAP_001
  ACTION Détails
    ↓
PLACE_001
```

Closing/dismissing the quick sheet returns to map context.

------------------------------------------------------------------------

# FLOW-11 --- RETRIEVE FROM MY PLACES

## 68. Goal

Retrieve saved places without relying on the map.

### User stories

`US-LIST-001` through `US-LIST-009`.

------------------------------------------------------------------------

## 69. Entry

``` text
Bottom Navigation
  ACTION Lieux
    ↓
PLACES_001
```

------------------------------------------------------------------------

## 70. Populated list

Each item must be identifiable through meaningful place/category
presentation.

The library supports access patterns for:

-   all places;
-   recent places;
-   favorites;
-   categories.

The exact visual grouping/segmentation belongs to detailed screen
design.

------------------------------------------------------------------------

## 71. Empty list

``` text
PLACES_001 / STATE_PLACES_001 Empty
```

Show:

-   clear empty message;
-   `Ajouter un lieu`.

``` text
ACTION Ajouter un lieu
→ ADD_001
```

------------------------------------------------------------------------

## 72. Open place

``` text
PLACES_001
  ACTION tap place
    ↓
PLACE_001
```

This works offline because local place data is canonical.

------------------------------------------------------------------------

# FLOW-12 --- EXTERNAL NAVIGATION

## 73. Goal

Hand a saved destination to a compatible navigation app.

### User stories

`US-NAVEXT-001` through `US-NAVEXT-006`.

------------------------------------------------------------------------

## 74. Entry points

Navigation can start from at least:

``` text
SURF_MAP_001 → Naviguer
PLACE_001    → Lancer la navigation
```

Both use the same external-navigation behavior.

------------------------------------------------------------------------

## 75. Preferred navigation app available

If the user has a valid preferred compatible app:

``` text
ACTION Naviguer
→ explicit external handoff
→ compatible navigation app
```

The destination coordinates and optional safe label are passed as
needed.

------------------------------------------------------------------------

## 76. No preferred app

If no preferred app is configured and several compatible targets exist:

``` text
ACTION Naviguer
→ SURF_NAVEXT_001 Navigation App Chooser
→ select available app
→ external handoff
```

Only actually compatible/available apps are offered at runtime.

------------------------------------------------------------------------

## 77. Preferred app no longer available

If a previously preferred app has been uninstalled or cannot handle the
intent:

``` text
preferred launch unavailable
→ chooser/recovery
→ select another compatible app
```

Do not fail permanently because a stored preference became stale.

------------------------------------------------------------------------

## 78. No compatible app

``` text
STATE_NAVEXT_001 NoCompatibleApp
```

Explain the issue.

Keep access to:

-   place detail;
-   coordinates;
-   other local data.

No saved data is affected.

------------------------------------------------------------------------

## 79. External navigation boundary

Wheris does not implement:

-   turn-by-turn navigation;
-   Mapbox Navigation;
-   Directions;
-   internal route calculation.

The handoff is explicit and user-initiated.

------------------------------------------------------------------------

# FLOW-13 --- PLACE DETAIL

## 80. Goal

Inspect all meaningful information about one saved place.

### User stories

`US-PIN-001` through `US-PIN-014`.

------------------------------------------------------------------------

## 81. Normal detail

``` text
PLACE_001
```

Show available data:

-   name;
-   category + icon;
-   map;
-   photo;
-   distance;
-   cardinal direction;
-   saved date;
-   recorded GPS accuracy;
-   altitude;
-   coordinates;
-   note;
-   favorite state.

Optional data is omitted gracefully.

------------------------------------------------------------------------

## 82. No current location

If current position is unavailable:

-   place remains fully inspectable;
-   distance may be unavailable;
-   direction may be unavailable;
-   recorded place data remains unchanged.

Do not confuse "cannot calculate distance now" with "place location
missing".

------------------------------------------------------------------------

## 83. Map unavailable

``` text
PLACE_001 / STATE_PLACE_001 MapUnavailable
```

Continue showing essential local information, especially:

-   identity;
-   distance if current position is available;
-   direction if calculable;
-   coordinates;
-   recorded accuracy;
-   note/photo/date where available.

------------------------------------------------------------------------

# FLOW-14 --- FAVORITES

## 84. Goal

Allow fast marking and retrieval of important places.

### User stories

`US-FAV-001` through `US-FAV-004`.

------------------------------------------------------------------------

## 85. Toggle favorite

From approved surfaces such as detail/edit:

``` text
ACTION favorite toggle
→ immediate visual state update
→ persist state
```

If persistence fails, UI must reconcile rather than silently pretending
success.

------------------------------------------------------------------------

## 86. Retrieve favorites

``` text
PLACES_001
  ACTION favorite filter/view
    ↓
favorites subset
```

Favorites remain local and usable offline.

------------------------------------------------------------------------

# FLOW-15 --- EDIT PLACE

## 87. Goal

Correct or enrich an existing place without changing unrelated data.

### User stories

`US-EDIT-001` through `US-EDIT-008`.

------------------------------------------------------------------------

## 88. Entry

``` text
PLACE_001
  ACTION Modifier
    ↓
PLACE_002
```

------------------------------------------------------------------------

## 89. Editable data

Current approved edit scope:

-   name;
-   category;
-   note;
-   favorite;
-   replace/remove local photo.

Manual coordinate editing or dragging the saved position is **not** part
of the approved flow.

------------------------------------------------------------------------

## 90. Save edit

``` text
PLACE_002
  ACTION Enregistrer
    ↓
local update
    ↓
PLACE_001 with updated data
```

Changes must propagate consistently to map markers, list and detail
through canonical data observation.

------------------------------------------------------------------------

## 91. Edit failure

On persistence failure:

-   keep the edit form/draft;
-   explain failure;
-   allow retry;
-   do not silently discard changes.

------------------------------------------------------------------------

## 92. Cancel edit

``` text
PLACE_002
  ACTION Annuler / Back
    ↓
PLACE_001
```

Unsaved edits do not alter the canonical place.

The exact unsaved-change confirmation policy is a later screen-level
decision.

------------------------------------------------------------------------

# FLOW-16 --- DELETE PLACE

## 93. Goal

Delete one place deliberately and clean its owned local resources.

### User stories

`US-DELETE-001` through `US-DELETE-006`.

------------------------------------------------------------------------

## 94. Request deletion

``` text
PLACE_001
  ACTION Supprimer
    ↓
SURF_PLACE_001 Delete Place Confirmation
```

------------------------------------------------------------------------

## 95. Cancel deletion

``` text
SURF_PLACE_001
  ACTION Annuler
    ↓
PLACE_001
```

Nothing changes.

------------------------------------------------------------------------

## 96. Confirm deletion

``` text
SURF_PLACE_001
  ACTION Supprimer
    ↓
delete canonical place
    ↓
clean owned local photo resource as appropriate
    ↓
return to previous valid context
```

The place disappears from:

-   map;
-   Lieux;
-   favorites;
-   category-derived place views.

------------------------------------------------------------------------

## 97. Delete failure

If deletion fails:

-   do not visually remove the place as if successful;
-   explain failure;
-   allow recovery/retry where meaningful.

Photo cleanup must follow data-integrity ownership so a partial failure
does not produce misleading state.

------------------------------------------------------------------------

# FLOW-17 --- CATEGORY MANAGEMENT

## 98. Goal

Create, inspect and edit custom categories independently of Add Place.

### User stories

`US-CATS-001` through `US-CATS-009`.

------------------------------------------------------------------------

## 99. Entry

``` text
PLUS_001
  ACTION Catégories
    ↓
CAT_001
```

------------------------------------------------------------------------

## 100. View categories

`CAT_001` displays:

-   system categories;
-   custom categories;
-   `Créer une catégorie`.

System categories can be identified with `Par défaut` where useful.

The flow remains functional offline.

------------------------------------------------------------------------

## 101. Create from management

``` text
CAT_001
  ACTION Créer une catégorie
    ↓
CAT_002
  ACTION Créer
    ↓
persist
    ↓
CAT_001
```

Unlike creation during Add Place, there is no parent place draft and no
auto-selection requirement.

------------------------------------------------------------------------

## 102. Edit custom category

``` text
CAT_001
  ACTION custom category
    ↓
CAT_003
```

Editable:

-   name;
-   icon;
-   accent color.

``` text
ACTION Enregistrer
→ persist
→ CAT_001
```

All places referencing the category automatically reflect its updated
presentation through the shared category relationship.

------------------------------------------------------------------------

## 103. Cancel category edit

``` text
CAT_003
  ACTION Annuler
    ↓
CAT_001
```

No canonical category change is persisted.

------------------------------------------------------------------------

## 104. System category protection

System categories do not expose destructive deletion.

Their stable identity is not the localized French label.

------------------------------------------------------------------------

# FLOW-18 --- DELETE & REASSIGN CATEGORY

## 105. Goal

Delete a custom category without ever deleting the places that use it.

### User stories

`US-CATDEL-001` through `US-CATDEL-008`.

------------------------------------------------------------------------

## 106. Unused category

``` text
CAT_003 or CAT_001 approved delete action
    ↓
system checks usage
    ↓
0 places
    ↓
SURF_CAT_001 Delete Unused Category
```

Confirm:

``` text
ACTION Supprimer
→ delete category
→ CAT_001
```

No place data is affected.

------------------------------------------------------------------------

## 107. Used category

``` text
delete requested
    ↓
usage > 0
    ↓
SURF_CAT_002 Delete Used Category / Reassign
```

The surface explains that places use the category.

Where available, display the number of affected places as useful
consequence information. The exact count presentation remains a
screen-specification detail.

------------------------------------------------------------------------

## 108. Choose replacement

The user chooses:

-   another valid category; or
-   stable system `Autre`.

Then:

``` text
ACTION Confirmer
    ↓
transaction
  1. reassign all affected places
  2. delete custom category
    ↓
CAT_001
```

This must appear as one coherent operation.

------------------------------------------------------------------------

## 109. Cancel category deletion

``` text
SURF_CAT_002
  ACTION Annuler
    ↓
previous category context
```

No reassignment and no deletion occur.

------------------------------------------------------------------------

## 110. Failure

If transactional reassignment/deletion fails:

-   category remains;
-   places remain;
-   do not expose a half-completed successful state;
-   explain failure;
-   allow retry.

Critical invariant:

> **Supprimer une catégorie ne supprime jamais les lieux.**

------------------------------------------------------------------------

# FLOW-19 --- SEARCH, FILTERS & SORT

## 111. Status

**MVP status: to confirm.**

The reference stories define these behaviors, but also explicitly mark
search/filter/sort as requiring MVP confirmation.

Therefore this flow is documented for continuity but must not silently
expand implementation scope.

------------------------------------------------------------------------

## 112. Search

Potential canonical behavior:

``` text
PLACES_001
  ACTION enter query
    ↓
STATE_PLACES_002 SearchResults
```

No match:

``` text
STATE_PLACES_003 NoSearchResults
```

Clear query returns to all applicable places.

------------------------------------------------------------------------

## 113. Filters

Potential filters supported by stories:

-   category;
-   favorites.

The user can:

-   see that a filter is active;
-   reset filters.

Final inline-chip vs bottom-sheet presentation belongs to screen
specification.

------------------------------------------------------------------------

## 114. Sort

Stories request possible sorting by:

-   date;
-   distance;
-   alphabetical order.

Distance sorting requires a current position to produce meaningful
current distance.

If current position is unavailable, Wheris must communicate that
limitation rather than inventing values.

------------------------------------------------------------------------

# FLOW-20 --- PLUS & SETTINGS

## 115. Goal

Access secondary management and preferences without cluttering the core
map/save experience.

### User stories

`US-MORE-001` through `US-MORE-004`, `US-SET-001` through `US-SET-005`.

------------------------------------------------------------------------

## 116. Plus

``` text
Bottom Navigation
  ACTION Plus
    ↓
PLUS_001
```

`PLUS_001` is the navigation destination **Plus**. It must not be confused
with the future commercial offer **Wheris Plus**.

Current approved entries include:

-   Catégories;
-   Paramètres;
-   Confidentialité/information entry;
-   À propos.

No monetization/paywall flow belongs to the current MVP.

------------------------------------------------------------------------

## 117. Settings

``` text
PLUS_001
  ACTION Paramètres
    ↓
SETTINGS_001
```

Settings may expose only implemented preferences.

Reference stories include:

-   appearance;
-   units;
-   preferred navigation application.

Exact option sets must be validated separately where not defined.

------------------------------------------------------------------------

## 118. Appearance

Reference behavior requires:

-   Light;
-   genuine Dark theme;
-   all screens readable/accessibly designed in both.

"Follow system" is not currently established by the source stories and
must not be silently treated as approved product behavior.

Canonical destination: `SETTINGS_002` when appearance is opened from Settings.

------------------------------------------------------------------------

## 119. Units

The stories require a user-selectable unit system and consistent
application across Wheris.

They do not define the exact choices.

Therefore do not freeze Metric/Imperial in this flow without product validation. Canonical destination: `SETTINGS_003`.

------------------------------------------------------------------------

## 120. Preferred navigation application

The settings flow may allow:

-   inspect compatible apps;
-   choose preferred app;
-   change preference;
-   remove preference.

Canonical destination: `SETTINGS_004`.

Preference persists across app restarts.

Runtime navigation must still recover if the chosen app later
disappears.

------------------------------------------------------------------------

# FLOW-21 --- PRIVACY INFORMATION

## 121. Goal

Explain Wheris's local-first behavior accurately.

### User stories

`US-PRIV-001` through `US-PRIV-004`.

------------------------------------------------------------------------

## 122. Entry

From `PLUS_001` or approved settings/information grouping:

``` text
ACTION Confidentialité
→ PRIVACY_001
```

`PRIVACY_001` is the canonical dedicated destination and must remain synchronized with the Screen Catalog and Screen Specifications.

------------------------------------------------------------------------

## 123. Content requirements

Explain that:

-   Wheris does not require an account for the local MVP;
-   saving a place does not require sending it to a Wheris backend;
-   Wheris does not permanently track movement;
-   place data is designed to remain local by default.

Security/privacy copy must remain technically accurate regarding
map-provider network traffic and Android backup behavior; avoid absolute
claims that have not been verified.

------------------------------------------------------------------------

# FLOW-22 --- ABOUT

## 124. Goal

Provide general Wheris information without inventing unsupported product
infrastructure.

### User story

`US-ABOUT-001`.

------------------------------------------------------------------------

## 125. Entry

``` text
PLUS_001
  ACTION À propos
    ↓
ABOUT_001
```

Do not invent unapproved:

-   social links;
-   support system;
-   changelog;
-   Play Store rating CTA.

Required map attribution must be exposed where provider/license rules
require it.

------------------------------------------------------------------------

# FLOW-23 --- OFFLINE RESILIENCE

## 126. Goal

Keep Wheris useful when network/cartography is unavailable.

### User stories

`US-OFF-001` through `US-OFF-013`.

------------------------------------------------------------------------

## 127. Offline capabilities

Without network, Wheris should continue to support local operations
where their own dependencies remain available:

-   view saved places;
-   save a place if GPS provides a usable fix;
-   edit a place;
-   delete a place;
-   view categories;
-   create/edit custom categories;
-   view favorites;
-   view coordinates;
-   view recorded accuracy;
-   view saved date;
-   calculate distance if current location is available;
-   calculate cardinal direction if current location is available.

------------------------------------------------------------------------

## 128. Map degradation

If map tiles/rendering are unavailable:

``` text
MAP_001 → STATE_MAP_003 or STATE_MAP_002
PLACE_001 → STATE_PLACE_001
ADD_001 → STATE_ADD_007
```

The UI explicitly separates:

`Carte indisponible`

from:

`Données du lieu indisponibles`

The latter should not occur merely because map rendering failed.

------------------------------------------------------------------------

## 129. GPS and Internet independence

Offline/network failure and current-location failure are distinct
dimensions.

Possible combinations include:

-   offline + GPS available;
-   online + GPS unavailable;
-   map unavailable + GPS available;
-   map available/cached + current location unavailable.

Design and implementation must not collapse these into one generic
error.

------------------------------------------------------------------------

# FLOW-24 --- ERROR & RECOVERY PRINCIPLES

## 130. Goal

Define cross-flow resilience.

### User stories

`US-ERR-001` through `US-ERR-006`.

------------------------------------------------------------------------

## 131. Error language

Errors should communicate:

1.  what happened;
2.  what remains safe/available;
3.  what the user can do next.

Do not display technical exception/provider language.

------------------------------------------------------------------------

## 132. Retry

Recoverable operations should expose retry when retry can reasonably
change the outcome.

Examples:

-   location acquisition;
-   local save;
-   edit persistence;
-   delete persistence;
-   category persistence.

Retry must not duplicate already successful operations.

------------------------------------------------------------------------

## 133. Preserve input

Intermediate failure must not silently erase user work.

Especially preserve:

-   Add Place draft;
-   category-creation draft;
-   place-edit draft.

------------------------------------------------------------------------

## 134. Distinct failure domains

At minimum distinguish:

-   permission failure/denial;
-   location service disabled;
-   GPS/location acquisition failure;
-   map/cartography failure;
-   local persistence failure;
-   photo interaction failure;
-   external navigation handoff failure.

A single generic "Une erreur est survenue" should not replace actionable
states.

------------------------------------------------------------------------

# FLOW-25 --- ACCESSIBILITY ACROSS FLOWS

## 135. Scope

Accessibility stories are transversal rather than separate navigation
flows.

### User stories

`US-A11Y-001` through `US-A11Y-007`.

------------------------------------------------------------------------

## 136. Required behavior

Every flow must remain usable with:

-   TalkBack;
-   larger text;
-   motor accessibility needs;
-   reduced reliance on color perception.

Requirements include:

-   logical reading/focus order;
-   sufficiently large touch targets;
-   semantic labels;
-   categories recognizable by icon/name, not color alone;
-   selected/loading/disabled states exposed semantically;
-   error messages with recovery information.

------------------------------------------------------------------------

# FLOW-26 --- INTERNATIONALIZATION

## 137. Scope

Internationalization is transversal.

### User stories

`US-I18N-001`, `US-I18N-002`.

------------------------------------------------------------------------

## 138. French-first behavior

All MVP product UI is available in French.

System category labels are localized UI resources resolved from stable
category keys.

Custom category names are user data and are not automatically
translated.

The architecture should permit future English localization without
introducing a language-selection screen now.

------------------------------------------------------------------------

# PRODUCT DECISIONS STILL OPEN

## 139. Open decision --- photo source

Not decided:

-   system photo picker only;
-   camera only;
-   both.

Do not add separate Wheris flows until product behavior is validated.

------------------------------------------------------------------------

## 140. Open decision --- search/filter/sort MVP

The stories exist, but strict MVP inclusion requires confirmation.

Design exploration may account for them without making them a release
blocker unless scope is approved.

------------------------------------------------------------------------

## 141. Open decision --- place position editing

Not approved:

-   manually edit coordinates;
-   drag saved marker;
-   re-acquire position for an existing place.

Do not add to `PLACE_002` without explicit product decision.

------------------------------------------------------------------------

## 142. Open decision --- units

User-selectable units are requested by the stories, but exact choices
are not defined.

Do not hardcode product policy in design documentation prematurely.

------------------------------------------------------------------------

## 143. Open decision --- follow system theme

Light and Dark are required.

Automatic system-theme following remains to be validated.

------------------------------------------------------------------------

## 144. Open decision --- unnamed-place fallback label

Places may be saved without a name.

The list/detail must present a comprehensible identity, likely using
category context, but exact copy/pattern must be decided in
`DESIGN/07_SCREEN_SPECIFICATIONS.md`.

------------------------------------------------------------------------

# OUT-OF-SCOPE FLOWS

## 145. Do not design for current MVP

The current flow backlog excludes:

-   sign-in/sign-up;
-   user profile/account;
-   password recovery;
-   cloud synchronization;
-   multi-device management;
-   friends;
-   social sharing;
-   comments/reviews;
-   public place discovery;
-   Google Places search;
-   internal routing;
-   turn-by-turn navigation;
-   Mapbox Navigation;
-   permanent GPS tracking;
-   movement history;
-   full downloadable offline map management;
-   complex collections;
-   AI;
-   advertising;
-   Android widget;
-   Wear OS;
-   iOS.

------------------------------------------------------------------------

## 146. Monetization / billing boundary

Do not create current MVP flows for:

-   free-limit blocking/paywall;
-   Wheris Plus purchase;
-   Wheris Premium subscription;
-   purchase/subscription restoration;
-   plan comparison;
-   entitlement recovery;
-   Premium cloud onboarding or sync setup.

The commercial architecture is now documented in
`WHERIS_BUSINESS_REFERENCE.md`, but documenting a future business model does
**not** activate those flows in the MVP. Future monetization User Stories
and flow IDs must be added only in a dedicated approved monetization phase.

When such a phase begins, flows must preserve the Business Reference rules:
value before blocking monetization, access to already-saved local places, no
data deletion on downgrade/expiration, explicit cloud opt-in, and clear
distinction between navigation `Plus` and commercial `Wheris Plus`.

This supersedes any implication in earlier visual cataloguing that a
Premium/paywall mockup is an active MVP flow.

------------------------------------------------------------------------

# FLOW COVERAGE MATRIX

## 147. Flow-to-screen matrix

  -----------------------------------------------------------------------
  Flow                                Main canonical screens/surfaces
  ----------------------------------- -----------------------------------
  FLOW-01 First Launch                `LAUNCH_001`, `ONB_001–003`,
                                      `PERM_001–002`, `MAP_001`

  FLOW-02 Main Navigation             `MAP_001`, `PLACES_001`, `PLUS_001`

  FLOW-03 Fast Add                    `MAP_001`, `ADD_001–004`

  FLOW-04 Enriched Add                `ADD_001–004` + system photo
                                      interaction

  FLOW-05 Location Recovery           `ADD_001` +
                                      `STATE_ADD_001/004/005/006`

  FLOW-06 Accuracy                    `ADD_001` + `STATE_ADD_003`

  FLOW-07 No Map / Offline Add        `ADD_001` + `STATE_ADD_007`,
                                      `ADD_002–004`

  FLOW-08 Category During Add         `ADD_002`, `CAT_002`

  FLOW-09 Save Failure                `ADD_003`, `ADD_004`

  FLOW-10 Retrieve Map                `MAP_001`, `SURF_MAP_001`,
                                      `PLACE_001`

  FLOW-11 Retrieve List               `PLACES_001`, `PLACE_001`

  FLOW-12 External Nav                `SURF_MAP_001` / `PLACE_001`,
                                      `SURF_NAVEXT_001`

  FLOW-13 Place Detail                `PLACE_001`

  FLOW-14 Favorites                   `PLACE_001` / approved controls,
                                      `PLACES_001`

  FLOW-15 Edit Place                  `PLACE_001`, `PLACE_002`

  FLOW-16 Delete Place                `PLACE_001`, `SURF_PLACE_001`

  FLOW-17 Category Management         `PLUS_001`, `CAT_001–003`

  FLOW-18 Delete Category             `CAT_001/003`, `SURF_CAT_001/002`

  FLOW-19 Search/Filter/Sort          `PLACES_001`, related
                                      states/surfaces --- conditional

  FLOW-20 Settings                    `PLUS_001`, `SETTINGS_001–004`

  FLOW-21 Privacy                     `PLUS_001`, `PRIVACY_001`

  FLOW-22 About                       `PLUS_001`, `ABOUT_001`

  FLOW-23 Offline                     cross-cutting

  FLOW-24 Errors                      cross-cutting

  FLOW-25 Accessibility               cross-cutting

  FLOW-26 I18N                        cross-cutting
  -----------------------------------------------------------------------

------------------------------------------------------------------------

## 148. User-story coverage principle

A user story may participate in several flows.

The purpose of this document is not to duplicate every story sentence.
It translates stories into interaction behavior and preserves
traceability through story IDs.

If a future story changes a flow materially:

1.  update the source user-story corpus;
2.  update this document;
3.  update `DESIGN/04_SCREEN_CATALOG.md` if ownership/destination changes;
4.  update detailed screen specifications;
5.  update Figma prototype;
6.  update implementation/tests when applicable.

------------------------------------------------------------------------

# FIGMA PROTOTYPE REQUIREMENTS

## 149. Prototype --- mandatory primary flows

The Figma prototype should make these journeys directly testable:

### Prototype A --- Fast save

``` text
MAP_001
→ ADD_001 Found
→ ADD_002
→ Enregistrer
→ ADD_004
→ MAP_001 with saved marker
```

### Prototype B --- Enriched save

``` text
MAP_001
→ ADD_001
→ ADD_002
→ ADD_003 with optional data
→ ADD_004
```

### Prototype C --- Retrieval

``` text
MAP_001
→ marker
→ SURF_MAP_001
→ PLACE_001 or Naviguer
```

------------------------------------------------------------------------

## 150. Prototype --- mandatory recovery branches

At minimum prototype representative branches for:

-   poor GPS accuracy;
-   location permission denied;
-   location services disabled;
-   map unavailable during Add;
-   category creation during Add;
-   save failure/retry;
-   no-map place detail;
-   delete place;
-   delete used category + reassignment;
-   no compatible navigation app.

Not every technical failure needs a unique prototype if the interaction
is genuinely identical.

------------------------------------------------------------------------

## 151. Prototype data continuity

Figma prototype transitions should visually preserve relevant draft
content where the real app must preserve it.

Example:

``` text
ADD_002
→ CAT_002
→ create “Champignons”
→ ADD_002
```

The returned screen should visibly show `Champignons` selected rather
than resetting the selector.

------------------------------------------------------------------------

# UX PERFORMANCE TARGETS

## 152. Save-speed target

The fast-save flow should minimize:

-   typing;
-   unnecessary dialogs;
-   repeated confirmation;
-   mandatory enrichment;
-   route transitions with no decision value.

The sub-10-second goal is an experience target, not a promise that GPS
acquisition itself always completes within 10 seconds.

------------------------------------------------------------------------

## 153. Retrieval-speed target

A place selected on the map should expose navigation in approximately
two user interactions:

``` text
tap marker
→ tap Naviguer
```

Do not require opening full detail first.

------------------------------------------------------------------------

# FLOW QA

## 154. Flow review checklist

For every flow verify:

-   entry is clear;
-   exit is clear;
-   back behavior is coherent;
-   destructive actions are explicit;
-   recovery exists where meaningful;
-   user data survives recoverable errors;
-   map failure is not confused with data loss;
-   GPS failure is not confused with network failure;
-   optional fields remain optional;
-   custom categories remain dynamic;
-   offline local operations remain available;
-   accessibility does not depend on color;
-   user-facing terminology says `lieu`;
-   no out-of-scope feature was introduced.

------------------------------------------------------------------------

## 155. Add Place QA

The Add Place flow fails review if:

-   a name is required;
-   a note is required;
-   a photo is required;
-   an address is required;
-   poor accuracy blocks the user without choice;
-   map failure blocks saving a valid GPS position;
-   creating a category loses the draft;
-   retrying save loses the draft;
-   repeated taps can create duplicate places;
-   the user is forced through monetization/account/cloud behavior.

------------------------------------------------------------------------

## 156. Category QA

Category flows fail review if:

-   custom categories are treated as a fixed enum;
-   system and custom category rendering use incompatible models;
-   a created category is not available immediately;
-   Add Place loses its draft during category creation;
-   deleting a category can delete places;
-   system categories expose unsupported deletion;
-   reassignment can leave a partially successful visible state.

------------------------------------------------------------------------

## 157. Offline QA

Offline flows fail review if:

-   local places disappear;
-   local categories become read-only solely due to no network;
-   valid GPS location cannot be saved merely because map tiles are
    absent;
-   distance/direction are treated as network-only calculations;
-   `Carte indisponible` is presented as `Lieu indisponible`.

------------------------------------------------------------------------

# DOCUMENT RELATIONSHIPS

## 158. Relationship with Screen Catalog

`DESIGN/04_SCREEN_CATALOG.md` owns:

> What screens/states/surfaces exist?

`DESIGN/05_USER_FLOWS.md` owns:

> How does the user move through them?

If this document requires a genuinely new destination, update the Screen
Catalog rather than silently inventing an ID.

------------------------------------------------------------------------

## 159. Relationship with Figma Build Rules

`DESIGN/06_FIGMA_BUILD_RULES.md` will define:

-   how these flows are represented in Figma;
-   frame/component/variant construction rules;
-   variable use;
-   naming;
-   prototype linking;
-   responsive/autolayout discipline;
-   handoff expectations.

It should not redefine the product behavior documented here.

------------------------------------------------------------------------

## 160. Relationship with Screen Specifications

`DESIGN/07_SCREEN_SPECIFICATIONS.md` will define each screen in detail:

-   hierarchy;
-   content;
-   components;
-   states;
-   exact actions;
-   responsive behavior;
-   accessibility;
-   Light/Dark;
-   prototype notes.

It must implement the transitions and preservation rules defined here.

------------------------------------------------------------------------

## 161. Acceptance criteria

`DESIGN/05_USER_FLOWS.md` is accepted when:

-   the three reference journeys are explicit;
-   onboarding and permission branches are explicit;
-   the fast Add Place path remains minimal;
-   GPS failure and accuracy branches are covered;
-   no-map/offline saving is covered;
-   category creation during Add preserves state;
-   save failure preserves state;
-   map and list retrieval paths are covered;
-   external navigation is delegated;
-   edit/delete flows preserve integrity;
-   category deletion requires safe reassignment;
-   settings/privacy/about are traceable;
-   open product decisions are marked rather than invented;
-   monetization/paywall is outside the MVP flow set and future rules defer to `WHERIS_BUSINESS_REFERENCE.md`;
-   accessibility/offline/error resilience are cross-cutting
    requirements;
-   Figma prototype requirements are explicit.

------------------------------------------------------------------------

## 162. Next checkpoint

After validation, proceed to:

> **`DESIGN/06_FIGMA_BUILD_RULES.md`**

The next document will convert the foundations, tokens, components,
screen catalog and user flows into strict rules for building the actual
Wheris Figma file.

------------------------------------------------------------------------

## 163. Final flow principle

A Wheris flow should feel shorter than its technical implementation.

The user should not have to understand:

-   location providers;
-   Room;
-   Mapbox;
-   network state;
-   category persistence;
-   Android intents.

They should understand only what matters:

> **Je suis ici. Je veux m'en souvenir. Wheris l'enregistre.**

And later:

> **Je veux le retrouver. Wheris me le montre.**
