package com.hospital.config;

import com.hospital.annotation.RequiresPermission;
import com.hospital.entity.Permission;
import com.hospital.exception.GlobalException;
import org.springframework.stereotype.Component;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.HandlerInterceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.List;

/**
 * 权限拦截器，用于检查用户是否拥有执行特定方法的权限
 */
@Component
public class PermissionInterceptor implements HandlerInterceptor {

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) {
        // 如果不是映射到方法直接通过
        if (!(handler instanceof HandlerMethod)) {
            return true;
        }

        HandlerMethod handlerMethod = (HandlerMethod) handler;
        
        // 获取方法上的RequiresPermission注解
        RequiresPermission annotation = handlerMethod.getMethodAnnotation(RequiresPermission.class);
        
        // 如果没有权限注解，直接通过
        if (annotation == null) {
            return true;
        }
        
        // 获取需要的权限编码
        String requiredPermission = annotation.value();
        
        // 从请求中获取用户权限列表（由JWTInterceptor设置）
        List<Permission> permissions = (List<Permission>) request.getAttribute("permissions");
        
        // 检查用户是否拥有所需权限
        boolean hasPermission = false;
        if (permissions != null) {
            for (Permission permission : permissions) {
                if (requiredPermission.equals(permission.getPermissionCode())) {
                    hasPermission = true;
                    break;
                }
            }
        }
        
        // 检查是否是管理员角色（管理员拥有所有权限）
        String roleCode = (String) request.getAttribute("roleCode");
        if ("ROLE_ADMIN".equals(roleCode)) {
            hasPermission = true;
        }
        
        // 如果没有权限，抛出异常
        if (!hasPermission) {
            throw new GlobalException(403, "权限不足，无法执行此操作");
        }
        
        return true;
    }
}
