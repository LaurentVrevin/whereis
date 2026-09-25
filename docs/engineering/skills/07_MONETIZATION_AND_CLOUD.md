# WHERIS — MONETIZATION & CLOUD PLAYBOOK

**Status:** Canonical future/commercial operational playbook  
**Version:** 2.1 — RevenueCat target architecture  
**Owns:** Commercial eligibility, Free / Wheris Plus / Wheris Premium implementation workflow, RevenueCat purchase and entitlement integration, restoration, downgrade semantics, and future cloud/account capabilities.

---

## 1. Mission

Implement future Wheris monetization and network-backed Premium capabilities without compromising:

- local-first behavior;
- user-data ownership;
- privacy;
- offline resilience;
- architecture boundaries;
- commercial correctness;
- product trust.

RevenueCat is the approved **target purchase and entitlement infrastructure** when monetization is explicitly activated.

This playbook exists because the commercial architecture is now defined. **Its existence does not activate monetization, Google Play Billing, RevenueCat, accounts, backend services or cloud in the current MVP.**

Activation requires an explicit product/engineering phase.

---

## 2. When to use

Use this playbook when a task involves one or more of:

- Wheris Free eligibility;
- Wheris Plus eligibility;
- Wheris Premium eligibility;
- free active-place limits;
- upsell or paywall behavior;
- one-time purchase;
- subscription;
- RevenueCat Products, Entitlements, Offerings or Packages;
- purchase restoration;
- renewal, cancellation, billing issue or expiry;
- entitlement state and local caching;
- Google Play product configuration;
- RevenueCat Android SDK integration;
- RevenueCat webhooks or server notifications;
- commercial analytics;
- account/authentication introduced for an approved feature;
- cloud backup;
- synchronization;
- multi-device state;
- cloud photos;
- entitlement backend;
- behavior after downgrade or subscription expiry;
- commercial access to future advanced offline/network capabilities.

Do not use this playbook as justification to build these systems before they are explicitly in scope.

---

## 3. Required sources

Always consult:

- `WHERIS_BUSINESS_REFERENCE.md` — commercial source of truth;
- `WHERIS_MASTER.md` — product/architecture scope;
- `RULES.md` — mandatory implementation constraints;
- `SECURITY_PRIVACY.md` — mandatory security/privacy constraints;
- `AGENT.md` — decision discipline.

For UX/flows consult, as applicable:

- `USER_STORIES_REFERENCE.pdf`;
- `DESIGN/01_DESIGN_FOUNDATIONS.md`;
- `DESIGN/03_DESIGN_COMPONENTS.md`;
- `DESIGN/04_SCREEN_CATALOG.md`;
- `DESIGN/05_USER_FLOWS.md`;
- `DESIGN/06_FIGMA_BUILD_RULES.md`;
- `DESIGN/07_SCREEN_SPECIFICATIONS.md`.

For purchase/provider behavior, verify current official documentation at implementation and release time, especially:

- RevenueCat Android SDK documentation;
- RevenueCat Entitlements;
- RevenueCat non-subscription purchases;
- RevenueCat Offerings / Packages;
- RevenueCat restoring purchases;
- RevenueCat customer identification / App User IDs;
- RevenueCat restore behavior;
- RevenueCat CustomerInfo caching;
- RevenueCat Platform Server Notifications / Google integration;
- RevenueCat pricing;
- current Google Play Billing and product requirements.

Do not rely on a previously remembered SDK version, pricing rule or provider behavior when the decision is consequential.

---

## 4. Source-of-truth boundaries

### 4.1 Business Reference owns

- offer structure;
- current status of Free / Wheris Plus / Wheris Premium;
- current test hypotheses for prices and limits;
- entitlement meaning;
- downgrade semantics;
- commercial KPIs and experiments;
- financial assumptions;
- whether RevenueCat is the current target provider.

### 4.2 Security/Privacy owns

