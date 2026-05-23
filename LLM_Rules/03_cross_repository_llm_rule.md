# Cross Repository LLM Rule

## repository 単位 chat

repository ごとに最低 1 chat 以上を分離する。

複数 repository を単一コンテキストで同時開発しない。

---

## HLDocS

ドキュメント管理は HLDocS ベースを基本とする。

---

## handover

cross-repository coordination は handover document により管理する。

---

## shared terminology

shared terminology / governance / provenance 系概念は、ProjectSansa 側へ申し送る。

repository-specific extension のみ repository 側で定義する。

---

## worklog

LLM が生成した roadmap / 思考結果 / 作業記録は repository ごとに Worklog へ保存する。

不要になった古い worklog は整理・削除してよい。

ただし必要情報は削除前に仕様へ反映する。
