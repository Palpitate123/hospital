package com.hospital.common;

import lombok.Getter;

/**
 * 响应状态码枚举
 * 定义系统所有响应状态码
 * 
 * @author 徐凌
 * @version 1.0.0
 */
@Getter
public enum ResultCode {

    /**
     * 操作成功
     */
    SUCCESS(200, "操作成功"),

    /**
     * 操作失败
     */
    FAIL(500, "操作失败"),

    /**
     * 参数错误
     */
    PARAM_ERROR(400, "参数错误"),

    /**
     * 未授权
     */
    UNAUTHORIZED(401, "未授权，请先登录"),

    /**
     * 无权限
     */
    FORBIDDEN(403, "无权限访问"),

    /**
     * 资源不存在
     */
    NOT_FOUND(404, "资源不存在"),

    /**
     * 用户名已存在
     */
    USERNAME_EXISTS(1001, "用户名已存在"),

    /**
     * 用户名或密码错误
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
     * 科室已关联医生，无法删除
     */
    DEPT_HAS_DOCTOR(2002, "科室已关联医生，无法删除"),

    /**
     * 排班时间冲突
     */
    SCHEDULE_CONFLICT(3001, "该医生同一时段已存在排班"),

    /**
     * 排班已发布，无法删除
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
     * 状态码
     */
    private final Integer code;

    /**
     * 提示信息
     */
    private final String message;

    ResultCode(Integer code, String message) {
        this.code = code;
        this.message = message;
    }
}
