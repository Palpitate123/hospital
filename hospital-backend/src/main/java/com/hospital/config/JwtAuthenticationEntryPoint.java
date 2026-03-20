package com.hospital.config;

import com.alibaba.fastjson2.JSON;
import com.hospital.common.Result;
import com.hospital.common.ResultCode;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * 认证入口点
 * 处理未认证请求，返回统一的错误响应
 * 
 * @author 徐凌
 * @version 1.0.0
 */
@Slf4j
@Component
public class JwtAuthenticationEntryPoint implements AuthenticationEntryPoint {

    /**
     * 处理认证失败
     * 当用户尝试访问受保护资源但未认证时调用
     */
    @Override
    public void commence(HttpServletRequest request, HttpServletResponse response,
                         AuthenticationException authException) throws IOException, ServletException {
        log.error("认证失败: {} - {}", request.getRequestURI(), authException.getMessage());

        // 设置响应头
        response.setContentType("application/json;charset=UTF-8");
        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);

        // 返回统一的错误响应
        Result<Void> result = Result.fail(ResultCode.UNAUTHORIZED);
        response.getWriter().write(JSON.toJSONString(result));
    }
}
