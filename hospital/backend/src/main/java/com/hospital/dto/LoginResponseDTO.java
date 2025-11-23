package com.hospital.dto;

import lombok.Data;

/**
 * 登录响应DTO
 */
@Data
public class LoginResponseDTO {
    private String token;
    private Long userId;
    private String username;
    private String realName;
    private String roleName;
    private String roleCode;
}
