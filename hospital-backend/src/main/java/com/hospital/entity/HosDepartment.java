package com.hospital.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * 科室实体类
 * 存储医院科室基础信息，是医生、排班的归属主体
 * 
 * @author 徐凌
 * @version 1.0.0
 */
@Data
@TableName("hos_department")
public class HosDepartment implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 科室唯一ID（主键，自增）
     */
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    /**
     * 科室名称（唯一）
     */
    private String deptName;

    /**
     * 科室简介
     */
    private String deptDesc;

    /**
     * 科室状态：1-启用，0-停用
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
