package com.hospital.exception;

/**
 * 全局异常类
 */
public class GlobalException extends RuntimeException {
    private int code;
    private String message;

    public GlobalException(String message) {
        super(message);
        this.code = 400;
        this.message = message;
    }

    public GlobalException(int code, String message) {
        super(message);
        this.code = code;
        this.message = message;
    }

    public int getCode() {
        return code;
    }

    @Override
    public String getMessage() {
        return message;
    }
}
