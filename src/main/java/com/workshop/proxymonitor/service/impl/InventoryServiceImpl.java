package com.workshop.proxymonitor.service.impl;

import com.workshop.proxymonitor.proxy.MicroserviceProxy;

import java.util.Map;

/**
 * Real Inventory microservice implementation.
 * Simulates operations: check-stock, update-stock, list-products.
 */
public class InventoryServiceImpl implements MicroserviceProxy<Object> {

    @Override
    public Object execute(String operation, Object... params) {
        return switch (operation) {
            case "check-stock" -> {
                String productId = params.length > 0 ? params[0].toString() : "UNKNOWN";
                int stock = (int) (Math.random() * 100) + 1;
                yield Map.of(
                        "productId", productId,
                        "stock", stock,
                        "available", stock > 0
                );
            }
            case "update-stock" -> {
                String productId = params.length > 0 ? params[0].toString() : "UNKNOWN";
                int quantity = params.length > 1 ? Integer.parseInt(params[1].toString()) : 0;
                yield Map.of(
                        "productId", productId,
                        "newStock", quantity,
                        "updated", true,
                        "message", "Stock updated successfully"
                );
            }
            case "list-products" -> {
                yield Map.of("products", java.util.List.of(
                        Map.of("id", "P001", "name", "Laptop", "stock", 15),
                        Map.of("id", "P002", "name", "Mouse", "stock", 42),
                        Map.of("id", "P003", "name", "Keyboard", "stock", 28)
                ));
            }
            default -> throw new UnsupportedOperationException(
                    "Unknown operation for InventoryService: " + operation);
        };
    }
}