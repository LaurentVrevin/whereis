# WHERIS --- SECURITY & PRIVACY

> Mandatory security, privacy and sensitive-data policy for Wheris.

This document complements `docs/product/WHERIS_MASTER.md`,
`docs/reference/WHERIS_BUSINESS_REFERENCE.md`, `AGENT.md` and `docs/product/RULES.md`. It governs
location data, saved places, photos, local storage, permissions, network
communication, secrets, backups, logs, analytics, diagnostics, commercial
entitlement infrastructure and future cloud functionality.

`docs/reference/WHERIS_BUSINESS_REFERENCE.md` may define which capabilities are Free, Plus
or Premium and how commercial eligibility behaves. It does **not** authorize
collection, transmission, retention or deletion of sensitive data. Those
conditions remain owned by this document and require the relevant security and
privacy review before implementation.

When implementation convenience conflicts with this policy, the
privacy-preserving and data-preserving behavior MUST be preferred unless
an explicit reviewed product/security decision says otherwise.

------------------------------------------------------------------------

## 1. Core principles

Wheris handles geographic information that can reveal a home, vehicle,
workplace, habits, campsites, meeting points and other highly personal
information. Location and saved-place data MUST therefore be treated as
sensitive user data.

Wheris follows: **local first, data minimization, least privilege,
explicit purpose, no unnecessary transmission, secure defaults,
user-data preservation, graceful degradation, no hidden tracking, and
privacy by design.**

Security and privacy are feature-design requirements, not release-time
cleanup.

------------------------------------------------------------------------

## 2. Local-first privacy promise

The local-first MVP is based on the product expectation:

> **Tes lieux restent sur ton téléphone.**

For the MVP, no Wheris account or proprietary backend is required to
save and retrieve places. Saved places, categories, notes and local
photos remain locally managed and photos MUST NOT be automatically
uploaded.

Future cloud synchronization, backup or sharing MUST be introduced as an
explicit product capability with appropriate disclosure and security
review. A Premium entitlement MUST NOT silently opt the user into cloud
processing. Product wording MUST accurately reflect actual network and backup
behavior.

Commercial limits, paywalls, subscription expiry or entitlement failures MUST
NOT delete, hide or corrupt canonical local places. Existing local places MUST
remain accessible according to the business rules even when creation of new
places is commercially restricted.

------------------------------------------------------------------------

## 3. Data classification

### Highly sensitive geographic data

Exact latitude/longitude, precise current location, saved-place
coordinates, movement/location history and combinations of location with
timestamps.

### Sensitive user content

Place names, notes, photos, custom category names, timestamps and
metadata associated with a saved place.

### Preferences

Theme, units, onboarding state, preferred navigation application and
similar settings.

### Commercial and entitlement data

Purchase identifiers/references, product identifiers, entitlement state,
subscription status, renewal/expiry metadata and billing diagnostics where
required for an approved monetization implementation. These data MUST be
minimized and MUST NOT be unnecessarily linked to saved-place content or exact
geographic data.

### Secrets

Private API credentials, Mapbox secret/download tokens, signing
credentials, passwords, private keys, CI secrets and service-account
credentials.

Higher sensitivity MUST imply stricter handling. Secrets MUST never be
treated as ordinary application data.

------------------------------------------------------------------------

## 4. Data minimization

Wheris MUST collect and persist only data needed for a defined product
purpose. The agent MUST NOT add collection merely because an API exposes
the information.

Before adding a persisted or transmitted field, determine its
user-facing purpose, required precision, retention, whether persistence
is necessary, whether it must leave the device, and whether a less
sensitive representation would suffice.

Temporary location samples used to obtain a good fix MUST NOT
automatically become a location history.

------------------------------------------------------------------------

## 5. Location acquisition

Location acquisition MUST be connected to a clear foreground user
purpose. Wheris MUST NOT continuously track the user in the background
or create implicit movement history.

Temporary foreground location updates MAY be used to improve a fix, but
MUST stop when the operation ends or no longer needs them. Unnecessary
raw samples SHOULD NOT be retained after the user-relevant location is
selected.

------------------------------------------------------------------------

## 6. Background location

