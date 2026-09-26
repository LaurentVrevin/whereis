# WHERIS --- 04 SCREEN CATALOG

> Canonical screen and UI-state inventory for Wheris.
>
> This document establishes **what screens exist**, why they exist,
> their stable IDs, their ownership and their meaningful states.
>
> It is intentionally less detailed than
> `docs/design/07_SCREEN_SPECIFICATIONS.md`. It does not prescribe every
> spacing value or complete layout. Its purpose is to prevent duplicate
> screens, missing states and inconsistent naming before user flows and
> detailed specifications are produced.
>
> Future monetization surfaces, when referenced here, derive their
> commercial semantics from
> `docs/reference/WHERIS_BUSINESS_REFERENCE.md` and remain outside the
> active MVP until explicitly activated.

------------------------------------------------------------------------

## 1. Catalog principles

Every full-screen destination receives a stable `SCREEN_ID`.

Transient UI such as dialogs, sheets and embedded states receives a
stable `SURFACE_ID` or `STATE_ID` when it is important enough to specify
independently.

A visual state does **not** automatically become a navigation
destination.

Examples:

-   GPS searching is usually a state of the Add Place location screen,
    not a separate route.
-   Poor GPS accuracy is a state/decision surface, not necessarily a
    separate route.
-   Marker quick detail is a bottom sheet, not a full screen.
-   Delete confirmation is a dialog/sheet, not a navigation destination.
-   Offline/map unavailable can be embedded states of screens rather
    than duplicate screens.

------------------------------------------------------------------------

## 2. ID convention

Screen IDs use:

`<AREA>_<NNN>`

Examples:

-   `ONB_001`
-   `MAP_001`
-   `ADD_001`
-   `PLACES_001`
-   `CAT_001`

Transient surfaces use:

`SURF_<AREA>_<NNN>`

States use:

`STATE_<AREA>_<NNN>`

IDs are stable references for design documentation, Figma frames,
prototypes and implementation discussions.

User-facing labels do not need to expose these IDs.

------------------------------------------------------------------------

## 3. Canonical product areas

The catalog is divided into:

1.  Launch
2.  Onboarding & permission
3.  Map / Carte
4.  Add Place
5.  Places / Lieux
6.  Place Detail
7.  Categories
8.  External Navigation
9.  Settings / Plus
10. Future monetization presentation
11. Cross-cutting transient surfaces and states

------------------------------------------------------------------------

# LAUNCH

## 4. LAUNCH_001 --- Splash

**Canonical name:** Splash\
**Type:** Full screen\
**Purpose:** Brief brand entry while the app resolves startup state.

### Core content

-   Wheris identity/logo;
-   warm brand background or approved splash treatment.

### Exit conditions

Routes to the appropriate first destination based on
onboarding/application state.

### Notes

Do not turn splash into a long marketing screen. Android platform splash
behavior should be respected in implementation.

------------------------------------------------------------------------

# ONBOARDING & PERMISSION

## 5. ONB_001 --- Onboarding 1 / Save quickly

**Purpose:** Introduce the core save promise.

**Primary message:** `Enregistre un lieu en quelques secondes`

**Core visual:** Simple Wheris illustration.

**Primary action:** Continue.

------------------------------------------------------------------------

## 6. ONB_002 --- Onboarding 2 / Personal map

**Purpose:** Explain that saved places form the user's personal
geographic memory.

**Primary message:** `Retrouve tous tes lieux`

**Primary action:** Continue.

------------------------------------------------------------------------

## 7. ONB_003 --- Onboarding 3 / Return easily

**Purpose:** Complete the value proposition.

**Primary message:** `Reviens-y facilement`

**Primary action:** Continue toward permission education.

------------------------------------------------------------------------

## 8. PERM_001 --- Location permission education

**Purpose:** Explain why foreground location improves Wheris before
triggering/continuing the Android permission flow.

### Core content

-   concise explanation;
-   location-oriented illustration/icon;
-   clear primary action.

### Important rule

This screen explains the permission; it is not the Android system
permission dialog.

------------------------------------------------------------------------

## 9. PERM_002 --- Permission denied recovery

**Purpose:** Explain what the user can do after denying location
permission.

