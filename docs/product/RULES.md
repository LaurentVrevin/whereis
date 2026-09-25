# WHERIS --- RULES

> Mandatory engineering rules for the Wheris AI development agent.
>
> This document defines constraints, boundaries, and non-negotiable
> conventions. It complements `AGENT.md`: `AGENT.md` defines the agent's
> role and mindset; this file defines what the agent MUST and MUST NOT
> do.

------------------------------------------------------------------------

## 1. Rule semantics

The keywords **MUST**, **MUST NOT**, **SHOULD**, **SHOULD NOT**, and
**MAY** are intentional.

-   **MUST / MUST NOT**: mandatory.
-   **SHOULD / SHOULD NOT**: default rule; deviation requires a concrete
    technical reason.
-   **MAY**: optional and context-dependent.

The agent MUST NOT silently violate a MUST or MUST NOT rule.

If a task appears to require violating one of these rules, the agent
MUST: 1. identify the conflict; 2. explain why the rule blocks or
affects the requested implementation; 3. propose the smallest safe
alternative; 4. obtain validation before introducing a structural
exception.

------------------------------------------------------------------------

## 2. Sources of truth

The agent MUST consult the relevant project references before making
consequential decisions.

Expected project references include:

-   `docs/product/WHERIS_MASTER.md` --- product, scope, architecture, domain and
    technical direction;
-   `docs/reference/WHERIS_BUSINESS_REFERENCE.md` --- canonical business model, commercial
    architecture, monetization rules, entitlements, business hypotheses,
    downgrade rules and business KPIs;
-   `AGENT.md` --- agent role and engineering mindset;
-   `docs/product/RULES.md` --- mandatory engineering constraints;
-   `docs/product/SECURITY_PRIVACY.md` --- security, privacy and sensitive-data
    requirements;
-   `docs/reference/USER_STORIES_REFERENCE.pdf` --- approved user needs, story-level scope and MVP/future-story boundary;
-   `docs/design/01_DESIGN_FOUNDATIONS.md` through `docs/design/07_SCREEN_SPECIFICATIONS.md` --- canonical design foundations, tokens, components, screens, flows, Figma build rules and screen contracts;
-   `docs/engineering/skills/00_SKILLS_INDEX.md` and `docs/engineering/skills/` --- operational routing and task-specific playbooks.

`docs/reference/WHERIS_BUSINESS_REFERENCE.md` owns commercial policy. Other documents MAY
reference its decisions, but MUST NOT create competing copies of prices,
free limits, entitlement definitions or downgrade semantics.

The agent MUST NOT duplicate a source of truth unnecessarily.

### Documentation layout invariant

Wheris-maintained design documentation MUST live under `docs/design/` and operational engineering playbooks MUST live under `docs/engineering/skills/`. The canonical routing files are `docs/design/00_DESIGN_INDEX.md` and `docs/engineering/skills/00_SKILLS_INDEX.md`.

Do not recreate a fragmented `skill-name/SKILL.md` tree for Wheris-maintained playbooks. Playbooks use descriptive, stable filenames directly under `docs/engineering/skills/`. If an external tool later requires a `SKILL.md` packaging convention, treat that as a generated adapter layer rather than the canonical documentation source.

When documentation and repository reality disagree materially, the agent
MUST identify the discrepancy rather than silently inventing a third
convention.

A direct instruction for the current task MAY refine the requested
behavior, but it MUST NOT silently override security, data-integrity or
destructive-operation safeguards.

------------------------------------------------------------------------

## 3. Scope discipline

The agent MUST implement the smallest coherent change that fully
satisfies the current task.

The agent MUST NOT: - implement roadmap features that were not
requested; - perform unrelated refactors; - rename unrelated APIs for
stylistic preference; - reorganize unrelated packages; - replace working
architecture without a concrete requirement; - add speculative
abstractions for hypothetical future needs; - change product behavior
outside the requested scope.

Necessary supporting changes are allowed when required for correctness,
compilation, testing, migration, accessibility or security.

When such supporting changes materially expand scope, the agent SHOULD
make that explicit.

------------------------------------------------------------------------

## 4. Repository inspection

Before modifying existing code, the agent MUST inspect the relevant
implementation.

It MUST identify, as applicable:

-   owning module;
-   package;
-   existing models;
-   existing interfaces;
-   existing repositories;
-   existing use cases;
-   existing Design System components;
-   navigation destinations;
-   dependency injection bindings;
-   persistence impact;
-   existing tests;
-   related conventions.

The agent MUST prefer appropriate reuse over duplicate implementation.

The agent MUST NOT assume that a documented class, module, API or
convention already exists without verifying it in the repository.

------------------------------------------------------------------------

## 5. Module architecture

The target module structure is defined by `docs/product/WHERIS_MASTER.md`.

Module boundaries MUST remain explicit.

### `:app`

`:app` MUST act primarily as the Android application composition root.

It MAY own: - application startup; - top-level dependency wiring; -
top-level navigation host; - application manifest integration; - build
configuration.

It MUST NOT become a dumping ground for business logic or reusable UI.

### `:domain`

`:domain` MUST remain Kotlin-pure.