`ACCESS_BACKGROUND_LOCATION` MUST NOT be requested for the current MVP.
Background tracking MUST NOT be recreated indirectly through workers,
alarms, long-running services or foreground services used as a
workaround.

Any future feature requiring background location MUST receive a
dedicated review covering purpose, user benefit, frequency, retention,
disclosure, permission flow, opt-in, battery impact, store policy and
less-invasive alternatives.

------------------------------------------------------------------------

## 7. Location permissions

Wheris MUST follow least privilege. Location permission MUST be
requested only when a feature needs it, with UX explaining the user
benefit.

The application MUST handle permission not requested, coarse/fine access
as applicable, denial, permanent denial/settings-required states and
disabled location services. Denial MUST NOT crash the app or erase saved
places.

Already-saved data SHOULD remain accessible without current-location
permission wherever meaningful. Wheris MUST NOT manipulate users into
granting more precise access than reasonably required.

------------------------------------------------------------------------

## 8. Location accuracy

Coordinates MUST NOT be presented as more precise than the underlying
fix. Accuracy metadata SHOULD be preserved when useful.

Poor accuracy MUST be communicated according to product UX. The app MUST
support product-defined recovery such as waiting for a better fix or
continuing anyway. It MUST NOT fabricate an accuracy value.

------------------------------------------------------------------------

## 9. Saved-place integrity

Saved places are core user-owned data and MUST be protected against
accidental deletion, corruption and destructive migrations.

A place MUST NOT disappear because its category is deleted,
map/network/provider functionality fails, or a marker cannot render.
Deletion MUST follow explicit product behavior.

Identifiers MUST remain stable. Ordinary edits MUST NOT rewrite creation
timestamps. Changes affecting persisted data MUST consider migration and
backward compatibility.

------------------------------------------------------------------------

## 10. Category ownership

A category does **not** own its places. Category deletion MUST NEVER
cascade-delete places.

When an in-use custom category is deleted, referenced places MUST be
transactionally reassigned to a valid selected replacement or the stable
system `Other` category according to the product flow.

System category identities MUST be stable and non-localized. Custom
category names are user content.

------------------------------------------------------------------------

## 11. Room database

Room is the local persistence mechanism for core MVP business data.
Released user data MUST NOT rely on destructive migration as a normal
strategy.

Migrations MUST preserve saved places, category relationships,
favorites, notes, photo references, timestamps and relevant location
metadata. Migration behavior MUST be tested.

Database content MUST NOT be dumped into logs or diagnostic reports.
Android application sandboxing/platform storage protections are the
baseline. Additional encryption MAY be considered only with a defined
threat model and key-management strategy.

The agent MUST NOT claim the database is encrypted unless encryption is
actually implemented and verified.

------------------------------------------------------------------------

## 12. DataStore

DataStore is for preferences, not the primary place/category database.
Sensitive geographic content SHOULD NOT be placed in DataStore for
convenience. Secrets MUST NOT be stored in ordinary DataStore.

------------------------------------------------------------------------

## 13. Photos

Place photos are sensitive user content. For the local-first MVP they
remain local and MUST NOT be automatically uploaded.

Room MUST NOT store full image BLOBs as place fields. Persist a neutral
application-owned photo reference/path/identifier. Domain APIs MUST NOT
use Android `Uri` as the canonical photo model.

Photo ownership and lifecycle MUST be explicit. A physical file MUST NOT
be deleted while still required by another reference/workflow. Temporary
files SHOULD be cleaned up. Local files MUST NOT be exposed through
broad public filesystem paths.

------------------------------------------------------------------------

## 14. Photo metadata

Images can contain location, capture time and device metadata. If Wheris
later exports, uploads or shares photos, EXIF/metadata handling MUST be
reviewed explicitly.

If external transmission does not require sensitive metadata, stripping
unnecessary metadata SHOULD be considered. Purely local photos MUST NOT
have metadata unnecessarily indexed or transmitted.

------------------------------------------------------------------------

## 15. Network transmission

Core saved-place persistence remains local for the local-first MVP.
Sensitive Wheris data MUST NOT be transmitted without an explicit
feature requirement.