### Possible states

-   denied but requestable again;
-   permanently denied / app settings required.

### Core behavior

Provide the appropriate recovery path without shaming the user.

### Data rule

Existing saved places remain available.

------------------------------------------------------------------------

# MAP / CARTE

## 10. MAP_001 --- Main Map / Carte

**Bottom navigation destination:** `Carte`

**Purpose:** Primary geographic workspace and fastest entry to
saving/retrieving places.

### Core content

-   full-bleed map when available;
-   current-position representation when available;
-   saved-place markers;
-   Add Place primary action;
-   bottom navigation;
-   map controls where justified.

### Embedded states

-   no saved places;
-   places available;
-   current location unavailable;
-   map loading;
-   map unavailable;
-   offline;
-   selected marker.

### Important rules

Map failure does not make saved places unavailable.

When a usable current position becomes available on entry, `MAP_001`
automatically centers once on it at a useful local zoom unless the user
has already moved the camera. Do not leave the opening experience on a
whole-Earth view when current position is usable. If current position is
unavailable but saved places exist, prefer a useful framing of saved
places.

When `SURF_MAP_001` is open, tapping an empty map area clears selection
and dismisses the quick detail. Tapping another saved marker replaces
selection.

------------------------------------------------------------------------

## 11. STATE_MAP_001 --- Map empty

**Owner:** `MAP_001`

**Purpose:** First-use/no-saved-place presentation.

### Core content

-   map/geographic context where available;
-   purpose-oriented empty message;
-   `Ajouter un lieu`.

Existing direction: `Quel lieu voulez-vous retrouver ?`

This is not a separate navigation destination.

------------------------------------------------------------------------

## 12. STATE_MAP_002 --- Map unavailable

**Owner:** `MAP_001`

**Purpose:** Continue exposing essential place/location functionality
when map rendering cannot be used.

### Must communicate

-   map representation is unavailable;
-   saved places are not lost;
-   useful non-map actions remain accessible.

------------------------------------------------------------------------

## 13. STATE_MAP_003 --- Offline map degradation

**Owner:** `MAP_001`

**Purpose:** Represent unavailable network/map content while retaining
local place data.

This state must not be conflated with GPS failure.

------------------------------------------------------------------------

## 14. SURF_MAP_001 --- Marker quick detail

**Type:** Bottom sheet\
**Owner:** `MAP_001`

**Trigger:** Tap a saved-place marker.

### Core content

-   category icon;
-   place name;
-   distance when available;
-   `Naviguer`;
-   `Détails`.

### UX target

Marker → `Naviguer` in two interactions.

### Dismissal

Tap an empty area of the map → clear selected marker → dismiss
`SURF_MAP_001`.

Tap another saved marker → replace selection and update `SURF_MAP_001`.

------------------------------------------------------------------------

## 3.1 Validation-slice priority

The catalog remains the inventory of the full approved MVP surface.
Build order is narrower: first validate the complete `save → retrieve`
loop using `MAP_001`, `ADD_001`, `ADD_002`, persistence, `ADD_004`,
`PLACES_001` / place detail, external navigation and map-unavailable
recovery.

Secondary screens remain canonical, but their existence must not delay
evidence that the North Star loop is fast, understandable and repeatedly
useful.

------------------------------------------------------------------------

# ADD PLACE

## 15. ADD_001 --- Acquire / Confirm Location

**Canonical user concept:** Add a place --- position

**Purpose:** Acquire the user's current foreground location and show
what point is about to be saved, while allowing deliberate manual
adjustment of the draft point before confirmation.

### Core content

-   visible map where available;
-   current location/search state;
-   location status card;
-   accuracy when available;
-   progression action when appropriate;
-   deliberate long-press + drag adjustment of the proposed position
    marker when the map is available.

### Embedded states

-   searching;
-   found;
-   poor accuracy;
-   timeout;
-   location disabled;
-   permission required/denied;
-   technical location error;
-   map unavailable while GPS remains usable;
-   manually adjusted position candidate.

This single screen replaces the need for multiple disconnected
`gps-searching`, `position-found`, `position-imprecise`, `gps-error`
full-screen routes.

