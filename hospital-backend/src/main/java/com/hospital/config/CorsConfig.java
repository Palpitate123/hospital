package com.hospital.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.web.filter.CorsFilter;

/**
 * 跨域配置类
 * 配置前后端分离架构下的跨域访问规则
 * 
 * @author 徐凌
 * @version 1.0.0
 */
@Configuration
public class CorsConfig {

    /**
     * 跨域过滤器
     * 允许前端应用跨域访问后端接口
     */
    @Bean
    public CorsFilter corsFilter() {
        CorsConfiguration config = new CorsConfiguration();

        // 允许的域名（开发环境允许所有域名）
        config.addAllowedOriginPattern("*");

        // 允许的请求头
        config.addAllowedHeader("*");

        // 允许的请求方法
        config.addAllowedMethod("*");

        // 允许携带Cookie
        config.setAllowCredentials(true);

        // 预检请求的有效期（秒）
        config.setMaxAge(3600L);

        // 暴露的响应头
        config.addExposedHeader("Authorization");
        config.addExposedHeader("Content-Disposition");

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", config);

        return new CorsFilter(source);
    }
}
