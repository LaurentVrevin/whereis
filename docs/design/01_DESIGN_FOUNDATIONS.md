# WHERIS --- 01 DESIGN FOUNDATIONS

> Design foundations for Wheris. This document defines the product's
> visual direction, UX principles, interaction philosophy, accessibility
> expectations, adaptive behavior and general design rules.
>
> It intentionally does not define exact token values, complete
> component specifications, individual screen contracts or commercial
> pricing/entitlements. Design details belong to `DESIGN/02_DESIGN_TOKENS.md`,
> `DESIGN/03_DESIGN_COMPONENTS.md`, `DESIGN/04_SCREEN_CATALOG.md`, `DESIGN/05_USER_FLOWS.md`,
> `DESIGN/06_FIGMA_BUILD_RULES.md` and `DESIGN/07_SCREEN_SPECIFICATIONS.md`.
> Monetization rules and business hypotheses belong to
> `WHERIS_BUSINESS_REFERENCE.md`.

------------------------------------------------------------------------

## 1. Product design definition

Wheris is a **personal geographic memory**. It lets a user save a
meaningful place or the position of an object so it can be found again
later: car, tent, bivouac, restaurant, fishing spot, photo spot,
viewpoint, hiking location, beach, meeting point or any user-defined
place.

Wheris is not primarily a navigation application, social map or
professional GIS. It is a personal map built from places the user wants
to remember.

## 2. Product promise

Fundamental promise:

> **Enregistre un lieu. Retrouve-le quand tu veux.**

Brand signature:

> **Wheris --- Ta mémoire géographique.**

Alternative expression:

> **Retrouve ce qui compte.**

The interface must make this promise feel immediate, understandable and
trustworthy.

## 3. North Star

The core loop is:

> « Je veux me souvenir de cet endroit. »\
> « C'est enregistré. »\
> Later: « Je veux retrouver cet endroit. »\
> « Le voilà. »

Every design decision must support this loop.

## 4. Primary UX invariant

> **Aucune évolution de Wheris ne doit rendre plus difficile l'action
> d'enregistrer immédiatement sa position.**

Saving a place must remain the shortest, clearest and most obvious
primary action. Sophistication comes after the essential save, not
before it. Optional metadata must not unnecessarily block saving.

A straightforward save should conceptually remain close to:

> Wheris → Ajouter un lieu → Catégorie → Enregistrer

Target: approximately **under 10 seconds** when location is available.
This target must never hide uncertainty such as poor GPS accuracy.

### Fast-save interaction contract

Once a usable position is confirmed and a category is selected, the primary action is **`Enregistrer`**. The optional-details screen is not a mandatory step in the ultra-fast path.

`Ajouter des détails` is a secondary branch for users who want to add a name, note, local photo or favorite state before saving. This branch must preserve the same draft and return to a saveable state without introducing required metadata.

Product validation should measure the real save path rather than infer simplicity from screen count: median and p90 save time when a usable location is available, save completion, and abandon/failure rate.

## 5. Design personality

Wheris should feel warm, modern, calm, trustworthy, outdoors-friendly,
simple, human and premium without being luxurious.

It should not feel technical, militaristic, surveillance-oriented, cold,
overloaded, gamified, childish, corporate or like a professional GIS.

## 6. Visual direction

The established direction uses warm cream/off-white backgrounds, Wheris
orange as the principal brand/action color, white or elevated surfaces,
warm dark-neutral text, rounded controls/cards, subtle elevation, simple
iconography, efficient generous spacing, clear hierarchy, strong primary
actions and restrained microinteractions.

References such as Airbnb, Linear, Notion, Apple Find My, Citymapper,
Material 3 and modern outdoor apps are directional only. Wheris must not
copy them.

## 7. Emotional objective

Geographic memory often begins with uncertainty: where did I park, where
is my tent, did the GPS save the correct point?

The interface should communicate:

-   I know what Wheris is doing.
-   I know whether my position was found.
-   I know whether accuracy is good or poor.
-   I know whether my place was saved.
-   I can still access it if the map fails.

Feedback must be visible, calm and unambiguous.

## 8. Local-first expression

> **Le lieu est la donnée essentielle. La carte est un enrichissement.**