Manual adjustment is optional and must never add a mandatory step to the
fast-save path. After the user drags the candidate marker, the UI must
make clear that the point was manually adjusted and must not reuse the
original GPS accuracy as if it described the new point.

------------------------------------------------------------------------

## 16. STATE_ADD_001 --- Searching for position

**Owner:** `ADD_001`

**Primary message:** `Recherche de votre position…`

### Behavior

Keep geographic context visible where possible. Show calm indeterminate
progress.

------------------------------------------------------------------------

## 17. STATE_ADD_002 --- Position found

**Owner:** `ADD_001`

**Primary message:** `Position détectée`

### Supporting content

Accuracy such as: `Précision : ± 5 m`

### Primary action

Continue to category selection.

------------------------------------------------------------------------

## 18. STATE_ADD_003 --- Poor accuracy

**Owner:** `ADD_001`

**Primary message:** `Précision faible`

### Actions

-   `Attendre une meilleure position`
-   `Continuer quand même`

The state informs rather than blocks automatically.

------------------------------------------------------------------------

## 19. STATE_ADD_004 --- Location services disabled

**Owner:** `ADD_001`

**Purpose:** Explain that device location services are disabled and
provide the appropriate recovery path.

Do not present this as missing network connectivity.

------------------------------------------------------------------------

## 20. STATE_ADD_005 --- Location timeout / no fix

**Owner:** `ADD_001`

**Purpose:** Explain that Wheris could not obtain a usable location in
the expected period.

### Actions

May include retry and safe exit according to flow specification.

Do not fabricate a position.

------------------------------------------------------------------------

## 21. STATE_ADD_006 --- Location technical error

**Owner:** `ADD_001`

**Purpose:** Recover from an unexpected location-provider failure.

Use calm actionable error treatment.

------------------------------------------------------------------------

## 22. STATE_ADD_007 --- Map unavailable during save

**Owner:** `ADD_001`

**Purpose:** Allow location acquisition/confirmation to continue without
map rendering when reliable coordinate/location data remains available.

This embodies:
`Le lieu est la donnée essentielle. La carte est un enrichissement.`

------------------------------------------------------------------------

## 23. ADD_002 --- Select Category

**Purpose:** Choose what type of place is being saved.

**Primary prompt:** `Quel type de lieu enregistres-tu ?`

### Core content

-   system categories;
-   custom categories;
-   selected state;
-   `Créer une catégorie`.

### Scalability

The screen must support many categories without assuming all fit in one
fixed grid.

### Exit

After a category is selected:

-   primary `Enregistrer` → persist the place directly → `ADD_004` on
    success;
-   secondary `Ajouter des détails` → `ADD_003`;
-   `Créer une catégorie` → `CAT_002`, then return with the new category
    selected.

The optional-details screen is not mandatory in the ultra-fast path.

A direct-save loading/error state remains owned by `ADD_002`; it must
preserve the accepted position and selected category and allow retry
without forcing `ADD_003`.

------------------------------------------------------------------------

## 24. ADD_003 --- Place Details

**Purpose:** Add optional metadata before final save.

### Core fields/actions

-   name;
-   note;
-   local photo;
-   favorite;
-   save action.

### Rule

Optional details must not make the quick-save experience feel mandatory
or form-heavy.

------------------------------------------------------------------------

## 25. ADD_004 --- Place Saved

**Purpose:** Short success confirmation.

**Primary message:** `Lieu enregistré !`

### Actions

-   `Voir sur la carte`
-   `Terminer`

### Notes

Short success motion may be used. Do not create a long blocking
celebration.

------------------------------------------------------------------------

# PLACES / LIEUX

## 26. PLACES_001 --- My Places

**Bottom navigation destination:** `Lieux`

**Purpose:** Browse and retrieve all saved places.

### Core content

-   top bar/title;
-   search;
-   filters where useful;
-   place list;
-   favorite/category visual metadata;
-   bottom navigation.

### States

-   populated;
-   empty;
-   search results;
-   no search results;
-   active filters.

------------------------------------------------------------------------

## 27. STATE_PLACES_001 --- Places empty

**Owner:** `PLACES_001`

**Purpose:** Explain that no places have been saved and provide
`Ajouter un lieu`.

Do not create a separate route.

