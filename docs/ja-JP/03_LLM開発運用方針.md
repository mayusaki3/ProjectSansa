<!--
HLDocS:LLM-MANAGED
doc_id: doc-20200619-010100Z-8D1B
lang: ja-JP
canonical_title: LLM開発運用方針
document_type: spec
canonical_document: true
-->

[目次](./目次.md) > 開発時専守事項 > LLM開発運用方針

# LLM開発運用方針

ProjectSansaでは、LLMを使用して開発を進めています。  
現状は ChatGPT を主に利用していますが、他の LLM を利用しても問題ありません。

本ドキュメントは ProjectSansa 固有の LLM 利用方針を定義する。  
ドキュメント作成手順、規約管理手順、検証手順などの共通方法論は HLDocS を参照する。

## 1. LLM利用のガイドライン

ProjectSansaは、複数のリポジトリから構成されています。  
LLMのコンテキストウィンドウの制限もあるため、基本的に以下のルールを守るようにしてください。

1. 複数のリポジトリにまたがって、１コンテキストウィンドウ内で開発を行わないでください。  
   基本、１リポジトリに１チャット（以上）の想定です。

2. ドキュメント管理等は、HLDocS規約に沿うようLLMに指示してください。  
   仕様→コード、仕様→テスト仕様→テストコード、テスト実施などのルール化で最低限の品質担保を行います。

3. LLMにリポジトリを更新させるのは問題ありません。  
   2026年5月頃からチャットでもリポジトリ更新ができるようになっています。

4. 作業ログ・申し送り・実験運用などの LLM 運用は、ProjectSansa の LLM_Rules および LLM_Workspace の最新方針に従う。  
   詳細運用手順は ProjectSansa 本体には保持せず、必要に応じて HLDocS および LLM_Rules を参照する。  
   もし HLDocS と LLM_Rules の手順が相反する場合は LLM_Rules を優先し、その内容は 6. の申し入れ対象とすること。

5. 共通用語・共通概念・共有規約は ProjectSansa を正本とする。  
   各シリーズは ProjectSansa を参照し、シリーズ固有事項のみを追加する。

6. HLDocSに対する要求や課題がある場合は、同様に申し入れを行ってください。

## 2. ガイドラインの利用

LLM利用のガイドラインをLLM向けに整理したものを用意してあります。  
つぎの様にLLMに与えてください。

```
下記フォルダ内のドキュメントを参照し、以降の作業はその内容に従うこと。
https://github.com/mayusaki3/ProjectSansa/tree/develop/LLM_Rules

以下のリポジトリを作業対象とする。
(URL)
```

(URL) は、https://github.com/mayusaki3/SansaSphere/tree/develop のようにブランチを含めます。

---

[目次](./目次.md) > 開発時専守事項 > LLM開発運用方針
