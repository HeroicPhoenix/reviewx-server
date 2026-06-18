package com.lvwyh.reviewx.web.config.security;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

/**
 * 密码加密配置。
 *
 * 登录密码统一使用 BCrypt 哈希存储，避免明文密码落库。
 */
@Configuration
public class PasswordConfig {

    /**
     * BCrypt 会自动生成盐值并写入哈希结果中，校验时不需要额外保存 salt。
     */
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}
