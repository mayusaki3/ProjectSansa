# ProjectSansa

ProjectSansa（プロジェクトサンサ）は、VR / AR / MR / 非XR を横断する xR ecosystem / federation 構想です。

「現実世界」「仮想世界」「理想」が交差する三叉をイメージして命名されています。

ProjectSansa は ecosystem / architecture / federation を主責務とし、単一 runtime や engine 実装を集中管理する monorepo とはしない方針を採用しています。

---

## ドキュメント

### 日本語ドキュメント目次

- [docs/ja-JP/目次.md](./docs/ja-JP/目次.md)

### 全体構想

- [ProjectSansa全体構想](./docs/ja-JP/仕様/00_全体構想/01_ProjectSansa全体構想.md)

---

## 関連 repository

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

## 主な構想領域

- VR / AR / MR / 非XR 統合
- ecosystem federation
- distributed architecture
- repository interoperability
- OpenXR ベース xR 基盤
- 分散サーバークラスタ
- Webサービス連携
- 統合認証
- タイムキャプチャ
- デスクトップ連携
- VRSNS 間連携
- AI / ロボット / MuJoCo 連携

---

## 開発方針

- ドキュメントは HLDocS ベースで管理
- 仕様・テスト・コードの Traceability を重視
- implementation monorepo 化を避ける
- runtime / engine 実装は専用 repository へ分離する
- OpenXR を基盤としたマルチ Runtime 対応
- 特定プラットフォームへの固定依存を避ける
- オープンな拡張性を重視

---

## コミュニティ

Discord

https://discord.gg/wN67tdzrCT
