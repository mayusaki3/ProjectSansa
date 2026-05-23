[目次](../目次.md) > 01_ProjectSansa共通事項

# ProjectSansa共通事項

## 1. 概要

ProjectSansa は、Sansa ecosystem 全体における terminology / architecture authority として扱う。

ProjectSansa 自体は implementation repository ではなく、ecosystem-wide shared concepts / governance / federation 方針を扱う。

---

## 2. ProjectSansa の主責務

ProjectSansa は、以下を主責務とする。

- ecosystem architecture
- repository federation
- shared terminology
- shared governance model
- shared provenance model
- ecosystem-wide policy
- federation policy
- repository coordination
- handover coordination

---

## 3. ecosystem-wide policy

### local-first

ローカル環境のみで主要 workflow を成立可能とする。

### offline-first

ネットワーク接続なしでも主要 workflow を継続可能とする。

### creator-first

creator ownership / creator workflow / creator control を重視する。

### optional ecosystem integration

ecosystem integration は optional とし、mandatory online runtime を前提にしない。

---

## 4. shared terminology authority

ProjectSansa は ecosystem-wide shared terminology authority として扱う。

### shared terminology examples

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

各 repository は terminology consumer として扱い、repository-specific extension のみ定義する。

---

## 5. repository role examples

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

---

## 6. LLM 利用方針

### repository ごとに最低 1 chat

repository ごとに、最低 1 つ以上の専用 chat を分離する。

cross-repository semantics divergence を防ぐため、repository responsibility を明確に分離する。

### HLDocS 利用

ドキュメント管理は HLDocS ベースを基本とする。

### shared terminology の申し送り

shared terminology / governance / provenance 系概念は、ProjectSansa 側へ申し送る。

repository 固有用語のみ repository-specific extension として定義する。

### 申し送り運用

cross-repository coordination は、申し送り document により管理する。

repository 間で semantic dependency が存在する場合、関連 repository 側へ申し送りを残す。

---

## 7. repository federation 方針

implementation monorepo 化を避ける。

実装責務は各 repository へ分離する。

### examples

- SansaXR
- SansaVRM
- SansaVRM-Studio-AI
- SansaSphere
- SansaCloth

ProjectSansa は ecosystem-wide coordination を扱う。

---

## 8. 将来想定

将来的に、以下のような shared reference directory が追加される可能性がある。

```text
LLM_Reference/
├─ SharedTerminology/
├─ Governance/
├─ Provenance/
├─ Federation/
└─ RepositoryGuidelines/
```

LLM 間で shared terminology / shared governance semantics を維持するための参照用 directory として扱う。

---

[目次](../目次.md) > 01_ProjectSansa共通事項