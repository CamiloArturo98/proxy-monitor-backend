package com.workshop.proxymonitor.dto;

import java.util.List;

public record OperationRequestDto(
        List<Object> params
) {
    public OperationRequestDto {
        if (params == null) params = List.of();
    }
}