package com.workshop.proxymonitor.controller;

import com.workshop.proxymonitor.dto.ApiResponseDto;
import com.workshop.proxymonitor.dto.PagedResponseDto;
import com.workshop.proxymonitor.dto.ServiceLogDto;
import com.workshop.proxymonitor.dto.ServiceMetricDto;
import com.workshop.proxymonitor.service.MetricsService;
import com.workshop.proxymonitor.service.SimulateLoadService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/metrics")
@RequiredArgsConstructor
public class MetricsController {

    private final MetricsService metricsService;
    private final SimulateLoadService simulateLoadService;

    /**
     * GET /api/metrics/summary
     * Returns aggregated metrics per service: total calls, error rate, avg response time.
     */
    @GetMapping("/summary")
    public ResponseEntity<ApiResponseDto<List<ServiceMetricDto>>> getSummary() {
        List<ServiceMetricDto> summary = metricsService.getSummary();
        return ResponseEntity.ok(ApiResponseDto.ok(summary));
    }

    /**
     * GET /api/metrics/logs?service=&status=&from=&to=&page=&size=
     * Returns filtered and paginated service logs.
     */
    @GetMapping("/logs")
    public ResponseEntity<ApiResponseDto<PagedResponseDto<ServiceLogDto>>> getLogs(
            @RequestParam(required = false) String service,
            @RequestParam(required = false) String status,
            @RequestParam(required = false) String from,
            @RequestParam(required = false) String to,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {

        PagedResponseDto<ServiceLogDto> logs = metricsService.getFilteredLogs(
                service, status, from, to, page, size);
        return ResponseEntity.ok(ApiResponseDto.ok(logs));
    }

    /**
     * POST /api/metrics/simulate-load
     * Generates 50 random calls across all services to populate logs.
     */
    @PostMapping("/simulate-load")
    public ResponseEntity<ApiResponseDto<Map<String, Object>>> simulateLoad() {
        Map<String, Object> result = simulateLoadService.runSimulation();
        return ResponseEntity.ok(ApiResponseDto.ok(result));
    }
}