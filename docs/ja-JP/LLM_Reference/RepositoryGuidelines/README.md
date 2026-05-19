[目次](../../目次.md) > LLM_Reference > RepositoryGuidelines

# RepositoryGuidelines

## 1. 概要

RepositoryGuidelines は、Sansa ecosystem 内の repository ごとの LLM 利用方針 / semantic boundary / repository responsibility を整理するための directory である。

ProjectSansa terminology / architecture authority を前提とし、各 repository は repository-specific extension のみ定義する。

---

## 2. 基本方針

### repository ごとに chat を分離

repository ごとに、最低 1 つ以上の専用 chat を分離する。

cross-repository semantic contamination を避ける。

### terminology authority

shared terminology は ProjectSansa を authority として扱う。

### repository-specific extension

各 repository は、repository 固有 semantics のみ追加する。

### handover operation

cross-repository semantic dependency が存在する場合、関連 repository 側へ申し送り document を残す。

---

## 3. Repository examples

### SansaXR

- runtime
- networking
- OpenXR
- synchronization

### SansaVRM

- schema
- governance schema
- provenance schema

### SansaVRM-Studio-AI

- assembly workflow
- conversion workflow
- AI workflow

### SansaSphere

- trust ecosystem
- verification ecosystem
- provenance ecosystem

---

## 4. Future direction

将来的に、repository ごとの detailed guideline を追加する可能性がある。

```text
RepositoryGuidelines/
├─ SansaXR/
├─ SansaVRM/
├─ SansaVRM-Studio-AI/
└─ SansaSphere/
```

---

[目次](../../目次.md) > LLM_Reference > RepositoryGuidelines