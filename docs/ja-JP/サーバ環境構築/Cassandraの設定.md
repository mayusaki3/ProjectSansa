[目次](../目次.md) > サーバ環境構築 Cassandraの設定

## はじめに
この手順では SansaXR クラスタの設定を行い、ノードとして設定します。  
以下の説明のIPアドレス 192.168.3.31 はシードノードの、 192.168.3.32 は自ノードのものに読み替えてください。

## SansaXR クラスタの設定
1. cqlsh を起動します。
   ```shell
   cqlsh
   ```
1. Cassandraの初期クラスタ名を SansaXR Cluster に変更します。  
   この操作をせずに cassandra.yaml の cluster_name を変更すると、Cassandraは起動に失敗します。
   ```sql
   UPDATE system.local SET cluster_name = 'SansaXR Cluster' where key = 'local';
   exit
   ```
1. cassandra.yaml ファイルを開きます。
   ```shell
   sudo nano /etc/cassandra/cassandra.yaml
   ```
1. 以下のように編集して保存します。
   ```yaml
   cluster_name: 'SansaXR Cluster'
   seed_provider:
     - class_name: org.apache.cassandra.locator.SimpleSeedProvider
       parameters:
         - seeds: "192.168.3.31:7000"  #シードノードのIPアドレス
   listen_address: 192.168.3.32        #自ノードのIPアドレス
   endpoint_snitch: SimpleSnitch       #ノードの物理的な位置 リリース時は環境に合わせて変更
   ```
1. Cassandraを再起動してクラスタ状態を確認します。
   また、cqlshの起動時に表示されるクラスタ名を確認します。
   ```shell
   sudo systemctl restart cassandra
   nodetool status
   cqlsh
   ```
   ```sql
   exit
   ```

## ノードを削除する場合（未検証）
1. クラスタからノードを削除します。
   ```shell
   nodetool -h 削除するノードのIPアドレス decommission
   nodetool status
   ```

***
[目次](../目次.md) > サーバ環境構築 Cassandraの設定
