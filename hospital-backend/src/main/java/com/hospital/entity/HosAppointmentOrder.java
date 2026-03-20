package com.hospital.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.io.Serializable;
import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * 预约订单实体类
 * 存储患者预约挂号全量订单数据，系统核心业务表
 * 
 * @author 徐凌
 * @version 1.0.0
 */
@Data
@TableName("hos_appointment_order")
public class HosAppointmentOrder implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 订单唯一ID（主键，自增）
     */
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    /**
     * 预约患者用户ID
     */
    private Long userId;

    /**
     * 预约医生ID
     */
    private Long doctorId;

    /**
     * 关联排班ID
     */
    private Long scheduleId;

    /**
     * 关联号源ID
     */
    private Long sourceId;

    /**
     * 预约科室名称（冗余字段，减少关联查询）
     */
    private String deptName;

    /**
     * 预约出诊日期
     */
    private LocalDate workDate;

    /**
     * 预约出诊时段
     */
    private String workTime;

    /**
     * 订单状态：1-待就诊，2-已取消，3-已完成
     */
    private Integer orderStatus;

    /**
     * 订单创建时间
     */
    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;

    /**
     * 状态更新时间
     */
    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updateTime;

    /**
     * 逻辑删除标识：0-未删除，1-已删除
     */
    @TableLogic
    private Integer deleted;
}
