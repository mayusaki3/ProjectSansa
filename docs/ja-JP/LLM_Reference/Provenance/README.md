[目次](../../目次.md) > LLM_Reference > Provenance

# Provenance

## 1. 概要

Provenance は、Sansa ecosystem 内で shared provenance semantics / provenance chain / provenance graph semantics を維持するための reference directory である。

ProjectSansa は provenance terminology authority として扱う。

---

## 2. 目的

cross-repository provenance divergence を防ぐ。

特に以下を対象とする。

- provenance semantics
- provenance chain
- provenance graph
- ownership trace
- conversion trace
- assembly provenance

---

## 3. provenance examples

### asset provenance

- creator provenance
- ownership provenance
- component provenance
- derivation provenance
- conversion provenance

### assembly provenance

```text
body from SansaVRM_A
hair from SansaVRM_B
clothing from SansaVRM_C
```

### AI provenance

- AI generation trace
- generation history
- workflow provenance
- model provenance

---

## 4. Important policy

strict prevention よりも、traceability / provenance visibility / misuse detection を重視する。

mandatory DRM runtime を ecosystem-wide mandatory policy としない。

以下を重視する。

- provenance visibility
- provenance verification
- creator visibility
- governance-aware workflow

---

## 5. Repository relationships

### SansaVRM

- provenance schema
- provenance chain
- ownership semantics

### SansaVRM-Studio-AI

- provenance-aware workflow
- assembly provenance
- AI workflow provenance

### SansaSphere

- provenance verification
- provenance lookup
- misuse detection ecosystem

---

[目次](../../目次.md) > LLM_Reference > Provenance