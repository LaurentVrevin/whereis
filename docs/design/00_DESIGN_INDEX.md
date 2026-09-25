# WHERIS — DESIGN INDEX

**Status:** navigation index only  
**Role:** route UX/UI work to the smallest useful set of canonical design documents.  
**Rule:** this index does not redefine design decisions.

---

## 1. Design document map

### `DESIGN/01_DESIGN_FOUNDATIONS.md`
Use for product-design principles, hierarchy, accessibility posture, responsive foundations, navigation principles and the boundary between MVP design and future monetization.

### `DESIGN/02_DESIGN_TOKENS.md`
Use for canonical design tokens: spacing, typography, shape, elevation, semantic colors and reusable visual values.

### `DESIGN/03_DESIGN_COMPONENTS.md`
Use for reusable UI components, component anatomy, variants, states and component-level consumption of tokens.

### `DESIGN/04_SCREEN_CATALOG.md`
Use to identify canonical screens/destinations, their IDs, ownership and whether they are active MVP, future or conditional surfaces.

### `DESIGN/05_USER_FLOWS.md`
Use for end-to-end journeys, transitions, success/failure paths and relationships between canonical screens.

### `DESIGN/06_FIGMA_BUILD_RULES.md`
Use when building or maintaining the Figma source: naming, structure, components, variants, variables, prototype rules and handoff discipline.

### `DESIGN/07_SCREEN_SPECIFICATIONS.md`
Use for detailed screen contracts: structure, content, states, interactions, accessibility and Android implementation notes.

---

## 2. Fast routing

| Task | Start with | Also consult |
|---|---|---|
| Create or change a screen | `DESIGN/07_SCREEN_SPECIFICATIONS.md` | `DESIGN/04_SCREEN_CATALOG.md`, `DESIGN/05_USER_FLOWS.md` |
| Add a reusable component | `DESIGN/03_DESIGN_COMPONENTS.md` | `DESIGN/02_DESIGN_TOKENS.md` |
| Change a token | `DESIGN/02_DESIGN_TOKENS.md` | affected components/screens |
| Change navigation/destination ownership | `DESIGN/04_SCREEN_CATALOG.md` | `DESIGN/05_USER_FLOWS.md`, `DESIGN/07_SCREEN_SPECIFICATIONS.md` |
| Change a user journey | `DESIGN/05_USER_FLOWS.md` | affected screen specs/catalog |
| Build/update Figma | `DESIGN/06_FIGMA_BUILD_RULES.md` | all semantic docs relevant to the work |
| Monetization UI | `WHERIS_BUSINESS_REFERENCE.md` first | then catalog, flows, specs and `SKILLS/07_MONETIZATION_AND_CLOUD.md` |

---

## 3. Design source boundaries

Design files own UX/UI semantics, not commercial truth or engineering policy.

- Prices, Free limits, entitlement meaning and RevenueCat target strategy come from `WHERIS_BUSINESS_REFERENCE.md`.
- Security/privacy constraints come from `SECURITY_PRIVACY.md`.
- Mandatory implementation constraints come from `RULES.md`.
- Operational implementation procedure comes from `SKILLS/`.

The navigation destination **Plus** and the commercial offer **Wheris Plus** remain distinct concepts.

---

## 4. Cross-document consistency

When a change affects a screen ID, destination ownership or user flow, update all impacted canonical design files together rather than patching one file in isolation.

Do not duplicate token values in component/screen documents when `DESIGN/02_DESIGN_TOKENS.md` already owns them. Do not duplicate full component anatomy in screen specifications when `DESIGN/03_DESIGN_COMPONENTS.md` owns it.

---

## 5. Implementation handoff

After the design contract is settled, use `SKILLS/00_SKILLS_INDEX.md` to choose the implementation playbook. Most UI work starts with `SKILLS/02_UI_AND_DESIGN.md` and adds another playbook only when behavior, persistence, location/map or monetization actually changes.