Map or network failure must never make a saved place appear lost.
Without a map, Wheris should still expose useful local information such
as place name, category, coordinates, distance/direction when available,
accuracy, note and local photo.

A map-unavailable state is a degraded representation, not a lost-data
state.

## 9. Map where geography matters

When Wheris proposes saving the current position, the map should remain
visible where specified by the product flow.

The user should understand where Wheris believes they are, whether
acquisition is ongoing, whether accuracy is sufficient and which point
will be saved.

The map provides spatial reassurance without competing with the primary
action.

## 10. Information hierarchy

For saving, prioritize: 1. geographic position/state; 2. primary
progression/save action; 3. category; 4. optional details.

For retrieval: 1. place identity; 2. where it is; 3. distance/direction;
4. navigate; 5. additional details.

For management: 1. identity; 2. relevant status; 3. primary action; 4.
secondary actions; 5. separated destructive actions.

Do not give all information equal visual weight.

## 11. Progressive disclosure

Optional name, note, photo and favorite status enrich a place but must
not turn quick save into a heavy form. Advanced management belongs in
detail/edit/category/settings screens.

## 12. Primary actions

Every important screen should make its principal action immediately
understandable. Primary actions should use strong hierarchy, consistent
placement in related flows, explicit labels and reachable placement
where practical.

Orange is primarily a brand/action signal. Excessive orange reduces its
ability to communicate priority.

## 13. Secondary and destructive actions

Secondary actions must not compete with the main task. Destructive
actions must be visually distinct, difficult to trigger accidentally and
confirmed when the consequences require it.

Category deletion must explain reassignment when existing places use the
category. Destructive styling must not be used for ordinary
cancellation.

## 14. Navigation model

Principal bottom navigation:

-   **Carte** --- map, current location, saved markers, quick detail,
    add place.
-   **Lieux** --- saved-place library, search, filters, favorites and
    relevant retrieval tools.
-   **Plus** --- category management, settings, Confidentialité and About.
    `Plus` here is the navigation destination, not the future commercial
    offer **Wheris Plus**. Monetization presentation is future scope and
    must not appear in the active MVP navigation.

The bottom navigation should remain stable and simple.

## 15. Terminology

User-facing French prefers **lieu** rather than **épingle**:

-   `Ajouter un lieu`
-   `Mes lieux`
-   `Type de lieu`
-   `Lieu enregistré`
-   `Détails du lieu`

Internal technical names such as `Pin` may remain in code.

## 16. Categories as visual language

Categories are first-class visual identifiers. Wheris supports
default/system and user-created categories with stable identity, name,
icon and accent color.

Category visuals must work across selection, map markers, lists,
filters, details and management. The system must scale to potentially
many custom categories and must not assume all categories fit on one
screen.

## 17. System and custom categories

System categories should be immediately useful. Custom categories should
become equally first-class once created.

Management UI may distinguish system categories with a subtle
`Par défaut` treatment. Custom categories must not look inferior.

System identity must not depend on localized display labels.

## 18. Category creation

Creation should remain lightweight: name, icon, accent color and
real-time preview.

When launched during Add Place: 1. preserve the current save task; 2.
create the category; 3. auto-select it; 4. return to the save flow.

This should feel continuous, not like leaving the task.

## 19. Category iconography

Category icons must be simple, legible at small sizes, recognizable and
visually coherent. Identity must never rely on color alone.

Use icon + color + label where context permits. The icon catalog should
be curated and finite.

## 20. Map markers

Markers must support dynamic category icon/color data. Selected markers
must be perceptibly different without relying only on a subtle color
shift.

The current user position must be visually distinct from saved places.
Marker design must remain scalable when many places exist.

## 21. Quick retrieval

Target interaction:

> Marker → quick detail → Naviguer

Quick detail prioritizes category/icon, place name, distance when
available, `Naviguer` and `Détails`.

Full details should not be required merely to launch external
navigation.

## 22. Place detail

Full detail may expose name, category, distance, cardinal direction,
map, saved date, GPS accuracy, altitude, coordinates, note, photo and
favorite.

Primary actions: external navigation, edit and delete.

Coordinates and technical metadata are useful but should not visually
dominate normal use.

## 23. GPS states

Location is not binary. Design must support, where relevant:

-   searching;
-   position found;
-   poor accuracy;
-   permission required;
-   permission denied;
-   permanently denied/settings required;
-   location services disabled;
-   timeout/no location;
-   technical error.

