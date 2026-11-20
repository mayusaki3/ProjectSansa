[日本語](./README.md) | [English](./README_en-US.md)

# <img src="Logo/Sansa2023.png" width="24"> Project Sansa（プロジェクトサンサ）とは

ProjectSansa（プロジェクトサンサ、以下 Sansa）とは、ざっくりいうと、こんなのあったら使いたいxRオープンプラットフォームから始まり、VR / AR / MR / 非XR を横断する xRシステム構想になりました。  
プロジェクト名のサンサは「現実世界」「仮想世界」「理想」が交差する三叉をイメージして名付けました。  
個人プロジェクトとして開始していますが、面白そうと思ったら誰でも参加歓迎です。

[Discord](https://discord.gg/wN67tdzrCT)

---

## 1. 実現したいこと

結構な大風呂敷になりますが、最終的には、スマートフォンに代わる新しいパーソナルデバイスとサービス基盤を作りたいと考えています。  
主に自分で使いたいがために。

Sansa では、VR / AR / MR / 非XR を1つのサービスとして扱います。

- 非XR: 通話・コミュニケーション
- AR: 現実空間との情報重畳
- VR: 仮想空間SNS
- MR: 現実空間と仮想空間の融合

これらは簡単な操作で切り替えられ、現実世界と仮想世界を自然につなぐことを目指しています。
また、Sansa 単体ではなく、他の VRSNS やサービスとも接続・連携できるオープンな構成を目指しています。  
そして、UGC(User Generated Content)の発展を後押しするため、権利関係や経済圏に関する仕組みも組み込んでいきたいです。

- [初期アイデアと利用シーン](./docs/ja-JP/01_初期アイデアと利用シーン.md)

---

## 2. 本リポジトリの位置づけ

本リポジトリは ProjectSansaの全体構想や、Sansaシリーズの紹介および導線の役割を持ちます。  
また、Sansaシリーズの共通認識、特に用語については本リポジトリの管理下で共有します。  
ProjectSansaでは、Sansaシリーズ共通の用語・運用・LLM連携方針を提供しています。  
各Sansaシリーズは、それらを参照して運用することを推奨します。

詳細は、以下のドキュメントを参照してください。  
利用シーンなどのアイデアもこちらに記載しています。

- [ProjectSansa 目次](./docs/ja-JP/目次.md)

## 3. 各Sansaシリーズの紹介

各リポジトリの `develop` ブランチの README・設計資料・実装・テスト構成をもとにした概要です（2026-11-20確認）。ここで示す進捗は概要であり、正式リリースや機能完成を保証するものではありません。詳細は各リポジトリの仕様・テスト・Worklogを参照してください。

<table>

<tr><td>

### [SansaSphere](https://github.com/mayusaki3/SansaSphere)

認証・アカウント、権利・来歴、監査・ログ、経済圏、フェデレーション連携を担う共通サービス基盤。機能別リポジトリへの分離と信頼連携も設計しています。

### 現在の状況
- 設計・基盤構成整理中

`認証` `権利管理` `来歴情報` `監査` `経済圏` `フェデレーション`
</td></tr>

<tr><td>

### [SansaVRM](https://github.com/mayusaki3/SansaVRM)

人型・非人型を扱う構造化モデル基盤。モデル、モジュール、装着状態、拡張情報や権利・来歴の扱いを整理し、Rustによるコア、バリデータ、形式アダプターの実装とテストを進めています。

### 現在の状況
- 設計・初版実装／検証中

`3Dモデル` `Rust` `glTF` `VRM` `モジュール` `検証`
</td></tr>

<tr><td>

### [SansaVRM Studio AI](https://github.com/mayusaki3/SansaVRM-Studio-AI)

SansaVRMを中心とした3Dアセットの制作・変換・検証とAI前処理支援環境。プロジェクト管理、ワークフロー、依存関係管理、ローカルサーバー、最小検証UIなどのPoCを実装しています。AIなしでも使える基盤とローカルAI優先の構成を目指します。

### 現在の状況
- アーキテクチャ整理・PoC実装中

`3D制作` `AI前処理` `ローカルAI` `Python` `ワークフロー` `PoC`
</td></tr>

<tr><td>

### [SansaXR](https://github.com/mayusaki3/SansaXR)

OpenXRに基づくXRランタイム、トラッキング、マルチプレイヤー、同期、インタラクションなどを担当。Demo 1でO3DEとGodotを同一要件で比較・検証し、エンジン固有実装から共通境界を整理します。

### 現在の状況
- 設計・XRランタイム技術検証中

`XRSNS` `OpenXR` `O3DE` `Godot` `マルチプレイヤー` `技術検証`
</td></tr>

<tr><td>

### [SansaXR-WorldCore](https://github.com/mayusaki3/SansaXR-WorldCore)

SansaXRのワールド管理基盤。霧で覆われた未定義領域、地理セル、写真からの3D再構築、SandboxからLive Worldへの公開、アクセス権限、ワールド／インスタンス遷移を構想しています。ライブイベント向けの出演側・観客側インスタンス分離やギミック配布・再同期も設計Worklogで検討しています。

### 現在の状況
- ワールド管理基盤を設計中（実装前）

`ワールド管理` `霧世界` `空間セル` `インスタンス` `アクセス制御` `ギミック同期`
</td></tr>

<tr><td>

### [SansaCloth](https://github.com/mayusaki3/SansaCloth)

衣服と身体の貫通を抑えるSDFベースのアンチクリッピング／表面応答基盤。Rustの参照実装とテストに加え、Godot・Unity・O3DE向けの検証コードを整備しています。製品版の完成を意味するものではありません。

### 現在の状況
- リファレンス実装・エンジン連携検証中

`衣服` `アンチクリッピング` `SDF` `Rust` `Godot` `Unity` `O3DE`
</td></tr>

<tr><td>

### [SansaVRM MuJoCo Adapter](https://github.com/mayusaki3/SansaVRM-MuJoCo-Adapter)

SansaVRMとMuJoCo/MJCF/制御設定を接続するアダプター。MuJoCo固有の変換・アクチュエータ対応付け・診断を本体から分離し、Python実装とテストを進めています。

### 現在の状況
- 設計・基礎実装／テスト中

`SansaVRM` `MuJoCo` `MJCF` `Python` `シミュレーション` `アダプター`
</td></tr>

</table>

---

## 4. ドキュメント規約：HLDocS

ProjectSansaと関連リポジトリでは、Human–LLM Document Roles Specification（[HLDocS](https://github.com/mayusaki3/HLDocS)）をドキュメント作成・検証・再構成の共通規約として活用します。HLDocSは、人間が要件・意味・設計判断を担い、LLMが文書構造・ID・対応関係の維持を支援する役割分担を定義します。

仕様・設計・テスト間のトレーサビリティ、Markdownの構造的な運用、長期作業を引き継ぐ `LLM_WORKSPACE`（Request／Worklog／Handover）などを扱います。リポジトリによって導入・移行状況やディレクトリ表記が異なるため、詳細は各リポジトリの規約を優先してください。

- **安定運用側：** HLDocS `main`（v0.6.1系）
- **次期仕様の評価：** HLDocS `develop`（v0.7.0再構成中。完成・安定版ではありません）
- [HLDocS リポジトリと利用方法](https://github.com/mayusaki3/HLDocS)

---
© 2020 ProjectSansa
