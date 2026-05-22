[目次](../目次.md) > 01_ProjectSansa共通事項 > Repository整理方針

# Repository整理方針

## 1. 概要

ProjectSansa は implementation repository ではなく、ecosystem-wide terminology / architecture authority として整理する。

そのため、implementation-heavy repository structure は各 SansaXX repository 側へ分離する。

---

## 2. ProjectSansa に残す責務

ProjectSansa は以下を主責務とする。

- ecosystem architecture
- repository federation
- shared terminology
- governance semantics
- provenance semantics
- federation semantics
- LLM coordination
- repository coordination
- handover coordination
- ecosystem-wide policy

---

## 3. implementation repository との分離

implementation-heavy responsibility は各 repository 側へ分離する。

### examples

#### SansaXR

- runtime
- OpenXR
- networking
- synchronization

#### SansaVRM

- schema
- governance schema
- provenance schema

#### SansaVRM-Studio-AI

- assembly workflow
- AI workflow
- conversion workflow

#### SansaSphere

- account
- authentication
- audit
- logging
- provenance verification
- detection ecosystem

---

## 4. CI 方針

ProjectSansa では implementation CI ではなく documentation governance CI を重視する。

### examples

- markdown link check
- dead link check
- HLDocS validation
- terminology consistency check
- document structure check

---

## 5. scripts 方針

scripts directory は ProjectSansa 側で保持せず、可能な限り HLDocS 側へ責務移管する。

### examples

- document validation
- document generation support
- document structure check
- HLDocS tooling

---

## 6. styleguide 方針

ProjectSansa は implementation repository ではないため、ecosystem-wide coding styleguide を mandatory responsibility としない。

必要になった場合、language-specific structure として再構築する。

### example

```text
styleguide/
├─ cpp/
├─ rust/
├─ go/
├─ java/
└─ python/
```

---

## 7. multilingual policy

### primary authoring language

- ja-JP

### canonical identifier language

- en-US

### localization policy

- English = embedded mandatory language
- other languages = external localization resource

---

## 8. Important policy

ProjectSansa は semantic consistency quality を管理する repository として扱う。

implementation quality は各 implementation repository 側で扱う。

---

[目次](../目次.md) > 01_ProjectSansa共通事項 > Repository整理方針