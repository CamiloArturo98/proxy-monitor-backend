package com.workshop.proxymonitor.service.impl;

import com.workshop.proxymonitor.proxy.MicroserviceProxy;

import java.time.Instant;
import java.util.List;
import java.util.Map;
import java.util.UUID;

/**
 * Real Orders microservice implementation.
 * Simulates operations: create-order, get-order, cancel-order, list-orders.
 */
public class OrderServiceImpl implements MicroserviceProxy<Object> {

    @Override
    public Object execute(String operation, Object... params) {
        return switch (operation) {
            case "create-order" -> {
                String orderId = "ORD-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase();
                String productId = params.length > 0 ? params[0].toString() : "P001";
                int quantity = params.length > 1 ? Integer.parseInt(params[1].toString()) : 1;
                yield Map.of(
                        "orderId", orderId,
                        "productId", productId,
                        "quantity", quantity,
                        "status", "CREATED",
                        "createdAt", Instant.now().toString()
                );
            }
            case "get-order" -> {
                String orderId = params.length > 0 ? params[0].toString() : "ORD-UNKNOWN";
                yield Map.of(
                        "orderId", orderId,
                        "status", "PROCESSING",
                        "items", 3,
                        "total", 149.99
                );
            }
            case "cancel-order" -> {
                String orderId = params.length > 0 ? params[0].toString() : "ORD-UNKNOWN";
                yield Map.of(
                        "orderId", orderId,
                        "status", "CANCELLED",
                        "cancelledAt", Instant.now().toString()
                );
            }
            case "list-orders" -> Map.of(
                    "orders", List.of(
                            Map.of("orderId", "ORD-001", "status", "DELIVERED"),
                            Map.of("orderId", "ORD-002", "status", "PROCESSING"),
                            Map.of("orderId", "ORD-003", "status", "CREATED")
                    ),
                    "total", 3
            );
            default -> throw new UnsupportedOperationException(
                    "Unknown operation for OrderService: " + operation);
        };
    }
}