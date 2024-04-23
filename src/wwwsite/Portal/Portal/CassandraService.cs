using Cassandra;
using Microsoft.Extensions.Options;

/// <summary>
/// Cassandraサービスクラス
/// </summary>
public partial class CassandraService
{
    /// <summary>
    /// Cassandraクラスタ
    /// </summary>
    private readonly Cluster cluster;

    /// <summary>
    /// コンストラクター
    /// </summary>
    /// <param name="settings">Cassandra設定</param>
    public CassandraService(IOptions<CassandraSettings> settings)
    {
        cluster = Cluster.Builder()
            .AddContactPoints(settings.Value.ContactPoints)
            .WithPort(settings.Value.Port)
            .Build();
    }

}
