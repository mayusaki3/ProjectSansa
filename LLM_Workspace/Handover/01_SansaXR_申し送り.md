[目次](../README.md) > docs/ja-JP > 申し送り > SansaXR

# SansaXR 申し送り

## 1. 目的

SansaXR は、ProjectSansa ecosystem における XR runtime / networking / OpenXR 系統を担う canonical repository とする。

ProjectSansa は ecosystem / federation / architecture を主責務とし、runtime implementation を集中保持しない。

---

## 2. SansaXR の責務

SansaXR は以下を主責務とする。

- OpenXR runtime
- VR runtime
- networking
- multiplayer
- world synchronization
- session
- runtime plugin
- controller input
- SteamVR integration
- desktop integration
- runtime abstraction

---

## 3. ProjectSansa との関係

```text
ProjectSansa
└─ ecosystem / federation / architecture

SansaXR
└─ XR runtime / networking / OpenXR
```

ProjectSansa は ecosystem-level repository として維持し、runtime implementation の中心 repository は SansaXR とする。

---

## 4. dependency direction

```text
ProjectSansa
 └─ SansaXR
      └─ depends on SansaVRM
```

以下は原則として避ける。

```text
SansaVRM → SansaXR
```

フォーマット層が runtime 実装へ依存しない構成を維持する。

---

## 5. 引き継ぎ対象

### Runtime

- runtime lifecycle
- runtime backend
- runtime plugin
- runtime abstraction

### XR

- OpenXR
- SteamVR
- HMD
- tracking
- controller

### Networking

- multiplayer
- synchronization
- distributed session
- relay
- cluster connection

### World / Session

- session
- world synchronization
- runtime-side state management
- presence

---

## 6. ProjectSansa 側の整理方針

ProjectSansa は implementation monorepo 化を避ける。

以下のような engine / runtime 固有実装は、原則として ProjectSansa から削除または分離対象とする。

```text
src/GameEngine/
```

SansaXR は ProjectSansa の複製として作成済みであり、runtime implementation の退避先として扱う。

---

## 7. 今後の整理

### Phase 1

- ProjectSansa responsibility 定義
- repository federation 定義
- SansaXR 申し送り

### Phase 2

- runtime implementation 分離
- ProjectSansa ecosystem 化
- repository map 強化

### Phase 3

- SansaXR runtime 再編
- OpenXR runtime canonicalization
- networking 再構成

---

[目次](../README.md) > docs/ja-JP > 申し送り > SansaXR