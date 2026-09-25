# WHERIS — DATA & PERSISTENCE PLAYBOOK

**Status:** Canonical operational playbook  
**Owns:** Room persistence, entities/DAOs, mappings, transactions, schema evolution, migrations and persistence integrity.

---

## 1. Mission

Persist Wheris local data reliably while protecting user geographic memory from loss, corruption, accidental cascade behavior and infrastructure leakage.

This playbook replaces the former Room-database and Room-migration skills.

---

## 2. When to use

Use for:

- Room entities;
- DAOs and queries;
- database configuration;
- entity/domain mapping;
- foreign keys, constraints and indexes;
- multi-step transactions;
- system category seeding;
- repository persistence implementation;
- schema version changes;
- data backfills;
- released-schema migrations;
- migration tests;
- persistence-related integrity fixes.

---

## 3. Required sources

Always consult:

- `WHERIS_MASTER.md`;
- `RULES.md`;
- `SECURITY_PRIVACY.md`.

Consult:

- `SKILLS/01_FEATURE_AND_DOMAIN.md` when repository/domain contracts change;
- current official Room documentation when API/version/migration behavior is material;
- `WHERIS_BUSINESS_REFERENCE.md` only if persisted state is proposed for commercial eligibility/entitlements;
- `SKILLS/07_MONETIZATION_AND_CLOUD.md` before persisting billing/entitlement/account/cloud state.

Do not treat commercial eligibility as ordinary place-domain data.

---

## 4. Entry checks

Before changing persistence:

1. Inspect current database version and exported schemas if configured.
2. Identify whether the affected schema has already shipped to users.
3. Inspect entities, DAOs, mappings, foreign keys, indexes and transactions.
4. Identify which user data could be lost/corrupted by the change.
5. Identify all supported upgrade paths.
6. Confirm seed/default behavior.
7. Decide whether the change is schema, query-only, mapping-only or domain-level.
8. Load `SKILLS/05_QUALITY_AND_BUGFIX.md` when risk/defect/migration regression warrants explicit validation planning.

---

## 5. Persistence design procedure

### 5.1 Start from the domain requirement

Define what Wheris needs to remember before designing columns.

Room shape is an implementation detail, not the domain model.

### 5.2 Entity boundary

Room entities stay inside the database/data boundary.

Do not expose them through:

- domain contracts;
- ViewModels;
- UI state;
- navigation;
- Design System.

Map explicitly between entity and domain representations.

### 5.3 IDs and identity

Use stable opaque IDs.

Do not encode sensitive content in IDs.

Category identity must not rely on localized display labels.

### 5.4 Constraints and indexes

Add constraints/indexes for actual integrity/query needs.

Do not over-index speculatively.

Review foreign-key deletion behavior carefully.

**Category deletion MUST NEVER cascade-delete places.**

### 5.5 Transactions

Use transactions when several persistence steps must succeed or fail as one coherent operation, including category reassignment before deletion.

Do not expose partially successful states when integrity requires atomicity.

### 5.6 Reactive observation

Use `Flow` for naturally observed data.

Do not turn every one-shot operation into a stream without benefit.

### 5.7 Threading

Database work must not block the main thread.

Follow Room/coroutine patterns appropriate to the current stack.

### 5.8 Photos/files

Room should store metadata/references, not place-photo image BLOBs unless an explicitly reviewed future decision changes that architecture.

File ownership/lifecycle must remain clear.

### 5.9 System categories

Seed deterministic system categories idempotently.

Stable system identity is separate from localized labels.

Re-running initialization must not duplicate system categories.

---

## 6. Released-schema decision

Before writing a migration, classify the change.

### Case A — schema has not shipped

If the relevant database schema truly has never been released to users and project policy permits it, direct schema modification may be acceptable.

Still update tests/schema exports and do not assume unreleased status without evidence.

### Case B — schema has shipped

A versioned migration is mandatory for supported upgrade paths.

Do not use destructive migration as a shortcut for real user data.

---

## 7. Migration procedure

