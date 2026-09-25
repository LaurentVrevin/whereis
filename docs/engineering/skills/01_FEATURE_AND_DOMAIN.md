# WHERIS — FEATURE & DOMAIN PLAYBOOK

**Status:** Canonical operational playbook  
**Owns:** Feature orchestration, domain behavior, repository/use-case boundaries, ViewModels, categories and feature-level integration.

---

## 1. Mission

Implement or evolve Wheris product behavior without weakening the product North Star, module boundaries, data integrity or local-first architecture.

This playbook replaces the former fragmented feature, repository and category skills.

Use it to answer:

> What behavior belongs to Wheris, which layer owns it, and how should it move through domain/data/UI boundaries?

---

## 2. When to use

Use this playbook when a task involves one or more of:

- new or substantially changed feature behavior;
- an existing `:feature:*` module;
- a new user flow or meaningful flow branch;
- domain validation/rules;
- repository contracts or implementations;
- domain ↔ infrastructure mappings;
- use cases;
- ViewModel state/action/effect orchestration;
- category creation/edit/delete/reassignment;
- cross-feature business behavior;
- feature navigation ownership.

Do not use it alone for purely visual changes, isolated Room schema work, provider-specific GPS/map changes, dependency upgrades or billing/cloud work. Load the corresponding playbook.

---

## 3. Required sources

Always consult:

- `WHERIS_MASTER.md`;
- `AGENT.md`;
- `RULES.md`.

Consult when relevant:

- `USER_STORIES_REFERENCE.pdf` for approved user need/scope;
- `SECURITY_PRIVACY.md` when behavior touches coordinates, places, notes, photos, permissions, network, analytics, logs, external apps, export, backup or sharing;
- `WHERIS_BUSINESS_REFERENCE.md` when behavior depends on Free / Wheris Plus / Wheris Premium, limits, entitlements, paywalls, downgrade or commercial eligibility;
- `DESIGN/04_SCREEN_CATALOG.md`, `DESIGN/05_USER_FLOWS.md` and `DESIGN/07_SCREEN_SPECIFICATIONS.md` for user-facing flow/screen contracts.

Mutable business values belong to the Business Reference, not this playbook.

---

## 4. Entry checks

Before implementation:

1. Inspect the existing repository/codebase.
2. Identify the current module that owns the behavior.
3. Find related domain models, repository contracts, use cases, ViewModels, routes/screens, DI bindings and tests.
4. Confirm whether the requested behavior is inside the currently approved product scope.
5. Identify data-integrity and privacy consequences.
6. Identify whether another playbook is required.
7. Do not create a new module if an existing module already owns the responsibility coherently.

If documentation and code disagree materially, report the discrepancy before silently building around it.

---

## 5. Feature procedure

### 5.1 Define behavior before structure

State clearly:

- user intent;
- entry condition;
- success outcome;
- recoverable failures;
- non-recoverable/exit behavior;
- offline behavior where relevant;
- what must remain preserved if the operation fails.

Do not start by inventing classes.

### 5.2 Identify ownership

Determine which layer owns each part:

- **domain** — stable Wheris concepts and rules;
- **repository contract** — domain-facing data capability;
- **data/infrastructure** — persistence/provider/network implementation;
- **use case** — meaningful orchestration/rule/transaction intent when justified;
- **ViewModel** — UI-facing orchestration and state transitions;
- **Route** — ViewModel, navigation and Android/system integration;
- **Screen** — stateless presentation and semantic callbacks.

### 5.3 Domain first when a domain rule exists

Use neutral Wheris concepts.

Do not leak:

- `Context`;
- Room entities/DAOs;
- Mapbox types;
- Android `Location`;
- `FusedLocationProviderClient`;
- billing-provider objects;
- network DTOs.

A domain model should remain understandable without knowing which Android/provider technology implements it.

### 5.4 Repository boundaries

Repository APIs should:

- use domain concepts;
- expose a minimal coherent surface;
- return `Flow` where observation is naturally reactive;
- propagate meaningful failures instead of hiding them;
- avoid provider/storage implementation types.

Repository implementations may depend on infrastructure but must map explicitly to domain.

### 5.5 Use cases

Create a UseCase when it expresses useful business intent, such as:

- reusable validation;
- orchestration across repositories;
- transaction-like intent;
- category reassignment before deletion;
- a meaningful domain operation shared across callers.

Do not create one trivial UseCase for every repository method merely to add layers.

Pure calculations can remain pure functions/services when that is clearer.

### 5.6 ViewModel contract

Use ViewModel for stateful UI orchestration.

Prefer:

- one stable `UiState` representing renderable state;
- semantic `UiAction` for meaningful user/UI intents;
- `UiEffect` only for true one-off effects that should not become persistent state.

ViewModels must not own:

- `NavController`;
- runtime permission launchers;
- Room DAO/entity details;
- Mapbox objects;
- Android `Location`/Fused provider;
- Play Billing client objects.