- whether and how sensitive data may leave the device;
- consent and opt-in;
- authentication/session security;
- authorization;
- encryption expectations;
- retention/deletion;
- logging and analytics constraints;
- trust boundaries;
- cloud-photo and cloud-place handling;
- what may be transmitted to RevenueCat or other third parties.

### 4.3 Product/Architecture owns

- whether monetization/cloud/account implementation is currently in scope;
- module and dependency boundaries;
- local-first invariants;
- canonical local place storage.

### 4.4 Provider configuration owns no Wheris product semantics

RevenueCat Dashboard and Google Play Console are infrastructure configuration, not independent product specifications.

If a RevenueCat Offering, Product, Package or Entitlement conflicts with `WHERIS_BUSINESS_REFERENCE.md`, treat it as configuration drift and resolve it deliberately.

A commercial entitlement is **not** authorization to upload user data.

---

## 5. Target purchase architecture

The target Android purchase path is conceptually:

```text
Google Play Product
        ↓
RevenueCat Product
        ↓
RevenueCat Offering / Package
        ↓
RevenueCat purchase + receipt processing
        ↓
RevenueCat CustomerInfo / Entitlements
        ↓
Wheris purchase infrastructure adapter
        ↓
Wheris EntitlementRepository
        ↓
Neutral Wheris access state
        ↓
Feature/domain policy
```

The application must not collapse this chain into direct SDK checks scattered throughout screens or ViewModels.

### Mandatory boundary

Feature/domain code MUST NOT depend directly on:

- `Purchases`;
- `CustomerInfo`;
- RevenueCat `Offering` or `Package` classes;
- RevenueCat SDK error types;
- Google Play purchase objects;
- Google Play product IDs;
- RevenueCat entitlement IDs as raw business-domain concepts.

Instead expose neutral Wheris concepts, for example:

```text
AccessTier
CommercialAccess
EntitlementState
PurchaseAccess
EntitlementRepository
PurchaseCoordinator
```

Exact naming belongs to implementation design, not this playbook.

RevenueCat must remain replaceable without rewriting the Wheris domain model.

---

## 6. Commercial invariants

Unless the Business Reference is deliberately changed:

1. Wheris remains genuinely usable before payment.
2. Existing local places are never deleted because a Free limit is reached.
3. Existing local places are never deleted because Premium expires.
4. A paywall blocks only the approved commercial action/capability; it does not confiscate user data.
5. Acquired permanent rights are not silently revoked because future prices change.
6. Prices and Free limits are policy/configuration, not immutable domain architecture.
7. Navigation destination `Plus` and commercial product `Wheris Plus` are different concepts.
8. Ads and data resale are not introduced unless the Business Reference explicitly changes.
9. Free-limit experiments must preserve the core trust promise.
10. Business values must not be duplicated across unrelated source files or code as independent truth.
11. Temporary RevenueCat, Google Play or network failure must not destroy or corrupt local geographic memory.
12. Premium purchase does not imply cloud opt-in.
13. RevenueCat receives purchase/entitlement data only; it is not a Wheris geographic-data backend.

Read the current Business Reference for active hypotheses rather than relying on remembered numeric values.

---

## 7. RevenueCat conceptual mapping

The intended model is:

### Wheris Plus

- commercial nature: permanent one-time local unlock;
- Android store nature: one-time purchase;
- RevenueCat nature: **non-consumable**;
- Wheris result: permanent Plus access when the purchase is valid/restored according to provider capabilities.

Do not model Plus as a consumable to simulate lifetime access.

### Wheris Premium

- commercial nature: recurring subscription;
- Wheris result: Premium capabilities while entitlement is active;
- Premium may also grant the local capabilities corresponding to Plus for the active subscription period, according to the Business Reference.

### Important

The exact RevenueCat entitlement identifiers, Google Play product IDs, Offerings and Packages are deployment configuration. Choose stable identifiers deliberately, document them in the implementation/configuration record, and do not leak them as domain language.

---

## 8. EntitlementRepository contract

