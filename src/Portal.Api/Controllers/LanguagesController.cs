using Microsoft.AspNetCore.Mvc;

/// <summary>
/// 言語関連APIコントローラー
/// </summary>
[ApiController]
[Route("api/[controller]")]
public class LanguagesController : ControllerBase
{
    private readonly CassandraService cassandra;

    /// <summary>
    /// コンストラクター
    /// </summary>
    /// <param name="cassandra">Cassandraサービス</param>
    public LanguagesController(CassandraService cassandra)
    {
        this.cassandra = cassandra;
    }

    /// <summary>
    /// サポート言語リストを取得する
    /// </summary>
    /// <returns>サポート言語リスト</returns>
    [HttpGet]
    public async Task<IEnumerable<KeyValuePair<string, string>>> Get()
    {
        return await cassandra.GetDisplayLanguages();
    }

    /// <summary>
    /// 言語コードから言語名を取得する
    /// </summary>
    /// <param name="code">言語コード</param>
    /// <returns>言語名</returns>
    [HttpGet("name/{code}")]
    public async Task<string> GetName(string code)
    {
        return await cassandra.GetLanguageName(code);
    }

    /// <summary>
    /// 言語名から言語コードを取得する
    /// </summary>
    /// <param name="name">言語名</param>
    /// <returns>言語コード</returns>
    [HttpGet("code/{name}")]
    public async Task<string> GetCode(string name)
    {
        return await cassandra.GetLanguageCode(name);
    }
}
