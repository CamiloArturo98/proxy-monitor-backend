package com.workshop.proxymonitor.domain;

import com.workshop.proxymonitor.domain.enums.LogStatus;
import jakarta.persistence.*;
import lombok.*;

import java.time.Instant;

@Entity
@Table(name = "service_logs", indexes = {
        @Index(name = "idx_service_id", columnList = "serviceId"),
        @Index(name = "idx_status", columnList = "status"),
        @Index(name = "idx_timestamp", columnList = "timestamp")
})
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ServiceLog {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true, length = 36)
    private String requestId;

    @Column(nullable = false, length = 50)
    private String serviceId;

    @Column(nullable = false, length = 100)
    private String operation;

    @Column(columnDefinition = "TEXT")
    private String params;

    @Column(nullable = false)
    private Instant timestamp;

    private Long durationMs;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 10)
    private LogStatus status;

    @Column(columnDefinition = "TEXT")
    private String response;

    @Column(columnDefinition = "TEXT")
    private String errorTrace;
}