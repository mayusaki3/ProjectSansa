[目次](../目次.md) > サーバー環境構築 SSL/TLS証明書の設定

## はじめに
この手順で、ブラウザから最初にアクセスされるサーバーに対し、Let's Encryptを使用してSSL/TLS証明書を取得して設定を行います。  
対象は外部公開のリモートサーバー/リバースプロキシまたはロードバランサのいずれかです。  
以降の説明では dev.sansa.com の部分は、自分の環境に合わせて変更してください。

## CertbotのインストールとSSL/TLS証明書の取得
1. Certbotをインストールします。
   ```shell
   sudo apt-get update
   sudo apt-get install certbot
   ```
1. NginxにCertbotが一時ファイルを置く場所を設定します。サイト設定ファイルを開きます。
   ```shell
   sudo nano /etc/nginx/sites-available/dev.sansa.com
   ```
1. http設定の server {} 内に以下を追加して保存します。  
   以降の通信経路にSSL/TLS保護が不要な場合は、https設定の proxy_pass で http のポートを指定してください。
   ```yaml
    location ~ /.well-known/acme-challenge {
        root /var/www/html; # Certbotが一時ファイルを置くディレクトリ
        allow all;
    }
   ```
1. 証明書を取得します。これで自動更新もされるようになります。
   ```shell
   sudo certbot certonly --webroot -w /var/www/html -d dev.sansa.com
   ```
## SSL/TLS証明書の設定
1. Ngonxに証明書を設定します。サイト設定ファイルを開きます。
   ```shell
   sudo nano /etc/nginx/sites-available/dev.sansa.com
   ```
1. https設定の server {} 内に以下を追加して保存します。
   ```yaml
    ssl_certificate /etc/letsencrypt/live/dev.sansa.com/fullchain.pem;
    ssl_certificate_key /etc/letsencrypt/live/dev.sansa.com/privkey.pem;
   ```
1. Ngonxを再起動してhttpsで接続できるか確認します。
   ```shell
   sudo nginx -t
   # エラーがなければNginxを再起動
   sudo systemctl restart nginx
   ```

***
[目次](../目次.md) > サーバー環境構築 SSL/TLS証明書の設定