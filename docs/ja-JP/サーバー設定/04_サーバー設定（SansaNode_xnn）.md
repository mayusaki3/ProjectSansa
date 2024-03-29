SansaNode_x01/02 設定（node2台目以降）
--------------------------------------
■Ubuntuインストール
  https://ubuntu.com/download/server から以下をダウンロードしてインストール
    ubuntu-23.10-live-server-amd64.iso
  ネットワーク
    インターネット : IPv4
      インターネット接続タイプ : 静的IP
      アドレス                 : 192.168.3.30 ←各自の環境に合わせて設定
  Mirror address
    http://jp.archive.ubuntu.com/ubuntu/  または
    http://ftp.riken.jp/Linux/ubuntu/
  [X] Install OpenSSH server

■SSH有効化 - SSHで接続できない場合  ssh user@ipaddress
  状態確認
    sudo systemctl status ssh
  起動
    sudo systemctl start ssh

■ミラーサイト情報の変更 - sudo apt update が失敗する場合
  sudo nano /etc/apt/sources.list
    http://ftp.riken.jp/Linux/ubuntu

cat /etc/apt/sources.list | grep -v "#"
sed -i -e 's/ports.ubuntu.com\/ubuntu-ports/old-releases.ubuntu.com\/ubuntu/g' /etc/apt/sources.list

■各種パッケージのアップグレード
  sudo apt update
  sudo apt-get upgrade

■Ubuntu Server を最新に更新
  sudo apt update
  sudo apt dist-upgrade
  sudo apt autoremove
  do-release-upgrade

■日本語導入 (x01はlocaleは変更しない、x02はlocaleも変更)
  sudo apt update
  sudo apt install -y language-pack-ja
# sudo update-locale LANG=ja_JP.UTF-8
  sudo timedatectl set-timezone Asia/Tokyo
  sudo reboot
# locale
# →LANG=ja_JP.UTF-8 があればOK

■Nginxのインストール
  sudo apt update
  sudo apt install nginx

■Javaのインストール
  sudo apt update
  sudo apt install -y openjdk-11-jdk
  java -version

■Cassandra 4.1.3のリポジトリ追加とインストール
  echo "deb https://debian.cassandra.apache.org 41x main" | sudo tee -a /etc/apt/sources.list.d/cassandra.sources.list 
  wget -q -O - https://downloads.apache.org/cassandra/KEYS | sudo apt-key add -
  sudo apt update
  sudo apt install cassandra
  sudo systemctl status cassandra
  nodetool status

■Nginxの設定（defaultページの変更）
  確認用にページにノード名を付ける
  sudo nano /var/www/html/index.nginx-debian.html

■ここまで（以下編集中）

■Cassandraの設定（ノード追加）
  cqlsh
  > UPDATE system.local SET cluster_name = 'クラスタ名' where key = 'local';
  > exit
  → Cassandra設定.txt の「クラスタ設定」のcluster_nameを設定
  sudo nano /etc/cassandra/cassandra.yaml
  → Cassandra設定.txt の「クラスタ設定」の内容を設定
  sudo systemctl restart cassandra
  nodetool status
  cqlsh ノードのIPアドレス

■ここまで（以下編集中）




■.NET SDKのUbuntu 23.10用リポジトリ追加と.NET SDK 8.0のインストール
  wget https://packages.microsoft.com/config/ubuntu/23.10/packages-microsoft-prod.deb -O packages-microsoft-prod.deb
  sudo dpkg -i packages-microsoft-prod.deb
  rm packages-microsoft-prod.deb
  sudo apt-get update
  sudo apt-get install -y apt-transport-https
  sudo apt-get update
  sudo apt-get install -y dotnet-sdk-8.0

■SansaXRの設定
  → 不要（同期される）