Before integrating RevenueCat, define a Wheris-owned interface that can answer product questions without exposing provider internals.

It should conceptually support:

- observing current commercial access;
- refreshing commercial access;
- distinguishing known-active, known-inactive and unresolved/error states where product behavior requires it;
- exposing permanent Plus ownership separately from active Premium where needed;
- triggering purchase orchestration through a dedicated boundary;
- restoring purchases;
- test fakes.

Do not expose `CustomerInfo` beyond the RevenueCat/infrastructure layer.

Do not reduce access logic to mutable booleans stored independently from verified provider state.

A local cache may support resilience, but must have explicit ownership and refresh semantics.

---

## 9. RevenueCat initialization procedure

Only when monetization is explicitly activated:

1. Verify the current RevenueCat Android SDK and platform requirements.
2. Add the dependency through the project dependency-management conventions.
3. Configure the SDK at the application/infrastructure boundary.
4. Use only the provider key intended for client-side distribution.
5. Never ship privileged RevenueCat secret API keys or Google service-account credentials in the APK.
6. Decide whether the initial app uses RevenueCat anonymous App User IDs or an approved custom App User ID.
7. Do not introduce a Wheris account merely to satisfy RevenueCat if the approved product remains accountless.
8. Configure logging so production logs do not contain sensitive user or geographic content.
9. Map RevenueCat `CustomerInfo` into neutral Wheris commercial state.
10. Add deterministic fakes before feature code depends on the abstraction.

RevenueCat SDK initialization must not make local-place persistence dependent on provider availability.

---

## 10. Anonymous identity and App User IDs

The initial local-first product may use RevenueCat anonymous App User IDs when no Wheris account exists.

Treat the anonymous RevenueCat App User ID as:

- a technical purchase-management identifier;
- not a Wheris account;
- not a permanent human identity guarantee;
- not appropriate for display as user identity;
- not a place to encode email, coordinates, device secrets or other sensitive content.

A reinstall can result in a different anonymous App User ID. Purchase restoration and RevenueCat restore/transfer behavior must therefore be designed and tested intentionally.

If Wheris later introduces accounts and custom App User IDs:

- perform a dedicated identity migration design;
- define login/logout behavior;
- define purchase ownership/transfer behavior;
- define guest-to-account migration;
- review account deletion and entitlement retention;
- review RevenueCat alias/transfer behavior using current official guidance;
- update `SECURITY_PRIVACY.md` before implementation.

Do not change Android Backup policy merely to preserve a RevenueCat anonymous ID without explicit review.

---

## 11. Free-limit procedure

If an approved Free active-place limit is implemented:

1. Read the current rule/status from the Business Reference or the approved configuration source.
2. Define exactly what counts toward the limit.
3. Define when the count is evaluated.
4. Define behavior at and above the limit.
5. Ensure existing places remain readable.
6. Ensure existing places remain editable unless an explicit approved rule says otherwise.
7. Ensure deletion remains possible.
8. Ensure deleting places can restore creation capability according to the approved rule.
9. Ensure verified Plus/Premium access lifts the restriction predictably.
10. Keep the fast Add flow unchanged until the limit condition is actually reached.
11. Measure only the non-sensitive events needed to validate the hypothesis.
12. Test off-by-one, restore, downgrade and offline states.

Never hardcode the current test value into Design System tokens or unrelated domain entities.

---

## 12. Offerings and Packages

RevenueCat Offerings/Packages may be used to configure which store products are presented without hardcoding product selection into screen code.

Use them as **commercial presentation configuration**, not as the owner of Wheris business policy.

Before relying on Offerings:

- define the expected offering purpose;
- define expected package roles;
- define fallback behavior if Offerings cannot be loaded;
- define which Wheris capabilities each resulting entitlement unlocks;
- ensure UI copy distinguishes one-time purchase from subscription;
- ensure a missing/invalid Offering does not make local data inaccessible;
- test empty/misconfigured offerings.

Do not infer product semantics only from a Package identifier or display order.

