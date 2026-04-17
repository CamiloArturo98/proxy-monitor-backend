package com.workshop.proxymonitor.proxy;

import com.workshop.proxymonitor.domain.ServiceLog;
import com.workshop.proxymonitor.domain.enums.LogStatus;
import com.workshop.proxymonitor.repository.ServiceLogRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.time.Instant;
import java.util.Arrays;
import java.util.UUID;

/**
 * Logging Proxy — wraps any MicroserviceProxy and intercepts calls to:
 *  1. Log start of execution with a unique requestId
 *  2. Measure duration in milliseconds
 *  3. Persist success/error log with full metadata
 */
@Slf4j
@RequiredArgsConstructor
public class LoggingProxy<T> implements MicroserviceProxy<T> {

    private final String serviceId;
    private final MicroserviceProxy<T> realService;
    private final ServiceLogRepository logRepository;

    @Override
    public T execute(String operation, Object... params) {
        String requestId = UUID.randomUUID().toString();
        Instant startTime = Instant.now();
        String paramsJson = Arrays.toString(params);

        log.info("[PROXY] START | serviceId={} | operation={} | requestId={} | params={}",
                serviceId, operation, requestId, paramsJson);

        ServiceLog serviceLog = ServiceLog.builder()
                .requestId(requestId)
                .serviceId(serviceId)
                .operation(operation)
                .params(paramsJson)
                .timestamp(startTime)
                .build();

        try {
            T result = realService.execute(operation, params);
            long durationMs = Instant.now().toEpochMilli() - startTime.toEpochMilli();

            serviceLog.setDurationMs(durationMs);
            serviceLog.setStatus(LogStatus.SUCCESS);
            serviceLog.setResponse(result != null ? result.toString() : "null");

            log.info("[PROXY] SUCCESS | serviceId={} | operation={} | requestId={} | durationMs={}",
                    serviceId, operation, requestId, durationMs);

            logRepository.save(serviceLog);
            return result;

        } catch (Exception ex) {
            long durationMs = Instant.now().toEpochMilli() - startTime.toEpochMilli();

            // Summarize stack trace (top 3 frames)
            StringBuilder sb = new StringBuilder(ex.getClass().getName())
                    .append(": ").append(ex.getMessage()).append("\n");
            StackTraceElement[] trace = ex.getStackTrace();
            int limit = Math.min(trace.length, 3);
            for (int i = 0; i < limit; i++) {
                sb.append("  at ").append(trace[i]).append("\n");
            }

            serviceLog.setDurationMs(durationMs);
            serviceLog.setStatus(LogStatus.ERROR);
            serviceLog.setErrorTrace(sb.toString());
            serviceLog.setResponse("ERROR: " + ex.getMessage());

            log.error("[PROXY] ERROR | serviceId={} | operation={} | requestId={} | durationMs={} | error={}",
                    serviceId, operation, requestId, durationMs, ex.getMessage());

            logRepository.save(serviceLog);
            throw ex;
        }
    }
}