A paid entitlement, purchase receipt or active Premium subscription is not by
itself a feature requirement for transmitting saved places, coordinates, notes
or photos. A specific user-facing capability such as explicitly enabled
backup/synchronization must exist and satisfy this policy.

Before introducing such transmission, document: destination, purpose,
exact fields, required coordinate precision, authentication, transport
security, retention, deletion behavior, privacy impact, cost, offline
behavior and user disclosure/consent where appropriate.

Saved coordinates MUST NOT be sent to a Wheris backend merely for
implementation convenience.

------------------------------------------------------------------------

## 16. Transport security

Sensitive production traffic MUST use secure transport. Plaintext HTTP
MUST NOT be introduced for sensitive production data.

Production TLS validation MUST NOT be disabled. Trust-all certificate
managers, hostname-verification bypasses and global cleartext exceptions
are prohibited.

Any development-only exception MUST be narrowly scoped and prevented
from shipping in release builds.

------------------------------------------------------------------------

## 17. Map provider boundary

Map rendering and Wheris user content are separate concerns. A map
provider MUST NOT receive Wheris notes, place names, custom category
names or photos merely to render a map.

Exact user/saved coordinates MUST NOT be added to provider requests
unless technically required by an explicitly adopted provider feature
and reviewed.

Using Mapbox as renderer does not authorize adding Mapbox Search,
Directions or Navigation. Provider attribution, licensing, privacy and
token requirements MUST be respected.

------------------------------------------------------------------------

## 18. OpenStreetMap infrastructure

OpenStreetMap data being open does not imply unlimited production use of
community public tile servers. Any OSM-derived tile/style provider MUST
be evaluated for usage policy, privacy, terms, quotas, attribution,
pricing and retention.

------------------------------------------------------------------------

## 19. External navigation

Delegating navigation intentionally transfers destination data to
another application. This MUST happen only after an explicit user
navigation action.

Only data needed for navigation SHOULD be transferred. Wheris MUST NOT
continuously feed place/location data to external navigation apps. The
UI SHOULD make clear that navigation is delegated externally.

------------------------------------------------------------------------

## 20. Production logs

Production logs MUST NOT contain sensitive Wheris data.

MUST NOT log: - exact latitude or longitude; - complete saved
coordinates; - user place names tied to personal data; - notes; -
sensitive custom content; - local photo paths/content URIs; - API
secrets; - signing secrets; - authentication tokens; - private keys.

Avoid logging whole domain objects whose `toString()` may expose fields.
Location diagnostics SHOULD use sanitized state/error categories and
safe coarse buckets rather than coordinates.

Temporary sensitive debug logging MUST NOT be committed or shipped.

------------------------------------------------------------------------

## 21. Analytics

Analytics MUST be privacy-minimized and MUST NOT be required for core
local functionality.

Business measurement MAY use non-sensitive events such as coarse counters or
threshold events, paywall impressions, purchase/renewal outcomes, billing
errors and retention signals when an approved analytics implementation exists.
Such measurement MUST NOT include exact coordinates, place names, notes,
photos, movement history or payloads capable of reconstructing personal
geographic habits.

If adopted, interaction events MAY describe actions such as place
creation completion or map load failure. They MUST NOT include exact
coordinates, place names, notes, photo paths, custom category names, raw
user text or precise movement history.

Before adding an analytics SDK, review automatic collection,
identifiers, location behavior, consent, retention, data destinations,
privacy policy, maintenance and cost.

------------------------------------------------------------------------

## 22. Crash reporting

Crash reporting MUST minimize sensitive exposure. Reports, breadcrumbs
and custom keys MUST NOT intentionally contain exact coordinates, place
content, notes, photo paths, secrets or tokens.

SDK automatic collection and attached logs/metadata MUST be reviewed.
Sensitive exception messages MUST be sanitized before reporting.

------------------------------------------------------------------------

## 23. User-facing errors

Errors MUST NOT expose stack traces, filesystem paths, secrets, internal
tokens, database internals or sensitive raw provider responses.
Technical diagnostics and user-facing messages SHOULD be separated.

------------------------------------------------------------------------

## 24. Secrets

Secrets MUST NEVER be committed. This includes Mapbox secret/download
tokens, private API keys, passwords, keystore passwords, private signing
keys, service-account credentials and CI credentials.

