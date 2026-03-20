package com.hospital.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * 系统操作日志实体类
 * 记录用户核心操作审计日志，用于安全追溯与问题排查
 * 
 * @author 徐凌
 * @version 1.0.0
 */
@Data
@TableName("sys_operation_log")
public class SysOperationLog implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 日志唯一ID（主键，自增）
     */
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    /**
     * 操作人用户ID
     */
    private Long userId;

    /**
     * 操作模块（如：排班管理、预约挂号）
     */
    private String module;

    /**
     * 操作类型（如：新增、编辑、预约、取消）
     */
    private String operationType;

    /**
     * 操作内容详情
     */
    private String operationContent;

    /**
     * 请求IP地址
     */
    private String requestIp;

    /**
     * 操作时间
     */
    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime operationTime;
}
