package com.hospital.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.util.Date;

/**
 * 权限实体类
 */
@Data
@TableName("permission")
public class Permission {
    private Long id;
    private String permissionName;
    private String permissionCode;
    private String description;
    private Date createTime;
    private Date updateTime;
}