Use appropriate local properties excluded from version control,
environment variables, CI secret stores and secure release
infrastructure. Documentation examples MUST use obvious non-functional
placeholders.

------------------------------------------------------------------------

## 25. Mapbox credentials

Mapbox runtime/public access tokens and secret/download tokens MUST be
treated differently. Secret tokens MUST NOT be embedded in the APK or
committed.

Runtime client-visible tokens MUST be scoped/restricted where supported
and carry only required privileges. Current official Mapbox credential
guidance MUST be checked when configuration changes.

------------------------------------------------------------------------

## 26. Android signing

Production signing credentials are highly sensitive. Keystore
passwords/private signing credentials MUST NOT be committed or stored in
plaintext Gradle/documentation.

Release signing MUST use an approved secure process. Production signing
material MUST NOT be generated, rotated or replaced casually.

------------------------------------------------------------------------

## 27. Client-visible configuration

Anything embedded in an APK --- including `BuildConfig`, resources and
manifest values --- MUST be assumed retrievable by a determined party.

A credential shipped to the client MUST therefore be designed as
client-visible and appropriately restricted. Obscuring a secret in
generated code is not secure secret storage.

------------------------------------------------------------------------

## 28. Git and secret exposure

`.gitignore` MUST exclude appropriate machine-local and secret-bearing
files such as `local.properties`, secret property files and unmanaged
keystores.

Adding a leaked file to `.gitignore` does not undo exposure. If a real
secret was committed, it MUST be treated as compromised and
rotated/revoked where applicable.

------------------------------------------------------------------------

## 29. Android exported components

Activities, services, receivers and providers MUST follow least exposure
and MUST NOT be exported unless external access is required.

Intent filters/deep links MUST be reviewed. Exported entry points MUST
validate untrusted input and MUST NOT expose sensitive place data
without explicit product behavior and platform safeguards.

------------------------------------------------------------------------

## 30. External input

Intents, deep links, shares, content URIs and data from other
applications MUST be treated as untrusted.

Validate expected schemes/hosts where applicable, identifiers,
coordinate ranges, URI permissions, input size and missing/malformed
values. Never assume an Intent originated from Wheris.

------------------------------------------------------------------------

## 31. FileProvider and content URIs

External file sharing SHOULD use secure Android content-sharing
mechanisms. Raw `file://` paths MUST NOT be used.

URI permissions SHOULD be temporary and scoped. FileProvider
configuration MUST expose only required directories, never the entire
application filesystem for convenience.

------------------------------------------------------------------------

## 32. Clipboard

Sensitive geographic data MUST NOT be automatically copied to the
clipboard. Explicit user copy actions SHOULD be clear. Clipboard MUST
NOT be used as an internal transport mechanism for sensitive data.

------------------------------------------------------------------------

## 33. Screenshots

Wheris does not need to globally block screenshots by default.
`FLAG_SECURE` MUST NOT be applied globally without a defined threat
model because it affects legitimate workflows/accessibility.

If a future screen requires protection, the decision SHOULD be scoped
and documented.

------------------------------------------------------------------------

## 34. Notifications

Future notifications MUST minimize sensitive content visible on lock
screens. Exact coordinates MUST NOT appear in notifications unless
explicitly required and justified.

Time-based reminders MUST NOT unnecessarily require location.
Location-triggered reminders require a separate
background-location/privacy review.

------------------------------------------------------------------------

## 35. Android backup --- mandatory review

Android backup behavior MUST be an explicit decision before production
release.

The project MUST NOT assume that "local only" automatically means "never
leaves the device." Platform backup/restore can copy application data
depending on configuration and Android behavior.

Before release, review backup/data-extraction behavior for: - Room
databases; - DataStore/preferences; - local photos; - caches; -
temporary files; - credentials/configuration.

The project MUST explicitly decide what is eligible, excluded and
restorable, then configure Android backup/data-extraction rules
accordingly. Privacy wording MUST match the chosen behavior.

If RevenueCat is later integrated, its locally cached anonymous App User ID /
CustomerInfo state MUST be included in this same backup review. Wheris MUST NOT
change Android backup eligibility merely to preserve RevenueCat state without
an explicit privacy/recoverability decision. Purchase restoration remains a
separate required user path.