It MAY contain: - repository contracts; - use cases; - business rules; -
domain services; - domain-specific validation.

It MUST NOT depend on: - Android framework; - Jetpack Compose; - Room; -
DataStore; - Mapbox; - Google Play Services; - Android `Context`; -
Android `Uri`; - Android `Location`.

### `:core:model`

`:core:model` MUST contain portable shared models where appropriate.

It MUST NOT expose framework-specific implementation types.

### `:data`

`:data` MAY implement domain repository contracts and coordinate data
sources.

It MUST NOT expose Room entities, DAO types or SDK-specific objects to
feature modules or domain APIs.

### `:core:database`

`:core:database` owns Room implementation details.

Room entities, DAOs, database classes and database-specific migrations
MUST remain within the persistence boundary.

### `:core:datastore`

`:core:datastore` owns preference persistence.

DataStore MUST be used for preferences, not for the primary
saved-place/category business database.

### `:core:location`

`:core:location` owns Android location-provider implementation details.

`FusedLocationProviderClient`, Android `Location` and provider-specific
implementation details MUST NOT leak into domain or feature APIs.

### `:core:map`

`:core:map` owns Mapbox implementation details.

Mapbox SDK types MUST NOT appear outside this module's implementation
boundary.

Other modules MUST interact with the map through Wheris-owned neutral
models and APIs.

### Feature modules

Feature modules own user-facing feature state and presentation.

A feature MUST NOT directly depend on another feature's internal
implementation.

Shared functionality MUST be promoted to an appropriate core/domain
layer rather than creating feature-to-feature coupling.

------------------------------------------------------------------------

## 6. Dependency direction

Dependencies MUST point toward stable abstractions.

The agent MUST avoid circular dependencies.

As a default:

-   features MAY depend on domain and appropriate core modules;
-   data MAY depend on domain and data-source core modules;
-   domain MUST NOT depend on data or features;
-   core modules MUST NOT depend on feature modules;
-   Design System MUST NOT depend on business features;
-   map/location/database SDK implementation types MUST remain isolated.

The agent MUST NOT bypass repository/domain boundaries simply because
direct access is faster to implement.

------------------------------------------------------------------------

## 7. Domain model purity

Domain and portable shared models MUST NOT contain:

-   `Context`;
-   `Activity`;
-   `Fragment`;
-   `NavController`;
-   Compose `Color`;
-   Compose `ImageVector`;
-   Android `Uri`;
-   Android `Location`;
-   Room annotations/entities;
-   Mapbox types;
-   Google Play Services types.

Coordinates MUST use a Wheris-owned neutral geographic model such as
`GeoPoint`.

Photos MUST use a neutral reference abstraction rather than exposing
Android `Uri` as domain state.

Category identity MUST NOT depend on a localized display label.

------------------------------------------------------------------------

## 8. Category rules

Categories are first-class Wheris data.

The agent MUST NOT model category identity as a fixed enum when the
model must support user-created categories.

System/default categories MUST have stable, non-localized identities.

Localized labels for system categories MUST be resolved at the
UI/resource boundary.

Custom category names are user data and MUST remain literal user-defined
values.

Deleting a category MUST NEVER delete its places.

If a custom category is deleted while places reference it, those places
MUST be reassigned transactionally to: - a valid user-selected
replacement; or - the stable system `Other` category according to the
product flow.

System categories that are defined as non-deletable MUST NOT be deleted
through normal application behavior.

The persistence model MUST NOT use a category-to-place cascade deletion
that can destroy places.

------------------------------------------------------------------------

## 9. Saved-place data integrity

A saved place is core user data.

The agent MUST prioritize preservation of saved places over
implementation convenience.

The agent MUST NOT: - silently discard a place; - silently overwrite a
place with unrelated data; - delete places as a side effect of category
deletion; - use destructive database migration as a normal production
migration strategy.

Changes affecting persisted place data MUST consider migration and
backward compatibility.

Identifiers MUST be stable.

Creation timestamps MUST NOT be silently rewritten during ordinary
edits.

Update timestamps SHOULD reflect actual modification.

------------------------------------------------------------------------

## 10. Room rules

Room is the persistence mechanism for saved places and categories in the
Android MVP.

Room entities MUST remain persistence models and MUST NOT become domain
models by convenience.

Mapping between persistence and domain representations SHOULD be
explicit.

Foreign-key behavior MUST preserve places.

Database migrations affecting released schemas MUST be explicit and
tested.

`fallbackToDestructiveMigration()` or equivalent destructive behavior
MUST NOT be introduced for production user data as a shortcut.

Default/system category seeding MUST be deterministic and idempotent.

Operations that reassign places and delete a category MUST execute
transactionally.

Database queries exposed as streams SHOULD use `Flow` when reactive
observation is appropriate.

Database work MUST NOT block the main thread.

------------------------------------------------------------------------

## 11. DataStore rules

DataStore MUST be reserved for preferences and lightweight application
settings.

Appropriate examples include:

-   onboarding completion;
-   theme preference;
-   unit preference;
-   preferred external navigation application;
-   non-sensitive feature preferences.