------------------------------------------------------------------------

## 28. STATE_PLACES_002 --- Search results

**Owner:** `PLACES_001`

**Purpose:** Show places matching the current query.

Search remains part of the library rather than a disconnected product
area unless future UX proves otherwise.

------------------------------------------------------------------------

## 29. STATE_PLACES_003 --- No search results

**Owner:** `PLACES_001`

**Purpose:** Explain that no saved place matches the query/filter and
provide a clear way to adjust/clear it.

Do not confuse this with having zero saved places.

------------------------------------------------------------------------

## 30. SURF_PLACES_001 --- Filters

**Type:** Inline chips, sheet or appropriate responsive surface; final
form specified later.

**Purpose:** Filter places by approved dimensions such as category and
favorites.

Do not expose speculative filter dimensions.

------------------------------------------------------------------------

# PLACE DETAIL

## 31. PLACE_001 --- Place Detail

**Purpose:** Inspect and act on one saved place.

### Core content

-   name;
-   category;
-   favorite;
-   map when available;
-   distance;
-   cardinal direction;
-   saved date;
-   accuracy;
-   altitude when available;
-   coordinates;
-   note;
-   local photo.

### Primary action

`Lancer la navigation`

### Secondary actions

-   `Modifier`
-   `Supprimer`

### States

-   complete data;
-   optional metadata absent;
-   current location unavailable;
-   map unavailable/offline.

------------------------------------------------------------------------

## 32. PLACE_002 --- Edit Place

**Purpose:** Modify an existing saved place.

### Editable content

-   name;
-   category;
-   note;
-   photo reference through approved local-photo flow;
-   favorite.

Editing/repositioning an already saved place should not be silently
invented unless explicitly added to product behavior.

### Actions

-   save;
-   cancel.

------------------------------------------------------------------------

## 33. SURF_PLACE_001 --- Delete Place Confirmation

**Type:** Destructive confirmation dialog/sheet.

**Purpose:** Confirm deletion of one saved place.

### Actions

-   `Annuler`
-   `Supprimer`

The consequence should be clear and concise.

------------------------------------------------------------------------

## 34. STATE_PLACE_001 --- Place map unavailable

**Owner:** `PLACE_001`

**Purpose:** Preserve place detail usefulness without map rendering.

Coordinates, note, photo and other local metadata remain accessible.

------------------------------------------------------------------------

# CATEGORIES

## 35. CAT_001 --- Category Management

**Entry:** `Plus` / settings-related area.

**Purpose:** Manage default and custom categories.

### Core content

-   system categories;
-   custom categories;
-   `Créer une catégorie`.

### System treatment

Subtle `Par défaut` badge.

### Rules

-   system categories cannot be deleted;
-   custom categories can be edited/deleted;
-   potentially many categories must scale.

------------------------------------------------------------------------

## 36. CAT_002 --- New Category

**Purpose:** Create a custom category.

### Core content

-   name field;
-   icon selector;
-   color selector;
-   live preview.

### Actions

-   `Créer`
-   `Annuler`

### Entry contexts

-   category management;
-   Add Place category selection.

### Special Add Place behavior

After creation, return to the interrupted save flow and auto-select the
new category.

------------------------------------------------------------------------

## 37. CAT_003 --- Edit Category

**Purpose:** Edit a custom category.

### Editable

-   name;
-   icon;
-   accent color.

### Actions

-   save;
-   cancel;
-   delete through separate destructive flow where appropriate.

System categories do not use this screen for destructive editing unless
future product behavior explicitly allows limited customization.

------------------------------------------------------------------------

## 38. SURF_CAT_001 --- Delete Unused Category

**Type:** Confirmation dialog/sheet.

**Condition:** No saved place currently references the custom category.

### Actions

-   cancel;
-   delete category.

No place data is affected.

------------------------------------------------------------------------

## 39. SURF_CAT_002 --- Delete Used Category / Reassign

**Type:** Consequence-aware dialog/sheet or focused modal flow.

**Condition:** One or more places reference the custom category.

### Core content

-   explain that the category is used;
-   select a replacement category or use stable `Autre`;
-   explicitly preserve places.

### Rule

Deleting a category must never delete its places.