------------------------------------------------------------------------

## 36. Backup trade-off

If saved places are backup-eligible, copies may exist under
platform-controlled backup infrastructure. If excluded, uninstall/device
loss may permanently remove local data.

This privacy-versus-recoverability trade-off MUST NOT be decided
accidentally by manifest defaults. Local photos require the same review.

------------------------------------------------------------------------

## 37. Export

Future export MUST be explicit and user-initiated. The UI MUST
communicate what is exported.

Exports containing coordinates, notes or photos are sensitive. Review
format, metadata, destination, temporary-file cleanup, secure content
URI sharing and post-export loss of Wheris control.

Wheris MUST NOT silently export user data to shared storage.

------------------------------------------------------------------------

## 38. Import

Future imported data MUST be treated as untrusted. Validate file
type/size, coordinate ranges, fields, identifiers, category references,
text limits and malformed content.

Import MUST NOT silently overwrite existing places.

------------------------------------------------------------------------

## 39. Deletion semantics

Deleting a place MUST delete only the intended place and follow the
defined lifecycle for resources exclusively owned by it.

Deleting a category MUST NEVER delete places.

A future "delete all Wheris data" action SHOULD explicitly define
deletion of saved places, custom categories, owned local photos,
relevant preferences and caches.

Wheris MUST NOT claim deletion from external services unless that
deletion is actually requested/handled according to those services'
capabilities.

------------------------------------------------------------------------

## 40. Uninstall and recoverability

Application-private local data is generally removed on uninstall,
subject to platform backup/restore and storage choices.

Wheris MUST NOT promise recoverability after uninstall/device loss
unless a real backup/sync mechanism provides it.

------------------------------------------------------------------------

## 41. Retention

Temporary data SHOULD have short lifetimes. Avoid indefinite retention
of temporary photos, raw location samples, exports, obsolete caches and
transient diagnostics.

If server-side functionality is introduced, each server-side data class
MUST receive an explicit retention/deletion policy.

------------------------------------------------------------------------

## 42. Cache policy

Caches MUST NOT become hidden canonical stores of sensitive data.
Clearing caches MUST NOT delete canonical saved-place records.

Canonical user data and caches MUST remain conceptually separate.
Sensitive domain objects SHOULD NOT be duplicated into caches without
need.

------------------------------------------------------------------------

## 43. In-memory data

Long-lived ViewModels/singletons MUST NOT unintentionally accumulate
location history. Global state MUST NOT become an undocumented store of
previous locations.

Sensitive objects SHOULD be retained only as long as required by current
functionality and canonical saved state.

------------------------------------------------------------------------

## 44. Input validation

Coordinates MUST be validated for legal geographic ranges. NaN/infinite
numeric values MUST NOT silently enter calculations or persistence.

User text SHOULD have reasonable limits where necessary without
rejecting legitimate international content. IDs, not display-name
uniqueness, SHOULD provide category/place identity.

------------------------------------------------------------------------

## 45. Identifiers

Identifiers SHOULD be stable and opaque. They SHOULD NOT encode
coordinates, place names, notes, email addresses or other sensitive
content.

------------------------------------------------------------------------

## 46. Authentication

The local-first MVP does not require a Wheris account. Authentication
MUST NOT be introduced merely as speculative infrastructure.

A Wheris account MUST NOT be made mandatory solely to restore a non-recurring
local purchase when Google Play + the approved purchase infrastructure can
securely restore the entitlement without a proprietary account. RevenueCat MAY
use an anonymous App User ID while Wheris has no account system; that identifier
MUST NOT be presented as a Wheris account or treated as guaranteed durable
identity across uninstall/reinstall. Future Premium cloud services MAY require
identity, but only after their authentication/authorization model is explicitly
approved.

Future accounts require dedicated review of identity provider,
credentials, session/token storage, authorization, recovery, revocation,
deletion and abuse prevention. Wheris MUST NOT invent custom
authentication cryptography.

------------------------------------------------------------------------

## 47. Future cloud synchronization

