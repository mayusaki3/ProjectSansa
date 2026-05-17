# Project Sansa（プロジェクトサンサ）とは

ProjectSansa（プロジェクトサンサ、以下 Sansa）は、VR / AR / MR / 非XR を横断する xR ecosystem / federation 構想です。  
「現実世界」「仮想世界」「理想」が交差する三叉をイメージして名付けました。  
個人プロジェクトとして開始していますが、面白そうと思ったら誰でも参加歓迎です。

Discord  
https://discord.gg/wN67tdzrCT

---

# 実現したいこと

最終的には、スマートフォンに代わる新しいパーソナルデバイスとサービス基盤を作りたいと考えています。

Sansa では、VR / AR / MR / 非XR を1つのサービスとして扱います。

- 非XR: 通話・コミュニケーション
- AR: 現実空間との情報重畳
- VR: 仮想空間SNS
- MR: 現実空間と仮想空間の融合

これらは簡単な操作で切り替えられ、現実世界と仮想世界を自然につなぐことを目指しています。

また、Sansa 単体ではなく、他の VRSNS やサービスとも接続・連携できるオープンな構成を目指しています。

---

# システム構想

ProjectSansa は ecosystem / architecture / federation を主責務とし、単一 runtime や engine 実装を集中管理する monorepo とはしない方針を採用しています。

実装は repository federation 方式で分離します。

```text
ProjectSansa
├─ ecosystem architecture
├─ federation
├─ interoperability
└─ repository map

SansaSphere
├─ account
├─ profile
├─ audit
├─ logging
├─ provenance
├─ analytics
└─ economy

SansaXR
├─ XR runtime
├─ networking
└─ OpenXR

SansaVRM
├─ avatar format
├─ validator
└─ adapters

SansaVRM-MuJoCo-Adapter
├─ MuJoCo integration
└─ simulation bridge

SansaVRM-Studio-AI
├─ AI tooling
└─ avatar generation

SansaCloth
├─ anti-clipping
└─ cloth interaction
```

---

## ドキュメント

### 日本語ドキュメント目次

- [docs/ja-JP/目次.md](./docs/ja-JP/目次.md)

### 全体構想

- [ProjectSansa全体構想](./docs/ja-JP/仕様/00_全体構想/01_ProjectSansa全体構想.md)
- [Repository Federation](./docs/ja-JP/仕様/00_全体構想/02_Repository_Federation.md)

---

## 関連 repository

### SansaSphere

account / profile / audit / logging / provenance / analytics 系統。

https://github.com/mayusaki3/SansaSphere

### SansaXR

XR runtime / networking / OpenXR 系統。

https://github.com/mayusaki3/SansaXR

### SansaVRM

avatar format / validator / canonicalization 系統。

https://github.com/mayusaki3/SansaVRM

### SansaVRM-MuJoCo-Adapter

MuJoCo adapter / MJCF integration 系統。

https://github.com/mayusaki3/SansaVRM-MuJoCo-Adapter

### SansaVRM-Studio-AI

AI-assisted avatar tooling 系統。

https://github.com/mayusaki3/SansaVRM-Studio-AI

### SansaCloth

anti-clipping / cloth interaction / rendering 系統。

https://github.com/mayusaki3/SansaCloth

---

## 開発方針

- ドキュメントは HLDocS ベースで管理
- 仕様・テスト・コードの Traceability を重視
- implementation monorepo 化を避ける
- runtime / engine 実装は専用 repository へ分離する
- OpenXR を基盤としたマルチ Runtime 対応
- 特定プラットフォームへの固定依存を避ける
- オープンな拡張性を重視
