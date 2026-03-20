package com.hospital.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.io.Serializable;
import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * 排班计划实体类
 * 存储医生出诊排班计划，是号源生成的核心依据
 * 
 * @author 徐凌
 * @version 1.0.0
 */
@Data
@TableName("hos_schedule")
public class HosSchedule implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 排班唯一ID（主键，自增）
     */
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    /**
     * 所属医生ID
     */
    private Long doctorId;

    /**
     * 所属科室ID
     */
    private Long deptId;

    /**
     * 出诊日期
     */
    private LocalDate workDate;

    /**
     * 出诊时段（如：上午、下午）
     */
    private String workTime;

    /**
     * 号源总量
     */
    private Integer totalNumber;

    /**
     * 排班状态：0-未发布，1-已发布，2-已下架
     */
    private Integer scheduleStatus;

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
