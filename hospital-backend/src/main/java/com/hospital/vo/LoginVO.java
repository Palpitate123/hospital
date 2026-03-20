package com.hospital.vo;

import lombok.Data;

import java.io.Serializable;

/**
 * 登录响应VO
 * 返回登录成功后的用户信息和令牌
 * 
 * @author 徐凌
 * @version 1.0.0
 */
@Data
public class LoginVO implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 用户ID
     */
    private Long userId;

    /**
     * 用户名
     */
    private String username;

    /**
     * 昵称
     */
    private String nickName;

    /**
     * 角色编码
     */
    private String roleCode;

    /**
     * 角色名称
     */
    private String roleName;

    /**
     * JWT令牌
     */
    private String token;

    /**
     * 令牌类型
     */
    private String tokenType = "Bearer";

    /**
     * 过期时间（毫秒）
     */
    private Long expiresIn;
}
