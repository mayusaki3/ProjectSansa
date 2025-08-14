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
    public string DisplayLanguageCode { get; set; }= "ja-JP";

    /// <summary>
    /// 現在の表示言語名
    /// </summary>
    public string DisplayLanguageName { get; set; } = "日本語";

    /// <summary>
    /// メールアドレス
    /// </summary>
    public string EMail { get; set; } = default!;

    /// <summary>
    /// 認証コード確認結果
    /// </summary>
    public bool IsEMailChecked { get; set; } = false;
}
