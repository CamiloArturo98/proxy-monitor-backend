package com.workshop.proxymonitor.service;

import com.workshop.proxymonitor.proxy.MicroserviceProxy;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.Random;

@Slf4j
@Service
@RequiredArgsConstructor
public class SimulateLoadService {

    @Qualifier("inventoryProxy")
    private final MicroserviceProxy<Object> inventoryProxy;

    @Qualifier("ordersProxy")
    private final MicroserviceProxy<Object> ordersProxy;

    @Qualifier("paymentsProxy")
    private final MicroserviceProxy<Object> paymentsProxy;

    @Value("${app.proxy.simulate-load-calls:50}")
    private int simulateCalls;

    private static final Map<String, List<String>> SERVICE_OPERATIONS = Map.of(
            "INVENTORY", List.of("check-stock", "update-stock", "list-products"),
            "ORDERS", List.of("create-order", "get-order", "cancel-order", "list-orders"),
            "PAYMENTS", List.of("process-payment", "refund-payment", "get-payment", "check-balance")
    );

    private static final List<String> SERVICES = List.of("INVENTORY", "ORDERS", "PAYMENTS");

    public Map<String, Object> runSimulation() {
        Random random = new Random();
        int successCount = 0;
        int errorCount = 0;

        for (int i = 0; i < simulateCalls; i++) {
            String service = SERVICES.get(random.nextInt(SERVICES.size()));
            List<String> operations = SERVICE_OPERATIONS.get(service);
            String operation = operations.get(random.nextInt(operations.size()));

            try {
                MicroserviceProxy<Object> proxy = resolveProxy(service);
                proxy.execute(operation, "SIM-PARAM-" + i, String.valueOf(random.nextInt(100)));
                successCount++;
            } catch (Exception e) {
                errorCount++;
                log.debug("[SIMULATE] Expected error during simulation: {}", e.getMessage());
            }
        }

        return Map.of(
                "totalCalls", simulateCalls,
                "successCount", successCount,
                "errorCount", errorCount,
                "message", "Load simulation completed"
        );
    }

    private MicroserviceProxy<Object> resolveProxy(String service) {
        return switch (service) {
            case "INVENTORY" -> inventoryProxy;
            case "ORDERS" -> ordersProxy;
            case "PAYMENTS" -> paymentsProxy;
            default -> throw new IllegalArgumentException("Unknown service: " + service);
        };
    }
}