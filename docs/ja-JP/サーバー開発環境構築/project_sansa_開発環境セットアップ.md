# ProjectSansa 開発環境セットアップ（Windows）

> **目的**: 別PCでもすぐに開発を再開できるよう、ProjectSansa のローカル開発環境構築手順をまとめます。今後、必要なツールや設定を追加したら本ファイルを更新してください。

---

## 対象/前提
- OS: **Windows 10/11**（WSL2 利用）
- Shell: **PowerShell**（管理者として実行を推奨する箇所有）
- ソース: GitHub `develop` ブランチ

> **最小要件**: WSL2 / Docker Desktop / JDK 21 / Maven 3.9+ / Git

---

## 1. 必要ソフトのインストール

### 1.0 PowerShell 7 の導入・既定化（推奨）
- **インストール**（Windows）：
  ```powershell
  winget install -e --id Microsoft.PowerShell
  pwsh -NoLogo -Command $PSVersionTable.PSVersion
  ```
- **既定シェルに設定**：
  - **Windows Terminal** → Settings > *Default profile* = **PowerShell**（pwsh）
  - **VS Code** → Terminal > *Select Default Profile* = **PowerShell**（pwsh）
- **実行ポリシー（ユーザー範囲）**：
  ```powershell
  Set-ExecutionPolicy -Scope CurrentUser RemoteSigned -Force
  ```
- **プロファイル（$PROFILE）に基本設定**：
  ```powershell
  # 未作成なら作成
  if (-not (Test-Path $PROFILE)) { New-Item -ItemType File -Force $PROFILE | Out-Null }
  notepad $PROFILE  # 好きなエディタで開く
  ```
  推奨の追記例：
  ```powershell
  $PSStyle.OutputRendering = 'Ansi'
  [Console]::OutputEncoding = [Text.Encoding]::UTF8
  $OutputEncoding = [System.Text.Encoding]::UTF8
  $ProgressPreference = 'SilentlyContinue'   # iwr/irmの進捗表示を抑制

  function New-JwtSecret([int]$Bytes=64){
    $b = New-Object byte[] $Bytes
    [System.Security.Cryptography.RandomNumberGenerator]::Fill($b)
    return ([Convert]::ToBase64String($b).TrimEnd('=')).Replace('+','-').Replace('/','_')
  }
  ```
  これで**強いJWT秘密鍵**をいつでも生成できます：`New-JwtSecret 64`

> 注: レガシーな Windows 専用モジュールが必要なケースのみ PowerShell 5.1 を併用してください（本プロジェクトの通常作業は PS7 で問題なし）。

### 1.1 WSL2 の有効化

### 1.1 WSL2 の有効化
```powershell
wsl --install
wsl --set-default-version 2
wsl -l -v   # ディストリ/バージョン確認
```
必要に応じて再起動。

### 1.2 Docker Desktop（WSL2 バックエンド）
1. 公式インストーラで Docker Desktop をインストール。
2. 設定で **Use the WSL 2 based engine** を有効（既定でONのはず）。
3. 起動確認：
```powershell
docker version
docker compose version
```

### 1.3 JDK 21（Eclipse Temurin）
**winget（推奨）**
```powershell
winget install -e --id EclipseAdoptium.Temurin.21.JDK
java -version
```

### 1.4 Maven 3.9 以上
1) 公式配布の ZIP を任意フォルダへ展開（例: `C:\Tools\apache-maven-3.9.11`）
2) 環境変数に追加（管理者 PowerShell）
```powershell
$env:MAVEN_HOME = "C:\\Tools\\apache-maven-3.9.11"
[Environment]::SetEnvironmentVariable("MAVEN_HOME", $env:MAVEN_HOME, "Machine")
$old = [Environment]::GetEnvironmentVariable("Path","Machine")
[Environment]::SetEnvironmentVariable("Path", "$old;$env:MAVEN_HOME\\bin", "Machine")
```
3) **新しい** PowerShell で確認：
```powershell
mvn -v
```

### 1.5 Git
```powershell
winget install -e --id Git.Git
git --version
```

---

## 2. リポジトリ取得
```powershell
cd C:\WORKPLACE\Makes\GitHub
git clone https://github.com/mayusaki3/ProjectSansa.git
cd ProjectSansa
git checkout develop
```

---

