using Cassandra;

/// <summary>
/// Cassandraサービスクラス・システム管理キースペース処理
/// </summary>
public partial class CassandraService
{
    #region メソッド

    #region サポート言語関連

    #region サポート言語リストを取得 (GetDisplayLanguages)

    /// <summary>
    /// サポート言語リストを取得する
    /// </summary>
    /// <returns>サポート言語リスト</returns>
    public async Task<IEnumerable<KeyValuePair<string, string>>> GetDisplayLanguages()
    {
        using var session = cluster.Connect();

        var statement = new SimpleStatement("SELECT language_code, language_name FROM SystemManage.display_languages");
        var rowSet = await session.ExecuteAsync(statement).ConfigureAwait(false);

        var languages = new List<KeyValuePair<string, string>>();
        foreach (var row in rowSet)
        {
            languages.Add(new KeyValuePair<string, string>(row.GetValue<string>("language_code"), row.GetValue<string>("language_name")));
        }

        return languages;
    }

    #endregion

    #region 言語コードから言語名を取得 (GetLanguageName)

    /// <summary>
    /// 言語コードから言語名を取得する
    /// </summary>
    /// <param name="languageCode">言語コード</param>
    /// <returns>言語名（未定義の場合は"日本語"を返す）</returns>
    public async Task<string> GetLanguageName(string languageCode)
    {
        using var session = cluster.Connect();

        var statement = new SimpleStatement(
            "SELECT language_name FROM SystemManage.display_languages WHERE language_code = ?", languageCode);

        var rowSet = await session.ExecuteAsync(statement).ConfigureAwait(false);
        var row = rowSet.SingleOrDefault();

        return row == null ? "日本語" : row.GetValue<string>("language_name");
    }

    #endregion

    #region 言語名から言語コードを取得 (GetLanguageCode)

    /// <summary>
    /// 言語名から言語コードを取得する
    /// </summary>
    /// <param name="languageName">言語名</param>
    /// <returns>言語コード（未定義の場合は"ja-JP"を返す）</returns>
    public async Task<string> GetLanguageCode(string languageName)
    {
        using var session = cluster.Connect();

        var statement = new SimpleStatement(
            "SELECT language_code FROM SystemManage.display_languages WHERE language_name = ?", languageName);

        var rowSet = await session.ExecuteAsync(statement).ConfigureAwait(false);
        var row = rowSet.SingleOrDefault();

        return row == null ? "ja-JP" : row.GetValue<string>("language_code");
    }

    #endregion

    #endregion

    #endregion
}