DataStore MUST NOT replace Room for: - saved places; - categories; -
relationships between places and categories; - structured business
history.

------------------------------------------------------------------------

## 12. Repository rules

Repository interfaces exposed to business logic MUST use domain models
and domain-oriented contracts.

Repositories MUST NOT expose: - DAO objects; - Room entities; - database
cursors; - Mapbox types; - Android `Location`; - `Context`.

A repository SHOULD abstract a meaningful data boundary.

The agent SHOULD NOT create repositories merely to wrap a single trivial
pure function.

Repository implementation details belong in the data/infrastructure
layer.

------------------------------------------------------------------------

## 13. Use case rules

A UseCase SHOULD exist when it provides at least one of:

-   business rule;
-   validation;
-   orchestration;
-   transaction-level intent;
-   reusable domain operation;
-   meaningful boundary between presentation and data.

The agent SHOULD NOT mechanically create one UseCase for every
repository method if it adds no semantic value.

UseCases MUST NOT depend on Android UI objects.

Business rules MUST NOT be hidden in Composables.

------------------------------------------------------------------------

## 14. ViewModel rules

ViewModels MUST represent presentation logic and UI state.

A ViewModel MUST NOT contain or depend directly on:

-   `Activity`;
-   `Fragment`;
-   `NavController`;
-   Mapbox SDK objects;
-   Room DAO;
-   Room entity as presentation architecture;
-   `FusedLocationProviderClient`;
-   permission-launcher APIs.

A ViewModel MUST NOT directly request Android runtime permissions.

Navigation and one-off platform interactions SHOULD be represented
through callbacks, route-level handling or explicit UI effects as
appropriate.

ViewModel state SHOULD be immutable from the UI's perspective.

`StateFlow` SHOULD be used for observable state where appropriate.

Long-running work MUST use structured coroutines.

The agent MUST avoid unmanaged coroutine scopes.

------------------------------------------------------------------------

## 15. Compose architecture

Each non-trivial screen SHOULD follow the project's Route / Screen
separation.

### Route

A Route MAY: - obtain the ViewModel; - collect UI state; - collect
one-off effects; - connect navigation callbacks; - connect
permission/system launchers; - adapt platform behavior to a stateless
Screen.

### Screen

A Screen MUST be UI-focused and SHOULD be stateless with respect to
business ownership.

A Screen MUST NOT: - resolve dependencies through Koin; - access Room; -
call repositories; - call DAOs; - request location directly; - use
Mapbox implementation APIs unless it is a map component owned by the map
boundary; - own a `NavController`; - perform business persistence.

Reusable Composables MUST accept a `Modifier` unless there is a strong
technical reason not to.

State SHOULD be hoisted.

Composable side effects MUST be intentional and lifecycle-aware.

------------------------------------------------------------------------

## 16. Design System rules

Wheris UI MUST use the project Design System when an appropriate
component or token exists.

The agent MUST NOT hardcode product styling repeatedly in feature
screens when the value belongs to the Design System.

This includes, where applicable:

-   colors;
-   typography;
-   shapes;
-   spacing;
-   elevation;
-   reusable buttons;
-   cards;
-   chips;
-   fields;
-   navigation components;
-   feedback states;
-   location indicators.

The agent MUST inspect existing Design System components before creating
a new one.

A new Design System component MUST be generic enough to belong there.

Business-specific logic MUST NOT be embedded in the Design System.

The Design System MUST NOT depend on: - Room; - Mapbox; - location
providers; - repositories; - feature modules.

------------------------------------------------------------------------

## 17. User-facing strings

User-facing business text MUST use Android string resources.

The agent MUST NOT hardcode production user-facing French strings
directly inside Composables or ViewModels.

French is the initial product language, but implementation MUST allow
future localization.

System category display names MUST use localization resources based on
stable system keys.

Custom category names MUST NOT be translated.

Formatting involving quantities, dates, distances or accessibility text
SHOULD be localization-aware.

------------------------------------------------------------------------

## 18. Map rules

Wheris uses the map as a geographic representation, not as the source of
truth for saved places.

Mapbox MUST remain isolated behind `:core:map`.

Mapbox SDK types MUST NOT leak into: - domain; - ViewModels; -
repository contracts; - general feature state.

The map API exposed to the application MUST use Wheris-owned neutral
models.

The agent MUST NOT introduce: - Mapbox Navigation; - Mapbox
Directions; - Mapbox Search; - Google Maps SDK;

unless the product architecture is explicitly changed and validated.

Map attribution and provider requirements MUST be respected.

A map failure MUST NOT make locally saved places inaccessible.

The application MUST have a meaningful non-map/fallback representation
for essential place information.

------------------------------------------------------------------------

## 19. Location rules

The Android implementation uses the platform/location provider through
the dedicated location boundary.

The agent MUST NOT request background location for the current MVP.

Location acquisition MUST be user-initiated or clearly connected to a
foreground product action.

The UI MUST represent relevant states rather than assuming location
always succeeds.

Relevant states include, as applicable:

-   loading;
-   available;
-   permission required;
-   permission denied;
-   permanently denied;
-   location disabled;
-   timeout;
-   no location available;
-   technical failure;
-   poor accuracy.