If RevenueCat Paywalls or Experiments are adopted later, `SKILLS/02_UI_AND_DESIGN.md` and the Business Reference must still own Wheris UX constraints and approved experiment boundaries.

---

## 13. Purchase flow — Wheris Plus

When Plus purchase is active:

1. User reaches an approved commercial entry point.
2. Wheris loads the approved RevenueCat Offering/Package or mapped store product.
3. UI clearly presents Plus as a one-time purchase.
4. User explicitly starts purchase.
5. RevenueCat performs purchase orchestration with Google Play.
6. Wheris waits for the resulting provider state / `CustomerInfo` response.
7. Infrastructure maps active Plus entitlement to Wheris commercial access.
8. Feature policy re-evaluates the blocked action.
9. UI confirms purchase only when the purchase state is appropriately resolved.
10. Local places remain untouched if purchase fails, is cancelled or provider state is unavailable.

Important: RevenueCat's current Offline Entitlements behavior does **not** provide offline-purchase fallback for one-time purchases. Therefore, do not promise that a new Plus lifetime purchase can always be completed/unlocked during a RevenueCat outage. Existing verified/cached access and a new one-time purchase during provider failure are different cases.

---

## 14. Purchase flow — Wheris Premium

When Premium is active:

1. Present the approved subscription option(s).
2. Clearly communicate recurring nature, period and terms according to store/legal requirements.
3. Start purchase only after explicit user action.
4. RevenueCat/Google Play resolve the transaction.
5. Map Premium entitlement into neutral Wheris access state.
6. If Premium implies temporary Plus capabilities, derive that through Wheris policy rather than duplicating UI-specific flags.
7. Premium activation MUST NOT automatically upload local places or photos.
8. Any cloud feature requires its own explicit opt-in and security-reviewed flow.

Subscription cancellation is not the same as immediate expiry. Access behavior must follow the actual entitlement state returned by the provider and approved business rules.

---

## 15. Purchase restoration

When monetization is active, Wheris MUST expose a user-triggered action equivalent to:

> `Restaurer mes achats`

The restore procedure should:

1. Be reachable without requiring the user to purchase again.
2. Invoke RevenueCat's current supported restore mechanism.
3. Re-synchronize applicable transactions from the current Google Play account.
4. Recompute neutral Wheris entitlement state from returned `CustomerInfo`.
5. Explain success/failure calmly without implying geographic-data restoration.
6. Never claim that restoring purchases restores local places/photos.
7. Test reinstall scenarios using anonymous RevenueCat users.
8. Test restore after Plus purchase.
9. Test restore after Premium purchase/expiry.
10. Verify RevenueCat project restore/transfer settings intentionally.

### Android one-time purchase caveat

Current RevenueCat documentation notes restoration limitations for **consumed** one-time purchases with Google Billing Client 8 when relying on anonymous users. Wheris Plus must therefore remain a proper **non-consumable** purchase, and restoration behavior must be revalidated against the current RevenueCat/Google Play implementation before release.

Do not use a consumable purchase for Plus and assume `restorePurchases()` will reconstruct permanent ownership later.

---

## 16. CustomerInfo and cache policy

RevenueCat `CustomerInfo` is the provider representation of current purchase/subscription state. It must be adapted before use by Wheris features.

Current RevenueCat SDK behavior includes local caching of CustomerInfo to reduce network dependency.

Wheris must define:

- when commercial state is read;
- when a refresh is requested;
- behavior when cached data exists;
- behavior when no cached data exists;
- behavior when the provider is temporarily unavailable;
- whether an operation needs a fresh provider result or can safely rely on cached access;
- how UI represents unresolved commercial state.

For already-granted access, temporary network unavailability should degrade gracefully whenever provider behavior permits.

Do not manually copy entitlement truth into unrelated DataStore/Room flags without a clearly defined cache/invalidation model.

Do not confuse RevenueCat entitlement cache with canonical Wheris geographic data.

---

## 17. RevenueCat outage and offline behavior

