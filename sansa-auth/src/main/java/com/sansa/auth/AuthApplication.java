package com.sansa.auth;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * アプリケーションのエントリポイント。
 *
 * <p>主な役割:
 * <ul>
 *   <li>Spring Boot の起動（コンポーネントスキャンの開始）</li>
 *   <li>プロファイル（例: {@code inmem}, {@code prod}）の切替により、Store 実装や設定を差し替え</li>
 * </ul>
 *
 * <p>注意:
 * <ul>
 *   <li>テストでは {@code @SpringBootTest} で本クラスを参照し、Webレイヤ単体テストでは {@code @WebMvcTest} を使用</li>
 *   <li>アプリ全体設定（例: 文字コード、ロケール、MessageSource 等）は必要に応じて別Configに分離</li>
 * </ul>
 */
@SpringBootApplication
public class AuthApplication {
    public static void main(String[] args) {
        SpringApplication.run(AuthApplication.class, args);
    }
}
