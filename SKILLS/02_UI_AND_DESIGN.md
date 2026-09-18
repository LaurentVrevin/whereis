# WHERIS — UI & DESIGN PLAYBOOK

**Status:** Canonical operational playbook  
**Owns:** Compose presentation, screen contracts, accessibility, Design System components/patterns and design-to-code consistency.

---

## 1. Mission

Translate approved Wheris product/design behavior into clear, accessible, state-driven Jetpack Compose UI without inventing product rules or leaking business/infrastructure logic into presentation.

This playbook replaces the former Compose-screen and Design-System-component skills.

---

## 2. When to use

Use for:

- new or changed Compose screens;
- screen redesigns;
- dialogs, bottom sheets and transient surfaces;
- loading/empty/error/offline/disabled/selected states;
- accessibility fixes;
- reusable Design System primitives/components;
- UI patterns shared by multiple screens;
- token/theme usage;
- screen-level Figma/Compose parity;
- decisions about keeping a component feature-local versus promoting it to `:core:designsystem`.

---

## 3. Required sources

Always consult:

- `RULES.md`;
- relevant sections of `WHERIS_MASTER.md`;
- `DESIGN/01_DESIGN_FOUNDATIONS.md`;
- `DESIGN/02_DESIGN_TOKENS.md`;
- `DESIGN/03_DESIGN_COMPONENTS.md`;
- `DESIGN/04_SCREEN_CATALOG.md`;
- `DESIGN/05_USER_FLOWS.md`;
- `DESIGN/07_SCREEN_SPECIFICATIONS.md`.

Consult:

- `DESIGN/06_FIGMA_BUILD_RULES.md` when Figma structure/handoff/visual parity matters;
- validated Figma when available for approved appearance;
- `USER_STORIES_REFERENCE.pdf` for user-intent confirmation;
- `SECURITY_PRIVACY.md` for permission, privacy, sensitive-content or network-facing UI;
- `WHERIS_BUSINESS_REFERENCE.md` only when commercial state is actually in scope.

Semantic design docs own behavior. Figma owns validated visual execution. Neither authorizes invention of new product rules.

---

## 4. Entry checks

Before creating UI:

1. Identify the canonical screen/surface ID.
2. Read its flow and screen specification.
3. Inspect neighboring screens and existing components.
4. Search the Design System for an existing primitive/component/pattern.
5. Identify all states that can materially occur at runtime.
6. Confirm whether the change is MVP-active or future/conditional.
7. If the UI depends on a business rule, read the Business Reference instead of inventing a value.
8. If a required screen does not exist in the Screen Catalog, do not silently create it.

---

## 5. Feature UI procedure

### 5.1 Define the renderable state

List relevant runtime states before layout implementation, for example:

- content;
- loading/searching;
- empty;
- error;
- offline;
- unavailable;
- selected;
- disabled;
- poor/unknown GPS accuracy;
- missing optional data;
- long user content;
- large font;
- Light/Dark.

Do not let a static mockup erase real runtime states.

### 5.2 Route / Screen separation

Prefer:

- `Route` — ViewModel/state collection, navigation callbacks, permission/system launchers;
- `Screen` — data + semantic callbacks only.

A screen must not resolve repositories, Koin dependencies or persistence directly.

### 5.3 State ownership

Hoist state when it affects product behavior or must survive recomposition/navigation semantics.

Keep local Compose state only for ephemeral visual interaction that is not business state.

### 5.4 Text/content

- Production user-facing strings belong in Android resources.
- Use canonical Wheris terminology.
- Do not expose implementation terms such as Room, Mapbox, DAO, PinEntity or provider jargon.
- Support French-first UI while avoiding layouts that depend on unusually short labels.
- User-generated names/notes/categories may be long, multiline and international.

### 5.5 Layout

- Phone-first, responsive, not screenshot-fixed.
- Respect system insets.
- Prefer scrolling over clipped content.
- Keep primary actions reachable.
- Handle keyboard-visible layouts for forms.
- Do not shrink controls below comfortable accessible sizes to fit content.

### 5.6 Accessibility

Every meaningful UI change must consider:

- minimum touch target;
- logical focus/read order;
- semantic labels and roles;
- selected/disabled/loading semantics;
- text scaling;
- contrast;
- no color-only meaning;
- icon-only content descriptions;
- reduced-motion comprehension where animation exists.

### 5.7 Theme

Verify Light and Dark.

Use semantic Wheris tokens and approved component mappings.

Do not hardcode repeated colors, typography, spacing or radii in feature screens.

### 5.8 Map/photo backgrounds

Essential text/actions over variable imagery require controlled readable surfaces.

Do not assume a map/photo background provides usable contrast.

---

## 6. Design System procedure