Design separately for:

### Existing access already known/cached

Wheris should preserve legitimate local usability according to RevenueCat caching behavior and approved access policy.

### Refresh unavailable

Do not delete data, revoke permanent local content blindly, or reinterpret a network error as confirmed entitlement expiry.

### New subscription purchase during RevenueCat failure

Follow current RevenueCat offline-entitlement behavior and test it with sandbox/test-store tooling before making product promises.

### New one-time Plus purchase during RevenueCat failure

Do not assume RevenueCat Offline Entitlements can grant a new one-time purchase offline. Current RevenueCat documentation explicitly distinguishes one-time purchases from supported Offline Entitlements behavior.

### Cloud Premium feature during outage

Even if Premium entitlement remains known, the cloud service itself may be unavailable. Commercial access and service availability are separate states.

---

## 18. Paywall / offer UI procedure

When monetization UI is explicitly active:

- use `SKILLS/02_UI_AND_DESIGN.md`;
- follow Screen Catalog, User Flows and Screen Specifications;
- use current offer names and values from approved configuration/business sources;
- clearly distinguish one-time purchase from subscription;
- explain benefits without claiming unimplemented cloud/security capabilities;
- expose purchase restoration where appropriate;
- communicate cancellation/renewal terms according to current platform requirements;
- never imply existing local data will be deleted if the user does not pay;
- do not make payment UI visually masquerade as a mandatory system dialog;
- preserve accessibility and large-text behavior.

Historical `PREMIUM_*` screen IDs may remain for compatibility. Their semantics come from current product/business docs, not the identifier string.

---

## 19. Refunds, revocations and server notifications

Before production monetization:

- configure current recommended RevenueCat/Google mechanisms for server-side purchase updates where applicable;
- explicitly cover refunds and revocations of one-time purchases;
- explicitly cover subscription cancellation, billing issues and expiry;
- verify the chosen RevenueCat restore/transfer settings against server-notification behavior;
- do not infer refund state solely from an old local cache indefinitely.

If webhooks are used:

- webhook secrets stay server-side;
- verify authenticity according to current provider guidance;
- design idempotent processing;
- do not place geographic user content in webhook payload extensions/metadata;
- record only the minimum business state needed;
- define retry and duplicate-event behavior.

A RevenueCat webhook does not justify building a general Wheris backend unless the approved feature requires one.

---

## 20. Business analytics

Business analytics must obey `SECURITY_PRIVACY.md`.

Prefer coarse events such as:

- paywall impression;
- offer shown;
- purchase started;
- purchase completed;
- purchase cancelled/failed by category;
- entitlement restored;
- Free limit reached;
- coarse place-count threshold if explicitly approved;
- subscription renewal/cancellation outcome where available and appropriate.

Do not send to RevenueCat analytics/Customer Attributes or another analytics provider:

- exact coordinates;
- place names;
- notes;
- photos;
- custom category content;
- sensitive location history;
- raw user-generated content.

Only collect what is necessary to answer a defined business question.

RevenueCat support for optional attribution/customer attributes does not make those fields mandatory or privacy-neutral.

---

## 21. Account/authentication boundary

The local-first MVP does not require a Wheris account.

Do not introduce authentication merely to support a theoretical future business model or to avoid designing anonymous purchase restoration correctly.

If an account becomes necessary for approved cloud/multi-device behavior, define and review:

- identity provider;
- account lifecycle;
- login/session/token storage;
- authorization;
- recovery/revocation;
- account deletion;
- abuse prevention;
- RevenueCat App User ID strategy;
- entitlement-to-account mapping;
- anonymous-to-account purchase migration;
- logout behavior;
- local-only user migration;
- offline behavior.

Do not invent custom authentication cryptography.

---

## 22. Cloud backup / sync boundary

Cloud sync and backup remain outside the current MVP until explicitly activated.

Before implementation define:

