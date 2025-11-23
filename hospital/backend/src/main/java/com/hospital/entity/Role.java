package com.hospital.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.util.Date;

/**
 * 角色实体类
 */
@Data
@TableName("role")
public class Role {
    private Long id;
    private String roleName;
    private String roleCode;
    private String description;
    private Date createTime;
    private Date updateTime;
}
