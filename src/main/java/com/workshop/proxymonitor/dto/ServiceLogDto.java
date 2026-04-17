package com.workshop.proxymonitor.dto;

import com.workshop.proxymonitor.domain.ServiceLog;
import com.workshop.proxymonitor.domain.enums.LogStatus;

import java.time.Instant;

public record ServiceLogDto(
        Long id,
        String requestId,
        String serviceId,
        String operation,
        String params,
        Instant timestamp,
        Long durationMs,
        LogStatus status,
        String response,
        String errorTrace
) {
    public static ServiceLogDto from(ServiceLog log) {
        return new ServiceLogDto(
                log.getId(),
                log.getRequestId(),
                log.getServiceId(),
                log.getOperation(),
                log.getParams(),
                log.getTimestamp(),
                log.getDurationMs(),
                log.getStatus(),
                log.getResponse(),
                log.getErrorTrace()
        );
    }
}