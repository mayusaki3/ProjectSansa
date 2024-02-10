[目次](../目次.md) > サーバ環境構築 Sambaのインストール

## はじめに
この手順では Samba をインストールします。  

## Sambaのインストール
1. Samba をインストールします。
   ```shell
   sudo apt update
   sudo apt install samba
   ```

## SansaXRディレクトリの作成と共有の設定
1. SansaXRディレクトリを作成します。  
   既存のusernameを使用します。usernameは実際のものに読み替えてください。
   ```shell
   mkdir /home/username/shared
   ```
1. SanbaでSansaXRディレクトリをフルコントロールで共有します。  
   smb.conf ファイルを開きます。
   ```shell
   sudo nano /etc/samba/smb.conf
   ```
1. 以下の内容を追加して保存します。
   usernameは実際のものに読み替えてください。
   ```ini
   [shared]
   path = /home/username/shared
   read only = no
   browsable = yes
   ```
1. Sanbaユーザーとして既存のusernameを指定して、Samba用のパスワードを設定します。
   設定を反映するためサービスを再起動します。
   ```shell
   sudo smbpasswd -a username
   sudo systemctl restart smbd
   ```
1. Windowsのエクスプローラで表示されるようにします。
   ```shell
   sudo apt install wsdd
   ```
1. 他のWindowsPCから、エクスプローラでこのノードにアクセスします。  
   sharedディレクトリが共有されていればOKです。

***
[目次](../目次.md) > サーバ環境構築 smbのインストール
