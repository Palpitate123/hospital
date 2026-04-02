package com.hospital.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * 系统用户实体类
 * 物理删除版本：删除后数据将从数据库彻底抹除
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
     * 登录用户名（唯一索引）
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
     * 物理删除模式下，deleted字段仅作为状态记录
     */
    private Integer deleted;

    /**
     * 角色ID（用于接收前端下拉框选中的ID，数据库无此字段）
     */
    @TableField(exist = false)
    private Long roleId;

    /**
     * 角色名称（用于列表展示角色名，数据库无此字段）
     */
    @TableField(exist = false)
    private String roleName;

    /**
     * 角色编码（用于权限判断）
     */
    @TableField(exist = false)
    private String roleCode;
}
