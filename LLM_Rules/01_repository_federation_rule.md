# Repository Federation Rule

## 概要

ProjectSansa ecosystem は repository federation 構成を採用する。

implementation monorepo 化を避け、repository responsibility を分離する。

---

## 基本ルール

- runtime implementation を ProjectSansa に追加しない
- engine-specific implementation を ProjectSansa に追加しない
- rendering implementation を ProjectSansa に追加しない
- repository responsibility を越えた実装追加を行わない
- implementation は専用 repository に分離する

---

## canonical repository

- ProjectSansa: ecosystem / federation / architecture
- SansaXR: XR runtime / OpenXR / networking
- SansaVRM: avatar format / validator
- SansaCloth: cloth interaction / anti-clipping
- SansaVRM-Studio-AI: AI-assisted tooling

---

## dependency direction

以下を維持する。

```text
SansaXR → SansaVRM
```

以下は原則禁止。

```text
SansaVRM → SansaXR
```

format layer が runtime implementation に依存しない構成を維持する。
