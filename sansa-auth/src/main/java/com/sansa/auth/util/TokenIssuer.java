package com.sansa.auth.util;

/**
 * サービス層から見た「トークン発行・検証」の抽象化。
 * - AuthServiceImpl などは JwtProvider の実装詳細を知らずに済む。
 * - テストではここを差し替え/モックにする。
 */
public interface TokenIssuer {

    /** リフレッシュID（jti）を新規採番する。 */
    String newRefreshId();

    /** アクセストークン発行。 */
    String issueAccessToken(String userId, int tokenVersion);

    /** リフレッシュトークン発行。 */
    String issueRefreshToken(String userId, String jti, int tokenVersion);

    /** リフレッシュのパース＆基本検証（署名・exp）。 */
    RefreshParseResult parseRefresh(String token);

    /** パース結果。 */
    record RefreshParseResult(String userId, String jti, int tokenVersion) {}
}
