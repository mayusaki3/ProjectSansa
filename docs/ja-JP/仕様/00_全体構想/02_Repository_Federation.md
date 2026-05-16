[目次](../../README.md) > docs/ja-JP > 仕様 > 00_全体構想 > Repository Federation

# Repository Federation

## 1. 概要

ProjectSansa ecosystem は、repository federation 構成を採用する。

ProjectSansa 自体は ecosystem / federation / architecture を主責務とし、実装 monorepo 化を避ける。

runtime / format / AI / rendering などの implementation は、専用 repository へ分離する。

---

## 2. repository 構成

```text
ProjectSansa
├─ ecosystem architecture
├─ federation
├─ repository map
├─ interoperability
├─ compatibility
└─ roadmap

SansaXR
├─ XR runtime
├─ OpenXR
├─ networking
├─ multiplayer
└─ runtime implementation

SansaVRM
├─ avatar format
├─ validator
├─ canonicalization
└─ adapters

SansaVRM-MuJoCo-Adapter
├─ MuJoCo adapter
├─ MJCF integration
├─ diagnostics
└─ schema validation

SansaVRM-Studio-AI
├─ AI tooling
├─ image→3D
└─ local AI integration

SansaCloth
├─ anti-clipping
├─ cloth interaction
├─ shader abstraction
└─ rendering integration
```

---

## 3. canonical responsibility

### ProjectSansa

ecosystem / federation / architecture の canonical repository。

### SansaXR

XR runtime / networking / OpenXR の canonical repository。

### SansaVRM

avatar format / validator / canonicalization の canonical repository。

### SansaVRM-MuJoCo-Adapter

MuJoCo integration の canonical repository。

### SansaVRM-Studio-AI

AI-assisted avatar tooling の canonical repository。

### SansaCloth

cloth interaction / anti-clipping / rendering integration の canonical repository。

---

## 4. dependency direction

```text
ProjectSansa
 ├─ SansaXR
 │   └─ depends on SansaVRM
 │
 ├─ SansaVRM
 │
 ├─ SansaVRM-MuJoCo-Adapter
 │   └─ depends on SansaVRM
 │
 ├─ SansaVRM-Studio-AI
 │   └─ depends on SansaVRM
 │
 └─ SansaCloth
     └─ depends on SansaVRM
```

以下は原則として避ける。

```text
SansaVRM → SansaXR
```

フォーマット層が runtime implementation に依存しない構成を維持する。

---

## 5. implementation 分離方針

ProjectSansa では、以下を原則として保持しない。

- engine-specific implementation
- runtime implementation
- rendering implementation
- single repository specific implementation

以下のような engine / runtime 実装は分離対象とする。

```text
src/GameEngine/
```

実装成果物は、必要に応じて SansaXR などの専用 repository へ移行する。

---

## 6. 目的

repository federation 構成により、以下を実現する。

- implementation monorepo 化回避
- repository 責務分離
- independent evolution
- dependency explosion 抑制
- runtime / format / AI / rendering の独立進化
- ecosystem-level interoperability 維持

---

[目次](../../README.md) > docs/ja-JP > 仕様 > 00_全体構想 > Repository Federation