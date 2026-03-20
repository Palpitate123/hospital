package com.hospital.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * 角色实体类
 * 定义系统角色信息，适配RBAC权限模型
 * 
 * @author 徐凌
 * @version 1.0.0
 */
@Data
@TableName("sys_role")
public class SysRole implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 角色唯一ID（主键，自增）
     */
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    /**
     * 角色名称（如：系统管理员、医生、患者）
     */
    private String roleName;

    /**
     * 角色标识（如：admin、doctor、patient）
     */
    private String roleCode;

    /**
     * 角色权限范围描述
     */
    private String roleDesc;

    /**
     * 记录创建时间
     */
    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;

    /**
     * 记录更新时间
     */
    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updateTime;
}