Normal recoverable situations such as waiting for GPS should not use
unnecessarily alarming error treatment.

## 24. Accuracy communication

Accuracy must be understandable to non-technical users. A value such as
`Précision : ± 5 m` may be combined with qualitative states: excellent,
good, medium, poor, unknown.

Poor accuracy should offer meaningful choices such as
`Attendre une meilleure position` and `Continuer quand même`.

Exact thresholds and semantic colors belong to later specifications.

## 25. Offline and unavailable states

Offline is a normal Wheris condition. Distinguish network unavailable,
map unavailable, location unavailable, location services disabled and
technical error.

Offline UI should explain what remains available. Locally stored places
remain useful even when map tiles are unavailable.

## 26. Empty states

Empty states should explain the next useful action, not merely state
that nothing exists.

Existing direction:

> `Quel lieu voulez-vous retrouver ?`

with:

> `Ajouter un lieu`

Simple illustrations may support the message but must not overpower the
action.

## 27. Success feedback

Saving should produce immediate confidence, potentially through a short
check animation, `Lieu enregistré !`, `Voir sur la carte` and
`Terminer`.

Feedback must be brief and never delay frequent work with ceremonial
animation.

## 28. Motion

Motion should be short, purposeful, subtle and spatially understandable.
Appropriate uses include save confirmation, bottom-sheet expansion,
marker/category selection and state transitions.

Avoid decorative motion that slows repeated actions. Respect
reduced-motion expectations where applicable.

## 29. Cards and surfaces

Cards provide grouping, hierarchy or interaction affordance, especially
over map/background contexts. Do not turn every content block into a
floating card.

Use controlled rounding and subtle elevation. Exact values belong to
tokens.

## 30. Density and spacing

Wheris should feel breathable while remaining efficient for lists and
categories. Outdoor/mobile use favors clear separation, readable labels
and generous touch targets.

Spacing must be systematic, not uniformly large.

## 31. Typography

Typography prioritizes legibility and clear hierarchy between screen
titles, sections, place names, metadata, labels, actions and geographic
status.

Avoid decorative typography. Exact families/sizes/weights/line heights
belong to tokens.

## 32. Color philosophy

Orange communicates Wheris identity, primary action and selected
emphasis where appropriate. Neutral surfaces carry most of the UI.

Semantic colors communicate success, error/destructive and information.
Category accent colors form a controlled separate system.

Exact values and theme mappings belong to `DESIGN/02_DESIGN_TOKENS.md`.

## 33. Dark theme

Dark theme is first-class and must not be a simple inversion.
Deliberately define background, surfaces, text hierarchy, borders,
primary orange, semantic colors, map overlays, chips, cards, dialogs and
markers.

It must remain warm and calm while prioritizing contrast and geographic
readability.

## 34. Accessibility

Accessibility is foundational. Support adequate touch targets, contrast,
font scaling, screen-reader labels, semantic roles, understandable focus
order and accessible alternatives to map-only information.

Important actions must not rely exclusively on gestures. Decorative
assets should not create screen-reader noise.

## 35. No color-only meaning

Poor accuracy, selected category, favorite, error, selected marker and
system-category status must not rely only on color. Combine text, icon,
shape, weight, border or position as appropriate.

## 36. Touch ergonomics

Wheris may be used outdoors and one-handed. Primary controls should be
reachable and reliable. Small map controls still need accessible touch
targets.

Destructive actions need separation from frequent primary actions.
Bottom actions must respect system/navigation insets.

## 37. Adaptive layout

Designs must not depend on one screenshot size. Figma should express
constraints and adaptation.

Use flexible widths, system insets, scrolling when content grows, text
expansion and layouts that do not clip long content.

Phone is the primary MVP target. Foundations should avoid obvious
phone-size lock-in even if tablet/foldable optimization comes later.

## 38. User-generated content

Place names, notes and custom category names are unpredictable. Support
long/multiline text, empty optional fields, international characters and
large fonts.

Do not assume every category has a short French name. Dense contexts may
truncate, but full content must remain available appropriately.

## 39. Localization readiness

French is first, not necessarily forever. System labels must tolerate
expansion. Buttons must not depend on one short French label.

