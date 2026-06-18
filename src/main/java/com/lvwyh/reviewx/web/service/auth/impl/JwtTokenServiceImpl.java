package com.lvwyh.reviewx.web.service.auth.impl;

import com.lvwyh.reviewx.web.common.exception.BusinessException;
import com.lvwyh.reviewx.web.config.security.JwtProperties;
import com.lvwyh.reviewx.web.service.auth.TokenService;
import com.lvwyh.reviewx.web.service.auth.model.TokenClaims;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jws;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import javax.annotation.PostConstruct;
import java.nio.charset.StandardCharsets;
import java.security.Key;
import java.util.Date;

@Service
public class JwtTokenServiceImpl implements TokenService {

    private static final String CLAIM_USER_ID = "userId";
    private static final String CLAIM_USERNAME = "username";
    private static final String CLAIM_TOKEN_VERSION = "tokenVersion";

    private final JwtProperties jwtProperties;
    private Key key;

    public JwtTokenServiceImpl(JwtProperties jwtProperties) {
        this.jwtProperties = jwtProperties;
    }

    @PostConstruct
    public void init() {
        if (!StringUtils.hasText(jwtProperties.getSecret())) {
            throw new IllegalStateException("reviewx.jwt.secret cannot be empty");
        }
        this.key = Keys.hmacShaKeyFor(jwtProperties.getSecret().getBytes(StandardCharsets.UTF_8));
    }

    @Override
    public String generate(Long userId, String username, Integer tokenVersion) {
        long now = System.currentTimeMillis();
        long expireMillis = (jwtProperties.getExpireSeconds() == null ? 7200L : jwtProperties.getExpireSeconds()) * 1000L;
        return Jwts.builder()
                .claim(CLAIM_USER_ID, userId)
                .claim(CLAIM_USERNAME, username)
                .claim(CLAIM_TOKEN_VERSION, tokenVersion)
                .setIssuedAt(new Date(now))
                .setExpiration(new Date(now + expireMillis))
                .signWith(key, SignatureAlgorithm.HS256)
                .compact();
    }

    @Override
    public TokenClaims parse(String token) {
        try {
            Jws<Claims> claimsJws = Jwts.parserBuilder().setSigningKey(key).build().parseClaimsJws(token);
            Claims claims = claimsJws.getBody();
            TokenClaims tokenClaims = new TokenClaims();
            Object userId = claims.get(CLAIM_USER_ID);
            Object tokenVersion = claims.get(CLAIM_TOKEN_VERSION);
            tokenClaims.setUserId(userId instanceof Number ? ((Number) userId).longValue() : null);
            tokenClaims.setUsername(claims.get(CLAIM_USERNAME, String.class));
            tokenClaims.setTokenVersion(tokenVersion instanceof Number ? ((Number) tokenVersion).intValue() : null);
            return tokenClaims;
        } catch (JwtException | IllegalArgumentException e) {
            throw new BusinessException(401, "无效或过期的访问令牌");
        }
    }
}
