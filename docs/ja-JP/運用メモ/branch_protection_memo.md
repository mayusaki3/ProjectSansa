# Branch protection / Rulesets 運用メモ（ProjectSansa）

このメモは **GitHub Rulesets** を用いたブランチ保護運用の手順とトラブル対処をまとめたものです。

- 方式: **Rulesets（新方式）** に統一（Classic と併用しない）
- 構成: **default**（全ブランチ・master除外）＋ **master-only**（master専用）
- 目的:
  - `develop` を含む通常ブランチは **コミット/プッシュを妨げない**（必須チェックなし）
  - `master` は **it-cluster** のみを Required Check に設定（マージ前にクラスターITが通ることを保証）

---

## 1) ルールの方針（方式1）

- **default**  
  - `include`: `refs/heads/*`  
  - `exclude`: `refs/heads/master`  
  - **required checks: なし**（開発速度を阻害しない）

- **master-only**  
  - `include`: `refs/heads/master`  
  - **required checks**: `backend-java CI / it-cluster`

> 必要に応じて、将来 default にも required checks（`unit` / `it` / `build`）を足すが、その場合 **push 不能**にならないよう設計（例: PR専用にする、もしくは do_not_enforce_on_create=true を検討。ただし現在のプランでは使えないオプションもあるため注意）。

---

## 2) JSON テンプレート

### 2.1 `infra/ruleset-default.json`（全ブランチ, master 除外, 必須チェックなし）
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
    { "type": "pull_request", "parameters": { "required_approving_review_count": 0, "dismiss_stale_reviews_on_push": false, "require_code_owner_review": false, "require_last_push_approval": false, "required_review_thread_resolution": false, "automatic_copilot_code_review_enabled": false, "allowed_merge_methods": ["merge", "squash", "rebase"] } }
    
    /* 必須チェックを追加したいときは、下のブロックを足す（コメントを外す）。
    ,{
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
    */
  ]
}
```

### 2.2 `infra/ruleset-master-only.json`（master に it-cluster を要求）
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
      "type": "required_status_checks",
      "parameters": {
        "strict_required_status_checks_policy": true,
        "do_not_enforce_on_create": false,
        "required_status_checks": [
          { "context": "backend-java CI / it-cluster" }
        ]
      }
    },
    { "type": "pull_request", "parameters": { "required_approving_review_count": 0, "dismiss_stale_reviews_on_push": false, "require_code_owner_review": false, "require_last_push_approval": false, "required_review_thread_resolution": false, "automatic_copilot_code_review_enabled": false, "allowed_merge_methods": ["merge", "squash", "rebase"] } }
  ]
}
```

> **注意**: Rulesets のスキーマは Classic と異なる。`checks: []` / `checks: null` は **Classic** の概念。Rulesets では `rules[].type=="required_status_checks"` の配列に **`context`** を列挙する。

---

## 3) 作成（POST）

```powershell
# default
gh api -X POST repos/mayusaki3/ProjectSansa/rulesets `
  -H "Accept: application/vnd.github+json" `
  --input infra/ruleset-default.json

# master-only
gh api -X POST repos/mayusaki3/ProjectSansa/rulesets `
  -H "Accept: application/vnd.github+json" `
  --input infra/ruleset-master-only.json
```

> 失敗時: `Validation Failed: Enforcement evaluate option is not supported ...` は、プラン未対応のオプション（"evaluate"）が JSON に含まれているケース。`enforcement` は `active`/`disabled` のみを使用。

---

## 4) 確認（GET）

### 4.1 一覧（id / name / enforcement / target）
```powershell
gh api repos/mayusaki3/ProjectSansa/rulesets `
  --jq '.[] | {id, name, enforcement, target}'
```

### 4.2 各ルールの include/exclude と Required checks を抽出（PowerShell）
```powershell
$all = gh api repos/mayusaki3/ProjectSansa/rulesets | ConvertFrom-Json
$all.id | %{
  $r = gh api "repos/mayusaki3/ProjectSansa/rulesets/$_" | ConvertFrom-Json
  $rc = $r.rules | ? { $_.type -eq 'required_status_checks' }
  $req = if ($rc -and $rc.parameters.required_status_checks){
    @($rc.parameters.required_status_checks | % { $_.context })
  } else { @() }
  [pscustomobject]@{
    id=$r.id; name=$r.name
    include=($r.conditions.ref_name.include); exclude=($r.conditions.ref_name.exclude)
    required_checks=$req
  }
}
```

---

## 5) 変更（PUT）・削除（DELETE）

```powershell
# id を確認
gh api repos/mayusaki3/ProjectSansa/rulesets --jq '.[] | {id,name}'

# 例: default を更新（JSONを書き換えたら実行）
gh api -X PUT repos/mayusaki3/ProjectSansa/rulesets/<RULESET_ID> `
  -H "Accept: application/vnd.github+json" `
  --input infra/ruleset-default.json

# 削除
gh api -X DELETE repos/mayusaki3/ProjectSansa/rulesets/<RULESET_ID>
```

---

## 6) トラブルシュート

### 6.1 Push がブロックされる（GH013）
- 例: `remote: GH013: Repository rule violations found for refs/heads/develop` / `3 of 3 required status checks are expected.`
- **原因**: `develop` が default の対象（`refs/heads/*`）で、default に `required_status_checks` を持たせていると **push 不能** になる。
- **対処**:
  1. 方式1の通り、**default から required checks を外す**（本メモのテンプレどおり）。
  2. もしくは PR ベース運用にして master-only 側で厳格化。

### 6.2 "Expected — Waiting for status to be reported" が消えない
- **context 名の完全一致**を確認（例: `backend-java CI / it-cluster`）。
- 直近の実行が無いと候補に出ないことがある → **ダミーPR** で一度 Actions を走らせる。

### 6.3 422 （Validation Failed / JSON スキーマ不一致）
- Rulesets は Classic と別スキーマ。`checks` フィールドは使わない。
- `required_status_checks` は `[{ "context": "..." }, ...]` 形式。

---

## 7) Context（表示名）運用の注意
- context は **Actions のジョブ名**がベース。**完全一致**が必要。
- `app_id` に依存する解決策は **非推奨**（IDは将来変わる可能性／再利用性が低下）。
- "push" と "pull_request" で **表示が異なる**場合あり。Rulesets の required checks には **表示名（context）**を登録する。

---

## 8) 差し替え（remove & add）ワンライナー

```powershell
# remove (全部) → add（差し替え）
$ids = gh api repos/mayusaki3/ProjectSansa/rulesets --jq '.[].id'
$ids | % { gh api -X DELETE repos/mayusaki3/ProjectSansa/rulesets/$_ }

# 再作成
gh api -X POST repos/mayusaki3/ProjectSansa/rulesets `
  -H "Accept: application/vnd.github+json" `
  --input infra/ruleset-default.json

gh api -X POST repos/mayusaki3/ProjectSansa/rulesets `
  -H "Accept: application/vnd.github+json" `
  --input infra/ruleset-master-only.json
```

---

## 9) 期待状態（最終チェック）

- **default**
  - include: `refs/heads/*`
  - exclude: `refs/heads/master`
  - required checks: **なし**

- **master-only**
  - include: `refs/heads/master`
  - required checks: `backend-java CI / it-cluster`

---

以上。必要に応じて、`unit` / `it` / `build` を default 側に追加する場合は、push への影響を踏まえチーム合意の上で実施すること。

