# WHERIS — SKILLS INDEX

**Status:** Canonical operational router  
**Role:** Select the minimum set of Wheris playbooks required for an engineering task.  
**Rule:** This index routes work. It does not redefine product, business, security, design, or architecture decisions.

---

## 1. Why this file exists

Wheris uses a small set of broad operational playbooks instead of many narrowly fragmented `SKILL.md` files.

The goal is to make task execution easier to navigate while preserving specialist depth.

All canonical Wheris playbooks live directly under `SKILLS/` with descriptive filenames. Do not create one folder per playbook and do not rename canonical playbooks to a generic `SKILL.md`. If an external system requires that packaging format, generate adapters from these canonical files instead of maintaining two competing sources.

A normal task should require:

- this index;
- one primary playbook;
- at most one or two additional playbooks when the task crosses real subsystem boundaries.

Do not load every playbook by default.

---

## 2. Higher-level sources of truth

Playbooks are subordinate to the canonical project documentation.

Before acting, respect the ownership defined by the project:

- `WHERIS_MASTER.md` — product vision, functional scope, architecture direction;
- `WHERIS_BUSINESS_REFERENCE.md` — commercial model, offer structure, entitlements, business hypotheses, downgrade semantics, KPIs and financial assumptions;
- `AGENT.md` — engineering role and decision mindset;
- `RULES.md` — mandatory engineering and repository constraints;
- `SECURITY_PRIVACY.md` — mandatory security/privacy rules for sensitive data and trust boundaries;
- `USER_STORIES_REFERENCE.pdf` — approved user needs and story-level scope;
- `DESIGN/00_DESIGN_INDEX.md` — design navigation/router only;
- `DESIGN/01_DESIGN_FOUNDATIONS.md` through `DESIGN/07_SCREEN_SPECIFICATIONS.md` — semantic design specification;
- validated Figma — visual reference only, constrained by the semantic sources above.

If repository paths differ, resolve the current canonical file by name rather than inventing a parallel source.

When sources disagree materially, stop treating the point as settled: identify the owning source and resolve the contradiction there.

---

## 3. The seven playbooks

### `SKILLS/01_FEATURE_AND_DOMAIN.md`
Use for:

- complete Android features;
- feature-module evolution;
- domain rules;
- repositories and mappings;
- use cases;
- ViewModels and UI state orchestration;
- category behavior;
- feature navigation ownership;
- functional integration across layers.

### `SKILLS/02_UI_AND_DESIGN.md`
Use for:

- Compose screens;
- dialogs, sheets and transient UI;
- UI states;
- accessibility;
- Design System primitives/components/patterns;
- design-token consumption;
- translating approved design specs into Compose;
- deciding whether a UI pattern remains feature-local or becomes reusable.

### `SKILLS/03_DATA_AND_PERSISTENCE.md`
Use for:

- Room entities;
- DAOs;
- database configuration;
- persistence mappings;
- transactions;
- indexes/constraints;
- system-data seeding;
- schema changes;
- released-schema migrations;
- migration tests.

### `SKILLS/04_LOCATION_AND_MAP.md`
Use for:

- foreground location;
- GPS/location permissions and acquisition;
- accuracy and stale/poor fixes;
- timeouts/provider-disabled/error states;
- Mapbox integration;
- map style/camera/markers;
- map-provider failure and fallback;
- geographic provider isolation;
- map attribution and token handling.

### `SKILLS/05_QUALITY_AND_BUGFIX.md`
Use for:

- defect investigation;
- regression fixes;
- test strategy;
- unit/ViewModel/Room/Compose tests;
- migration regression protection;
- validation planning;
- data-integrity incident fixes.

### `SKILLS/06_DEPENDENCIES_AND_RELEASE.md`
Use for:

- Gradle/toolchain changes;
- Version Catalog changes;
- new/update/remove dependency or SDK;
- provider licensing/cost/privacy review;
- AGP/Kotlin/KSP/Compose/Room/Koin compatibility;
- Android release checks;
- manifest/permission/exported-component review;
- secrets/signing/release configuration;
- current Google Play/Android requirements.

### `SKILLS/07_MONETIZATION_AND_CLOUD.md`
Use for:

- Free / Wheris Plus / Wheris Premium behavior;
- free limits and commercial eligibility;
- paywalls;
- one-time purchase;
- subscription;
- purchase restoration;
- entitlement state;
- downgrade/expiry behavior;
- Google Play Billing;
- business analytics;
- future accounts;
- cloud backup/sync;
- cloud photos;
- entitlement/backend trust boundaries.

**Important:** the existence of this playbook does not activate monetization, accounts, billing or cloud in the MVP.

---

## 4. Fast routing table

