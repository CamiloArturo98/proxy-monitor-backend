package com.workshop.proxymonitor.service;

import com.workshop.proxymonitor.domain.enums.LogStatus;
import com.workshop.proxymonitor.dto.PagedResponseDto;
import com.workshop.proxymonitor.dto.ServiceLogDto;
import com.workshop.proxymonitor.dto.ServiceMetricDto;
import com.workshop.proxymonitor.repository.ServiceLogRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.List;

@Service
@RequiredArgsConstructor
public class MetricsService {

    private final ServiceLogRepository logRepository;

    public List<ServiceMetricDto> getSummary() {
        return logRepository.findSummaryRaw().stream()
                .map(row -> {
                    String serviceId = (String) row[0];
                    long total = (Long) row[1];
                    long errors = (Long) row[2];
                    long successes = total - errors;
                    double errorRate = total > 0 ? (double) errors / total * 100 : 0.0;
                    double avgMs = row[3] != null ? ((Double) row[3]) : 0.0;
                    return new ServiceMetricDto(serviceId, total, errors, successes,
                            Math.round(errorRate * 100.0) / 100.0, Math.round(avgMs * 100.0) / 100.0);
                })
                .toList();
    }

    public PagedResponseDto<ServiceLogDto> getFilteredLogs(
            String serviceId,
            String status,
            String from,
            String to,
            int page,
            int size) {

        LogStatus statusEnum = null;
        if (status != null && !status.isBlank()) {
            statusEnum = LogStatus.valueOf(status.toUpperCase());
        }

        Instant fromInstant = (from != null && !from.isBlank()) ? Instant.parse(from) : null;
        Instant toInstant = (to != null && !to.isBlank()) ? Instant.parse(to) : null;

        String serviceIdFilter = (serviceId != null && !serviceId.isBlank()) ? serviceId.toUpperCase() : null;

        var pageResult = logRepository.findFiltered(
                serviceIdFilter, statusEnum, fromInstant, toInstant,
                PageRequest.of(page, size));

        return PagedResponseDto.from(pageResult.map(ServiceLogDto::from));
    }
}