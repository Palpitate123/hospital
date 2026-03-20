package com.hospital.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * 医生实体类
 * 存储医生执业信息，关联用户账号与所属科室
 * 
 * @author 徐凌
 * @version 1.0.0
 */
@Data
@TableName("hos_doctor")
public class HosDoctor implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 医生唯一ID（主键，自增）
     */
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    /**
     * 关联系统用户ID（唯一）
     */
    private Long userId;

    /**
     * 所属科室ID
     */
    private Long deptId;

    /**
     * 医生姓名
     */
    private String doctorName;

    /**
     * 执业职称（如：主任医师、副主任医师）
     */
    private String title;

    /**
     * 专业擅长
     */
    private String specialty;

    /**
     * 医生出诊简介
     */
    private String doctorDesc;

    /**
     * 医生头像图片路径
     */
    private String avatar;

    /**
     * 账号状态：1-正常，0-停用
     */
    private Integer status;

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

    /**
     * 逻辑删除标识：0-未删除，1-已删除
     */
    @TableLogic
    private Integer deleted;
}