| Task | Primary playbook | Also load when needed |
|---|---|---|
| Add a new product feature | `01_FEATURE_AND_DOMAIN` | `02`, `03`, `04`, or `07` depending on boundaries |
| Add/edit a repository or use case | `01_FEATURE_AND_DOMAIN` | `03` if persistence changes |
| Category CRUD/reassignment | `01_FEATURE_AND_DOMAIN` | `03` if DB behavior changes; `02` for UI |
| Build/modify a Compose screen | `02_UI_AND_DESIGN` | `01` if behavior/state changes |
| Create reusable UI component | `02_UI_AND_DESIGN` | `01` only if business semantics are involved |
| Add Room entity/DAO/query | `03_DATA_AND_PERSISTENCE` | `01` if domain/repository contract changes |
| Change released Room schema | `03_DATA_AND_PERSISTENCE` | `05` for migration regression validation |
| Implement GPS/current location | `04_LOCATION_AND_MAP` | `02` for permission/error UI |
| Implement Mapbox markers/camera/style | `04_LOCATION_AND_MAP` | `02` for visible interaction changes |
| Fix a bug | `05_QUALITY_AND_BUGFIX` | load the subsystem playbook owning the defect |
| Add tests only | `05_QUALITY_AND_BUGFIX` | subsystem playbook if behavior contract is unclear |
| Add/update SDK/library/plugin | `06_DEPENDENCIES_AND_RELEASE` | subsystem playbook using it |
| Prepare Android release | `06_DEPENDENCIES_AND_RELEASE` | `05` for targeted validation; `07` if billing/cloud is active |
| Implement free-place limit/paywall | `07_MONETIZATION_AND_CLOUD` | `01` + `02`; `03` only if local persistence changes |
| Implement billing/restoration | `07_MONETIZATION_AND_CLOUD` | `06` + `05` |
| Implement cloud backup/sync | `07_MONETIZATION_AND_CLOUD` | `03` + `05`; security review is mandatory |

---

## 5. Routing examples

### Example — Add Place behavior change

Load:

1. `SKILLS/01_FEATURE_AND_DOMAIN.md`;
2. `SKILLS/02_UI_AND_DESIGN.md` if UI/state presentation changes;
3. `SKILLS/04_LOCATION_AND_MAP.md` only if acquisition/map behavior changes.

Do not load persistence or monetization playbooks unless the task actually crosses those boundaries.

### Example — New Room field on a released app

Load:

1. `SKILLS/03_DATA_AND_PERSISTENCE.md`;
2. `SKILLS/05_QUALITY_AND_BUGFIX.md` for migration/regression validation;
3. `SKILLS/01_FEATURE_AND_DOMAIN.md` only if the field changes domain/repository behavior.

### Example — Future Free limit

Load:

1. `SKILLS/07_MONETIZATION_AND_CLOUD.md`;
2. `SKILLS/01_FEATURE_AND_DOMAIN.md` for entitlement/business-state integration;
3. `SKILLS/02_UI_AND_DESIGN.md` for the paywall/limit state.

Do not infer the active numeric free limit from this index. Read `WHERIS_BUSINESS_REFERENCE.md`.

### Example — Mapbox SDK update

Load:

1. `SKILLS/06_DEPENDENCIES_AND_RELEASE.md` for current compatibility/licensing/cost review;
2. `SKILLS/04_LOCATION_AND_MAP.md` for Wheris provider-boundary behavior;
3. `SKILLS/05_QUALITY_AND_BUGFIX.md` if regression coverage is needed.

---

## 6. Universal execution rules

Regardless of playbook:

1. Inspect existing code before designing a solution.
2. Identify the current owner of the behavior.
3. Preserve the shortest safe solution.
4. Do not create architecture merely to anticipate a hypothetical future feature.
5. Preserve user data and local-first behavior.
6. Do not leak provider/database/platform types across architectural boundaries.
7. Treat coordinates, notes, photos and saved places as sensitive data.
8. Do not invent current library versions, provider pricing, legal requirements or store policies.
9. Do not hardcode mutable commercial assumptions in unrelated layers.
10. Add tests proportional to regression/data risk.
11. Run actual validation before claiming success.
12. Report limitations and unresolved contradictions explicitly.

---

## 7. When two playbooks appear to overlap

Choose ownership by **the thing that is changing**, not by the files being edited.

Examples:

- A Compose file changed because a new business rule exists → `01` owns behavior; `02` owns presentation.
- A repository implementation changed because a database query changed → `03` owns persistence; `01` owns the contract if it changes.
- A map screen changed because Mapbox API changed → `04` owns provider behavior; `06` owns dependency compatibility.
- A paywall screen changed because entitlement semantics changed → `07` owns commercial behavior; `02` owns visual implementation.

Never solve a cross-domain problem by putting all logic in the layer that happens to be easiest to edit.

---

## 8. Definition of a well-routed task

Routing is correct when:

- one playbook clearly owns the main behavior;
- additional playbooks are loaded only for genuine crossed boundaries;
- canonical project sources are consulted for decisions they own;
- no playbook is used as an excuse to expand scope;
- implementation remains understandable without knowing the playbook structure itself.
