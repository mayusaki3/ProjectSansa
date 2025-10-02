# メモ: ブランチ保護 & 必須チェック（rulesets・ProjectSansa）

> **目的**  
> GitHub **Rulesets**（クラシック保護ではなく新方式）で、`develop`/`master` の必須チェックを機械的に再現できるようにする。

## 方針（結論）

- **default**（全ブランチ・ただし `master` 除外）  
  必須: `CI / build`、`backend-java CI / unit`、`backend-java CI / it`
- **master-only**（`refs/heads/master` のみ）  
  必須: 上3つ + **`backend-java CI / it-cluster`**

> Required checks の **context 名は _Actions の表示名_ と完全一致**させること。  
> 例: `backend-java CI / unit`, `backend-java CI / it`, `backend-java CI / it-cluster`, `CI / build`  
> （`(pull_request)` 等の接尾辞は **不要**。Rulesets は **ラベル(=context)** を見る）

---

## 1) JSON 置き場

- `infra/ruleset-default.json`
- `infra/ruleset-master-only.json`

### 1.1 `infra/ruleset-default.json`（そのまま保存）

```json
{
  "name": "default",
  "target": "branch",
  "enforcement": "active",
  "conditions": {
    "ref_name": {
      "include": ["refs/heads/*"],
      "exclude": ["refs/heads/master"]
    }
  },
  "rules": [
    {
      "type": "pull_request",
      "parameters": {
        "required_approving_review_count": 0,
        "dismiss_stale_reviews_on_push": false,
        "require_code_owner_review": false,
        "require_last_push_approval": false,
        "required_review_thread_resolution": false,
        "automatic_copilot_code_review_enabled": false,
        "allowed_merge_methods": ["merge", "squash", "rebase"]
      }
    },
    {
      "type": "required_status_checks",
      "parameters": {
        "strict_required_status_checks_policy": true,
        "do_not_enforce_on_create": false,
        "required_status_checks": [
          { "context": "backend-java CI / unit" },
          { "context": "backend-java CI / it" },
          { "context": "CI / build" }
        ]
      }
    }
  ]
}
```

### 1.2 `infra/ruleset-master-only.json`（そのまま保存）

```json
{
  "name": "master-only",
  "target": "branch",
  "enforcement": "active",
  "conditions": {
    "ref_name": {
      "include": ["refs/heads/master"],
      "exclude": []
    }
  },
  "rules": [
    {
      "type": "pull_request",
      "parameters": {
        "required_approving_review_count": 0,
        "dismiss_stale_reviews_on_push": false,
        "require_code_owner_review": false,
        "require_last_push_approval": false,
        "required_review_thread_resolution": false,
        "automatic_copilot_code_review_enabled": false,
        "allowed_merge_methods": ["merge", "squash", "rebase"]
      }
    },
    {
      "type": "required_status_checks",
      "parameters": {
        "strict_required_status_checks_policy": true,
        "do_not_enforce_on_create": false,
        "required_status_checks": [
          { "context": "backend-java CI / unit" },
          { "context": "backend-java CI / it" },
          { "context": "CI / build" },
          { "context": "backend-java CI / it-cluster" }
        ]
      }
    }
  ]
}
```

---

## 2) 適用手順（**差し替え = remove → add**）

> 事前に `gh auth login` 済みであること。

### 2.1 Classic 保護が残っていれば削除（無ければ 404 でOK）

```powershell
gh api -X DELETE repos/mayusaki3/ProjectSansa/branches/develop/protection
gh api -X DELETE repos/mayusaki3/ProjectSansa/branches/master/protection
```

### 2.2 既存 Ruleset を**名前一致で削除**（remove）

```powershell
# 既存 rulesets 一覧を取得
$all = gh api repos/mayusaki3/ProjectSansa/rulesets | ConvertFrom-Json

# 削除対象（あれば）
$targets = @("default","master-only")
$all | Where-Object { $targets -contains $_.name } | ForEach-Object {
  gh api -X DELETE "repos/mayusaki3/ProjectSansa/rulesets/$($_.id)"
}
```

（bash の場合）
```bash
for id in $(gh api repos/mayusaki3/ProjectSansa/rulesets --jq '.[] | select(.name=="default" or .name=="master-only") | .id'); do
  gh api -X DELETE "repos/mayusaki3/ProjectSansa/rulesets/$id"
done
```

