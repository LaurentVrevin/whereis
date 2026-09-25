# WHERIS — DEPENDENCIES & RELEASE PLAYBOOK

**Status:** Canonical operational playbook  
**Owns:** Dependency/toolchain changes and evidence-based Android release readiness.

---

## 1. Mission

Keep Wheris's external technical surface intentional, compatible, low-cost, privacy-conscious and releasable.

This playbook replaces the former Dependency Management and Android Release Check skills.

---

## 2. When to use

Use for:

- adding/removing/updating libraries;
- Android/Gradle plugins;
- AGP/Kotlin/KSP changes;
- Compose BOM/Material 3 changes;
- Room/Koin/Navigation/Lifecycle changes;
- Play Services Location changes;
- Mapbox SDK/provider changes;
- new external SDK/service integrations;
- Version Catalog edits;
- build configuration;
- release variant validation;
- manifest/permission/exported-component review;
- signing/secrets/release configuration;
- Google Play requirement review;
- pre-internal/beta/production release checks.

---

## 3. Required sources

Always consult:

- `RULES.md`;
- `SECURITY_PRIVACY.md`.

For release work also consult:

- `WHERIS_MASTER.md`;
- active release scope/notes;
- `WHERIS_BUSINESS_REFERENCE.md` if monetization is active in the release.

For consequential changing facts, verify current authoritative documentation at the time of the change, especially:

- Android/Google Play requirements;
- AGP/Kotlin/KSP compatibility;
- Compose BOM/Material 3;
- Room;
- Koin;
- Navigation/Lifecycle;
- Play Services Location;
- Mapbox APIs, terms, attribution and pricing;
- billing/store policy when monetization is active;
- library licenses and release notes.

Never invent versions or assume old policy knowledge is current.

---

## 4. Dependency entry checks

Before adding/updating a dependency:

1. Confirm the capability is actually needed.
2. Check whether Android/standard library/existing project dependency already solves it.
3. Inspect Version Catalog and affected module Gradle files.
4. Identify coupled toolchain constraints.
5. Identify transitive SDK/privacy/network behavior.
6. Review maintenance status/license.
7. Review cost/quotas/vendor lock-in for external services.
8. Review minimum SDK/runtime impact.
9. Identify which architectural boundary will contain provider types.
10. Identify tests/builds affected.

---

## 5. Dependency change procedure

1. Choose the smallest sufficient dependency.
2. Verify current stable/appropriate version and compatibility from official sources.
3. Record material version constraints/risks.
4. Add/update through the established Version Catalog strategy.
5. Scope the dependency only to modules that need it.
6. Do not duplicate BOM-managed versions unnecessarily.
7. Keep AGP/Kotlin/KSP and code-generation tooling aligned.
8. Apply KSP/plugins only where needed.
9. Prevent third-party types from leaking across Wheris boundaries.
10. Review SDK automatic collection/network behavior and disable non-required collection where supported/required.
11. Sync/configure/build affected modules.
12. Run relevant tests.
13. Do not opportunistically upgrade unrelated dependencies.

---

## 6. External service review

For any service/SDK that communicates externally, review:

- purpose;
- data transmitted;
- whether coordinates/content/identifiers are involved;
- privacy policy/default collection;
- offline behavior;
- reliability;
- quotas;
- pricing;
- licensing/attribution;
- vendor lock-in;
- replacement boundary;
- failure/degradation behavior;
- security/trust implications.

Core place persistence must not become network-dependent without an explicit product decision.

---

## 7. Release procedure

Before a release, define:

- target variant;
- version/version code;
- intended feature scope;
- supported migration paths;
- active external services;
- whether monetization/cloud/accounts are actually included.

Then verify:

1. Build the release variant.
2. Run required unit tests.
3. Run relevant instrumentation/Compose tests.
4. Run configured lint/static checks.
5. Review Room schema history/migrations.
6. Review Manifest permissions.
7. Review exported activities/services/receivers/providers.
8. Confirm no unintended background location.
9. Review Android backup/data extraction policy/configuration.
10. Review production logging/crash/analytics for sensitive data.
11. Review secrets/tokens/signing configuration.
12. Review network security/endpoints.
13. Verify Mapbox/provider attribution/licensing.
14. Verify local files/photos are not broadly exposed.
15. Verify debug-only code/configuration is absent.
16. Review material dependency advisories/issues relevant to used versions.
17. Verify Light/Dark and accessibility-critical flows.
18. Smoke-test the Wheris North Star: save → retrieve → detail → external navigation.
19. Smoke-test category reassignment/deletion safety.
20. If monetization is active, validate purchase/restoration/downgrade flows and local-data preservation.
21. Record what was actually validated and remaining known limitations.

---

## 8. Release security gate

Production release is blocked by unresolved serious issues such as:

- active exposure of exact coordinates or sensitive place content;
- destructive migration/data loss path;
- leaked privileged secret;
- unintended background tracking;
- insecure network transport;
- broadly exported sensitive component/file path;
- entitlement/paywall logic that deletes or locks away canonical local user data contrary to approved rules;
- known billing/cloud trust failure that can corrupt ownership/data state.

Do not ship by disabling safeguards or suppressing evidence.

---

## 9. Monetization/release boundary

The Business Reference may define future Free / Wheris Plus / Wheris Premium behavior, but:

- Google Play Billing is not added merely because business docs exist;
- billing is added only in an explicitly approved monetization implementation phase;
- current Play billing/store requirements must be verified at implementation/release time;
- mutable prices and limits must not be copied into build constants as independent sources of truth unless the approved architecture explicitly requires a cached/configured representation;
- purchase restoration and entitlement source-of-truth must be tested before release;
- Premium/cloud availability must match actual implemented behavior and privacy documentation.

Use `SKILLS/07_MONETIZATION_AND_CLOUD.md` when monetization is active.

---

## 10. Cross-domain triggers

Load:

- `SKILLS/01_FEATURE_AND_DOMAIN.md` when dependency change modifies domain/feature behavior;
- `SKILLS/02_UI_AND_DESIGN.md` for UI-library/design-system consequences;
- `SKILLS/03_DATA_AND_PERSISTENCE.md` for Room/KSP/schema consequences;
- `SKILLS/04_LOCATION_AND_MAP.md` for Play Services Location/Mapbox consequences;
- `SKILLS/05_QUALITY_AND_BUGFIX.md` for regression/validation strategy;
- `SKILLS/07_MONETIZATION_AND_CLOUD.md` for billing, account, entitlement or cloud services.

---

## 11. Testing & validation

A dependency is not “compatible” until the relevant project configuration/build succeeds.

At minimum:

- Gradle sync/configuration must resolve;
- affected modules compile;
- relevant tests pass;
- provider initialization/failure path is checked where applicable.

A release is not “ready” because debug builds work.

Validate the release variant and record actual evidence.

---

## 12. Definition of done

Dependency/release work is done when:

- dependency/service need is justified;
- current compatibility was checked from authoritative sources;
- scope is minimal;
- privacy/cost/license/lock-in impacts were reviewed where relevant;
- provider types remain contained;
- release-critical configuration and migration paths are validated;
- actual builds/tests/checks have evidence;
- unresolved material risk is explicitly reported.

---

## 13. Never

Never:

- invent dependency versions;
- upgrade unrelated libraries opportunistically;
- add overlapping libraries without justification;
- assume a current free tier will remain free indefinitely;
- embed privileged secrets in the APK/repository;
- disable security controls to make release pass;
- ship destructive migration to avoid migration work;
- claim release readiness without a real release build/check;
- introduce billing/account/cloud merely because future documentation mentions it.
