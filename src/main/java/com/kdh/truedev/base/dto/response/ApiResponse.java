package com.kdh.truedev.base.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;

// 응답 래퍼
@Schema(name = "응답 래퍼")
public record ApiResponse<T>(String message, T data) {
    public static <T> ApiResponse<T> ok(String msg, T data) { return new ApiResponse<>(msg, data); }
    public static ApiResponse<Void> ok(String message) {return new ApiResponse<>(message, null);}
    public static <T> ApiResponse<T> error(String msg) { return new ApiResponse<>(msg, null); }

}
