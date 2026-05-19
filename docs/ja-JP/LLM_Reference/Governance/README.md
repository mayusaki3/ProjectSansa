[目次](../../目次.md) > LLM_Reference > Governance

# Governance

## 1. 概要

Governance は、Sansa ecosystem 内で shared governance semantics / restriction semantics / merge semantics を維持するための reference directory である。

ProjectSansa は governance terminology authority として扱う。

---

## 2. 目的

cross-repository governance divergence を防ぐ。

特に以下を対象とする。

- governance semantics
- restriction semantics
- merge semantics
- inheritance semantics
- conflict semantics

---

## 3. governance examples

### permissions

- assembly_allowed
- conversion_allowed
- redistribution_allowed
- ai_training_allowed

### inheritance

- restriction inheritance
- policy merge
- component policy merge
- assembly policy merge

### diagnostics

- governance diagnostics
- conflict diagnostics
- restriction conflict
- policy visibility

---

## 4. Important policy

strict runtime DRM enforcement を ecosystem-wide mandatory policy としない。

以下を重視する。

- governance visibility
- provenance traceability
- misuse detection
- creator control

strict prevention よりも、traceability / detection ecosystem を重視する。

---

## 5. Repository relationships

### SansaVRM

- governance schema
- restriction schema
- inheritance semantics

### SansaVRM-Studio-AI

- governance-aware workflow
- conflict diagnostics
- assembly workflow

### SansaSphere

- governance verification
- governance visibility
- detection ecosystem

---

[目次](../../目次.md) > LLM_Reference > Governance