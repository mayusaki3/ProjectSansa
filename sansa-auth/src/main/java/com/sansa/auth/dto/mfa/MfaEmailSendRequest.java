package com.sansa.auth.dto.mfa;

import lombok.*;

// 実装事情で空でもOK（identifier等を持たせるならここに追加）
@Value @Builder
public class MfaEmailSendRequest {
  String hint; // 任意: 配送先ヒントやテンプレ切替など
}
