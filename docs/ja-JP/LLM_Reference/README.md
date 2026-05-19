[目次](../目次.md) > LLM_Reference

# LLM_Reference

## 1. 概要

LLM_Reference は、Sansa ecosystem 内で shared terminology / governance semantics / provenance semantics を維持するための shared reference directory である。

ProjectSansa を terminology / architecture authority として扱い、各 repository は terminology consumer として利用する。

---

## 2. 目的

cross-repository semantic divergence を防ぐ。

特に以下を対象とする。

- governance semantics
- provenance semantics
- assembly semantics
- conversion semantics
- federation semantics
- trust semantics

---

## 3. 想定構造

```text
LLM_Reference/
├─ SharedTerminology/
├─ Governance/
├─ Provenance/
├─ Federation/
└─ RepositoryGuidelines/
```

---

## 4. SharedTerminology

ecosystem-wide terminology を扱う。

### examples

- assembly
- derivation
- conversion
- provenance
- provenance chain
- provenance graph
- governance
- restriction
- creator
- assembler
- distributor
- ownership
- verification
- trust
- policy

---

## 5. Governance

shared governance semantics / restriction semantics / merge semantics を扱う。

---

## 6. Provenance

shared provenance semantics / provenance chain / provenance graph を扱う。

---

## 7. Federation

federation trust / distributed verification / signed audit event を扱う。

---

## 8. RepositoryGuidelines

repository-specific guideline / repository-specific extension policy を扱う。

---

## 9. Important policy

ProjectSansa は shared terminology authority として扱う。

各 repository は terminology consumer として扱い、repository-specific extension のみ定義する。

---

[目次](../目次.md) > LLM_Reference