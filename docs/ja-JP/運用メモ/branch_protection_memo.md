# メモ: ブランチ保護 & 必須チェック（ProjectSansa）

このメモは **develop / master** のブランチ保護を CLI（`gh api`）で適用・確認する手順です。  
JSON テンプレートは `infra/` 配下に配置（`protection.json` / `protection_it-cluster.json`）。

---

## 1) 目的

- **develop**: 通常開発用。必須チェック = `CI / build`、`backend-java CI / unit`、`backend-java CI / it`  
- **master**: リリース前の厳格検証。上記に **`backend-java CI / it-cluster`** を追加

> これらのラベルは **ブランチ保護の Required checks** に手入力する“固定ジョブ名”です。

---

## 2) JSON（テンプレート抜粋）

**develop（infra/protection.json）**:
```json
{
  "required_status_checks": {
    "strict": true,
    "contexts": [
      "backend-java CI / unit",
      "backend-java CI / it",
      "CI / build"
    ]
  },
  "enforce_admins": false,
  "required_pull_request_reviews": {
    "required_approving_review_count": 1
  },
  "restrictions": null
}
```

**master（infra/protection_it-cluster.json）**:
```json
{
  "required_status_checks": {
    "strict": true,
    "contexts": [
      "backend-java CI / unit",
      "backend-java CI / it",
      "CI / build",
      "backend-java CI / it-cluster"
    ]
  },
  "enforce_admins": false,
  "required_pull_request_reviews": {
    "required_approving_review_count": 1
  },
  "restrictions": null
}
```

---

## 3) 適用コマンド

> 事前に `gh auth login` を実施。

```powershell
# develop に適用
gh api -X PUT repos/mayusaki3/ProjectSansa/branches/develop/protection `
  --input infra/protection.json `
  -H "Accept: application/vnd.github+json"

# master に適用
gh api -X PUT repos/mayusaki3/ProjectSansa/branches/master/protection `
  --input infra/protection_it-cluster.json `
  -H "Accept: application/vnd.github+json"
```

**現在の設定を確認**
```powershell
gh api repos/mayusaki3/ProjectSansa/branches/develop/protection | jq
gh api repos/mayusaki3/ProjectSansa/branches/master/protection  | jq
```

UI: Settings → Branches → Branch protection rules → Required status checks に **固定ラベル**が出ていること。

---

## 4) よくある詰まりポイント

- **チェック名が UI に出ない**  
  → 直近 1 週間でそのチェックが実行されていない可能性。**対象ブランチ宛 PR を1回実行**。

- **`it-cluster` が Pending のまま**  
  → 外部 3 ノード Cassandra に到達不可／Secrets 未設定／`local-datacenter` 不一致。  
  → Actions ログで `CONTACT_POINTS` / `DC` / `KEYSPACE` / `CONSISTENCY` / `TIMEOUT` を確認。

- **`422 Unprocessable Entity`（PUT 失敗）**  
  → JSON の `contexts` のジョブ名ミス。**実行ログの表示名をコピペ**して修正。

- **自己承認したい**  
  → `required_approving_review_count: 1` のまま、オーナー自己承認（B案）の運用で可。  
  → 一時的に緩める場合は `0` にして再適用（戻し忘れ注意）。

---

## 5) Required checks（手入力するジョブ名・再掲）

- `CI / build`  
- `backend-java CI / unit`  
- `backend-java CI / it`  
- `backend-java CI / it-cluster`（master 限定で必須化）

> `.github/workflows/backend-java-ci.yml` の `jobs.<id>.name` に一致。名前変更時は JSON も更新する。

---

_Last updated: 2025-09-24_
