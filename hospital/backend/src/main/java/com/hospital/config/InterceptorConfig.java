package com.hospital.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * 拦截器配置
 */
@Configuration
public class InterceptorConfig implements WebMvcConfigurer {

    @Autowired
    private JWTInterceptor jwtInterceptor;
    
    @Autowired
    private PermissionInterceptor permissionInterceptor;

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        // 添加JWT拦截器，排除登录、注册和科室列表接口
        registry.addInterceptor(jwtInterceptor)
                .addPathPatterns("/api/**")
                .excludePathPatterns(
                        "/api/user/login", 
                        "/api/user/register", 
                        "/api/user/register-doctor",
                        "/api/department/list" // 允许无需登录访问科室列表
                );
        
        // 添加权限拦截器，确保在JWT验证之后执行
        // 注意：拦截器的顺序很重要，先执行JWT验证，再执行权限检查
        registry.addInterceptor(permissionInterceptor)
                .addPathPatterns("/api/**")
                .excludePathPatterns(
                        "/api/user/login", 
                        "/api/user/register", 
                        "/api/user/register-doctor",
                        "/api/department/list" // 允许无需登录访问科室列表
                );
    }
}