------------------------------------------------------------------------

## 40. SURF_CAT_003 --- Icon Selector

**Purpose:** Choose an icon from the curated Wheris category icon
catalog.

May be embedded directly in `CAT_002`/`CAT_003` or presented as a modal
surface depending on final component density.

It is not a standalone navigation destination by default.

------------------------------------------------------------------------

## 41. SURF_CAT_004 --- Color Selector

**Purpose:** Choose from the curated Wheris category accent palette.

Uses the approved category token palette.

It is not an arbitrary color picker in MVP.

------------------------------------------------------------------------

# EXTERNAL NAVIGATION

## 42. NAVEXT_001 --- Navigation Handoff

**Purpose:** Prepare/confirm delegation to an external compatible
navigation application when a dedicated Wheris surface is needed.

### Core content

-   destination identity;
-   geographic context;
-   compatible app choice when applicable.

### Important rule

Wheris does not render internal turn-by-turn navigation.

Depending on Android behavior, this may be a sheet/chooser rather than a
full-screen destination. The ID remains useful for documenting the
handoff experience.

------------------------------------------------------------------------

## 43. SURF_NAVEXT_001 --- Navigation App Chooser

**Type:** Chooser/dialog/sheet.

**Purpose:** Select an available compatible external navigation app if
Wheris owns the choice.

Potential apps may include compatible installed providers, but runtime
availability must be determined by implementation.

Do not hardcode an app as available merely because it exists in a
mockup.

------------------------------------------------------------------------

## 44. STATE_NAVEXT_001 --- No compatible navigation app

**Purpose:** Explain that no compatible navigation target is available
and preserve access to coordinates/place detail.

This is recoverable and should not affect saved data.

------------------------------------------------------------------------

# PLUS / SETTINGS

## 45. PLUS_001 --- Plus

**Bottom navigation destination:** `Plus`

**Purpose:** Entry point to secondary product areas.

### Terminology rule

`PLUS_001` is the navigation destination **Plus**. It is not the
commercial offer **Wheris Plus** defined in
`docs/reference/WHERIS_BUSINESS_REFERENCE.md`. Do not use plan
entitlement or purchase state to redefine this navigation ID.

### Potential entries

-   Categories;
-   Settings;
-   Confidentialité;
-   About.

Do not populate this screen with speculative future features or a
current MVP monetization entry.

------------------------------------------------------------------------

## 46. SETTINGS_001 --- Settings

**Purpose:** Configure existing Wheris preferences.

### Candidate MVP groups

-   appearance/theme;
-   preferred navigation app if implemented;
-   units if implemented;
-   category management entry;
-   information links as appropriate.

Only implemented settings should appear.

------------------------------------------------------------------------

## 51. ABOUT_001 --- About Wheris

**Purpose:** Explain the product, version and relevant legal/information
links.

### Product explanation

Wheris helps save and find meaningful places, from a parked car or
festival tent to a bivouac or viewpoint.

Keep product copy concise.

------------------------------------------------------------------------

# PRIVACY

## 51. PRIVACY_001 --- Confidentialité

**Purpose:** Explain Wheris's local-first privacy behavior accurately.

### Core content

-   no account required for the local MVP;
-   saved places/categories/notes/local photos are locally managed;
-   no permanent movement tracking;
-   foreground location is used for user-requested geographic actions;
-   map/external providers may have their own network behavior;
-   privacy wording must remain accurate relative to Android backup
    configuration.

### Rule

Use the product promise `Tes lieux restent sur ton téléphone.` only with
the qualification and implementation checks defined in
`docs/product/SECURITY_PRIVACY.md`. Do not claim that no data ever
leaves the device.

------------------------------------------------------------------------

# FUTURE MONETIZATION PRESENTATION --- OUTSIDE ACTIVE MVP

The legacy `PREMIUM_*` IDs are retained for reference stability. Their
IDs do not imply a Premium-only business model. Commercial truth belongs
to `docs/reference/WHERIS_BUSINESS_REFERENCE.md`.

## 52. PREMIUM_001 --- Plans & Upgrade

**Purpose:** Present approved monetization value when monetization UI is
in scope.

### Commercial roles

The future surface may compare or present the currently approved roles
of:

