# WHERIS — PROJECT INDEX

**Status:** navigation index only  
**Role:** help humans and agents find the right source quickly.  
**Important:** this file does not override any canonical document.

---

## 1. Project structure

```text
WHERIS/
├── 00_PROJECT_INDEX.md
├── WHERIS_MASTER.md
├── WHERIS_BUSINESS_REFERENCE.md
├── WHERIS_PREVISIONNEL_FINANCIER_36M_V0_3.xlsx
├── AGENT.md
├── RULES.md
├── SECURITY_PRIVACY.md
├── USER_STORIES_REFERENCE.pdf
│
├── DESIGN/
│   ├── 00_DESIGN_INDEX.md
│   ├── 01_DESIGN_FOUNDATIONS.md
│   ├── 02_DESIGN_TOKENS.md
│   ├── 03_DESIGN_COMPONENTS.md
│   ├── 04_SCREEN_CATALOG.md
│   ├── 05_USER_FLOWS.md
│   ├── 06_FIGMA_BUILD_RULES.md
│   └── 07_SCREEN_SPECIFICATIONS.md
│
└── SKILLS/
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

## 2. What belongs at the root

The root contains the project-wide sources of truth and cross-cutting references.

- `WHERIS_MASTER.md` — product vision, scope, architecture direction and MVP boundaries.
- `WHERIS_BUSINESS_REFERENCE.md` — business model, Free / Wheris Plus / Wheris Premium, commercial rules and hypotheses, RevenueCat target architecture.
- `WHERIS_PREVISIONNEL_FINANCIER_36M_V0_3.xlsx` — financial assumptions and 36-month model associated with the Business Reference.
- `AGENT.md` — engineering role, mindset and source hierarchy.
- `RULES.md` — mandatory implementation constraints.
- `SECURITY_PRIVACY.md` — mandatory privacy/security rules and third-party trust boundaries.
- `USER_STORIES_REFERENCE.pdf` — approved user-story reference and MVP/future-story boundary.

Do not move a topic into `DESIGN/` or `SKILLS/` merely because it is used there. Ownership stays with the canonical root document.

---

## 3. DESIGN/

`DESIGN/` contains the semantic UX/UI specification only.

Start with `DESIGN/00_DESIGN_INDEX.md` when the task is primarily about screens, components, flows, tokens or Figma.

Design documents do not own business pricing, entitlement semantics, security policy or engineering architecture.

---

## 4. SKILLS/

`SKILLS/` contains operational engineering playbooks only.

Start with `SKILLS/00_SKILLS_INDEX.md` to select the minimum playbooks required for a task.

Skills explain **how to execute work safely**. They do not redefine product, business, security or design truth.

---

## 5. Recommended reading order by task

### Product or architecture decision

1. `WHERIS_MASTER.md`
2. `RULES.md`
3. `AGENT.md`
4. relevant canonical source for the topic

### UX/UI implementation

1. `DESIGN/00_DESIGN_INDEX.md`
2. relevant `DESIGN/` documents
3. `SKILLS/02_UI_AND_DESIGN.md`
4. another skill only if the task crosses a real subsystem boundary

### Feature implementation

1. `SKILLS/00_SKILLS_INDEX.md`
2. `SKILLS/01_FEATURE_AND_DOMAIN.md`
3. additional playbooks only when required

### Monetization / purchases / RevenueCat

1. `WHERIS_BUSINESS_REFERENCE.md`
2. `RULES.md`
3. `SECURITY_PRIVACY.md`
4. `SKILLS/07_MONETIZATION_AND_CLOUD.md`
5. relevant design documents if user-facing commercial UI is in scope

### Security or cloud change

1. `SECURITY_PRIVACY.md`
2. `RULES.md`
3. `WHERIS_MASTER.md`
4. relevant `SKILLS/` playbook

---

## 6. Naming rules

- Root canonical documents use stable filenames without duplicate suffixes such as `(1)` or `(3)`.
- The business reference uses the stable filename `WHERIS_BUSINESS_REFERENCE.md`; its version is stored inside the document.
- Design documents use the `DESIGN/NN_NAME.md` convention.
- Operational playbooks use the `SKILLS/NN_NAME.md` convention.
- No Wheris-maintained playbook should be named generically `SKILL.md`.
- Internal references should use root-relative canonical paths such as `DESIGN/04_SCREEN_CATALOG.md` or `SKILLS/04_LOCATION_AND_MAP.md`.

---

## 7. Source ownership rule

When documents appear to overlap, use ownership rather than proximity:

- product/scope/architecture → `WHERIS_MASTER.md`;
- commercial model → `WHERIS_BUSINESS_REFERENCE.md`;
- mandatory engineering constraints → `RULES.md`;
- security/privacy → `SECURITY_PRIVACY.md`;
- user-story scope → `USER_STORIES_REFERENCE.pdf`;
- UX/UI semantics → `DESIGN/`;
- execution procedure → `SKILLS/`.

An index or skill may point to a decision, but it must not silently replace the owning source.
