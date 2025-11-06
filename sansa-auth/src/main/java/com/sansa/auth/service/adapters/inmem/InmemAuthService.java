package com.sansa.auth.service.adapters.inmem;

import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;
import com.sansa.auth.service.AuthService;

/**
 * 役割: inmem プロファイルのプレースホルダ。
 * 方針:
 *  - コアの AuthServiceImpl が存在する限り、このBeanは登録しない（ConditionalOnMissingBean）。
 *  - インターフェースは実装しない（コンパイルエラー回避のため）。
 *  - 将来、本当に in-memory 実装が必要になったら、Port実装またはAdapterに置換する。
 */
@Service
@Profile("inmem")
@ConditionalOnMissingBean(AuthService.class)
public class InmemAuthService {
    // プレースホルダ。メソッド実装は持たない。
}
