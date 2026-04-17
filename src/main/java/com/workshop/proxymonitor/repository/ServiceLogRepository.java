package com.workshop.proxymonitor.repository;

import com.workshop.proxymonitor.domain.ServiceLog;
import com.workshop.proxymonitor.domain.enums.LogStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.Instant;
import java.util.List;

@Repository
public interface ServiceLogRepository extends JpaRepository<ServiceLog, Long> {

    @Query("""
            SELECT l FROM ServiceLog l
            WHERE (:serviceId IS NULL OR l.serviceId = :serviceId)
              AND (:status IS NULL OR l.status = :status)
              AND (:from IS NULL OR l.timestamp >= :from)
              AND (:to IS NULL OR l.timestamp <= :to)
            ORDER BY l.timestamp DESC
            """)
    Page<ServiceLog> findFiltered(
            @Param("serviceId") String serviceId,
            @Param("status") LogStatus status,
            @Param("from") Instant from,
            @Param("to") Instant to,
            Pageable pageable
    );

    @Query("""
            SELECT l.serviceId,
                   COUNT(l),
                   SUM(CASE WHEN l.status = 'ERROR' THEN 1 ELSE 0 END),
                   AVG(l.durationMs)
            FROM ServiceLog l
            GROUP BY l.serviceId
            """)
    List<Object[]> findSummaryRaw();

    // ✅ FIX: usar Pageable en lugar de LIMIT (H2 no soporta LIMIT en JPQL)
    @Query("""
            SELECT l FROM ServiceLog l
            WHERE l.serviceId = :serviceId
            ORDER BY l.timestamp DESC
            """)
    List<ServiceLog> findLastNByService(
            @Param("serviceId") String serviceId,
            Pageable pageable
    );
}