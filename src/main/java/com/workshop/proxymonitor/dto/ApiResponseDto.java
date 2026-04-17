package com.workshop.proxymonitor.dto;

import com.fasterxml.jackson.annotation.JsonInclude;

import java.time.Instant;

@JsonInclude(JsonInclude.Include.NON_NULL)
public record ApiResponseDto<T>(
        boolean success,
        String message,
        T data,
        String requestId,
        Instant timestamp
) {
    public static <T> ApiResponseDto<T> ok(T data, String requestId) {
        return new ApiResponseDto<>(true, "OK", data, requestId, Instant.now());
    }

    public static <T> ApiResponseDto<T> ok(T data) {
        return new ApiResponseDto<>(true, "OK", data, null, Instant.now());
    }

    public static <T> ApiResponseDto<T> error(String message) {
        return new ApiResponseDto<>(false, message, null, null, Instant.now());
    }
}