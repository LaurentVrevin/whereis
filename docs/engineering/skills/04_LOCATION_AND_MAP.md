# WHERIS --- LOCATION & MAP PLAYBOOK

**Status:** Canonical operational playbook\
**Owns:** Foreground location acquisition and map-provider integration
while preserving provider isolation, privacy, battery discipline and
offline resilience.

------------------------------------------------------------------------

## 1. Mission

Make geographic interaction reliable without letting GPS or map-provider
behavior become Wheris's source of truth.

This playbook replaces the former Location and Map skills.

Core principle:

> The saved place is the important data. The map is a representation.
> Current location is a user-requested capability, not a permanent
> tracking stream.

------------------------------------------------------------------------

## 2. When to use

Use for:

-   current-location acquisition;
-   foreground location lifecycle;
-   location permission flow behavior;
-   GPS/location-service disabled states;
-   timeout/no-fix/technical errors;
-   stale fixes;
-   accuracy evaluation/presentation inputs;
-   altitude/timestamp handling;
-   Mapbox map rendering;
-   map styles;
-   camera behavior;
-   initial camera centering/zoom;
-   user-location representation;
-   saved-place markers;
-   marker selection and empty-map deselection;
-   draft marker drag during add-place confirmation;
-   map errors/unavailable states;
-   attribution/licensing;
-   provider token handling;
-   Mapbox provider/API updates (with dependency playbook).

------------------------------------------------------------------------

## 3. Required sources

Always consult:

-   `docs/product/WHERIS_MASTER.md`;
-   `docs/product/RULES.md`;
-   `docs/product/SECURITY_PRIVACY.md`.

For visible behavior consult:

-   `docs/design/01_DESIGN_FOUNDATIONS.md`;
-   `docs/design/03_DESIGN_COMPONENTS.md`;
-   `docs/design/04_SCREEN_CATALOG.md`;
-   `docs/design/05_USER_FLOWS.md`;
-   `docs/design/07_SCREEN_SPECIFICATIONS.md`.

For current provider/platform behavior consult authoritative current
documentation when material:

-   Android location/permission docs;
-   Google Play Services Location docs;
-   Mapbox official docs, terms/pricing/licensing where relevant.

Do not rely on remembered SDK details when they can change.

------------------------------------------------------------------------

## 4. Entry checks

Before editing:

1.  Identify whether the task is location acquisition, map presentation,
    or both.
2.  Inspect `:core:location`, `:core:map` and their neutral
    interfaces/models.
3.  Identify lifecycle owner and cancellation behavior.
4.  Identify all user-visible error/fallback states.
5.  Identify sensitive data leaving the device, if any.
6.  Confirm whether map/network availability is being incorrectly
    coupled to GPS availability.
7.  Confirm whether the task would introduce background behavior; if so,
    stop and review scope/security because background location is not
    part of the current MVP.

------------------------------------------------------------------------

## 5. Location acquisition procedure

### 5.1 User intent

Every acquisition must have a clear foreground user purpose.

Examples:

-   save my current position;
-   show my current distance/direction to a saved place;
-   recenter map when requested.

Do not create continuous history merely because location APIs support
it.

### 5.2 Provider isolation

Keep Android/Play Services implementation inside the location
infrastructure boundary.

Map provider objects to neutral Wheris concepts such as `UserLocation` /
`GeoPoint`.

No `FusedLocationProviderClient` or Android `Location` in
domain/ViewModel/general feature contracts.

### 5.3 Acquisition strategy

Prefer bounded current-location acquisition appropriate to the task.

Use temporary updates only when necessary to improve a fix.

Stop updates promptly when:

-   a usable result is accepted;
-   the user cancels/leaves;
-   timeout occurs;
-   lifecycle no longer requires acquisition.

### 5.4 State model

Preserve distinct reasons such as:

-   searching;
-   available/found;
-   permission required/denied;
-   location services disabled;
-   timeout;
-   unavailable/no usable fix;
-   stale candidate;
-   poor accuracy;
-   technical error.

Do not collapse GPS failure, map failure and Internet failure into one
generic state.

### 5.5 Accuracy

Do not fabricate accuracy.

Carry accuracy only when available.

Apply the domain/product-defined evaluation rule.

Poor accuracy must be represented honestly and preserve user agency
according to the approved flow.

### 5.6 Stale fixes

Never label an old/stale fix as fresh current position without
qualification.

If a stale fix is internally useful, preserve correct semantics at the
UI boundary.

### 5.7 Permission boundary

Runtime permission request belongs to the Android/UI boundary, not the
ViewModel/domain.

Denial does not delete or hide saved places.

Use least privilege.

### 5.8 Privacy/battery

-   No background location in the current MVP.
-   No hidden movement history.
-   No exact coordinates in production logs.
-   Do not keep location updates alive longer than needed.

------------------------------------------------------------------------

## 6. Map procedure

### 6.1 Provider boundary

Keep Mapbox implementation inside `:core:map` or the approved provider
boundary.

Expose Wheris-owned neutral models/callbacks.

No Mapbox types in:

-   domain;
-   repositories;
-   ViewModels;
-   generic feature state outside the map adapter boundary.

### 6.2 Source of truth

The map is never the canonical place store.

Place availability must not depend on:

-   tile load success;
-   map style success;
-   Mapbox token/network availability;
-   marker rendering success.

### 6.3 User vs saved-place representation

Current user position and saved places must remain visually/semantically
distinct.

A selected saved marker must have explicit selected state.

### 6.4 Categories/markers

Render marker style from neutral category icon/color data.

Do not create a fixed provider enum that becomes category identity.

### 6.5 Map failure

Provide the approved fallback.

A map error must communicate that saved-place data remains accessible.

Do not use a full-screen fatal error when list/detail/core local data
still works.

### 6.6 Camera

Camera behavior should follow user intent and avoid surprising jumps.

On `MAP_001`, once a usable current position becomes available after
entry, automatically center once at a useful local/neighborhood zoom
unless the user has already moved the camera. Do not leave the user on a
whole-Earth view when a usable current position exists. If current
position is unavailable but saved places exist, prefer a useful
saved-place framing. After deliberate user camera movement, automatic
updates must not fight the user; recenter remains explicit.

Keep provider camera APIs isolated.

### 6.7 Selection dismissal

Saved-marker selection is transient map state. When `SURF_MAP_001` is
open, a tap on an empty map area clears the selected `PinId` and
dismisses the quick detail. Tapping another saved marker replaces
selection. This must not mutate persisted place coordinates.

### 6.8 Draft position adjustment in ADD_001

The default add-place candidate is the detected current foreground
position. When the map is available, the user may intentionally
long-press and drag the proposed candidate marker before confirmation.

Requirements:

-   this gesture changes only the add-place draft `GeoPoint`;
-   it does not move the live user-location representation;
-   it does not enable post-save marker editing on `MAP_001`;
-   original GPS accuracy must not be reused as the accuracy of the
    manually chosen coordinate;
-   no extra permission, background location or provider-specific domain
    type is introduced;
-   map-unavailable save behavior remains valid using the detected
    position.

### 6.9 Style/theme

Keep Light/Dark/provider style configuration centralized and
replaceable.

Do not spread provider style IDs throughout features.

### 6.10 Attribution/licensing

Respect current provider attribution/licensing requirements.

Do not infer requirements from old screenshots or previous SDK versions.

### 6.11 Sensitive data minimization

Do not send place names, notes, photos or other sensitive content to the
map provider merely to render markers if neutral local rendering is
sufficient.

------------------------------------------------------------------------

## 7. Offline/degraded behavior matrix

