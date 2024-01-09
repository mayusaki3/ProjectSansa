Project Sansa: Cassandraの設定

■クラスタ設定: cassandra.yaml に設定
  cluster_name: 'SansaXR Cluster'
  seed_provider:
    - class_name: (既定値)
      parameters:
        - seeds: "192.168.33.1:7000"  ←シードのIPアドレス   各自の環境に合わせて設定
  listen_address: 192.168.33.2        ←自ノードのIPアドレス 各自の環境に合わせて設定
  rpc_address:: 192.168.33.2          ←自ノードのIPアドレス 各自の環境に合わせて設定
  endpoint_snitch: SimpleSnitch       ←ノードの物理的な位置 各自の環境に合わせて設定

■Cassandra Keyspace 定義: sansaxr
  cqlsh
  CREATE KEYSPACE IF NOT EXISTS sansaxr
    WITH replication = {'class': 'SimpleStrategy', 'replication_factor' : 3}
    AND DURABLE_WRITES = true;
  DESCRIBE keyspaces;

■ここまで（以下編集中）


CREATE TABLE IF NOT EXISTS sansaxr.users (
  userid ASCII PRIMARY KEY,
  startdate DATE,
  username TEXT,
  password TEXT,
  emmail TEXT
);



■ロール設定
  CREATE ROLE admin WITH PASSWORD = 'password' AND LOGIN = true;
  CREATE ROLE admin WITH PASSWORD = 'password' AND LOGIN = true;
GRANT SELECT ON KEYSPACE chat_app TO admin;


CREATE TABLE IF NOT EXISTS SansaXR.Roles (
  RoleId TEXT PRIMARY KEY,
  RoleName TEXT
);


■アカウント情報
  Keyspace[]
    key:    User
    value:  ColumnFamily[]
      key: 
      value: SuperColumn[]
        key: 
        value: Column[]

      
