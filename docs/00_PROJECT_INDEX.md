# WHERIS — PROJECT INDEX

**Status:** navigation index only  
**Role:** help humans and agents find the right source quickly.  
**Important:** this file does not override any canonical document.

---

## 1. Project structure

```text
WHERIS/
├── AGENT.md
└── docs/
    ├── 00_PROJECT_INDEX.md
    ├── product/
    │   ├── WHERIS_MASTER.md
    │   ├── RULES.md
    │   └── SECURITY_PRIVACY.md
    ├── reference/
    │   ├── WHERIS_BUSINESS_REFERENCE.md
    │   ├── WHERIS_PREVISIONNEL_FINANCIER_36M_V0_3.xlsx
    │   └── USER_STORIES_REFERENCE.pdf
    ├── design/
    │   ├── 00_DESIGN_INDEX.md
    │   ├── 01_DESIGN_FOUNDATIONS.md
    │   ├── 02_DESIGN_TOKENS.md
    │   ├── 03_DESIGN_COMPONENTS.md
    │   ├── 04_SCREEN_CATALOG.md
    │   ├── 05_USER_FLOWS.md
    │   ├── 06_FIGMA_BUILD_RULES.md
    │   └── 07_SCREEN_SPECIFICATIONS.md
    └── engineering/skills/
        ├── 00_SKILLS_INDEX.md
        ├── 01_FEATURE_AND_DOMAIN.md
        ├── 02_UI_AND_DESIGN.md
        ├── 03_DATA_AND_PERSISTENCE.md
        ├── 04_LOCATION_AND_MAP.md
        ├── 05_QUALITY_AND_BUGFIX.md
        ├── 06_DEPENDENCIES_AND_RELEASE.md
        └── 07_MONETIZATION_AND_CLOUD.md
```

---

## 2. Canonical project references

The root contains [AGENT.md](../AGENT.md). Product sources live in `docs/product/`, and cross-cutting references live in `docs/reference/`.

- [docs/product/WHERIS_MASTER.md](product/WHERIS_MASTER.md) — product vision, scope, architecture direction and MVP boundaries.
- [docs/reference/WHERIS_BUSINESS_REFERENCE.md](reference/WHERIS_BUSINESS_REFERENCE.md) — business model, Free / Wheris Plus / Wheris Premium, commercial rules and hypotheses, RevenueCat target architecture.
- [docs/reference/WHERIS_PREVISIONNEL_FINANCIER_36M_V0_3.xlsx](reference/WHERIS_PREVISIONNEL_FINANCIER_36M_V0_3.xlsx) — financial assumptions and 36-month model associated with the Business Reference.
- [AGENT.md](../AGENT.md) — engineering role, mindset and source hierarchy.
- [docs/product/RULES.md](product/RULES.md) — mandatory implementation constraints.
- [docs/product/SECURITY_PRIVACY.md](product/SECURITY_PRIVACY.md) — mandatory privacy/security rules and third-party trust boundaries.
- [docs/reference/USER_STORIES_REFERENCE.pdf](reference/USER_STORIES_REFERENCE.pdf) — approved user-story reference and MVP/future-story boundary.

Do not move a topic into `docs/design/` or `docs/engineering/skills/` merely because it is used there. Ownership stays with the canonical document in its current location.

---

## 3. docs/design/

`docs/design/` contains the semantic UX/UI specification only.

Start with [docs/design/00_DESIGN_INDEX.md](design/00_DESIGN_INDEX.md) when the task is primarily about screens, components, flows, tokens or Figma.

Design documents do not own business pricing, entitlement semantics, security policy or engineering architecture.

---

## 4. docs/engineering/skills/

`docs/engineering/skills/` contains operational engineering playbooks only.

Start with [docs/engineering/skills/00_SKILLS_INDEX.md](engineering/skills/00_SKILLS_INDEX.md) to select the minimum playbooks required for a task.

Skills explain **how to execute work safely**. They do not redefine product, business, security or design truth.

---

## 5. Recommended reading order by task

Repository entry: read [AGENT.md](../AGENT.md) first, then this index. Source priority remains product truth → mandatory rules/security → semantic design → validated Figma as visual truth → implementation. Continue with the relevant sources below.

### Product or architecture decision

1. [docs/product/WHERIS_MASTER.md](product/WHERIS_MASTER.md)
2. [docs/product/RULES.md](product/RULES.md)
3. relevant security/privacy requirements
4. relevant canonical source for the topic

### UX/UI implementation

1. [docs/design/00_DESIGN_INDEX.md](design/00_DESIGN_INDEX.md)
2. relevant `docs/design/` documents
3. [docs/engineering/skills/02_UI_AND_DESIGN.md](engineering/skills/02_UI_AND_DESIGN.md)
4. another skill only if the task crosses a real subsystem boundary

### Feature implementation

1. [docs/engineering/skills/00_SKILLS_INDEX.md](engineering/skills/00_SKILLS_INDEX.md)
2. [docs/engineering/skills/01_FEATURE_AND_DOMAIN.md](engineering/skills/01_FEATURE_AND_DOMAIN.md)
3. additional playbooks only when required

### Monetization / purchases / RevenueCat

1. [docs/reference/WHERIS_BUSINESS_REFERENCE.md](reference/WHERIS_BUSINESS_REFERENCE.md)
2. [docs/product/RULES.md](product/RULES.md)
3. [docs/product/SECURITY_PRIVACY.md](product/SECURITY_PRIVACY.md)
4. [docs/engineering/skills/07_MONETIZATION_AND_CLOUD.md](engineering/skills/07_MONETIZATION_AND_CLOUD.md)
5. relevant design documents if user-facing commercial UI is in scope

### Security or cloud change

1. [docs/product/SECURITY_PRIVACY.md](product/SECURITY_PRIVACY.md)
2. [docs/product/RULES.md](product/RULES.md)
3. [docs/product/WHERIS_MASTER.md](product/WHERIS_MASTER.md)
4. relevant `docs/engineering/skills/` playbook

---

## 6. Naming rules

- Canonical documents use stable filenames without duplicate suffixes such as `(1)` or `(3)`.
- The business reference uses the stable filename [docs/reference/WHERIS_BUSINESS_REFERENCE.md](reference/WHERIS_BUSINESS_REFERENCE.md); its version is stored inside the document.
- Design documents use the `docs/design/NN_NAME.md` convention.
- Operational playbooks use the `docs/engineering/skills/NN_NAME.md` convention.
- No Wheris-maintained playbook should be named generically `SKILL.md`.
- Internal references should use root-relative canonical paths such as [docs/design/04_SCREEN_CATALOG.md](design/04_SCREEN_CATALOG.md) or [docs/engineering/skills/04_LOCATION_AND_MAP.md](engineering/skills/04_LOCATION_AND_MAP.md).

---

## 7. Source ownership rule

When documents appear to overlap, use ownership rather than proximity:

- product/scope/architecture → [docs/product/WHERIS_MASTER.md](product/WHERIS_MASTER.md);
- commercial model → [docs/reference/WHERIS_BUSINESS_REFERENCE.md](reference/WHERIS_BUSINESS_REFERENCE.md);
- mandatory engineering constraints → [docs/product/RULES.md](product/RULES.md);
- security/privacy → [docs/product/SECURITY_PRIVACY.md](product/SECURITY_PRIVACY.md);
- user-story scope → [docs/reference/USER_STORIES_REFERENCE.pdf](reference/USER_STORIES_REFERENCE.pdf);
- UX/UI semantics → `docs/design/`;
- execution procedure → `docs/engineering/skills/`.

An index or skill may point to a decision, but it must not silently replace the owning source.