GPS/location accuracy MUST be treated as uncertain data, not as exact
truth.

Poor accuracy MUST NOT silently be presented as precise.

Permission handling belongs at the Android/UI boundary, not in pure
domain logic.

------------------------------------------------------------------------

## 20. Geographic calculations

Distance, bearing and cardinal-direction calculations SHOULD be
implemented as deterministic, testable Kotlin logic.

These calculations MUST NOT require Mapbox.

Where the project defines a canonical calculation, the agent MUST reuse
it instead of creating competing implementations.

Geographic calculations MUST include tests for representative and edge
cases.

Units and formatting MUST remain separate from raw geographic
calculation where practical.

------------------------------------------------------------------------

## 21. External navigation

Wheris MUST delegate turn-by-turn navigation to compatible external
applications for the current product scope.

The agent MUST NOT build an internal routing engine as part of ordinary
Wheris navigation work.

The launcher boundary SHOULD accept neutral destination data.

Android `Context` and Intent details MUST remain in the Android
implementation boundary.

The UI MUST handle the case where the preferred external navigation
application is unavailable.

------------------------------------------------------------------------

## 22. Photos

For the local-first MVP, photos MUST remain local unless a future
product requirement explicitly introduces upload/sync.

Room MUST NOT store photo bitmaps or large image BLOBs as place fields.

The database SHOULD store an application-owned neutral photo
reference/path/identifier.

Domain APIs MUST NOT expose Android `Uri` as the canonical photo model.

Photo lifecycle behavior MUST be explicit.

The agent MUST NOT silently delete a photo that may still be referenced.

The agent MUST NOT automatically upload a place photo to an external
service.

------------------------------------------------------------------------

## 23. Permissions

The agent MUST follow least-privilege principles.

Every new Android permission MUST have a concrete product and technical
justification.

The agent MUST NOT add a permission preemptively for a possible future
feature.

Location permissions MUST be requested only when needed by the relevant
flow.

The agent MUST avoid broad storage permissions when platform APIs or
application-controlled storage provide a safer alternative.

Before release-related work, manifest permissions MUST be reviewed.

Detailed sensitive-data requirements belong to `docs/product/SECURITY_PRIVACY.md` and
are mandatory.

------------------------------------------------------------------------

## 24. Security and privacy boundary

`docs/product/SECURITY_PRIVACY.md` is mandatory whenever a task affects:

-   coordinates;
-   location acquisition;
-   saved places;
-   notes;
-   photos;
-   backups;
-   logs;
-   analytics;
-   crash reporting;
-   network transmission;
-   secrets;
-   tokens;
-   application signing;
-   exported components;
-   cloud functionality.

Until a more specific security policy is defined, the agent MUST at
minimum:

-   never commit secrets;
-   never intentionally log exact user coordinates in production;
-   never transmit saved-place data without an explicit product
    requirement;
-   never introduce background location casually;
-   never weaken data preservation for convenience.

Security requirements MUST NOT be bypassed to make a feature easier to
implement.

------------------------------------------------------------------------

## 25. Secrets and configuration

Secrets MUST NOT be hardcoded in Kotlin, committed XML resources or
version-controlled configuration.

Secret values MUST use appropriate local/CI secret mechanisms.

Mapbox credentials MUST follow the distinction between runtime/public
tokens and secret/download credentials.

The agent MUST NOT print secrets in: - source code; - tests; - logs; -
documentation examples; - build output summaries.

Files containing local secrets MUST be excluded from version control as
appropriate.

Missing required configuration SHOULD fail clearly in development rather
than producing an unexplained runtime failure.

------------------------------------------------------------------------

## 26. Network and external services

The agent MUST NOT introduce a new external network service without
considering:

-   purpose;
-   transmitted data;
-   privacy impact;
-   offline behavior;
-   pricing;
-   quotas;
-   licensing;
-   reliability;
-   vendor lock-in.

If a new service materially affects cost, privacy or architecture, the
agent MUST surface that decision before treating it as settled.

Core place persistence MUST NOT become network-dependent without an
explicit product decision.

When monetization is explicitly activated, RevenueCat is the currently
approved target infrastructure for purchase and entitlement management as
defined by `docs/reference/WHERIS_BUSINESS_REFERENCE.md`. This approval is scoped to the
commercial infrastructure role only. It does not authorize RevenueCat to
become a general application backend, user-data store, analytics sink for
sensitive geographic content, or source of core place data.

RevenueCat integration MUST follow the same third-party review discipline as
any other network service, including transmitted identifiers, SDK behavior,
pricing, availability, privacy, vendor lock-in and future migration paths.

------------------------------------------------------------------------

## 27. Dependency management

Dependencies MUST be declared through the project's established
Gradle/Version Catalog strategy.

The agent MUST inspect existing dependencies before adding a new
library.

The agent MUST NOT: - declare Material 3 redundantly; - individually
version Compose libraries already governed by the Compose BOM; - add
multiple libraries solving the same problem without justification; -
invent dependency versions; - upgrade unrelated dependencies
opportunistically during feature work.

When current compatibility matters, versions MUST be verified against
authoritative documentation.