- explicit user opt-in;
- authentication and authorization;
- data classes uploaded;
- encryption in transit and at rest as actually supported/claimed;
- conflict-resolution strategy;
- device trust/session behavior;
- deletion semantics;
- retention;
- account deletion;
- backup restore semantics;
- offline queue/retry behavior;
- data residency if relevant;
- cost and quotas;
- downgrade/expiry behavior;
- export/download/grace period if applicable.

Do not expose Room entities directly as network DTOs.

Local canonical data and network representations require deliberate boundaries.

Premium entitlement MUST NOT silently opt the user into uploading local places or photos.

RevenueCat entitlement state is not cloud authorization by itself; cloud access also requires the Wheris account/session/authorization model defined for that service.

---

## 23. Cloud photos

Cloud photo functionality requires a separate review covering:

- explicit upload/opt-in semantics;
- compression/resolution policy;
- EXIF metadata handling;
- object naming/opaque IDs;
- access control/private delivery;
- signed URLs or equivalent where appropriate;
- CDN/cache behavior;
- retention/deletion;
- orphan cleanup;
- account deletion;
- downgrade/expiry behavior;
- storage and egress costs.

Place photos must not become publicly accessible by default.

Do not send photo content or EXIF-derived location to RevenueCat.

---

## 24. Downgrade / expiry

Before shipping Premium, define precisely what happens when:

- the user cancels but remains entitled until the paid period ends;
- subscription renewal fails;
- subscription expires;
- entitlement is revoked/refunded;
- RevenueCat cannot be reached;
- cached entitlement state is stale;
- device is offline;
- cloud account remains but paid entitlement ends;
- user has more local places than the Free limit;
- a Plus owner lets Premium expire;
- a Premium-only user lets Premium expire.

At minimum:

- local places remain preserved;
- an existing Plus lifetime purchase remains Plus after Premium expiry;
- Premium-only users return to the approved Free behavior when Premium actually expires;
- being over the Free limit does not delete or hide places;
- commercial state failure is not treated as geographic-data failure.

Any server-side grace, retention, read-only period, download/export window or deletion behavior must be explicit before implementation.

---

## 25. Google Play / RevenueCat configuration checklist

Before release of monetization, verify in the real production configuration:

- package/application identity;
- Google Play products exist and are active as intended;
- Plus is configured as a non-consumable one-time purchase;
- Premium subscriptions/base plans/offers match the approved model;
- RevenueCat project/app is linked to the correct Play app;
- RevenueCat Products map to the intended Play products;
- Entitlements map to the intended products;
- Offerings and Packages point to the intended products;
- restore/transfer behavior is intentionally configured;
- server notifications are configured where required/recommended;
- production API keys are in the correct environments;
- no privileged secret is in the APK/repository;
- test-store/sandbox purchases have been validated;
- real release build uses expected configuration;
- user-facing prices come from approved store/provider data/configuration rather than stale hardcoded display values where dynamic retrieval is intended.

Never assume Dashboard configuration is correct merely because local mock tests pass.

---

## 26. Testing matrix

When monetization is active, cover as applicable:

### Free / limit

- Free below limit;
- exactly at limit;
- first blocked add attempt;
- delete place then retry;
- off-by-one behavior;
- over-limit user after downgrade.

### Plus

- successful non-consumable Plus purchase;
- cancelled purchase;
- failed purchase;
- pending purchase if applicable;
- purchase result refresh;
- restore Plus on reinstall with same Google Play account;
- RevenueCat anonymous App User ID changed after reinstall;
- provider temporarily unavailable during new one-time purchase;
- existing Plus access with network unavailable;
- refund/revocation.

### Premium

- activation;
- renewal;
- cancellation while still active;
- billing issue;
- expiry;
- restore subscription;
- direct Free → Premium;
- Plus → Premium;
- Premium expiry for Plus owner;
- Premium expiry for non-Plus user.

### RevenueCat state

- CustomerInfo available from cache;
- refresh succeeds;
- refresh fails;
- no cached state;
- Offering load succeeds;
- Offering missing/misconfigured;
- package mapping mismatch;
- listener/state update;
- RevenueCat outage behavior;
- server notification/refund propagation where applicable.

