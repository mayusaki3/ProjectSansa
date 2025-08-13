/// <summary>
/// Cassandra設定クラス
/// </summary>
public class CassandraSettings
{
    /// <summary>
    /// 接続先IPアドレスリスト
    /// </summary>
    public string[] ContactPoints { get; set; } = new string[] { "127.0.0.1" };

    /// <summary>
    /// 接続先ポート
    /// </summary>
    public int Port { get; set; } = 9042;
}