-   **Wheris Free** --- genuine discovery/core local use within the
    active free rule;
-   **Wheris Plus** --- one-time local upgrade, currently intended to
    unlock the approved local entitlement set;
-   **Wheris Premium** --- recurring-service offer for approved
    cloud/sync and other recurring-value capabilities.

Exact prices, billing periods, free limits and entitlement lists are not
owned by this catalog. They must be read from the active Business
Reference or product configuration when this future scope is
implemented.

### Terminology rule

Do not confuse **Wheris Plus** (commercial offer) with `PLUS_001`
(**Plus**, the bottom-navigation destination).

### Data/trust rule

The surface must not suggest that payment is required to retain access
to already-saved local places, nor that plan expiration deletes local
user data.

------------------------------------------------------------------------

## 53. PREMIUM_002 --- Free Limit Reached

**Purpose:** Explain the active free-place rule when the user attempts
an action that the approved business model does not permit at their
current entitlement level.

### Terminology

Use `lieux`, not `épingles`.

### Important rules

No numeric limit is canonical in this catalog. The active value is owned
by `docs/reference/WHERIS_BUSINESS_REFERENCE.md` / business
configuration.

Existing local places remain accessible. The state must explain what
action is limited and must not imply deletion, loss of access to
existing local places or loss of previous data. Future upgrade actions
may present Wheris Plus and/or Wheris Premium when those purchase flows
are explicitly in scope.

This screen remains conditional and outside the active MVP.

------------------------------------------------------------------------

# CROSS-CUTTING STATES

## 54. STATE_GLOBAL_001 --- Generic technical error

**Purpose:** Last-resort recoverable technical failure when no more
specific state applies.

Use only after subsystem-specific states are considered.

Never expose stack traces/provider jargon to users.

------------------------------------------------------------------------

## 55. STATE_GLOBAL_002 --- Offline

**Purpose:** Shared offline language/pattern.

This does not mean every screen becomes a dedicated Offline screen.

Screens should embed the offline pattern while preserving available
local functionality.

------------------------------------------------------------------------

## 56. STATE_GLOBAL_003 --- Loading

**Purpose:** Shared loading semantics.

Prefer contextual loading over unnecessary blank full-screen loaders.

------------------------------------------------------------------------

# SCREEN OWNERSHIP / ROUTING

## 57. Canonical navigation destinations

Expected navigation destinations:

``` text
Splash
Onboarding1
Onboarding2
Onboarding3
Permission
PermissionDenied
HomeMap
AddPinLocation
SelectPinCategory
AddPinDetails
PinCreated
Pins
PinDetail
EditPin
Categories
CreateCategory
EditCategory
Plus
Settings
About
Appearance
Units
PreferredNavigationApp
Privacy
```

Technical destination names may use `Pin` internally while UI copy uses
`lieu`.

Dialogs/sheets/states should not automatically become Navigation Compose
destinations.

------------------------------------------------------------------------

## 58. Screens that should NOT be separate routes by default

The following are primarily states/surfaces:

``` text
gps-searching
position-found
position-imprecise
gps-disabled
gps-error
map-unavailable
offline-state
pin-quick-detail
delete-confirmation
category-icon-selector
category-color-selector
navigation-app-chooser
```

This avoids fragmenting one coherent task into unnecessary routes.

------------------------------------------------------------------------

## 59. Mapping from existing Figma concepts