Kotlin, KSP, AGP, Compose, Room and other tightly coupled tooling MUST
remain compatible.

A dependency SHOULD be added only to the modules that require it.

KSP SHOULD be applied only where annotation processing/code generation
is actually needed.

------------------------------------------------------------------------

## 28. Koin rules

Dependency injection MUST occur at appropriate composition boundaries.

The agent MUST NOT use service-location patterns from arbitrary
Composables or domain objects.

Screens intended to be stateless MUST receive data and callbacks rather
than resolving repositories/services themselves.

Koin modules SHOULD be organized by clear infrastructure or feature
ownership.

Bindings MUST expose abstractions where abstraction is meaningful.

The agent SHOULD avoid creating unnecessary DI bindings for trivial pure
objects.

------------------------------------------------------------------------

## 29. Coroutines and Flow

Coroutines MUST use structured concurrency.

The agent MUST NOT use `GlobalScope`.

Blocking database/network/location operations MUST NOT execute on the
main thread.

Flows MUST NOT be collected indefinitely without lifecycle awareness in
Android UI.

Compose state collection SHOULD use lifecycle-aware APIs where
appropriate.

Exceptions in asynchronous work MUST be represented or handled
intentionally.

The agent MUST avoid swallowing coroutine exceptions silently.

Hot flows and sharing policies SHOULD be chosen deliberately rather than
copied mechanically.

------------------------------------------------------------------------

## 30. Navigation

Navigation destinations MUST remain explicit and owned by the navigation
architecture defined by the project.

ViewModels MUST NOT own `NavController`.

Screens SHOULD expose semantic callbacks such as:

-   `onBack`;
-   `onPlaceClick`;
-   `onNavigate`;
-   `onCreateCategory`;

rather than performing navigation directly.

Navigation arguments SHOULD use stable identifiers rather than passing
large mutable domain objects when retrieval by ID is appropriate.

One-off navigation events MUST not be accidentally replayed after
configuration/process state changes.

------------------------------------------------------------------------

## 31. Error handling

Errors MUST NOT be silently ignored when they affect user-visible
behavior or data integrity.

The agent SHOULD distinguish:

-   expected recoverable states;
-   validation errors;
-   unavailable platform capability;
-   temporary technical failure;
-   unexpected programming failure.

User-facing errors SHOULD provide an actionable recovery path where
possible.

Technical exceptions SHOULD NOT be exposed verbatim as user-facing
messages.

The agent MUST preserve user-entered data when a recoverable failure
occurs whenever practical.

------------------------------------------------------------------------

## 32. Offline behavior

Wheris MUST remain useful without network connectivity for its essential
local functionality.

The following SHOULD remain available offline when data is stored
locally:

-   saved place metadata;
-   categories;
-   notes;
-   local photo references/content where available;
-   coordinates;
-   distance/direction calculations based on available device location.

Missing map tiles/network access MUST NOT be interpreted as missing
saved places.

Offline and map-unavailable states MUST be represented deliberately.

------------------------------------------------------------------------

## 33. Accessibility

Accessibility is a product requirement, not optional polish.

Interactive targets MUST be appropriately sized.

Meaningful icons MUST have appropriate accessible labels when needed.

Category identity MUST NOT rely on color alone.

Text and controls MUST support reasonable font scaling.

Contrast MUST remain acceptable in both light and dark themes.

The agent MUST avoid replacing semantic controls with visually similar
but inaccessible custom implementations without justification.

Map-only information MUST have an accessible non-map representation for
essential actions/data.

------------------------------------------------------------------------

## 34. Light and dark themes

New reusable UI MUST be compatible with the Wheris light and dark
themes.

The agent MUST NOT implement dark mode as arbitrary color inversion.

Feature code SHOULD use semantic Design System colors rather than fixed
palette values.

Hardcoded colors in feature UI are prohibited unless explicitly required
for external content or a narrowly justified visual element.

------------------------------------------------------------------------

## 35. Performance and battery

The agent MUST consider performance and battery impact for location and
map work.

It MUST NOT: - continuously request high-accuracy location without a
product requirement; - keep location updates alive after the relevant
foreground need ends; - trigger unnecessary recompositions through
unstable state design; - repeatedly perform expensive geographic
calculations in composition without reason; - load full-resolution
photos unnecessarily for small UI previews.

Optimization SHOULD be evidence-driven, but obvious waste MUST be
avoided.

------------------------------------------------------------------------

## 36. Build configuration

Gradle configuration MUST remain centralized and understandable.

The agent SHOULD use:

-   Gradle Kotlin DSL;
-   Version Catalog aliases;
-   established project conventions.

The agent MUST NOT scatter dependency versions across module build files
when the project uses a catalog.

Build-time secrets MUST NOT be committed.

New build variants, flavors or plugins MUST have a concrete requirement.

The agent MUST avoid changing toolchain versions during unrelated tasks.

------------------------------------------------------------------------

## 37. Code quality

Production code MUST favor clarity over cleverness.

The agent SHOULD use descriptive domain terminology.

The agent MUST avoid ambiguous generic containers such as: - `Utils`; -
`Helper`; - `Manager`;

