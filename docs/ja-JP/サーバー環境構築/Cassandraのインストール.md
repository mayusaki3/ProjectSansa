[目次](../目次.md) > サーバー環境構築 Cassandraのインストール

## はじめに
この手順では Java 17 と Cassandra 5.0-bata1 をインストールします。  
異なるバージョンの Java と Cassandra をインストールする場合は、バージョン部分を読み替えてください。  

## Javaのインストール
1. Cassandraを動かすために Jdk 17 をインストールします。
   ```shell
   sudo apt update
   sudo apt install -y openjdk-17-jdk
   java -version
   ```

## Cassandraのインストール
1. Cassandra 5.0-bata1のリポジトリを追加してインストールします。
   ```shell
   echo "deb https://debian.cassandra.apache.org 50x main" | sudo tee -a /etc/apt/sources.list.d/cassandra.sources.list
   wget -q -O - https://downloads.apache.org/cassandra/KEYS | sudo apt-key add -
   sudo apt update
   sudo apt install cassandra
   sudo systemctl status cassandra
   nodetool status
   ```

***
[目次](../目次.md) > サーバー環境構築 Cassandraのインストール