Existing visual concepts map canonically as follows:

  Existing concept                    Canonical owner
  ----------------------------------- --------------------------------------
  `screen-splash`                     `LAUNCH_001`
  `screen-onboarding-1`               `ONB_001`
  `onboarding-2`                      `ONB_002`
  `onboarding-3`                      `ONB_003`
  `screen-permission`                 `PERM_001`
  `screen-denied`                     `PERM_002`
  `map-empty` / `screen-home-empty`   `MAP_001` + `STATE_MAP_001`
  `map-with-pins`                     `MAP_001` populated
  `gps-searching`                     `ADD_001` + `STATE_ADD_001`
  `position-found`                    `ADD_001` + `STATE_ADD_002`
  `position-imprecise`                `ADD_001` + `STATE_ADD_003`
  `gps-disabled`                      `ADD_001` + `STATE_ADD_004`
  `gps-error`                         `ADD_001` + `STATE_ADD_006`
  `map-unavailable`                   owner screen + map-unavailable state
  `offline-state`                     owner screen + offline state
  `pin-type-selection`                `ADD_002`
  `add-details`                       `ADD_003`
  `pin-saved-success`                 `ADD_004`
  `pin-quick-detail`                  `SURF_MAP_001`
  `pin-full-details`                  `PLACE_001`
  `pin-list`                          `PLACES_001`
  `pin-list-empty`                    `PLACES_001` + `STATE_PLACES_001`
  `edit-pin`                          `PLACE_002`
  `delete-confirmation`               `SURF_PLACE_001`
  `external-navigation`               `NAVEXT_001` / handoff surface
  `settings`                          `SETTINGS_001`
  `about`                             `ABOUT_001`
  `premium`                           `PREMIUM_001`
  `free-limit`                        `PREMIUM_002`

This mapping preserves useful existing design work while cleaning up
screen/state ownership.

------------------------------------------------------------------------

# STATE MATRIX

## 60. Main Map state matrix

`MAP_001` should ultimately account for:

  --------------------------------------------------------------------------------
  State         Map                          Places      Current       Main action
                                                         location      
  ------------- ---------------------------- ----------- ------------- -----------
  First use     available/unavailable        none        optional      Add place

  Normal        available                    present     optional      Add place

  Selected      available                    present     optional      quick
  place                                                                detail

  Offline       cached/partial/unavailable   local       GPS may work  local
                                             present                   actions

  Map failure   unavailable                  local       may work      local
                                             present                   actions

  Location      available                    present     unavailable   map/place
  unavailable                                                          actions
                                                                       remain
  --------------------------------------------------------------------------------

------------------------------------------------------------------------

## 61. Add Location state matrix

`ADD_001` should account for:

  ------------------------------------------------------------------------
  State           Location        Map                     User decision
  --------------- --------------- ----------------------- ----------------
  Searching       pending         available/unavailable   wait/cancel

  Found           usable          available/unavailable   continue

  Poor accuracy   usable but weak available/unavailable   wait/continue

  Services        unavailable     may load                enable/recover
  disabled                                                

  Permission      unavailable     may load                permission flow
  missing                                                 

  Timeout         unavailable     available/unavailable   retry/exit

  Technical error unavailable     available/unavailable   retry/exit
  ------------------------------------------------------------------------

------------------------------------------------------------------------

## 62. Place Detail state matrix

`PLACE_001` should support:

-   full metadata;
-   no name/custom optional fields;
-   no photo;
-   no note;
-   no altitude;
-   current location unavailable, therefore distance/direction
    unavailable;
-   map unavailable;
-   offline;
-   favorite/non-favorite.

Absence of optional data must not make the screen look broken.

------------------------------------------------------------------------

## 63. Category management state matrix

`CAT_001` should support:

-   only system categories;
-   system + custom categories;
-   many custom categories;
-   category selected for edit;
-   custom category unused;
-   custom category used by places;
-   long custom names.

System categories remain protected from deletion.

------------------------------------------------------------------------

# CONTENT / TERMINOLOGY

## 64. Canonical French labels

Preferred product labels include:

``` text
Carte
Lieux
Plus
Ajouter un lieu
Mes lieux
Type de lieu
Créer une catégorie
Nouvelle catégorie
Modifier la catégorie
Lieu enregistré !
Voir sur la carte
Terminer
Naviguer
Détails
Lancer la navigation
Modifier
Supprimer
Annuler
Par défaut
Position détectée
Précision faible
Attendre une meilleure position
Continuer quand même
```

Final screen copy belongs to detailed specifications/resources, but
terminology must remain consistent.

------------------------------------------------------------------------

# CONDITIONAL / FUTURE SCREENS

## 65. Not part of V1 catalog

Do not design full production flows yet for:

-   account creation/login;
-   cloud sync;
-   collaboration/friends;
-   sharing system;
-   collections;
-   advanced statistics;
-   internal turn-by-turn routing;
-   downloadable full offline map management;
-   widgets;
-   Wear OS;
-   iOS;
-   AI features.

