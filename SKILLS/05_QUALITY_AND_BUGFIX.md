# WHERIS — QUALITY & BUGFIX PLAYBOOK

**Status:** Canonical operational playbook  
**Owns:** Root-cause defect resolution, regression protection, test design and evidence-based validation.

---

## 1. Mission

Protect Wheris behavior and user data by fixing root causes with the smallest coherent change and by testing the behavior most likely to regress.

This playbook replaces the former Testing and Bug Fix skills.

---

## 2. When to use

Use for:

- crashes;
- incorrect UI/state;
- regressions;
- data-integrity defects;
- Room/migration failures;
- location/map bugs;
- offline/fallback bugs;
- category reassignment issues;
- business/entitlement bugs when monetization is active;
- adding/refining tests;
- designing a validation plan;
- pre-release targeted regression work.

For a bug, also load the playbook that owns the affected subsystem.

---

## 3. Required sources

Always consult:

- `RULES.md`;
- the owning subsystem playbook.

Consult:

- `SECURITY_PRIVACY.md` for sensitive data, permissions, network, logs, cloud, analytics, billing identifiers or security defects;
- `WHERIS_BUSINESS_REFERENCE.md` for commercial behavior bugs;
- relevant design/user-story sources for behavioral expectations.

---

## 4. Bug entry checks

Before changing code:

1. Capture the observed symptom.
2. Establish a reproducible condition where practical.
3. Identify expected behavior from canonical sources.
4. Locate the owning layer/subsystem.
5. Inspect relevant recent/existing code and tests.
6. Assess whether user data may be lost, corrupted, exposed or made inaccessible.
7. Assess whether the issue can affect users already on older persisted schemas/configurations.
8. Avoid “fixing” a symptom before understanding the root cause.

If the issue is security/privacy-sensitive, prioritize containment and evidence preservation without logging sensitive data.

---

## 5. Root-cause fix procedure

1. Reproduce or create a deterministic failing test/condition where possible.
2. Separate visible symptom from causal defect.
3. Identify the minimal coherent ownership boundary for the fix.
4. Implement the root-cause correction.
5. Avoid unrelated refactors unless they are required for correctness.
6. Add regression protection appropriate to the layer.
7. Validate neighboring behavior likely to be affected.
8. For persistence issues, test representative existing data/migration paths.
9. For location/map issues, test degraded and cancellation states.
10. For monetization issues, verify local data remains intact across entitlement failure/expiry.
11. Report root cause, changed behavior, tests actually executed and residual limitations.

A bug is never permission to bypass architecture or weaken security.

---

## 6. Test selection model

Choose the lowest reliable layer that expresses the contract.

| Behavior | Preferred test |
|---|---|
| Pure domain calculation/rule | Unit test |
| Repository mapping/logic | Unit/fake/in-memory test |
| ViewModel orchestration | State-transition test with controlled fakes |
| DAO/query/transaction | Room/in-memory database test |
| Released schema change | Room migration test |
| Compose interaction/semantics | Compose UI test |
| Provider adapter mapping | Unit test around neutral adapter/fake |
| End-to-end critical path | Targeted instrumentation/smoke test where justified |

Do not use expensive integration tests when a stable unit-level contract is sufficient.

Do not use a pure unit test to pretend a real migration/provider integration has been validated.

---

## 7. High-priority Wheris coverage

Prioritize tests for:

- `GeoPoint` validation;
- distance;
- bearing;
- cardinal direction;
- accuracy evaluation;
- Add Place save behavior;
- place edit/delete;
- category create/edit/delete/reassignment;
- Room mappings/queries/transactions;
- Room migrations;
- location timeout/no-fix/disabled/error/stale/poor accuracy;
- map unavailable fallback;
- offline local behavior;
- ViewModel state transitions;
- draft preservation on recoverable failures;
- entitlement/downgrade local-data preservation when monetization is active.

---

## 8. Test data/privacy rules

Use synthetic data.

Never put real personal:

- coordinates;
- names/notes;
- photos;
- tokens;
- receipts;
- credentials;
- account identifiers

into committed tests/fixtures.

Test logs must not expose sensitive data either.

---

## 9. Regression strategy by risk

### Low risk

Examples: pure formatting/helper change.

- targeted unit test;
- affected module compile.

### Medium risk

Examples: ViewModel/UI flow change, repository logic.

- focused unit/state tests;
- neighboring behavior tests;
- affected feature compile;
- Compose test when interaction is important.

### High risk

Examples: migration, deletion semantics, location/provider lifecycle, cloud/billing trust state.

- dedicated regression test;
- edge/failure-path tests;
- broader affected-suite validation;
- explicit data-integrity/security review;
- release-path validation when appropriate.

---

## 10. Non-regression principles

- A regression test should fail on the defective behavior and pass after the fix where practical.
- Test behavior, not implementation trivia.
- Keep tests deterministic and independent.
- Avoid arbitrary sleeps; control clocks/dispatchers/providers where possible.
- Keep fixtures minimal and readable.
- Do not assert unstable provider internals when Wheris owns a neutral contract.

---

## 11. Special safety gates

### Data loss/corruption

Never “fix” by:

- destructive migration;
- clearing user data;
- silently dropping invalid rows without an approved recovery policy;
- cascading category deletion into place deletion.

### Security/privacy

Never “fix” by:

- disabling TLS verification;
- enabling broad cleartext;
- broadening permissions without purpose;
- exposing FileProvider paths broadly;
- logging exact coordinates/notes/photos/receipts;
- weakening a security control silently.

### Errors

Do not solve defects by broad exception swallowing that hides failed operations or presents false success.

---

## 12. Cross-domain triggers

Load:

- `SKILLS/01_FEATURE_AND_DOMAIN.md` for domain/repository/ViewModel defects;
- `SKILLS/02_UI_AND_DESIGN.md` for Compose/accessibility/state rendering defects;
- `SKILLS/03_DATA_AND_PERSISTENCE.md` for Room/data/migration defects;
- `SKILLS/04_LOCATION_AND_MAP.md` for GPS/Mapbox/provider defects;
- `SKILLS/06_DEPENDENCIES_AND_RELEASE.md` for toolchain/dependency/release defects;
- `SKILLS/07_MONETIZATION_AND_CLOUD.md` for billing/entitlement/account/cloud defects.

---

## 13. Validation reporting

Report exactly:

- what was reproduced;
- root cause;
- what changed;
- tests run;
- builds/checks run;
- result;
- what was not possible to validate;
- any remaining risk.

Do not use vague language such as “should work” when validation was not executed.

---

## 14. Definition of done

Quality/bugfix work is done when:

- root cause, not merely symptom, is addressed;
- architecture/security was not bypassed;
- regression protection exists where valuable;
- neighboring high-risk behavior is checked;
- data integrity is preserved;
- actual validation evidence is available or limitations are explicit.

---

## 15. Never

Never:

- claim tests passed without running them;
- use real personal/sensitive data in fixtures;
- hide a bug behind destructive migration or broad exception swallowing;
- weaken security to make a test/build pass;
- overfit tests to implementation details;
- refactor unrelated areas during a focused bugfix without necessity.
