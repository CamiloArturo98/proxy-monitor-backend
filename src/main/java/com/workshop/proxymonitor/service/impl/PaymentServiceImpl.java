package com.workshop.proxymonitor.service.impl;

import com.workshop.proxymonitor.proxy.MicroserviceProxy;
import lombok.RequiredArgsConstructor;

import java.time.Instant;
import java.util.Map;
import java.util.UUID;

/**
 * Real Payment microservice implementation.
 * Simulates a 10% random failure rate as specified in the workshop requirements.
 */
@RequiredArgsConstructor
public class PaymentServiceImpl implements MicroserviceProxy<Object> {

    private final double failureRate;

    @Override
    public Object execute(String operation, Object... params) {
        // Simulate intentional 10% failure rate for PaymentService
        if (Math.random() < failureRate) {
            throw new RuntimeException(
                    "Payment gateway timeout — simulated failure for operation: " + operation);
        }

        return switch (operation) {
            case "process-payment" -> {
                String paymentId = "PAY-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase();
                double amount = params.length > 0 ? Double.parseDouble(params[0].toString()) : 0.0;
                String currency = params.length > 1 ? params[1].toString() : "USD";
                yield Map.of(
                        "paymentId", paymentId,
                        "amount", amount,
                        "currency", currency,
                        "status", "APPROVED",
                        "processedAt", Instant.now().toString(),
                        "transactionCode", "TXN-" + (int)(Math.random() * 999999)
                );
            }
            case "refund-payment" -> {
                String paymentId = params.length > 0 ? params[0].toString() : "PAY-UNKNOWN";
                yield Map.of(
                        "paymentId", paymentId,
                        "status", "REFUNDED",
                        "refundedAt", Instant.now().toString()
                );
            }
            case "get-payment" -> {
                String paymentId = params.length > 0 ? params[0].toString() : "PAY-UNKNOWN";
                yield Map.of(
                        "paymentId", paymentId,
                        "amount", 99.99,
                        "status", "APPROVED",
                        "currency", "USD"
                );
            }
            case "check-balance" -> Map.of(
                    "balance", Math.round(Math.random() * 10000.0 * 100.0) / 100.0,
                    "currency", "USD",
                    "accountStatus", "ACTIVE"
            );
            default -> throw new UnsupportedOperationException(
                    "Unknown operation for PaymentService: " + operation);
        };
    }
}