Custom names and notes are literal user content and must not be treated
as localization keys.

## 40. Privacy expression

Local-first privacy should be communicated clearly at relevant moments
without repetitive security banners.

Location permission, photos and future backup/cloud/sharing flows must
use wording consistent with actual implementation. Do not promise strict
device-only storage if backup/network behavior contradicts it.

## 41. Permission UX

Explain why Wheris needs a capability. Keep location education concise,
distinguish app explanation from Android's system dialog and provide a
path forward after denial.

Do not shame or pressure users into granting permission.

## 42. External navigation UX

Turn-by-turn navigation is delegated to external apps. The action must
be explicit. App selection, if needed, should remain simple.

The UI must not imply that external navigation is a native Wheris
screen. Handle the absence of a compatible app clearly.

## 43. Favorites

Favorite is a lightweight attribute. It should be recognizable, easy to
toggle where appropriate and usable in list/filter/detail contexts
without competing with primary save/navigation actions.

It must not rely on color alone.

## 44. Search and filters

As geographic memory grows, retrieval becomes more important.
Search/filter design must scale beyond a handful of places.

Filters should remain unobtrusive when unused. Category and favorite are
natural filtering dimensions; future dimensions require product
validation.

## 45. Scalability

The visual system must support a few places, dozens of places, many
custom categories and richer future metadata.

Solve scale with search, scrolling, hierarchy and filtering---not by
shrinking controls below comfortable sizes.

## 46. Future monetization presentation

Monetization is outside the active MVP design. Existing Premium/paywall
mockups are exploratory/future references only. Do not place a free-limit,
Wheris Plus, Wheris Premium, purchase, subscription or paywall surface in
canonical MVP flows, navigation or handoff.

`WHERIS_BUSINESS_REFERENCE.md` is the canonical source for the commercial
model, including the distinction between **Wheris Free**, the one-time
local upgrade **Wheris Plus**, and the recurring-service offer **Wheris
Premium**. Prices, exact free limits, billing periods and entitlement
details must not be duplicated as immutable design constants.

The navigation destination `Plus` is unrelated to entitlement state and
must not be visually or semantically conflated with the commercial offer
`Wheris Plus`.

When monetization is activated, the UI must preserve the business
guardrails defined by the Business Reference: demonstrate value before a
blocking paywall, never imply that existing user places will be deleted
because of plan status, preserve access to already-saved local places,
and keep cloud/network-dependent value explicitly separate from the
local-first core.

Business hypotheses may be represented in future exploration only when
clearly marked as hypotheses and sourced from the Business Reference.

## 47. Figma-to-Compose parity

Figma and Compose should share the same conceptual naming system.

Examples:

-   design color token ↔ Wheris semantic color token;
-   spacing token ↔ `WherisSpacing`;
-   shape token ↔ `WherisShapes`;
-   Figma component ↔ Wheris Compose component.

The objective is not pixel-to-code generation; it is translation of a
shared design system.

## 48. Sources of truth

`WHERIS_MASTER.md` is the product source of truth.

`WHERIS_BUSINESS_REFERENCE.md` is the source of truth for monetization
architecture, plan roles, entitlements, business rules and the status of
commercial hypotheses. Design documentation may reference those decisions
but must not silently redefine them.

The design documentation is the **semantic source of truth** for UI
structure and behavior.

Figma is the **visual source of truth** for validated appearance.

If they diverge materially: 1. identify the contradiction; 2. determine
which source changed; 3. update the appropriate specification; 4. never
allow silent drift.

Compose must respect both.

## 49. Checkpoint discipline

Build Wheris design progressively:

1.  Foundations
2.  Tokens
3.  Atomic components
4.  Complex/domain components
5.  Screens by user flow
6.  Prototype/navigation
7.  Design QA

Later checkpoints must not casually redefine validated earlier
foundations.

If a screen reveals a missing token/component, update the appropriate
source rather than inventing a one-off value locally.

## 50. Screens by user flow

Design screens in journeys, not as unrelated screenshots.

Expected flow families:

-   onboarding and permission;
-   add a place;
-   create category during place creation;
-   map and quick retrieval;
-   place library;
-   place detail;
-   edit/delete place;
-   category management;
-   external navigation;
-   GPS/network/map failure states;
-   settings and About.

Validate each flow coherently.

## 51. State completeness