1. Identify exact old schema(s).
2. Define exact new schema.
3. Increment database version intentionally.
4. Write explicit migration SQL/API steps.
5. Preserve representative user data.
6. Define deterministic defaults/backfills for new required fields.
7. Preserve place/category relationships.
8. Preserve notes, favorites, timestamps, accuracy/metadata and photo references where applicable.
9. Keep system-category seeding idempotent.
10. Update/export Room schemas if configured.
11. Add migration tests from every supported source version that can reach the new release.
12. Test direct and chained upgrade paths as required.
13. Verify domain/repository mappings after migration.

If the data transformation is complex, document assumptions and failure consequences explicitly.

---

## 8. Data integrity invariants

1. Saved places are canonical local user data.
2. Map/cache/provider failure cannot delete canonical place records.
3. Category deletion cannot delete places.
4. Clearing caches cannot delete canonical records.
5. A migration cannot silently discard real user data merely to simplify implementation.
6. A failed monetization entitlement check cannot delete local places.
7. Premium expiry/cancellation cannot be implemented as database deletion of local place data.
8. Temporary/export/cache data must remain conceptually separate from canonical data.
9. User content must not be duplicated into persistent stores without need.
10. Sensitive fields must not be added to logs/diagnostics as part of persistence work.

---

## 9. Commercial/entitlement persistence boundary

If future monetization is active:

- consult `WHERIS_BUSINESS_REFERENCE.md` for entitlement semantics;
- consult `SECURITY_PRIVACY.md` for billing identifiers/trust/privacy;
- use `SKILLS/07_MONETIZATION_AND_CLOUD.md` for architecture.

Do not:

- store mutable price text as a place-domain invariant;
- treat a local boolean as authoritative for server/store subscription status without an approved entitlement model;
- delete user content when entitlement expires;
- mix billing records into Room entities simply because Room already exists.

Entitlement persistence requires its own explicit trust/source-of-truth decision.

---

## 10. Cross-domain triggers

Load `SKILLS/01_FEATURE_AND_DOMAIN.md` when:

- repository/domain contracts or business behavior change.

Load `SKILLS/02_UI_AND_DESIGN.md` when:

- schema/query changes create new visible states.

Load `SKILLS/04_LOCATION_AND_MAP.md` when:

- persisted geographic metadata changes location/map behavior.

Load `SKILLS/05_QUALITY_AND_BUGFIX.md` when:

- fixing corruption/data loss;
- migration risk is non-trivial;
- regression strategy must be explicit.

Load `SKILLS/06_DEPENDENCIES_AND_RELEASE.md` when:

- Room/KSP/database tooling versions change.

Load `SKILLS/07_MONETIZATION_AND_CLOUD.md` when:

- entitlements, accounts, cloud sync/backup or billing state is persisted.

---

## 11. Testing & validation

### DAO/database tests

Test relevant:

- CRUD;
- queries/sorts/filters;
- relationships;
- favorites;
- category reassignment;
- deterministic seeds;
- constraints/uniqueness when present;
- transaction rollback behavior.

### Migration tests

Migration tests must verify:

- resulting schema;
- representative preserved records;
- relationship integrity;
- required backfills/defaults;
- supported upgrade paths.

### Build validation

Compile affected database/data/domain/features and run targeted tests.

Do not claim a migration is safe merely because the app compiles.

---

## 12. Definition of done

Persistence work is done when:

- database shape matches the approved requirement;
- entity/domain boundaries are explicit;
- constraints and indexes are justified;
- relationships preserve place integrity;
- released schemas migrate safely;
- representative data survives migration;
- tests cover meaningful persistence risk;
- affected modules build successfully or limitations are explicitly reported.

---

## 13. Never

Never:

- expose DAO/entity types through domain/UI APIs;
- use `fallbackToDestructiveMigration()` to avoid a required production migration;
- cascade-delete places through category deletion;
- silently overwrite imported/user records without an approved rule;
- use localized labels as stable category identity;
- turn Room into a dumping ground for provider/billing/network objects;
- claim migration safety without testing supported upgrade paths.
