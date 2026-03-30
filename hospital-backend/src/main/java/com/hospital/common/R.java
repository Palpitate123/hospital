package com.hospital.common;

import lombok.Data;

import java.io.Serializable;

/**
 * 统一响应结果类
 * 前后端分离项目中，所有接口返回统一格式的数据
 *
 * @param <T> 返回数据的泛型类型
 */
@Data
public class R<T> implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 响应状态码：200-成功，其他-失败
     */
    private Integer code;

    /**
     * 响应提示信息
     */
    private String message;

    /**
     * 响应数据
     */
    private T data;

    // ==================== 静态工厂方法（快速创建对象） ====================

    /**
     * 成功响应（无数据）
     */
    public static <T> R<T> ok() {
        R<T> r = new R<>();
        r.setCode(200);
        r.setMessage("操作成功");
        return r;
    }

    /**
     * 成功响应（带数据）
     *
     * @param data 返回的数据
     */
    public static <T> R<T> ok(T data) {
        R<T> r = new R<>();
        r.setCode(200);
        r.setMessage("操作成功");
        r.setData(data);
        return r;
    }

    /**
     * 成功响应（自定义提示信息+带数据）
     *
     * @param message 自定义提示信息
     * @param data    返回的数据
     */
    public static <T> R<T> ok(String message, T data) {
        R<T> r = new R<>();
        r.setCode(200);
        r.setMessage(message);
        r.setData(data);
        return r;
    }

    /**
     * 失败响应（默认提示）
     */
    public static <T> R<T> error() {
        R<T> r = new R<>();
        r.setCode(500);
        r.setMessage("操作失败");
        return r;
    }

    /**
     * 失败响应（自定义提示信息）
     *
     * @param message 自定义错误提示
     */
    public static <T> R<T> error(String message) {
        R<T> r = new R<>();
        r.setCode(500);
        r.setMessage(message);
        return r;
    }

    /**
     * 失败响应（自定义状态码+提示信息）
     *
     * @param code    自定义错误码
     * @param message 自定义错误提示
     */
    public static <T> R<T> error(Integer code, String message) {
        R<T> r = new R<>();
        r.setCode(code);
        r.setMessage(message);
        return r;
    }
}