Cloud sync is outside the current MVP. The Business Reference may classify
cloud backup/synchronization as a Premium benefit, but that commercial
classification does not make the feature security-approved. Before
implementation define consent/opt-in policy, authentication, authorization,
encryption in transit, conflict resolution, deletion, retention, backup,
device trust, data residency where relevant, cost and offline behavior.

Subscription expiry behavior for server-side data MUST be explicitly defined
before implementation. The current Business Reference intentionally leaves
cloud grace period, download/export window and eventual server-side deletion
policy open; implementation MUST NOT invent such a policy.

Room entities MUST NOT simply become network DTOs. Persistence, domain
and network boundaries SHOULD remain distinct.

------------------------------------------------------------------------

## 48. Future cloud photos

Cloud photo storage requires separate review covering explicit upload,
compression, EXIF metadata, access control, private/signed delivery,
retention, deletion, orphan cleanup, account deletion, CDN/cache
behavior and cost.

Place photos MUST NOT be publicly accessible by default.

------------------------------------------------------------------------

## 49. Future sharing

Sharing a place is deliberate disclosure of geographic information. It
MUST be explicit and define which fields are shared: coordinates, name,
note, category, photo and/or timestamp.

Prefer the minimal representation. Private content MUST NOT become
discoverable through guessable public identifiers. Share links require
authorization/revocation design.

------------------------------------------------------------------------

## 50. Third-party SDK review

Every new third-party SDK MUST be reviewed for automatic data
collection, location, identifiers, network destinations, permissions,
privacy policy, licensing, maintenance, vulnerabilities,
size/performance, defaults, ability to disable collection and cost.

An SDK is not privacy-neutral merely because Wheris does not explicitly
call a tracking API.

### RevenueCat-specific privacy and security requirements

When RevenueCat is explicitly activated for monetization, it is an approved
**purchase/entitlement processor**, not a recipient of Wheris geographic
content. The integration MUST follow these constraints:

- RevenueCat may receive the minimum technical and transaction information
  required to manage purchases and entitlements, including an App User ID,
  store transaction/purchase information and device/application technical
  information as defined by RevenueCat's current service behavior;
- Wheris MUST NOT send exact coordinates, saved-place names, notes, photos,
  custom category names, location history or other sensitive geographic
  content to RevenueCat as Customer Attributes, subscriber attributes,
  metadata, webhook payload enrichment or analytics identifiers;
- optional attribution or advertising identifiers MUST NOT be collected merely
  because RevenueCat supports them. Any such collection requires a documented
  purpose, applicable consent/policy review and an update to user-facing
  privacy disclosures;
- while Wheris has no proprietary account, anonymous RevenueCat App User IDs
  MAY be used. They MUST be treated as provider identifiers, not user identity;
- a future custom RevenueCat App User ID MUST be opaque and MUST NOT encode an
  email address, location, place name or other sensitive content. Its lifecycle
  must match the approved Wheris account model;
- Wheris Plus MUST be configured as a non-consumable one-time product. This is
  both a commercial and recoverability requirement;
- Wheris MUST expose a user-triggered purchase restoration flow when
  monetization is active. Restoration failure MUST NOT delete or corrupt local
  places;
- RevenueCat CustomerInfo / entitlement cache is not canonical geographic data
  and MUST NOT become a hidden store of Wheris place content;
- Google/RevenueCat server notification configuration SHOULD be used where
  recommended for timely refunds, voids and subscription-state changes, but no
  Wheris geographic content should be added to those flows;
- RevenueCat's public application SDK key MAY be shipped only as intended by
  the provider. Privileged Google service credentials, RevenueCat secret keys,
  webhook secrets or server credentials MUST NOT be embedded in the APK;
- adding RevenueCat integrations, webhooks or downstream analytics destinations
  requires a fresh third-party data-flow review.

Current provider privacy/technical claims MUST be verified from official
RevenueCat documentation and privacy materials at implementation/release time.

------------------------------------------------------------------------

## 51. Dependency security

Dependencies SHOULD come from trusted/official sources. Avoid abandoned
or unnecessary libraries for security-sensitive work.

Security updates SHOULD be deliberate. Reported vulnerabilities MUST be
evaluated against the actual version and code path used rather than
ignored or treated without context.