### Data integrity

In every commercial failure state verify:

- local places remain intact;
- local categories remain intact;
- photos remain intact;
- map failure/commercial failure are not conflated;
- cloud opt-in remains separate from purchase;
- failed purchase does not corrupt Add Place draft/state;
- logout/account changes do not silently delete local canonical data.

Use provider sandbox/test products and deterministic local fakes according to current official guidance.

---

## 27. Cross-domain triggers

Load `SKILLS/01_FEATURE_AND_DOMAIN.md` for entitlement/domain/ViewModel integration.

Load `SKILLS/02_UI_AND_DESIGN.md` for paywall, offer, Free-limit, restore-purchase and settings UI.

Load `SKILLS/03_DATA_AND_PERSISTENCE.md` if local schema/cache persistence changes.

Load `SKILLS/04_LOCATION_AND_MAP.md` if a future gated map/offline geographic capability is involved.

Load `SKILLS/05_QUALITY_AND_BUGFIX.md` for purchase/restore/downgrade/cloud regression planning.

Load `SKILLS/06_DEPENDENCIES_AND_RELEASE.md` for RevenueCat SDK, Google Play configuration, current provider requirements, store release and external-service cost/license checks.

Always consult `SECURITY_PRIVACY.md` before introducing accounts, cloud upload, RevenueCat Customer Attributes, webhooks, new analytics destinations or any new transmitted data.

---

## 28. Definition of done

Monetization/cloud work is done only when:

- the feature is explicitly in scope;
- current business rules come from `WHERIS_BUSINESS_REFERENCE.md`;
- RevenueCat/provider configuration matches those rules;
- mutable prices/limits are not duplicated as independent truth;
- RevenueCat remains behind a Wheris-owned infrastructure abstraction;
- entitlement authority, cache, refresh, failure and offline semantics are defined;
- Plus is implemented as the correct non-consumable model;
- purchase restoration is available and tested;
- anonymous identity/reinstall behavior is tested when no Wheris account exists;
- local data is protected across paywall, purchase failure, downgrade, expiry and provider outage;
- security/privacy review covers every new network/account/cloud behavior;
- current Google Play and RevenueCat requirements were verified from authoritative documentation;
- relevant sandbox/store/integration tests passed;
- release configuration was checked in the real dashboards;
- user-facing claims match actual implementation.

---

## 29. Never

Never:

- activate monetization merely because this playbook exists;
- add RevenueCat to the MVP solely because the Business Reference mentions it;
- call RevenueCat SDK directly from feature/domain business logic;
- expose `CustomerInfo`, `Offering`, `Package` or Google purchase objects as domain models;
- hardcode Google Play or RevenueCat identifiers as product-domain semantics;
- model lifetime Plus as a consumable purchase;
- assume consumed one-time purchases can always be restored on modern Android billing;
- assume RevenueCat Offline Entitlements covers new one-time purchases;
- interpret a RevenueCat/network failure as confirmed entitlement expiry;
- delete or hide local places because a purchase, restore or subscription fails;
- silently upload geographic data because Premium is active;
- send saved-place content to RevenueCat Customer Attributes;
- hardcode current prices or limits throughout the application;
- introduce a Wheris account merely to make purchase code easier;
- invent cloud retention/grace/deletion behavior;
- put RevenueCat secret keys or Google service-account credentials in the APK;
- claim encryption, restore guarantees or privacy properties that have not been technically established;
- treat RevenueCat as the source of truth for Wheris geographic memory.

---

## 30. Final principle

RevenueCat exists to answer:

> **What commercial capabilities is this user entitled to use?**

Wheris itself remains responsible for:

> **What does that entitlement mean for the product, and how do we preserve the user's geographic memory safely?**

The purchase system must remain replaceable.  
The user's local geographic memory must remain durable.  
Commercial complexity must never become product-data fragility.
