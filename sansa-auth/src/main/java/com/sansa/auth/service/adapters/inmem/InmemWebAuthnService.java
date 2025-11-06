package com.sansa.auth.service.adapters.inmem;

import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;
import com.sansa.auth.service.WebAuthnService;

/**
 * 役割: inmem プロファイルのプレースホルダ。
 * 方針: 上記と同様。コア WebAuthnServiceImpl 優先。
 */
@Service
@Profile("inmem")
@ConditionalOnMissingBean(WebAuthnService.class)
public class InmemWebAuthnService {
    // プレースホルダ。メソッド実装は持たない。
}
