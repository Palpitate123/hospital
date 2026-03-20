package com.hospital.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * 系统用户实体类
 * 存储系统所有用户的通用账号信息
 * 
 * @author 徐凌
 * @version 1.0.0
 */
@Data
@TableName("sys_user")
public class SysUser implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 用户唯一ID（主键，自增）
     */
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    /**
     * 登录用户名（唯一）
     */
    private String username;

    /**
     * 登录密码（BCrypt加密存储）
     */
    private String password;

    /**
     * 用户昵称
     */
    private String nickName;

    /**
     * 账号状态：1-正常，0-停用
     */
    private Integer status;

    /**
     * 账号创建时间
     */
    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;

    /**
     * 信息更新时间
     */
    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updateTime;

    /**
     * 备注信息
     */
    private String remark;

    /**
     * 逻辑删除标识：0-未删除，1-已删除
     */
    @TableLogic
    private Integer deleted;

    /**
     * 角色ID（关联查询）
     */
    @TableField(exist = false)
    private Long roleId;

    /**
     * 角色编码（关联查询）
     */
    @TableField(exist = false)
    private String roleCode;

    /**
     * 角色名称（关联查询）
     */
    @TableField(exist = false)
    private String roleName;
}