If concept work is requested later, it should be clearly separated from
the V1 canonical catalog.

------------------------------------------------------------------------

## 66. Monetization conditionality

`PREMIUM_001` and `PREMIUM_002` are catalogued because future
monetization is anticipated and legacy references already exist. They
are **conditional** and outside the active MVP; they are not evidence
that billing belongs in the initial engineering foundation.

Their future content must remain synchronized with
`docs/reference/WHERIS_BUSINESS_REFERENCE.md`. A change to pricing, free
limits or entitlements is a business-reference change first, not a
Screen Catalog change first.

------------------------------------------------------------------------

# FIGMA ORGANIZATION

## 67. Recommended Figma frame naming

Use stable IDs:

``` text
LAUNCH_001 — Splash
ONB_001 — Onboarding — Save
ONB_002 — Onboarding — Places
ONB_003 — Onboarding — Return
PERM_001 — Location Permission
MAP_001 — Map
ADD_001 — Add Place — Location
ADD_002 — Add Place — Category
ADD_003 — Add Place — Details
ADD_004 — Add Place — Success
PLACES_001 — My Places
PLACE_001 — Place Detail
PLACE_002 — Edit Place
CAT_001 — Categories
CAT_002 — New Category
CAT_003 — Edit Category
PLUS_001 — Plus
SETTINGS_001 — Settings
SETTINGS_002 — Appearance
SETTINGS_003 — Units
SETTINGS_004 — Navigation App
PRIVACY_001 — Privacy
ABOUT_001 — About
```

States can use suffixes:

``` text
ADD_001 / State=Searching
ADD_001 / State=Found
ADD_001 / State=PoorAccuracy
MAP_001 / State=Empty
MAP_001 / State=Offline
```

Do not create unrelated frame names for every state.

------------------------------------------------------------------------

## 68. Light / Dark organization

Light and Dark are theme modes of the same canonical screens/components,
not different product screens.

Figma may organize dedicated presentation pages for review, but IDs
remain identical.

Example:

``` text
ADD_001 / Theme=Light / State=Found
ADD_001 / Theme=Dark / State=Found
```

------------------------------------------------------------------------

# CATALOG QA

## 69. Duplicate-screen rule

Before adding a new screen, ask:

1.  Is this genuinely a new navigation destination?
2.  Is it actually a state of an existing screen?
3.  Is it a dialog/sheet?
4.  Can an existing canonical screen own it?
5.  Does adding a route improve the user journey or merely mirror
    implementation state?

Prefer coherent task ownership over route proliferation.

------------------------------------------------------------------------

## 70. Missing-screen rule

A new screen is justified when it has: - a distinct user purpose; -
meaningful entry/exit conditions; - enough independent interaction/state
to warrant a destination; - no better existing owner.

Add it to this catalog before silently creating it in Figma or Compose.

------------------------------------------------------------------------

## 71. Screen catalog acceptance criteria

The catalog is accepted when:

-   every known MVP screen has a canonical owner;
-   existing Figma concepts map cleanly to canonical screens/states;
-   GPS states are consolidated;
-   offline/map failure are modeled as degradation rather than lost
    data;
-   custom category creation/edit/delete/reassignment is represented;
-   bottom navigation is consistently `Carte / Lieux / Plus`;
-   user-facing terminology uses `lieu`;
-   monetization/paywall surfaces are clearly marked as future and
    excluded from the active MVP;
-   speculative non-MVP screens are excluded;
-   Figma and Android can reference the same stable IDs.

------------------------------------------------------------------------

## 72. Next checkpoint

After this catalog is validated, proceed to:

> **`docs/design/05_USER_FLOWS.md`**

That document will connect these stable screen/surface IDs into explicit
user journeys, entry conditions, transitions, branches, recovery paths
and completion conditions.

------------------------------------------------------------------------

## 73. Final catalog principle

A screen catalog is not a gallery of mockups.

It is a map of product responsibilities.

Wheris should have as many screens as the user journey requires --- and
no more.

States belong to the screen that owns the task. Dialogs and sheets
remain transient when appropriate. Stable IDs allow Figma, product
documentation and Compose implementation to discuss exactly the same
interface.