A screen is not complete merely because its default state exists. Where
relevant consider loading, content, empty, selected, disabled, success,
error, offline, permission denied, map unavailable, GPS unavailable and
poor accuracy.

Do not generate irrelevant states mechanically.

## 52. Figma component philosophy

Reusable elements should become Figma components. Variants represent
meaningful states, not every arbitrary property combination.

Screens use instances. Validated instances should not be detached simply
to make one screen easier to modify.

Detailed construction rules belong to `DESIGN/06_FIGMA_BUILD_RULES.md`.

## 53. Auto Layout philosophy

Layouts should express relationships rather than screenshot coordinates.
Use Auto Layout for applicable containers so longer labels, variable
content, different heights and variants remain robust.

Reserve absolute positioning for genuinely spatial/layered UI such as
map overlays.

## 54. Content design

Copy should be concise, concrete, reassuring, action-oriented and
non-technical.

Prefer `Position détectée` over provider jargon and
`Attendre une meilleure position` over unexplained GPS diagnostics.

Technical values can be shown when useful, framed for ordinary users.

## 55. Error tone

Errors should be calm and actionable. A good state answers: 1. What
happened? 2. What can I still do? 3. What can I do next?

Map failure must never imply saved places are gone.

## 56. Illustrations

Illustrations may support onboarding, empty states, success, permission
education and unavailable states.

They should be simple, warm and secondary, never creating a separate
cartoon identity or overpowering functional UI.

## 57. Icon consistency

General UI icons should use a coherent Android-compatible family or
approved Wheris set. Do not mix unrelated styles.

Category icons may be broader but need consistent visual weight and
legibility at Android UI sizes.

## 58. Borders and elevation

Use borders/elevation subtly. Avoid simultaneously using strong border,
heavy shadow and strong surface contrast for the same hierarchy.

Light and dark themes may use different strategies. Exact values belong
to tokens.

## 59. Loading

Loading feedback should match expected duration. During GPS acquisition,
show calm active progress while retaining geographic context where
appropriate.

Avoid blocking full-screen spinners when useful content can remain
visible. Never simulate progress values that are not real.

## 60. Confirmation

Confirmation should be proportional to risk. High-frequency reversible
actions should avoid excessive dialogs.

Destructive/data-impacting actions may require confirmation. Category
deletion requires consequence-aware reassignment handling.

Avoid confirmation fatigue.

## 61. Android alignment

Wheris is Android-first using Jetpack Compose and Material 3
foundations. Respect Android conventions for back behavior, system
bars/insets, permissions, touch, dialogs/sheets and accessibility.

Material 3 is a foundation, not Wheris's final brand identity.

## 62. Scope guard

Design must not invent unapproved mandatory accounts, social feeds,
friends, internal turn-by-turn navigation, AI assistants, advanced
statistics, collaboration, advanced offline-map management, Wear OS,
iOS-specific flows or monetization rules.

Pricing, free limits, plan names/roles and entitlements must come from
`WHERIS_BUSINESS_REFERENCE.md` when monetization scope is explicitly
activated. Design the approved MVP rather than silently expanding it.

## 63. Design QA

Before considering the system complete, review token consistency,
component reuse/variants, Auto Layout, naming, light/dark parity,
typography, touch targets, contrast, long content, state coverage, map
fallback, GPS states, category scalability, terminology, navigation
consistency, destructive-action safety and Figma/Compose parity.

Design QA is a checkpoint, not cosmetic cleanup.

## 64. Foundation acceptance criteria

Future decisions should be able to answer:

-   Does this preserve the fast-save North Star?
-   Does it feel like Wheris?
-   Is the primary action obvious?
-   Is geographic uncertainty communicated honestly?
-   Does it remain useful without map/network?
-   Can custom categories scale?
-   Is it accessible?
-   Does it support light/dark?
-   Can it translate cleanly to Compose?
-   Is complexity providing real user value?

If not, reconsider the design.

## 65. Final design principle

Wheris should make geographic memory feel effortless.

The user should not need to understand GPS, providers, persistence,
coordinates or offline infrastructure.

The interface should transform that complexity into three simple
experiences:

> **Je suis ici.**

> **Je veux m'en souvenir.**

> **Je peux le retrouver.**

That is the foundation of the Wheris design system.