Treat these dimensions independently:

  ---------------------------------------------------------------------------
  Network           GPS               Map                  Expected principle
  ----------------- ----------------- -------------------- ------------------
  Offline           Available         unavailable/cached   local save may
                                                           still work if
                                                           position is usable

  Online            unavailable       available            saved places/map
                                                           may still be
                                                           shown;
                                                           current-location
                                                           actions degrade

  Online            available         unavailable          geographic save
                                                           can continue
                                                           without map when
                                                           approved

  Offline           unavailable       unavailable          saved local
                                                           list/detail remain
                                                           accessible
  ---------------------------------------------------------------------------

Do not label all combinations as "offline error".

------------------------------------------------------------------------

## 8. Wheris invariants

1.  Saving a place must not require map rendering.
2.  GPS and Internet are independent concerns.
3.  Map failure must not destroy or hide canonical local data.
4.  No fake coordinate/accuracy/current-location success.
5.  No permanent tracking or hidden location history in MVP.
6.  Exact coordinates must not enter production logs/analytics.
7.  External navigation is user-initiated.
8.  Mapbox Navigation/Directions/Search and Google Maps SDK are not
    introduced unless product/architecture explicitly changes.
9.  Provider cost/lock-in changes must be surfaced before committing
    architecture.
10. Provider tokens/secrets follow security/release rules.

------------------------------------------------------------------------

## 9. Cross-domain triggers

Load `docs/engineering/skills/01_FEATURE_AND_DOMAIN.md` when:

-   domain location model/state or feature orchestration changes.

Load `docs/engineering/skills/02_UI_AND_DESIGN.md` when:

-   location/map states or interactions change visually.

Load `docs/engineering/skills/03_DATA_AND_PERSISTENCE.md` when:

-   persisted geographic metadata/schema changes.

Load `docs/engineering/skills/05_QUALITY_AND_BUGFIX.md` when:

-   fixing provider/location regressions;
-   designing state-machine/fallback tests.

Load `docs/engineering/skills/06_DEPENDENCIES_AND_RELEASE.md` when:

-   Mapbox/Play Services/Android tooling versions or provider terms
    change.

Load `docs/engineering/skills/07_MONETIZATION_AND_CLOUD.md` only when:

-   advanced offline/cloud/map capabilities are commercially gated or
    network-backed.

------------------------------------------------------------------------

## 10. Testing & validation

### Location

Test deterministic orchestration for:

-   success;
-   timeout;
-   no fix;
-   permission state handling where abstracted;
-   location services disabled;
-   technical error;
-   poor/unknown accuracy;
-   stale fix;
-   cancellation/lifecycle stop.

### Map

Test neutral logic for:

-   marker mapping;
-   marker selection and empty-map deselection;
-   draft marker drag during add-place confirmation;
-   category visual inputs;
-   fallback/unavailable state;
-   no dependence of place data on map success.

Use fakes/neutral adapters where possible instead of real
provider/network dependence in unit tests.

Compile affected location/map/features and verify actual
stop/cancellation/provider configuration.

------------------------------------------------------------------------

## 11. Definition of done

Location/map work is done when:

-   provider types remain isolated;
-   foreground acquisition is bounded;
-   location states are semantically accurate;
-   map failure degrades gracefully;
-   saved-place data remains usable independently;
-   privacy/battery constraints are preserved;
-   provider attribution/terms relevant to the implementation are
    respected;
-   tests/build validation pass or limitations are reported.

------------------------------------------------------------------------

## 12. Never

Never:

-   add background tracking as a convenience;
-   log exact coordinates;
-   fabricate a position or accuracy;
-   keep temporary location updates running indefinitely;
-   make place persistence depend on Mapbox success;
-   leak Mapbox/Android Location types through domain contracts;
-   send sensitive place content to a provider without purpose;
-   introduce navigation/search provider SDKs outside approved scope;
-   claim provider terms/pricing/API behavior from stale knowledge when
    consequential.
