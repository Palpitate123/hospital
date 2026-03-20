package com.hospital.exception;

import lombok.Getter;

/**
 * 错误码枚举
 * 定义系统所有错误码
 * 
 * @author 徐凌
 * @version 1.0.0
 */
@Getter
public enum ErrorCode {

    /**
     * 系统错误
     */
    SYSTEM_ERROR(500, "系统异常，请稍后重试"),

    /**
     * 参数错误
     */
    PARAM_ERROR(400, "参数错误"),

    /**
     * 数据不存在
     */
    DATA_NOT_FOUND(404, "数据不存在"),

    /**
     * 用户名已存在
     */
    USERNAME_EXISTS(1001, "用户名已存在"),

    /**
     * 登录失败
     */
    LOGIN_ERROR(1002, "用户名或密码错误"),

    /**
     * 账号已停用
     */
    ACCOUNT_DISABLED(1003, "账号已停用"),

    /**
     * Token无效
     */
    TOKEN_INVALID(1004, "Token无效或已过期"),

    /**
     * 科室名称已存在
     */
    DEPT_NAME_EXISTS(2001, "科室名称已存在"),

    /**
     * 科室已关联医生
     */
    DEPT_HAS_DOCTOR(2002, "科室已关联医生，无法删除"),

    /**
     * 排班时间冲突
     */
    SCHEDULE_CONFLICT(3001, "该医生同一时段已存在排班"),

    /**
     * 排班已发布
     */
    SCHEDULE_PUBLISHED(3002, "排班已发布，无法删除"),

    /**
     * 号源已约满
     */
    SOURCE_FULL(4001, "号源已约满"),

    /**
     * 重复预约
     */
    APPOINTMENT_DUPLICATE(4002, "您已预约过该医生该时段"),

    /**
     * 订单无法取消
     */
    ORDER_CANCEL_ERROR(5001, "订单无法取消");

    /**
     * 错误码
     */
    private final Integer code;

    /**
     * 错误信息
     */
    private final String message;

    ErrorCode(Integer code, String message) {
        this.code = code;
        this.message = message;
    }
}