when a clearer responsibility-specific name is available.

Large classes SHOULD be decomposed by responsibility.

The agent MUST avoid: - duplicated business rules; - hidden mutable
global state; - magic constants scattered across features; - catch-all
repositories; - giant Composables; - giant ViewModels.

Comments SHOULD explain non-obvious intent or constraints, not restate
obvious code.

------------------------------------------------------------------------

## 38. API design

Public/internal module APIs SHOULD expose the minimum required surface.

Implementation details MUST remain private/internal where possible.

The agent MUST avoid leaking third-party SDK types through Wheris-owned
interfaces.

Function and model names SHOULD use Wheris domain language consistently.

Breaking changes to established project APIs MUST NOT be introduced
casually.

------------------------------------------------------------------------

## 39. Testing

A task is not considered technically complete merely because code was
generated.

Tests MUST be added or updated when the task introduces or changes
meaningful testable behavior.

High-priority test areas include:

-   business rules;
-   distance calculations;
-   bearing calculations;
-   cardinal directions;
-   accuracy evaluation;
-   category validation;
-   category deletion/reassignment;
-   Room mappings;
-   Room migrations;
-   repository behavior;
-   ViewModel state transitions;
-   save/edit/delete flows;
-   error states;
-   offline states;
-   regression bugs.

A bug fix SHOULD include a regression test whenever practical.

Tests MUST test behavior rather than implementation details where
possible.

The agent MUST NOT claim that tests pass unless they were actually
executed successfully.

------------------------------------------------------------------------

## 40. Build and validation

After code changes, the agent MUST perform the strongest practical
validation available for the affected scope.

Depending on the task, this includes:

-   compile affected module(s);
-   run affected unit tests;
-   run relevant instrumentation/Compose tests when available and
    appropriate;
-   run lint/static analysis when relevant;
-   inspect warnings/errors;
-   verify imports;
-   verify Gradle synchronization/configuration.

For structural Gradle work, the agent MUST validate the build
configuration rather than merely writing files.

The agent MUST NOT state: - "build passes"; - "tests pass"; - "lint
passes";

unless that specific validation was actually executed and succeeded.

If validation cannot be executed, the agent MUST say so clearly.

------------------------------------------------------------------------

## 41. Bug-fix rules

For a bug fix, the agent SHOULD:

1.  understand or reproduce the failure;
2.  identify the root cause;
3.  avoid symptom-only patches when the root cause is reasonably
    fixable;
4.  implement the smallest safe correction;
5.  add/update a regression test where practical;
6.  validate affected behavior.

The agent MUST NOT use broad unrelated refactoring to disguise
uncertainty about the bug.

------------------------------------------------------------------------

## 42. Migration rules

Any change to persisted schema or meaning of persisted data MUST
consider existing users.

Released Room schema changes MUST have a migration strategy.

Migration code MUST preserve user places unless explicit product
behavior requires deletion and that behavior has been validated.

Migration tests SHOULD cover representative previous schemas.

Seed changes MUST be idempotent and MUST NOT duplicate system
categories.

Changing a localized label MUST NOT require changing category identity.

------------------------------------------------------------------------

## 43. Git safety

The agent MUST preserve unrelated user work.

It MUST NOT:

-   run destructive reset operations without explicit authorization;
-   discard unrelated working-tree changes;
-   overwrite files wholesale when a targeted change is safer;
-   force-push;
-   rewrite shared history;
-   commit secrets;
-   commit generated local credentials;
-   remove code merely because it appears unused without verifying its
    role.

Changes SHOULD remain focused and reviewable.

If unrelated modifications are encountered, the agent MUST work around
them rather than erasing them.

------------------------------------------------------------------------

## 44. Generated code and files

Generated files MUST NOT be manually edited when they are expected to be
regenerated from a source definition.

The agent SHOULD modify the source of generation instead.

Build artifacts, IDE-local files, secrets and machine-specific
configuration MUST NOT be committed unless intentionally part of the
repository.

The agent MUST respect the repository's `.gitignore`.

------------------------------------------------------------------------

## 45. Documentation

Architecture-changing work MUST update the relevant documentation when
the existing documentation would otherwise become false.

The agent MUST NOT duplicate the same detailed rule across multiple
documents without a clear reason.

When adding a new permanent convention, it SHOULD be placed in the
document that owns that concern. In particular:

-   product/scope rules belong to `docs/product/WHERIS_MASTER.md`;
-   commercial architecture, prices, free limits, entitlements, downgrade
    semantics and business experiments belong to
    `docs/reference/WHERIS_BUSINESS_REFERENCE.md`;
-   security/privacy requirements belong to `docs/product/SECURITY_PRIVACY.md`;
-   UI semantics belong to the owning design document.

Temporary implementation notes MUST NOT silently become permanent
architecture.

A business hypothesis copied into implementation documentation MUST remain
clearly identified as a hypothesis when the Business Reference classifies it
as such.

------------------------------------------------------------------------

## 46. Figma and UI implementation

Figma is an approved UX/design reference, but the agent MUST implement
behavior consistent with Android platform constraints, accessibility and
the Wheris architecture.

