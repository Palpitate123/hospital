package com.hospital.security;

import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.security.Key;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * JWT令牌生成与验证工具类
 * 负责Token的生成、解析、验证等核心操作
 * 
 * @author 徐凌
 * @version 1.0.0
 */
@Slf4j
@Component
public class JwtTokenProvider {

    /**
     * JWT签名密钥
     */
    @Value("${jwt.secret}")
    private String jwtSecret;

    /**
     * JWT过期时间（毫秒）
     */
    @Value("${jwt.expiration}")
    private long jwtExpiration;

    /**
     * 签名密钥对象
     */
    private Key key;

    /**
     * 初始化签名密钥
     */
    @PostConstruct
    public void init() {
        this.key = Keys.hmacShaKeyFor(jwtSecret.getBytes());
    }

    /**
     * 生成JWT令牌
     * 
     * @param userId 用户ID
     * @param username 用户名
     * @param roleCode 角色编码
     * @return JWT令牌
     */
    public String generateToken(Long userId, String username, String roleCode) {
        Date now = new Date();
        Date expiryDate = new Date(now.getTime() + jwtExpiration);

        Map<String, Object> claims = new HashMap<>();
        claims.put("userId", userId);
        claims.put("username", username);
        claims.put("roleCode", roleCode);

        return Jwts.builder()
                .setClaims(claims)
                .setSubject(username)
                .setIssuedAt(now)
                .setExpiration(expiryDate)
                .signWith(key, SignatureAlgorithm.HS512)
                .compact();
    }

    /**
     * 从Token中获取用户名
     * 
     * @param token JWT令牌
     * @return 用户名
     */
    public String getUsernameFromToken(String token) {
        Claims claims = Jwts.parserBuilder()
                .setSigningKey(key)
                .build()
                .parseClaimsJws(token)
                .getBody();
        return claims.getSubject();
    }

    /**
     * 从Token中获取用户ID
     * 
     * @param token JWT令牌
     * @return 用户ID
     */
    public Long getUserIdFromToken(String token) {
        Claims claims = Jwts.parserBuilder()
                .setSigningKey(key)
                .build()
                .parseClaimsJws(token)
                .getBody();
        return claims.get("userId", Long.class);
    }

    /**
     * 从Token中获取角色编码
     * 
     * @param token JWT令牌
     * @return 角色编码
     */
    public String getRoleCodeFromToken(String token) {
        Claims claims = Jwts.parserBuilder()
                .setSigningKey(key)
                .build()
                .parseClaimsJws(token)
                .getBody();
        return claims.get("roleCode", String.class);
    }

    /**
     * 验证Token是否有效
     * 
     * @param token JWT令牌
     * @return 是否有效
     */
    public boolean validateToken(String token) {
        try {
            Jwts.parserBuilder()
                    .setSigningKey(key)
                    .build()
                    .parseClaimsJws(token);
            return true;
        } catch (MalformedJwtException ex) {
            log.error("无效的JWT令牌: {}", ex.getMessage());
        } catch (ExpiredJwtException ex) {
            log.error("JWT令牌已过期: {}", ex.getMessage());
        } catch (UnsupportedJwtException ex) {
            log.error("不支持的JWT令牌: {}", ex.getMessage());
        } catch (IllegalArgumentException ex) {
            log.error("JWT令牌为空: {}", ex.getMessage());
        }
        return false;
    }

    /**
     * 判断Token是否即将过期（小于1小时）
     * 
     * @param token JWT令牌
     * @return 是否即将过期
     */
    public boolean isTokenExpiringSoon(String token) {
        try {
            Claims claims = Jwts.parserBuilder()
                    .setSigningKey(key)
                    .build()
                    .parseClaimsJws(token)
                    .getBody();
            Date expiration = claims.getExpiration();
            long timeLeft = expiration.getTime() - System.currentTimeMillis();
            return timeLeft < 3600000;
        } catch (Exception e) {
            return true;
        }
    }
}
