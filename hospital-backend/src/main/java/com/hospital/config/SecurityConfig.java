package com.hospital.config;

import com.hospital.security.JwtAuthenticationFilter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

/**
 * Spring Security安全配置类
 * 配置认证授权规则、密码加密方式、JWT过滤器等
 * 
 * @author 徐凌
 * @version 1.0.0
 */
@Configuration
@EnableWebSecurity
@EnableGlobalMethodSecurity(prePostEnabled = true)
public class SecurityConfig extends WebSecurityConfigurerAdapter {

    @Autowired
    private UserDetailsService userDetailsService;

    @Autowired
    private JwtAuthenticationFilter jwtAuthenticationFilter;

    @Autowired
    private AuthenticationEntryPoint authenticationEntryPoint;

    /**
     * 密码加密器
     * 使用BCrypt算法进行密码加密
     */
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    /**
     * 认证管理器
     */
    @Bean
    @Override
    public AuthenticationManager authenticationManagerBean() throws Exception {
        return super.authenticationManagerBean();
    }

    /**
     * 配置认证管理器
     */
    @Override
    protected void configure(AuthenticationManagerBuilder auth) throws Exception {
        auth.userDetailsService(userDetailsService).passwordEncoder(passwordEncoder());
    }

    /**
     * 配置HTTP安全规则
     */
    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http
                // 禁用CSRF（使用JWT不需要CSRF保护）
                .csrf().disable()
                // 禁用Session（使用JWT无状态认证）
                .sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                .and()
                // 配置异常处理
                .exceptionHandling()
                .authenticationEntryPoint(authenticationEntryPoint)
                .and()
                // 配置请求授权规则
                .authorizeRequests()
                // 允许匿名访问的接口
                .antMatchers(
                        "/auth/login",
                        "/auth/register",
                        "/auth/captcha",
                        "/department/list",
                        "/department/page",
                        "/department/*",
                        "/doctor/page",
                        "/doctor/dept/*",
                        "/doctor/*",
                        "/schedule/page",
                        "/schedule/doctor/*",
                        "/schedule/*",
                        "/appointment/stats/*",
                        "/appointment/page",
                        "/swagger-ui/**",
                        "/swagger-ui.html",
                        "/v3/api-docs/**",
                        "/v2/api-docs",
                        "/swagger-resources/**",
                        "/webjars/**",
                        "/druid/**",
                        "/file/upload",
                        "/file/download/**",
                        "/file/preview/**"
                ).permitAll()
                // 静态资源允许访问
                .antMatchers(
                        "/",
                        "/*.html",
                        "/*.css",
                        "/*.js",
                        "/favicon.ico",
                        "/static/**",
                        "/assets/**"
                ).permitAll()
                // OPTIONS请求允许访问
                .antMatchers("/OPTIONS", "/**").permitAll()
                // 其他所有请求需要认证
                .anyRequest().authenticated()
                .and()
                // 添加JWT过滤器
                .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class)
                // 禁用缓存
                .headers().cacheControl();
    }
}