The agent MUST NOT:

-   bypass the Design System to reproduce a screen quickly;
-   duplicate components for each screen when a reusable component
    already exists;
-   hardcode a layout solely to match one screenshot size;
-   omit loading/error/empty/offline states merely because a static
    mockup does not show them.

When Figma and product specifications conflict materially, the agent
MUST identify the conflict.

------------------------------------------------------------------------

## 47. Product terminology

User-facing French terminology MUST prefer **lieu** over **épingle**
where defined by the product specification.

Examples:

-   `Ajouter un lieu`
-   `Mes lieux`
-   `Type de lieu`
-   `Lieu enregistré`
-   `Détails du lieu`

Internal technical names such as `Pin`, `PinEntity`, `PinRepository` MAY
remain when they are established domain/code terminology.

The agent MUST NOT perform broad internal renaming solely to mirror
every user-facing wording change unless explicitly requested.

------------------------------------------------------------------------

## 48. Core Wheris UX invariant

No implementation MUST make the primary action of saving the user's
current place unnecessarily harder.

The agent MUST protect the core product invariant:

> Aucune évolution de Wheris ne doit rendre plus difficile l'action
> d'enregistrer immédiatement sa position.

Optional enrichment MUST come after or around the essential save action
rather than blocking it without necessity.

The map SHOULD remain visible during the current-position save
experience where specified by the product design.

The sophistication of Wheris MUST NOT be moved in front of the user's
immediate intent to save a place.

------------------------------------------------------------------------

## 49. MVP boundary

The agent MUST respect the current MVP scope defined by
`docs/product/WHERIS_MASTER.md`.

Unless explicitly requested as a new product decision, the agent MUST
NOT introduce as part of ordinary MVP implementation:

-   mandatory account creation;
-   cloud synchronization;
-   social sharing;
-   collaboration/friends;
-   internal turn-by-turn routing;
-   Mapbox Navigation;
-   downloadable advanced offline map infrastructure;
-   Wear OS;
-   iOS implementation;
-   AI features;
-   speculative backend infrastructure.

The architecture MAY leave reasonable extension points, but MUST NOT
build unused systems in advance.

The existence of `docs/reference/WHERIS_BUSINESS_REFERENCE.md` does not itself activate
monetization in the MVP. Paywalls, purchases, subscriptions, purchase
restoration, entitlement backends, accounts and cloud services remain outside
the active MVP until explicitly brought into scope.

------------------------------------------------------------------------

## 50. Monetization boundary

`docs/reference/WHERIS_BUSINESS_REFERENCE.md` is the canonical source for Wheris
commercial policy. The current reference architecture is
**Free → Plus → Premium**, but the implementation MUST respect the decision
status assigned by that document.

A value classified as **HYPOTHÈSE BUSINESS** or **CANDIDAT** MUST NOT be
treated as an immutable product or architecture constant merely because it
appears in documentation, a mockup or a financial model. This applies in
particular to:

-   exact Free limits;
-   Plus purchase price;
-   Premium subscription price or period;
-   upgrade pricing;
-   upsell timing;
-   candidate Plus/Premium benefits;
-   cloud retention after subscription expiry.

When monetization is explicitly activated, business rules SHOULD be modeled
behind clear entitlement/policy boundaries so approved experiments can change
commercial parameters without corrupting the saved-place domain or requiring
destructive migrations.

### RevenueCat boundary

RevenueCat is the currently approved target purchase/entitlement
infrastructure when the monetization phase is explicitly activated.
Google Play remains the Android store/payment system; RevenueCat coordinates
purchase state and entitlement interpretation for Wheris.

RevenueCat MUST remain behind a Wheris-owned abstraction such as an
`EntitlementRepository`, purchase service or equivalent infrastructure
boundary. Domain and feature code MUST NOT depend directly on:

-   RevenueCat SDK classes;
-   `CustomerInfo` or equivalent provider response types;
-   RevenueCat `Offering`, `Package` or product objects;
-   Google Play product IDs / SKUs;
-   RevenueCat entitlement, offering or package identifiers as business-domain
    types.

The application SHOULD reason in Wheris concepts such as Free, Plus, Premium
and approved capability access. Provider-specific IDs and mappings belong to
configuration/infrastructure and MUST be translated at the boundary.

The agent MUST NOT duplicate prices, free limits or entitlement definitions in
RevenueCat-facing code when those values are owned by
`docs/reference/WHERIS_BUSINESS_REFERENCE.md` or remotely configured commercial metadata.

For the current target commercial model, a permanent Wheris Plus unlock MUST
be represented by an appropriate non-consumable purchase model, while Wheris
Premium uses the approved subscription products. Exact store configuration and
provider identifiers MUST be verified at implementation time and MUST NOT be
invented from documentation examples.

Purchase restoration MUST be supported when monetization is active. The
implementation MUST account for anonymous purchase identity and reinstall / new
RevenueCat App User ID scenarios without promising restoration beyond what the
store/provider can actually recover.

RevenueCat Offerings or remotely configured paywalls MAY control which
approved commercial packages are presented, but they MUST NOT become the
source of Wheris domain rules such as whether a saved place exists, how a Free
limit is counted, or whether local data may be deleted.

