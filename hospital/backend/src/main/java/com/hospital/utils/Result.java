package com.hospital.utils;

import lombok.Data;

/**
 * 统一响应结果类
 */
@Data
public class Result<T> {
    private int code;          // 状态码
    private String message;    // 提示信息
    private T data;            // 响应数据

    private Result(int code, String message, T data) {
        this.code = code;
        this.message = message;
        this.data = data;
    }

    /**
     * 成功响应
     */
    public static <T> Result<T> success(T data) {
        return new Result<>(200, "success", data);
    }

    public static Result<String> success() {
        return new Result<>(200, "success", null);
    }

    /**
     * 失败响应
     */
    public static <T> Result<T> fail(int code, String message) {
        return new Result<>(code, message, null);
    }

    public static <T> Result<T> fail(String message) {
        return new Result<>(400, message, null);
    }
}