## 3. 開発用 Cassandra の起動
**単一ノード（dev）**
```powershell
docker compose -f infra/docker-compose.dev.yml up -d --remove-orphans
# ヘルス/初期化確認
docker compose -f infra/docker-compose.dev.yml ps
docker logs sansa-cass-1 --tail=50
# 初期CQLが完了していれば↓が見える
# cassandra-init のログ: CQL-init-done
```
停止/掃除：
```powershell
docker compose -f infra/docker-compose.dev.yml down -v
```

> ※ 3ノード検証が必要な場合は `infra/docker-compose.dev-3n.yml` を使用。

---

## 4. Java Backend のビルド & テスト
作業ディレクトリ：`backend-java`

### 4.1 POM 構文チェック
```powershell
cd backend-java
mvn -q validate
```

### 4.2 単体テスト（Integration Test 除外）
```powershell
mvn -B -ntp -DskipITs=true test
```

### 4.3 結合テスト（Testcontainers を使用）
> **Docker Desktop が起動していること**
```powershell
# Quarkus テスト時ポートの固定（初回のみ設定。既に入っていれば不要）
#   src/test/resources/application.properties に以下を追加:
#   quarkus.http.test-port=8080

mvn -B -ntp -DskipITs=false verify
```
**期待:** `register → login → post → list` の IT が通過し、`BUILD SUCCESS`。

---

## 5. 開発起動（ホットリロード） & スモーク
### 5.1 起動
```powershell
mvn quarkus:dev
```

### 5.2 スモーク（別コンソール: PowerShell）
```powershell
# register（200 or 409）
$reg = irm -Method Post -Uri http://localhost:8080/auth/register -ContentType 'application/json' -Body '{"username":"dev1","password":"dev1"}'

# login → token
$login = irm -Method Post -Uri http://localhost:8080/auth/login -ContentType 'application/json' -Body '{"username":"dev1","password":"dev1"}'
$token = $login.access_token

# post（POST /posts は JWT 必須）
irm -Method Post -Uri http://localhost:8080/posts -ContentType 'application/json' -Headers @{Authorization="Bearer $token"} -Body '{"text":"hello from PS"}'

# list（GET /posts は匿名可）
irm http://localhost:8080/posts?limit=5
```

---

## 6. JWT 設定（本番を見据えた注意）

### 6.1 目的（なぜ JWT？）
- **ステートレス**認証でスケールしやすい（Web/モバイル/O3DE/デスクトップで共通利用）。
- **リバースプロキシ/複数インスタンス**構成でもセッション共有が不要。
- 現状のポリシー：**POST /posts は認証必須**、**GET /posts は匿名可**（将来ポリシーに合わせて変更可能）。

### 6.2 必須設定 & 参照優先順位
- **PSANSA_JWT_SECRET**（必須・32バイト以上）
- **psansa.jwt.issuer**（例: `ProjectSansa`）
- **psansa.jwt.audience**（例: `ProjectSansaClients`）
- **psansa.jwt.expires.min**（例: `60`）

> コード側の参照優先順位：① **JVMシステムプロパティ** → ② **環境変数** → ③ デフォルト値。

### 6.3 秘密鍵の生成方法（推奨: 64バイトランダム）
**PowerShell（URLセーフ Base64）**
```powershell
# 64バイトの強乱数を生成 → URL-safe Base64 文字列に変換
$bytes = New-Object byte[] 64
[System.Security.Cryptography.RandomNumberGenerator]::Fill($bytes)
$secret = [Convert]::ToBase64String($bytes).TrimEnd('=')
$secret = $secret.Replace('+','-').Replace('/','_')
$secret  # ← これを PSANSA_JWT_SECRET に設定
```
**bash（WSL など）**
```bash
# 64バイト → URL-safe Base64（= を除去）
openssl rand -base64 64 | tr '+/' '-_' | tr -d '='
```
> 文字数ではなく**エントロピー（バイト数）**が重要。32バイト未満は **WeakKeyException** で弾かれます。

**PowerShell 5.1 互換（.NET Framework）**
```powershell
# PS5.1 には Fill が無いので Create()+GetBytes を使用
$b = New-Object byte[] 64
$rng = [System.Security.Cryptography.RandomNumberGenerator]::Create()
$rng.GetBytes($b); $rng.Dispose()
$secret = [Convert]::ToBase64String($b).TrimEnd('=').Replace('+','-').Replace('/','_')
$secret
```
**環境変数へ即反映（例）**
```powershell
[Environment]::SetEnvironmentVariable("PSANSA_JWT_SECRET", (New-JwtSecret 64), "Machine")
```

