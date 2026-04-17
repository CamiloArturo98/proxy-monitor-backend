package com.workshop.proxymonitor.dto;

public record ServiceMetricDto(
        String serviceId,
        long totalCalls,
        long errorCalls,
        long successCalls,
        double errorRate,
        double avgDurationMs
) {}