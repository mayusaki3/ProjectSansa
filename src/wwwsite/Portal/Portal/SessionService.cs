/// <summary>
/// セッションサービス
/// </summary>
/// <remarks>
/// サーバーサイドで管理するセッション情報
/// </remarks>
public class SessionService
{
    /// <summary>
    /// ログイン状態
    /// </summary>
    public bool IsLoggedIn { get; set; } = false;

    /// <summary>
    /// 現在の表示言語
    /// </summary>
    public string DisplayLanguage { get; set; }= "ja-JP";
}
