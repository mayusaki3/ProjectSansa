[目次](../../README.md) > docs/ja-JP > 仕様 > 00_全体構想 > Shared Terminology Authority

# Shared Terminology Authority

## 1. 概要

ProjectSansa は ecosystem-wide terminology / architecture authority として shared terminology を管理する。

Sansa ecosystem 内の repository 群は、ProjectSansa terminology を参照し、repository-specific extension のみ定義する方向を基本方針とする。

---

## 2. 背景

現在、以下 repository 群で governance / provenance / assembly / trust 系概念が増加している。

- ProjectSansa
- SansaVRM
- SansaVRM-Studio-AI
- SansaSphere
- SansaXR

このまま各 repository が独自定義を進めると、以下のリスクが高い。

- terminology divergence
- governance semantics divergence
- provenance semantics divergence
- workflow incompatibility
- federation incompatibility

---

## 3. terminology authority 方針

### ProjectSansa

ProjectSansa は以下 authority を主責務とする。

- shared terminology authority
- ecosystem authority
- architecture authority
- governance model authority
- provenance model authority

### SansaXX repositories

各 repository は以下を基本責務とする。

- terminology consumer
- architecture consumer
- repository-specific extension provider

---

## 4. shared terminology examples

### governance / provenance

- provenance
- provenance chain
- provenance graph
- governance
- restriction
- ownership
- verification
- trust
- policy

### workflow / assembly

- assembly
- modular assembly
- derivation
- conversion
- component
- creator
- assembler
- distributor

### ecosystem architecture

- local-first
- offline-first
- creator-first
- optional ecosystem integration
- provenance ecosystem
- governance ecosystem
- trust ecosystem
- detection ecosystem

---

## 5. ecosystem-wide policy

以下を ecosystem-wide policy として扱う。

### local-first

ローカル環境のみで主要 workflow を成立可能とする。

### offline-first

ネットワーク接続なしでも主要 workflow を継続可能とする。

### creator-first

creator ownership / creator workflow / creator control を重視する。

### optional ecosystem integration

ecosystem integration は optional とし、mandatory online runtime を前提にしない。

---

## 6. repository role examples

### SansaVRM

- schema authority
- governance authority
- provenance authority

### SansaVRM-Studio-AI

- governance-aware workflow
- provenance-aware workflow
- assembly workflow

### SansaSphere

- trust ecosystem
- provenance ecosystem
- governance ecosystem
- detection ecosystem

### SansaXR

- runtime
- networking
- OpenXR
- session runtime

---

## 7. repository-specific extension 方針

以下は repository-specific extension として扱う。

- runtime-specific semantics
- schema-specific semantics
- workflow-specific semantics
- implementation-specific semantics

shared concepts 自体は ProjectSansa 側へ集約する。

---

## 8. dependency direction

```text
ProjectSansa
= terminology / architecture authority

SansaVRM
= schema / governance / provenance authority

SansaVRM-Studio-AI
= governance-aware workflow

SansaSphere
= trust / verification / detection ecosystem

SansaXR
= runtime / networking / OpenXR
```

---

## 9. Summary

ProjectSansa は ecosystem-wide terminology authority として shared terminology を管理し、各 repository は terminology consumer として整理する。

これにより、cross-repository governance / provenance / workflow semantics を統一し、ecosystem-wide interoperability を維持する。

---

[目次](../../README.md) > docs/ja-JP > 仕様 > 00_全体構想 > Shared Terminology Authority