### 6.4 値の渡し方（例）
**環境変数（Windows・永続）**
```powershell
# 管理者 PowerShell（マシン環境変数）
[Environment]::SetEnvironmentVariable("PSANSA_JWT_SECRET", "<強い秘密鍵>", "Machine")
[Environment]::SetEnvironmentVariable("psansa.jwt.issuer", "ProjectSansa", "Machine")
[Environment]::SetEnvironmentVariable("psansa.jwt.audience", "ProjectSansaClients", "Machine")
[Environment]::SetEnvironmentVariable("psansa.jwt.expires.min", "60", "Machine")
```
**JVM システムプロパティ**
```powershell
mvn quarkus:dev `
  -Dpsansa.jwt.secret="<強い秘密鍵>" `
  -Dpsansa.jwt.issuer=ProjectSansa `
  -Dpsansa.jwt.audience=ProjectSansaClients `
  -Dpsansa.jwt.expires.min=60
```
**Docker Compose（本番/検証）**
```yaml
services:
  backend:
    image: ghcr.io/xxx/backend-java:latest
    environment:
      PSANSA_JWT_SECRET: "${PSANSA_JWT_SECRET}"
      psansa.jwt.issuer: "ProjectSansa"
      psansa.jwt.audience: "ProjectSansaClients"
      psansa.jwt.expires.min: "60"
```

### 6.5 よくある落とし穴
- **短い鍵** → 起動失敗（WeakKeyException 等）。→ **32バイト以上**の鍵を使用。
- **issuer/audience 不一致** → すべて **401**。→ 発行（JwtService）と検証（JwtAuthFilter）で値を統一。
- **平文HTTP** → トークン漏洩リスク。→ 実運用は **HTTPS**（リバースプロキシ/IngressでTLS終端）。

### 6.6 期限と鍵ローテーションのヒント
- アクセストークンは目安 **15〜60分**（現状は 60 分）。
- 将来は**複数鍵**（Primary/Secondary）＋ `kid` ヘッダ対応を検討：
  1) 検証は両鍵OK、発行はPrimaryのみ。
  2) ローテ後しばらくして旧鍵を無効化。
## 7. よくあるエラーと対処
- **`docker: not recognized`**: Docker Desktop をインストール/起動。WSL2 バックエンド有効化。
- **`Codec not found [TIMEUUID <-> Instant]`**: 実装側で `timeuuid -> Instant` 変換済み（`Uuids.unixTimestamp(UUID)`）。最新の `develop` を使用。
- **`Expected status 200 but was 401`（ITのGET）**: `GET /posts` を匿名可に設定済み。`@JwtSecured` は `POST /posts` のみに付与。
- **`mvn: not recognized`**: Maven の `bin` が PATH に入っているか確認。新しい PowerShell を開き直す。
- **`class…がありません`**: クラスファイルのパス/パッケージ名が一致しているか確認（例: `psansa.api.security.JwtSecured` は `src/main/java/psansa/api/security/JwtSecured.java`）。

---

## 8. 日常運用コマンド（チートシート）
```powershell
# infra 起動/停止
cd C:\WORKPLACE\Makes\GitHub\ProjectSansa
docker compose -f infra/docker-compose.dev.yml up -d --remove-orphans
docker compose -f infra/docker-compose.dev.yml down -v

# backend ビルド/テスト
cd backend-java
mvn -q validate
mvn -B -ntp -DskipITs=true test
mvn -B -ntp -DskipITs=false verify
mvn quarkus:dev
```

---

## 9. オプション/拡張
- **依存の警告解消**: `quarkus-resteasy-reactive-jackson` → `quarkus-rest-jackson` へ移行。
- **OpenAPI/Swagger UI**: 依存 `io.quarkus:quarkus-smallrye-openapi` を追加し、`quarkus.swagger-ui.always-include=true`。
- **Maven Wrapper**: CI/他端末の再現性向上。
```powershell
cd backend-java
mvn -N wrapper:wrapper
git add mvnw mvnw.cmd .mvn/wrapper/*
```
- **GNU Make（任意）**: `make up` 等を使いたい場合に導入。

---

## 10. メンテナンス方針
- 新しい依存やツールを採用したら、**本ファイルに追記**してから PR を作成。
- OS/バージョン差異（Windows/macOS/Linux）に関する注意点も、項目ごとに追記。

---

### 付録: トラブルシュート補助コマンド
```powershell
# WSL 状態
wsl -l -v

# Docker 稼働確認
docker version
docker compose ls

# Cassandra 健康確認（dev）
docker compose -f infra/docker-compose.dev.yml ps
docker logs sansa-cass-1 --tail=100

# Java/Maven
java -version
mvn -v
```