RevenueCat webhooks, a Wheris entitlement backend, server-side subscription
state, mandatory accounts or cross-device identity MUST NOT be introduced
unless the relevant product phase explicitly requires them.

The agent MUST preserve the following commercial data-integrity rules:

-   reaching a Free limit MUST NOT delete, hide or corrupt existing places;
-   an expired Premium subscription MUST NOT delete local places;
-   a price change MUST NOT revoke an already acquired non-recurring Plus
    entitlement;
-   blocking creation because of an approved Free limit MUST NOT block
    consultation, modification, deletion or retrieval/navigation of existing
    local places;
-   a paywall MUST NOT discard an in-progress Add Place draft merely because
    the entitlement check occurs;
-   temporary RevenueCat, Google Play or network failure MUST NOT corrupt or
    delete local places;
-   monetization MUST NOT make the essential local place store permanently
    dependent on RevenueCat, a Wheris backend or continuous network access.

A business entitlement defines what the user is commercially eligible to use.
It MUST NOT be interpreted as authorization to collect, upload or retain
sensitive data. Any such processing remains governed by
`docs/product/SECURITY_PRIVACY.md`.

RevenueCat, Google Play Billing, purchase restoration, subscription
infrastructure or an entitlement backend MUST NOT be introduced merely because
the Business Reference or future monetization screens exist. They require an
explicit monetization implementation phase.

If billing is implemented, current authoritative Google Play and RevenueCat
requirements, SDK behavior, fees, product types, restoration behavior,
identifier behavior and regional/legal constraints MUST be re-verified at
implementation time rather than copied from planning assumptions.

------------------------------------------------------------------------

## 51. Current information

When implementation depends on changing external facts, the agent MUST
verify current authoritative documentation.

This applies especially to:

-   Android Gradle Plugin compatibility;
-   Kotlin compatibility;
-   KSP compatibility;
-   Compose BOM;
-   Room;
-   Navigation;
-   Lifecycle;
-   Koin;
-   Play Services Location;
-   Mapbox SDK;
-   Google Play Billing Library;
-   RevenueCat Android SDK and platform requirements;
-   RevenueCat pricing / MTR rules when economically relevant;
-   SDK requirements;
-   pricing;
-   quotas;
-   licensing;
-   deprecations.

The agent MUST NOT invent a version number or claim current
compatibility from memory when the decision is consequential.

Unrelated dependencies MUST NOT be upgraded merely because newer
versions exist.

------------------------------------------------------------------------

## 52. Decision discipline

The agent MAY make low-risk implementation decisions independently.

The agent MUST surface decisions before proceeding when they materially
affect:

-   user data;
-   destructive behavior;
-   permissions;
-   privacy;
-   security;
-   recurring external cost;
-   vendor lock-in;
-   core UX;
-   public architecture;
-   database compatibility;
-   MVP scope;
-   commercial entitlement semantics;
-   billing or purchase restoration;
-   paywall behavior that can affect an in-progress save;
-   cloud retention after subscription expiry.

The agent SHOULD provide a recommended option with trade-offs rather
than presenting every possible alternative without guidance.

------------------------------------------------------------------------

## 53. Completion reporting

After completing implementation work, the agent SHOULD report:

-   what changed;
-   which modules/files were materially affected;
-   important architectural decisions;
-   tests/build validation actually performed;
-   any remaining limitation or follow-up that matters.

The report MUST distinguish verified facts from assumptions.

The agent MUST NOT claim completion of work it did not perform.

------------------------------------------------------------------------

## 54. Prohibited shortcuts

The following shortcuts are prohibited unless explicitly validated for a
narrowly defined non-production experiment:

-   destructive Room migration for real user data;
-   category cascade deletion of places;
-   Mapbox types outside the map boundary;
-   Android framework types in domain;
-   Room entities as domain contracts;
-   DAO access from UI;
-   repository access directly from stateless Screen;
-   runtime permission requests from ViewModel;
-   `GlobalScope`;
-   hardcoded production secrets;
-   hardcoded production user-facing strings in Composables;
-   exact user coordinates in production logs;
-   background location without explicit product requirement;
-   hidden network transmission of saved-place data;
-   direct RevenueCat SDK/provider types in domain or feature business logic;
-   hardcoding Google Play or RevenueCat product identifiers as domain concepts;
-   treating RevenueCat entitlement state as authorization to upload sensitive user data;
-   adding Google Maps SDK contrary to the defined map architecture;
-   implementing internal routing contrary to the product scope;
-   claiming successful tests/builds that were not run.

------------------------------------------------------------------------

## 55. Final rule

When choosing between a shortcut and preserving the integrity of Wheris,
preserve the integrity of Wheris.

The agent's work MUST protect:

1.  the user's saved places;
2.  the simplicity of saving a place;
3.  the privacy of geographic data;
4.  the architectural boundaries of the application;
5.  the maintainability of the codebase.

The fundamental product behavior remains:

> « Je veux me souvenir de cet endroit. »
>
> « C'est enregistré. »
>
> Plus tard :
>
> « Le voilà. »
