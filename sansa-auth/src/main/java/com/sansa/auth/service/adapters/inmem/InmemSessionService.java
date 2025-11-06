package com.sansa.auth.service.adapters.inmem;

import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;
import com.sansa.auth.service.SessionService;

/**
 * 役割: inmem プロファイルのプレースホルダ。
 * 方針: 上記 InmemAuthService と同様。コア SessionServiceImpl 優先。
 */
@Service
@Profile("inmem")
@ConditionalOnMissingBean(SessionService.class)
public class InmemSessionService {
    // プレースホルダ。メソッド実装は持たない。
}