### 2.3 新規作成（add）

```powershell
gh api -X POST repos/mayusaki3/ProjectSansa/rulesets `
  -H "Accept: application/vnd.github+json" `
  --input infra/ruleset-default.json

gh api -X POST repos/mayusaki3/ProjectSansa/rulesets `
  -H "Accept: application/vnd.github+json" `
  --input infra/ruleset-master-only.json
```

---

## 3) 検証（一覧 → 詳細）

### 3.1 一覧（id / name / enforcement / target）

```powershell
gh api repos/mayusaki3/ProjectSansa/rulesets `
  --jq '.[] | {id, name, enforcement, target}'
```

### 3.2 詳細（include/exclude と Required checks を抽出）

```powershell
$all = gh api repos/mayusaki3/ProjectSansa/rulesets | ConvertFrom-Json
$all.id | ForEach-Object {
  $r = gh api "repos/mayusaki3/ProjectSansa/rulesets/$_" | ConvertFrom-Json

  $includes = @()
  if ($r.conditions -and $r.conditions.ref_name -and $r.conditions.ref_name.include) {
    $includes = @($r.conditions.ref_name.include)
  }

  $excludes = @()
  if ($r.conditions -and $r.conditions.ref_name -and $r.conditions.ref_name.exclude) {
    $excludes = @($r.conditions.ref_name.exclude)
  }

  $reqChecks = @()
  if ($r.rules) {
    $rc = $r.rules | Where-Object { $_.type -eq 'required_status_checks' }
    if ($rc -and $rc.parameters -and $rc.parameters.required_status_checks) {
      $reqChecks = @($rc.parameters.required_status_checks | ForEach-Object { $_.context })
    }
  }

  [pscustomobject]@{
    id              = $r.id
    name            = $r.name
    include         = $includes
    exclude         = $excludes
    required_checks = $reqChecks
  }
}
```

**期待結果（例）**
- `default`: include=`refs/heads/*`, exclude=`refs/heads/master`, required_checks=`unit,it,build`
- `master-only`: include=`refs/heads/master`, required_checks=`unit,it,build,it-cluster`

---

## 4) よくある詰まりと対処

- **PR で “Expected — Waiting for status to be reported” が出続ける**  
  - Rulesets の `required_status_checks[].context` が **表示名と完全一致していない**。  
    → Actions 実行履歴から **ラベル（context）をコピペ**して JSON を修正し、PUT/再作成。  
  - 対象ブランチで **直近実行がない**と UI 候補に出にくい。**ダミーPRで一度回す**。

- **`it-cluster` だけ Pending 長い / 失敗**  
  - 3ノード Cassandra 起動～ヘルス待ちの**時間切れ**や **Secrets 未設定**。  
  - CI ログで `docker compose ... wait cass-seed cass-2 cass-3` の成否を確認。

- **`422 Unprocessable Entity`（作成/更新エラー）**  
  - JSON スキーマ不一致。`required_status_checks` は配列で `context` キー必須。  
  - `checks: []` や `checks: null` は **Classic API 用**の概念。Rulesets では **使わない**。

- **Classic と Rulesets の二重管理**  
  - 片方だけ残ると表示/挙動が紛らわしい。**Classic は削除**して Rulesets のみ運用。

---

## 5) 変更（PUT）・削除（DELETE）

```powershell
# id を確認
gh api repos/mayusaki3/ProjectSansa/rulesets --jq '.[] | {id,name}'

# 例: default を更新
gh api -X PUT repos/mayusaki3/ProjectSansa/rulesets/<RULESET_ID> `
  -H "Accept: application/vnd.github+json" `
  --input infra/ruleset-default.json

# 削除
gh api -X DELETE repos/mayusaki3/ProjectSansa/rulesets/<RULESET_ID>
```

---

## 6) 運用メモ

- `backend-java-ci.yml` の **表示名（`jobs.<id>.name`）を変えたら必ず ruleset JSON も更新**。  
- ルールは **`default`（全体）** と **`master-only`（厳格）** の **2本**でシンプル運用。  
- 例外が必要になったら `exclude` / 追加 ruleset で調整（優先度は UI 側で設定可能）。
