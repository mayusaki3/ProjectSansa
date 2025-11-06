package com.sansa.auth.rate;

import java.time.Duration;
import java.time.Instant;

/**
 * レート制限ラッパ。
 * 役割: Store.hit(...) を呼び出した結果から、HTTPヘッダ計算に必要な最小情報を提供。
 */
public interface RateLimiter {

    /**
     * 指定キーのカウントを1加算し、現在の残数およびリセット時刻を返す。
     * @param key 一意キー（例: "preReg:{email}"）
     * @param limit 窓内の最大回数
     * @param window 窓幅
     * @param now 現在時刻
     * @return RateStatus
     */
    RateStatus hit(String key, int limit, Duration window, Instant now);

    /**
     * 状態値。HTTPヘッダ算出に必要な最小項目のみ。
     */
    final class RateStatus {
        public final int limit;
        public final int remaining;
        public final Instant resetAt;
        public final boolean limited;
        public final long retryAfterSec;

        public RateStatus(int limit, int remaining, Instant resetAt, Instant now) {
            this.limit = limit;
            this.remaining = Math.max(0, remaining);
            this.resetAt = resetAt;
            this.limited = this.remaining == 0;
            long sec = (resetAt == null) ? 0L : Math.max(0L, resetAt.getEpochSecond() - now.getEpochSecond());
            this.retryAfterSec = sec;
        }
    }
}
