<!--
HLDocS:LLM-MANAGED
doc_id: doc-20260526-050000Z-A7F2
lang: ja-JP
canonical_title: ProjectSansa共有規約
document_type: spec
canonical_document: true
-->

[目次](./目次.md) > Sansaシリーズの共通認識 > ProjectSansa共有規約

# ProjectSansa共有規約

## 1. 概要

本規約は、ProjectSansa および各 Sansa シリーズで共有する上位方針を定義します。

本規約では、HLDocS で定義済みのドキュメント構造・生成規約・再生成規約・Traceability・階層リンク形式・Transport Encoding などは再定義しません。  
ProjectSansa 固有の共有原則のみを扱います。

---

## 2. ProjectSansa の位置づけ

ProjectSansa は、Sansa シリーズ全体における以下の共有ハブとして扱います。

- 共通概念
- 共通用語
- 共有規約
- LLM運用の入口
- リポジトリ分散時の連携方針
- シリーズ間の意味整合性

各 Sansa シリーズは、ProjectSansa を共通参照元として扱い、個別リポジトリ側では固有仕様・固有実装・固有運用のみを追加します。

---

## 3. 規約継承方針

各 Sansa シリーズは、ProjectSansa の共有規約を継承します。

個別シリーズ側の規約は、ProjectSansa 共有規約を置き換えるものではなく、追加・具体化として扱います。

ProjectSansa 共有規約と個別シリーズ規約に矛盾がある場合は、以下の順で判断します。

1. HLDocS で定義済みの事項は HLDocS を優先します。
2. ProjectSansa 固有の共有事項は ProjectSansa 共有規約を優先します。
3. 個別シリーズ固有の事項は、個別シリーズ規約を優先します。
4. 例外を認める場合は、個別シリーズ側で例外理由と適用範囲を明記します。

---

## 4. 共通用語参照方針

Sansa シリーズ間で共有する概念は、ProjectSansa の共通用語定義を参照します。

共通概念を各シリーズ側で独自定義することは避けます。

各シリーズ側では、以下のみを追加します。

- シリーズ固有用語
- 実装固有用語
- 外部システム連携に必要な補足用語
- ProjectSansa 側へ昇格する前の暫定用語

共通化が必要になった用語は、ProjectSansa 側の共通用語定義へ反映します。

---

## 5. LLM_Rules の位置づけ

`LLM_Rules` は、LLM が作業前に参照する入口として扱います。

`LLM_Rules` には、以下の導線を置きます。

- HLDocS 規約への導線
- ProjectSansa 共有規約への導線
- ProjectSansa 共通用語定義への導線
- LLM_Workspace など、実験中の LLM 作業運用への導線

`LLM_Rules` は、詳細仕様や正本そのものを保持する場所ではありません。  
安定した内容は `docs/ja-JP` 配下へ移し、`LLM_Rules` から参照します。

---

## 6. LLM運用変更の共有方法

ProjectSansa で LLM 運用が変わった場合は、以下の順で共有します。

1. 安定規約・正本に該当する内容は、`docs/ja-JP` 配下の該当ドキュメントを更新します。
2. 実験中または暫定運用の内容は、`LLM_Rules` 側に記録します。
3. `LLM_Rules` の導線を更新し、LLM が最新の参照先へ到達できるようにします。
4. 各 Sansa シリーズは、ProjectSansa の `LLM_Rules` を入口として最新運用を再確認します。

これにより、各 Sansa シリーズへ個別に同じ説明を複製せず、ProjectSansa 側の入口更新によって共有します。

---

## 7. Repository Federation 方針

ProjectSansa は、Sansa シリーズ全体を単一巨大リポジトリに統合するのではなく、責務ごとに分離されたリポジトリ群として扱います。

ProjectSansa 側には、シリーズ横断の共通概念・共有規約・用語・構想を置きます。

個別シリーズ側には、以下を置きます。

- 個別仕様
- 個別実装
- 個別検証
- 個別運用
- シリーズ固有の補足規約

---

## 8. LLM_Workspace の暫定位置づけ

`LLM_Workspace` は、GitHub 上で LLM 作業を共有するための暫定運用領域です。

現時点では、ProjectSansa 内の実験中の運用として扱います。

想定する用途は以下です。

- Handover: リポジトリ間・チャット間の申し送り
- Request: LLM への作業依頼や入力受け渡し
- Worklog: LLM 作業ログ、検討メモ、暫定整理

`LLM_Workspace` の運用方式は、将来的に HLDocS 側へ共通化を申し入れる候補とします。

---

## 9. 正本管理方針

ProjectSansa では、安定した規約・方針・用語定義を `docs/ja-JP` 配下で管理します。

暫定運用・作業中メモ・申し送りは、`LLM_Rules` または `LLM_Workspace` に置きます。

暫定内容が安定した場合は、`docs/ja-JP` 配下の正本へ反映します。

---

[目次](./目次.md) > Sansaシリーズの共通認識 > ProjectSansa共有規約