Promote UI into `:core:designsystem` only when its semantics are stable and reusable.

### 6.1 Before promotion

Ask:

1. Does an existing component already solve the problem?
2. Does the pattern repeat or clearly belong to Wheris visual language?
3. Can its API be business-independent?
4. Does centralization improve consistency rather than merely reduce one duplicated line?

If not, keep it feature-local.

### 6.2 Component API

Reusable components should:

- be stateless where practical;
- accept data and semantic callbacks;
- accept `Modifier`;
- consume semantic Wheris tokens;
- support meaningful variants without combinatorial explosion;
- avoid business strings and persistence/provider objects.

### 6.3 Design System boundary

`:core:designsystem` must not know:

- Room/DAO/entity;
- repositories;
- Koin resolution;
- Mapbox;
- Fused Location;
- navigation destinations;
- billing clients;
- entitlement policy;
- business persistence.

It may render neutral display data and visual states.

### 6.4 Previews/tests

Provide previews for states that materially alter appearance:

- Light/Dark;
- selected/unselected;
- enabled/disabled;
- loading where applicable;
- long text;
- optional-data absence;
- with/without image if relevant.

Add Compose tests for non-trivial interaction/semantics, not every static component.

---

## 7. Wheris UI invariants

1. The fast Add Place flow remains visually obvious and operationally short.
2. Optional metadata must look optional.
3. Map failure must never visually imply place loss.
4. GPS uncertainty must be communicated honestly.
5. User agency is preserved for approved low-accuracy flows.
6. Destructive actions are visually separated from frequent primary actions.
7. Category deletion UI must communicate reassignment/preservation, never cascade loss.
8. Core local place access must remain understandable offline.
9. Monetization UI must never imply existing user places are confiscated or deleted.
10. Commercial UI remains outside the active MVP until explicitly activated by product scope.

---

## 8. Monetization presentation boundary

When future monetization UI is explicitly in scope:

- distinguish the navigation destination `Plus` from the commercial offer `Wheris Plus`;
- obtain current offer names, limits, prices, entitlement semantics and downgrade behavior from `WHERIS_BUSINESS_REFERENCE.md`;
- do not encode mutable numeric limits/prices in Design System tokens/components;
- keep paywall/offer components separate from core place-saving controls;
- existing-place access after downgrade/expiry must be represented consistently with business/security rules;
- cloud benefits must not be worded as active unless actually implemented and security-reviewed.

Historical IDs such as `PREMIUM_001` may remain for compatibility if the Screen Catalog retains them; do not infer a Premium-only model from the ID name.

---

## 9. Cross-domain triggers

Load `SKILLS/01_FEATURE_AND_DOMAIN.md` when:

- ViewModel/domain behavior changes;
- UI is introducing a new business state or action.

Load `SKILLS/03_DATA_AND_PERSISTENCE.md` when:

- UI requirements imply schema/query/storage changes.

Load `SKILLS/04_LOCATION_AND_MAP.md` when:

- permission/location acquisition/map provider behavior changes.

Load `SKILLS/05_QUALITY_AND_BUGFIX.md` when:

- fixing a visual/state regression;
- designing important Compose regression coverage.

Load `SKILLS/06_DEPENDENCIES_AND_RELEASE.md` when:

- adding/updating UI libraries or provider SDKs.

Load `SKILLS/07_MONETIZATION_AND_CLOUD.md` when:

- paywall, purchase, subscription, entitlement, downgrade, account or cloud state is involved.

---

## 10. Testing & validation

For significant screen work:

- compile the affected feature and Design System if touched;
- run relevant unit/ViewModel tests;
- add/run Compose tests for meaningful user-observable behavior;
- visually inspect representative states when tooling permits;
- verify Light/Dark;
- verify large font/long content;
- verify semantics/touch targets;
- verify error/offline/fallback behavior.

Tests should assert user-observable outcomes rather than internal Composable structure.

---

## 11. Definition of done

UI work is done when:

- it matches the approved semantic screen/flow contract;
- it uses canonical tokens/components;
- it handles relevant real runtime states;
- it is accessible and responsive;
- it preserves Wheris terminology and North Star;
- business/infrastructure logic has not leaked into generic UI;
- future monetization has not been accidentally activated;
- relevant tests/builds pass or limitations are explicitly reported.

---

## 12. Never

Never:

- copy a screenshot with brittle absolute pixel hacks;
- invent a screen, business rule, price, limit or entitlement in UI;
- hardcode repeated design values that belong to tokens;
- put repository/DAO/Koin/provider/billing logic in stateless screens;
- move business-specific behavior into `:core:designsystem`;
- omit failure/offline states just because the mockup lacks them;
- imply data loss when a map/provider/entitlement fails;
- create speculative Premium screens in the active MVP prototype.
