package com.sansa.auth.web.advice;

import com.sansa.auth.exception.BadRequestException;
import com.sansa.auth.exception.ConflictException;
import com.sansa.auth.exception.DomainException;
import com.sansa.auth.exception.GoneException;
import com.sansa.auth.exception.InvalidCodeException;
import com.sansa.auth.exception.InvalidCredentialsException;
import com.sansa.auth.exception.InvalidTokenException;
import com.sansa.auth.exception.NotFoundException;
import com.sansa.auth.exception.RateLimitException;
import com.sansa.auth.exception.SessionNotFoundException;
import com.sansa.auth.exception.TokenExpiredException;
import com.sansa.auth.exception.TokenReusedException;
import com.sansa.auth.exception.UnauthorizedException;
import java.net.URI;
import java.util.Arrays;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

/**
 * 認証サービス(sansa-auth)におけるグローバル例外ハンドラ。
 *
 * <p>責務:
 * <ul>
 *   <li>DomainException（認証ドメイン例外）を HTTP の ProblemDetail (RFC7807) に変換する。</li>
 *   <li>ProblemDetail.type は {@code urn:sansa:auth:error:<code>} を採用する。</li>
 *   <li>翻訳・編集はクライアント側で行う前提とし、サーバ側で detail を組み立てない。</li>
 *   <li>機密情報が {@code args} に入らないよう、例外を投げる側で制御する。</li>
 *   <li>想定外例外は 500 + 固定 code(INTERNAL_ERROR) でまとめる。</li>
 * </ul>
 */
@RestControllerAdvice
public class GlobalExceptionHandler {

    private static final Logger LOG = LoggerFactory.getLogger(GlobalExceptionHandler.class);

    /** sansa-auth 固有: ProblemDetail.type の prefix */
    private static final String TYPE_PREFIX = "urn:sansa:auth:error:";

    /** 想定外例外用の固定コード */
    private static final String INTERNAL_ERROR_CODE = "INTERNAL_ERROR";

    /**
     * 認証ドメイン例外（HTTP 4xx）を ProblemDetail に変換して返す。
     *
     * <p>レスポンス設計:
     * <ul>
     *   <li>type: urn:sansa:auth:error:&lt;code&gt;</li>
     *   <li>title: code（クライアントで翻訳可能にするため）</li>
     *   <li>detail: 原則 null（機密・内部事情の露出を避ける。翻訳はクライアント）</li>
     *   <li>extensions.code: code</li>
     *   <li>extensions.args: args（そのまま。機密は入れない運用）</li>
     * </ul>
     */
    @ExceptionHandler(DomainException.class)
    public ResponseEntity<ProblemDetail> handleDomainException(DomainException ex) {
        HttpStatus status = mapStatus(ex);
        ProblemDetail pd = buildProblemDetail(status, ex.getCode(), ex.getArgs());

        // 4xx は原則 warn（運用方針により info に落としてもよい）
        if (status.is5xxServerError()) {
            LOG.error("[problem] status={} code={} args={}", status.value(), ex.getCode(), safeArgs(ex.getArgs()), ex);
        } else {
            LOG.warn("[problem] status={} code={} args={}", status.value(), ex.getCode(), safeArgs(ex.getArgs()));
        }

        return ResponseEntity.status(status).body(pd);
    }

    /**
     * 想定外例外を 500 + INTERNAL_ERROR にまとめて返す。
     *
     * <p>機密露出を避けるため、例外 message / stacktrace はレスポンスに含めない。
     * ログには stacktrace を出す。
     */
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ProblemDetail> handleUnexpectedException(Exception ex) {
        HttpStatus status = HttpStatus.INTERNAL_SERVER_ERROR;
        ProblemDetail pd = buildProblemDetail(status, INTERNAL_ERROR_CODE, new Object[0]);

        LOG.error("[problem] status={} code={}", status.value(), INTERNAL_ERROR_CODE, ex);

        return ResponseEntity.status(status).body(pd);
    }

    /**
     * DomainException のサブ型に基づき HTTP ステータスを割り当てる。
     *
     * <p>注意:
     * <ul>
     *   <li>ここは sansa-auth の外部仕様（API契約）に直結するため、変更時は仕様更新が必要。</li>
     *   <li>トークン系は「再認証を促す」意味で 401 に寄せる。</li>
     *   <li>TokenReused は意味的に Conflict(409) として扱う。</li>
     * </ul>
     */
    private HttpStatus mapStatus(DomainException ex) {
        // 400
        if (ex instanceof BadRequestException || ex instanceof InvalidCodeException) {
            return HttpStatus.BAD_REQUEST;
        }

        // 401
        if (ex instanceof UnauthorizedException
                || ex instanceof InvalidCredentialsException
                || ex instanceof InvalidTokenException
                || ex instanceof TokenExpiredException) {
            return HttpStatus.UNAUTHORIZED;
        }

        // 404
        if (ex instanceof NotFoundException || ex instanceof SessionNotFoundException) {
            return HttpStatus.NOT_FOUND;
        }

        // 409
        if (ex instanceof ConflictException || ex instanceof TokenReusedException) {
            return HttpStatus.CONFLICT;
        }

        // 410
        if (ex instanceof GoneException) {
            return HttpStatus.GONE;
        }

        // 429
        if (ex instanceof RateLimitException) {
            return HttpStatus.TOO_MANY_REQUESTS;
        }

        // 想定外の DomainException は 400 扱い（＝クライアント入力・状態起因）
        return HttpStatus.BAD_REQUEST;
    }

    /**
     * ProblemDetail を構築する共通処理。
     *
     * @param status HTTP ステータス
     * @param code 機械可読なエラーコード（翻訳キー）
     * @param args 翻訳・文言組み立て用の引数（機密禁止）
     * @return ProblemDetail
     */
    private ProblemDetail buildProblemDetail(HttpStatus status, String code, Object[] args) {
        ProblemDetail pd = ProblemDetail.forStatus(status);
        pd.setType(URI.create(TYPE_PREFIX + (code == null ? "" : code)));
        pd.setTitle(code);
        // 翻訳・編集はクライアント側で行う方針のため、detail は積極的に埋めない。
        pd.setDetail(null);

        // extensions
        pd.setProperty("code", code);
        pd.setProperty("args", args == null ? new Object[0] : Arrays.copyOf(args, args.length));

        return pd;
    }

    /**
     * ログ用に args を安全に整形する（null 安全）。
     *
     * <p>注意: ここはマスキングを行わない。機密は args に入れない運用で担保する。
     * 機密混入リスクが現実化した場合は、ここでマスキング戦略を導入する。
     */
    private String safeArgs(Object[] args) {
        if (args == null) {
            return "[]";
        }
        try {
            return Arrays.toString(args);
        } catch (RuntimeException e) {
            return "[<args_toString_failed>]";
        }
    }
}