------------------------------------------------------------------------

## 52. Cryptography

The agent MUST NOT invent cryptographic algorithms/protocols. Use
established platform or well-reviewed solutions when cryptography is
genuinely required.

Do not claim encryption without defining encrypted data, key storage and
threat model. Encoding, hashing and encryption are distinct. Base64 is
not encryption.

------------------------------------------------------------------------

## 53. Device threat assumptions

Android sandbox/platform protections are the baseline, not a guarantee
against a rooted or compromised device.

Wheris MUST NOT claim complete protection against compromised-device
scenarios. Security claims MUST accurately reflect realistic application
controls.

------------------------------------------------------------------------

## 54. Accessibility and privacy

Privacy/security controls MUST remain accessible. Permission
explanations, deletion confirmations and privacy choices MUST support
screen readers, adequate touch targets and font scaling.

Security measures SHOULD NOT unnecessarily block legitimate assistive
workflows.

------------------------------------------------------------------------

## 55. Developer and demo data

Tests, screenshots, fixtures, sample databases and committed demos MUST
use synthetic data rather than developers' real homes, places, photos,
notes or credentials.

Debug menus MUST NOT expose production secrets. Debug-only capabilities
MUST NOT silently remain enabled in release builds.

------------------------------------------------------------------------

## 56. CI/CD

CI/CD MUST inject credentials through secure secret mechanisms and MUST
NOT echo them. Release pipelines SHOULD separate source, build
artifacts, signing material and provider credentials.

Production credential access SHOULD follow least privilege. Secrets MUST
NOT be hardcoded in committed workflow files.

------------------------------------------------------------------------

## 57. Release configuration

Release builds MUST be reviewed separately from debug builds. Verify
that debug logging/endpoints/network exceptions are absent, secrets are
handled correctly, exported components are intentional, permissions are
minimal, backup rules are deliberate, tokens are scoped, files are not
broadly exposed, and diagnostics/third-party collection match policy.

------------------------------------------------------------------------

## 58. Release security checklist

Before production release, review at minimum:

### Manifest

Permissions, exported components, providers, services, receivers, deep
links, backup/data extraction and cleartext traffic.

### Secrets

Repository, Gradle properties, BuildConfig/resources, manifest
placeholders, CI and signing configuration.

### Storage

Room migrations, DataStore, photos, temporary files, caches and backup
eligibility.

### Location

No unapproved background access; updates stop correctly;
accuracy/denial/disabled states work.

### Network

Destinations understood; TLS enforced; sensitive payloads minimized;
provider behavior understood.

### Diagnostics

No exact coordinates, notes, photo paths or secrets in logs/crash
metadata.

### Third parties

SDKs, permissions, collection, licenses and attribution reviewed.

------------------------------------------------------------------------

## 59. Privacy documentation

User-facing privacy documentation MUST describe actual implementation
behavior.

Claims that data stays local MUST be reconciled with map/network
requests and Android backup behavior. Documentation SHOULD be revisited
when adding accounts, analytics, crash reporting, cloud sync, sharing,
photo upload, billing/entitlement providers such as RevenueCat, or new location/map services.

------------------------------------------------------------------------

## 60. Store and legal review

Before release or permission/data-collection changes, current official
Android/Google Play requirements SHOULD be checked. When monetization is
activated, current billing, subscription, purchase restoration, cancellation,
price-change, disclosure and regional consumer requirements MUST also be
verified against authoritative sources.

Historical policy knowledge and financial-planning assumptions MUST NOT be
assumed current. This engineering policy does not substitute for professional
legal advice where legal review is needed.

------------------------------------------------------------------------

## 61. Incident response

If a security/privacy issue is found: 1. identify affected data and
paths; 2. stop further exposure where possible; 3. preserve useful
non-sensitive diagnostics; 4. rotate/revoke exposed credentials where
applicable; 5. fix the root cause; 6. add regression protection; 7.
review similar paths; 8. document material follow-up.

A committed secret remains exposed even after deletion from the latest
file revision and MUST be rotated/revoked as appropriate.

------------------------------------------------------------------------

## 62. No silent security downgrade

Security/privacy controls MUST NOT be silently weakened to fix build,
emulator, certificate, provider or permission problems.