### 5.7 Route / Screen split

When Compose is involved:

- `Route` owns ViewModel collection, navigation wiring and Android/system integration;
- `Screen` is stateless or nearly stateless presentation;
- ephemeral visual state may remain local if it has no business meaning.

Use `SKILLS/02_UI_AND_DESIGN.md` for detailed UI rules.

### 5.8 Dependency injection

Wire Koin only at appropriate composition/infrastructure boundaries.

Do not resolve repositories/services directly inside generic UI components or stateless screens.

### 5.9 Navigation

Use stable route IDs/arguments already defined by the product/navigation architecture.

Do not create a new destination for a state that belongs to an existing screen/sheet/dialog.

If a genuinely new destination is needed, update the owning product/design catalog rather than silently inventing it in code.

---

## 6. Category-specific invariants

Categories are dynamic domain data, not a hardcoded feature enum.

### System identity

- System categories require stable non-localized identity.
- Display labels are localized UI resources.
- Custom category names are literal user data.
- Do not persist the French display label as the category's canonical identity.

### Creation

When a category is created during Add Place:

- preserve the draft;
- return coherently to the Add flow;
- make the new category immediately available;
- auto-select it when that is the approved flow behavior.

### Deletion

**Category deletion MUST NEVER delete places.**

If places reference a custom category:

1. determine affected references;
2. require/resolve the approved replacement path;
3. keep the stable fallback category (`Autre`) available where specified;
4. reassign referenced places transactionally;
5. delete the category only after successful reassignment.

System categories that are protected by product rules must not expose unsupported deletion.

### Icon/color

Persist neutral supported keys/data.

Do not let UI-only resources become domain identity.

---

## 7. Wheris invariants

Any feature implementation must preserve:

1. Saving a place remains fast and simple.
2. A map failure does not make saved places unavailable.
3. Network access is not a prerequisite for core local persistence unless an explicitly approved future feature requires it.
4. User place data is not silently deleted by secondary-object operations.
5. No fake GPS coordinate, accuracy or successful persistence state is presented.
6. Optional metadata remains optional unless product documentation explicitly changes it.
7. External navigation remains user-initiated.
8. Commercial eligibility must not silently become data ownership.
9. An expired/failed entitlement must not delete or corrupt local places.
10. Do not create future account/cloud/billing architecture merely because the business documents discuss it.

---

## 8. Cross-domain triggers

Load `SKILLS/02_UI_AND_DESIGN.md` when:

- a screen/state/dialog/sheet changes materially;
- accessibility or Design System behavior changes.

Load `SKILLS/03_DATA_AND_PERSISTENCE.md` when:

- entities/DAOs/schema/transactions change;
- category reassignment requires persistence changes.

Load `SKILLS/04_LOCATION_AND_MAP.md` when:

- location acquisition, accuracy, permissions, provider state or Mapbox behavior changes.

Load `SKILLS/05_QUALITY_AND_BUGFIX.md` when:

- the task is a defect/regression;
- data integrity risk is high;
- a test strategy needs explicit design.

Load `SKILLS/06_DEPENDENCIES_AND_RELEASE.md` when:

- a new library/SDK/plugin/toolchain change is required.

Load `SKILLS/07_MONETIZATION_AND_CLOUD.md` when:

- feature availability depends on Free / Plus / Premium;
- paywall/entitlement/billing/cloud/account behavior is involved.

---

## 9. Testing & validation

Prioritize tests around observable rules:

- ViewModel state transitions;
- orchestration success/failure;
- preservation of drafts/data after recoverable failures;
- repository/domain mappings;
- category creation/select/delete/reassignment;
- entitlement-independent preservation of local data when relevant;
- navigation/effect semantics where they contain real behavior.

Run the smallest useful tests first, then broader validation proportional to risk.

Compile affected modules and verify DI/navigation wiring.

Do not report validation that was not actually executed.

---

## 10. Definition of done

A feature/domain task is done when:

- requested behavior matches approved scope;
- ownership is clear;
- domain boundaries remain provider-independent;
- repository/use-case surface is minimal and meaningful;
- ViewModel/UI contracts are coherent;
- user data is preserved under failure paths;
- relevant privacy/business constraints are respected;
- tests protect meaningful behavior;
- affected code compiles and relevant tests pass, or limitations are explicitly reported.

---

## 11. Never

Never:

- bypass repository/domain boundaries for speed;
- create speculative architecture;
- expose Room/Mapbox/Android/billing provider types through domain contracts;
- create meaningless one-method abstraction layers;
- put business rules inside generic Compose components;
- cascade-delete places through category deletion;
- silently make local core behavior network-dependent;
- implement a paywall merely because a future monetization screen exists;
- duplicate mutable prices/limits/entitlements outside the Business Reference;
- claim successful validation without running it.
