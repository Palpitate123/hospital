package com.hospital.security;

import com.hospital.common.ResultCode;
import com.hospital.entity.SysUser;
import com.hospital.mapper.SysUserMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Collections;

/**
 * JWT认证过滤器
 * 拦截所有HTTP请求，验证JWT令牌有效性并设置认证信息
 * 
 * @author 徐凌
 * @version 1.0.0
 */
@Slf4j
@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    @Autowired
    private JwtTokenProvider jwtTokenProvider;

    @Autowired
    private SysUserMapper sysUserMapper;

    @Value("${jwt.header}")
    private String tokenHeader;

    @Value("${jwt.prefix}")
    private String tokenPrefix;

    /**
     * 过滤器核心方法
     * 从请求头中提取JWT令牌，验证并设置认证信息
     */
    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {
        try {
            // 从请求头获取JWT令牌
            String jwt = getJwtFromRequest(request);

            // 验证令牌有效性
            if (StringUtils.hasText(jwt) && jwtTokenProvider.validateToken(jwt)) {
                // 从令牌中获取用户ID
                Long userId = jwtTokenProvider.getUserIdFromToken(jwt);

                // 查询用户完整信息
                SysUser user = sysUserMapper.selectByIdWithRole(userId);

                if (user != null && user.getStatus() == 1) {
                    // 获取角色编码
                    String roleCode = jwtTokenProvider.getRoleCodeFromToken(jwt);

                    // 创建认证对象
                    UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(
                            user,
                            null,
                            Collections.singletonList(new SimpleGrantedAuthority("ROLE_" + roleCode))
                    );

                    // 设置到安全上下文
                    SecurityContextHolder.getContext().setAuthentication(authentication);
                }
            }
        } catch (Exception ex) {
            log.error("无法设置用户认证: {}", ex.getMessage());
        }

        filterChain.doFilter(request, response);
    }

    /**
     * 从请求头中提取JWT令牌
     * 
     * @param request HTTP请求
     * @return JWT令牌
     */
    private String getJwtFromRequest(HttpServletRequest request) {
        String bearerToken = request.getHeader(tokenHeader);
        if (StringUtils.hasText(bearerToken) && bearerToken.startsWith(tokenPrefix)) {
            return bearerToken.substring(tokenPrefix.length());
        }
        return null;
    }
}
