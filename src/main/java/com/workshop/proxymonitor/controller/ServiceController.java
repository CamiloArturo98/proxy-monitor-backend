package com.workshop.proxymonitor.controller;

import com.workshop.proxymonitor.dto.ApiResponseDto;
import com.workshop.proxymonitor.dto.OperationRequestDto;
import com.workshop.proxymonitor.proxy.MicroserviceProxy;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/services")
@RequiredArgsConstructor
public class ServiceController {

    @Qualifier("inventoryProxy")
    private final MicroserviceProxy<Object> inventoryProxy;

    @Qualifier("ordersProxy")
    private final MicroserviceProxy<Object> ordersProxy;

    @Qualifier("paymentsProxy")
    private final MicroserviceProxy<Object> paymentsProxy;

    // ─── INVENTORY ────────────────────────────────────────────────────────────
    @PostMapping("/inventory/{operation}")
    public ResponseEntity<ApiResponseDto<Object>> inventory(
            @PathVariable String operation,
            @RequestBody(required = false) OperationRequestDto body) {

        Object[] params = resolveParams(body);
        try {
            Object result = inventoryProxy.execute(operation, params);
            return ResponseEntity.ok(ApiResponseDto.ok(result));
        } catch (Exception ex) {
            return ResponseEntity.badRequest()
                    .body(ApiResponseDto.error(ex.getMessage()));
        }
    }

    // ─── ORDERS ───────────────────────────────────────────────────────────────
    @PostMapping("/orders/{operation}")
    public ResponseEntity<ApiResponseDto<Object>> orders(
            @PathVariable String operation,
            @RequestBody(required = false) OperationRequestDto body) {

        Object[] params = resolveParams(body);
        try {
            Object result = ordersProxy.execute(operation, params);
            return ResponseEntity.ok(ApiResponseDto.ok(result));
        } catch (Exception ex) {
            return ResponseEntity.badRequest()
                    .body(ApiResponseDto.error(ex.getMessage()));
        }
    }

    // ─── PAYMENTS ─────────────────────────────────────────────────────────────
    @PostMapping("/payments/{operation}")
    public ResponseEntity<ApiResponseDto<Object>> payments(
            @PathVariable String operation,
            @RequestBody(required = false) OperationRequestDto body) {

        Object[] params = resolveParams(body);
        try {
            Object result = paymentsProxy.execute(operation, params);
            return ResponseEntity.ok(ApiResponseDto.ok(result));
        } catch (Exception ex) {
            return ResponseEntity.internalServerError()
                    .body(ApiResponseDto.error(ex.getMessage()));
        }
    }

    // ─── Helpers ──────────────────────────────────────────────────────────────
    private Object[] resolveParams(OperationRequestDto body) {
        if (body == null || body.params() == null || body.params().isEmpty()) {
            return new Object[0];
        }
        return body.params().toArray();
    }
}