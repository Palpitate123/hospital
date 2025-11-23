package com.hospital.config;

import com.hospital.exception.GlobalException;
import com.hospital.mapper.RoleMapper;
import com.hospital.mapper.PermissionMapper;
import com.hospital.entity.Role;
import com.hospital.entity.Permission;
import com.hospital.utils.JWTUtils;
import io.jsonwebtoken.Claims;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.HandlerInterceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.List;

/**
 * JWT拦截器
 */
@Component
public class JWTInterceptor implements HandlerInterceptor {

    @Autowired
    private JWTUtils jwtUtils;

    @Autowired
    private RoleMapper roleMapper;

    @Autowired
    private PermissionMapper permissionMapper;

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) {
        // 从请求头中获取token
        String token = request.getHeader("token");

        // 如果不是映射到方法直接通过
        if (!(handler instanceof HandlerMethod)) {
            return true;
        }

        // 检查token是否存在
        if (token == null || token.isEmpty()) {
            throw new GlobalException(401, "请先登录");
        }

        // 验证token
        try {
            if (jwtUtils.isTokenExpired(token)) {
                throw new GlobalException(401, "登录已过期，请重新登录");
            }
            
            // 解析token获取claims
            Claims claims = jwtUtils.parseToken(token);
            
            // 将用户ID存入请求中
            Long userId = jwtUtils.getUserIdFromToken(token);
            request.setAttribute("userId", userId);
            
            // 从token的claims中获取角色信息，如果没有则从数据库查询
            String roleCode = (String) claims.get("roleCode");
            if (roleCode == null) {
                // 从数据库查询用户角色
                List<Role> roles = roleMapper.selectByUserId(userId);
                if (!roles.isEmpty()) {
                    roleCode = roles.get(0).getRoleCode();
                }
            }
            
            if (roleCode != null) {
                request.setAttribute("roleCode", roleCode);
            }
            
            // 从数据库查询用户权限列表
            List<Permission> permissions = permissionMapper.selectByUserId(userId);
            request.setAttribute("permissions", permissions);
            
        } catch (Exception e) {
            throw new GlobalException(401, "无效的登录凭证");
        }

        return true;
    }
}
