package com.hospital.dto;

import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.io.Serializable;

/**
 * 医生请求DTO
 * 接收医生新增/编辑请求参数
 * 
 * @author 徐凌
 * @version 1.0.0
 */
@Data
public class DoctorDTO implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 医生ID（编辑时必填）
     */
    private Long id;

    /**
     * 用户名（新增时必填）
     */
    private String username;

    /**
     * 密码（新增时必填）
     */
    private String password;

    /**
     * 所属科室ID
     */
    @NotNull(message = "所属科室不能为空")
    private Long deptId;

    /**
     * 医生姓名
     */
    @NotBlank(message = "医生姓名不能为空")
    private String doctorName;

    /**
     * 执业职称
     */
    private String title;

    /**
     * 专业擅长
     */
    private String specialty;

    /**
     * 医生简介
     */
    private String doctorDesc;

    /**
     * 医生头像
     */
    private String avatar;

    /**
     * 账号状态
     */
    private Integer status;
}
