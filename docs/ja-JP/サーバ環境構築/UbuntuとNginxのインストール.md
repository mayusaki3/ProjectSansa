[目次](../目次.md) > サーバ環境構築 UbuntuとNginxのインストール

## はじめに
この手順では Ubuntu Server 23.10 と Nginx をインストールします。  
異なるバージョンの Ubuntu Server をインストールする場合は、バージョン部分を読み替えてください。  
記述がない部分については、既定値で進めてかまいません。

## Ubuntuのインストール
1. Ubuntuの公式サイトから以下のインストーラをダウンロードします。  
   https://ubuntu.com/download/server    
   ubuntu-23.10-live-server-amd64.iso  
1. isoファイルからインストールを進めます。
1. ネットワークの設定で、制的IPに変更します。  
    インターネット : IPv4  
      インターネット接続タイプ : 静的IP  
      アドレス                 : 192.168.3.30 ←各自の環境に合わせて設定
1. OpenSSHのインストールを指定します。  
    [X] Install OpenSSH server
1. インストールが完了したらリブートします。  
1. SSHを起動します。
   ```
   sudo systemctl start ssh
   ```
1. ターミナルから接続を確認します。
   ```
   ssh user@ipaddress
   ```
   再インストール後に接続できない場合、.ssh/known_hosts の古い情報を削除する必要があります。(Windowsの場合)

1. 各種パッケージと Ubuntu Server を最新に更新します。
   ```shell
   sudo apt update
   sudo apt-get upgrade
   sudo apt update
   sudo apt dist-upgrade
   sudo apt autoremove
   do-release-upgrade
   ```
1. 日本語を使用できるようにします。  
   ターミナルで日本語を使用する場合は # を削除して実行してください。
   ```shell
   sudo apt update
   sudo apt install -y language-pack-ja
   # sudo update-locale LANG=ja_JP.UTF-8
   sudo timedatectl set-timezone Asia/Tokyo
   sudo reboot
   locale
   ```
   ターミナルで日本語を使用する場合、LANG=ja_JP.UTF-8 があればOKです。

## Nginxのインストール
1. Nginxをインストールします。
   ```shell
   sudo apt update
   sudo apt install nginx
   ```
1. ブラウザで IPアドレスにアクセスします。  
   Nginxのデフォルトページが表示されます。  
   デフォルトページを編集するには次のようにします。
   ```shell
   sudo nano /var/www/html/index.nginx-debian.html
   ```

***
[目次](../目次.md) > サーバ環境構築 UbuntuとNginxのインストール