Prohibited production shortcuts include: - disabling TLS verification; -
globally enabling cleartext; - exposing all FileProvider paths; -
exporting components without need; - hardcoding privileged secrets; -
broadening permissions without purpose; - destructive migration; -
logging exact coordinates.

Any development exception MUST be explicitly scoped and prevented from
reaching release.

------------------------------------------------------------------------

## 63. No false security claims

The agent MUST NOT claim: - data is encrypted without verified
encryption; - data never leaves the device without reviewing network and
backup behavior; - a token is secret because it is in BuildConfig; -
Wheris is legally compliant solely from technical implementation; - the
application is completely secure.

Security statements MUST be precise and evidence-based.

------------------------------------------------------------------------

## 64. Security decision records

Consequential decisions SHOULD be documented, especially backup
eligibility, analytics/crash reporting, cloud opt-in, new permissions,
sharing authorization and photo handling.

Record the decision, reason, alternatives, security/privacy impact and
implementation consequences so future agents do not accidentally reverse
intentional safeguards.

------------------------------------------------------------------------

## 65. Future threat-model triggers

A dedicated threat-model review SHOULD precede accounts, cloud sync,
public/private sharing, background location, location history,
collaboration, server-side storage, cloud photos, web access, third-party
authentication, billing integrations that introduce new trust boundaries or
entitlement backends.

Review assets, actors, trust boundaries, entry points, abuse cases,
consequences and mitigations. Do not prematurely build security
infrastructure for features that do not yet exist.

------------------------------------------------------------------------

## 66. Feature security acceptance criteria

For any feature involving sensitive data, be able to answer: - What
sensitive data does it use? - Where is it stored? - Does it leave the
device? - Which third party receives it? - Which permission is
required? - What is retained afterward? - What happens offline/on
failure/on deletion? - Can logs or backups expose it? - Can another
Android app access it? - Is there a less invasive design?

An undocumented privacy/security change requires review before the
feature is considered complete.

------------------------------------------------------------------------

## 67. Wheris security invariants

The following are non-negotiable for the current architecture:

1.  Saving a place MUST NOT require a Wheris backend.
2.  Category deletion MUST NEVER delete places.
3.  Background location MUST NOT be introduced for the MVP.
4.  Exact coordinates MUST NOT appear in production logs.
5.  Notes and local photo paths MUST NOT appear in production logs.
6.  Privileged secrets MUST NOT be committed or embedded in the APK.
7.  Local photos MUST NOT be uploaded automatically.
8.  Sensitive geographic/user content MUST NOT be sent to analytics.
9.  Map failure MUST NOT destroy canonical local data.
10. Destructive database migration MUST NOT be a production shortcut.
11. New permissions MUST follow least privilege.
12. External navigation MUST be user-initiated.
13. Network transmission of saved-place content requires explicit
    purpose.
14. Android backup behavior MUST be consciously reviewed before release.
15. Security controls MUST NOT be silently weakened for development
    convenience.
16. A paywall, entitlement failure or subscription expiry MUST NOT delete,
    hide or corrupt canonical local places.
17. Premium status MUST NOT silently opt the user into cloud upload, sync or
    backup.
18. Commercial analytics MUST NOT contain sensitive geographic/user content.
19. RevenueCat or another billing provider MUST NOT receive saved-place
    content, exact coordinates, notes, photos or custom category names as
    customer attributes or monetization metadata.
20. Loss, staleness or temporary unavailability of entitlement state MUST NOT
    delete, hide or corrupt local user data.
21. Purchase restoration MUST be available when monetization is active, but
    restoration MUST remain logically separate from geographic-data backup.

------------------------------------------------------------------------

## 68. Final principle

The user's geographic memory is the asset Wheris exists to protect and
make useful.

The implementation MUST preserve:

> **Confidentiality** --- geographic information is not exposed
> unnecessarily.
>
> **Integrity** --- saved places are not silently corrupted, replaced or
> destroyed.
>
> **Availability** --- essential local place information remains usable
> when network, map or external services fail.

Wheris should know only what it needs, retain it only as required,
transmit it only when a feature truly requires it, and never treat a
user's location as ordinary telemetry.
