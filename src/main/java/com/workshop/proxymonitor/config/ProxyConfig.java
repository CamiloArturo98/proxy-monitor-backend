package com.workshop.proxymonitor.config;

import com.workshop.proxymonitor.proxy.LoggingProxy;
import com.workshop.proxymonitor.proxy.MicroserviceProxy;
import com.workshop.proxymonitor.repository.ServiceLogRepository;
import com.workshop.proxymonitor.service.impl.InventoryServiceImpl;
import com.workshop.proxymonitor.service.impl.OrderServiceImpl;
import com.workshop.proxymonitor.service.impl.PaymentServiceImpl;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class ProxyConfig {

    @Value("${app.proxy.payment-failure-rate:0.10}")
    private double paymentFailureRate;

    @Bean("inventoryProxy")
    public MicroserviceProxy<Object> inventoryProxy(ServiceLogRepository repo) {
        return new LoggingProxy<>("INVENTORY", new InventoryServiceImpl(), repo);
    }

    @Bean("ordersProxy")
    public MicroserviceProxy<Object> ordersProxy(ServiceLogRepository repo) {
        return new LoggingProxy<>("ORDERS", new OrderServiceImpl(), repo);
    }

    @Bean("paymentsProxy")
    public MicroserviceProxy<Object> paymentsProxy(ServiceLogRepository repo) {
        return new LoggingProxy<>("PAYMENTS", new PaymentServiceImpl(paymentFailureRate), repo);
    }
}