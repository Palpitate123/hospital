package com.hospital.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * 号源信息实体类
 * 存储排班对应的号源库存数据，是预约挂号核心业务表
 * 
 * @author 徐凌
 * @version 1.0.0
 */
@Data
@TableName("hos_number_source")
public class HosNumberSource implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 号源唯一ID（主键，自增）
     */
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    /**
     * 关联排班ID（唯一）
     */
    private Long scheduleId;

    /**
     * 所属医生ID
     */
    private Long doctorId;

    /**
     * 号源总量
     */
    private Integer totalNumber;

    /**
     * 剩余可预约数量（非负约束，防止超卖）
     */
    private Integer remainNumber;

    /**
     * 号源状态：1-可预约，2-已约满，3-已下架
     */
    private Integer sourceStatus;

    /**
     * 版本号（乐观锁，用于并发控制）
     */
    @Version
    private Integer version;

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
