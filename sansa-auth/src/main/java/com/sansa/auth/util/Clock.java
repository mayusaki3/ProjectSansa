package com.sansa.auth.util;

import java.time.Instant;

/**
 * 時刻抽象。
 * 役割: 実装差し替え可能な now() 提供（UTで固定時刻に差し替え可能）
 */
public interface Clock {
    /**
     * 現在時刻（UTC）を返す。
     * @return Instant now
     */
    Instant now();
}
