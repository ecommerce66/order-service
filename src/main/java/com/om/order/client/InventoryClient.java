package com.om.order.client;

import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import io.github.resilience4j.retry.annotation.Retry;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.service.annotation.GetExchange;

public interface InventoryClient {

    Logger LOGGER = LoggerFactory.getLogger(InventoryClient.class.getName());

    @GetExchange("/api/inventory/isInStock")
    @CircuitBreaker(name = "inventory", fallbackMethod = "fallbackMethod")
    @Retry(name = "inventory")
    Boolean isInStock(@RequestParam String skuCode, @RequestParam Integer quantity);

    default Boolean fallbackMethod(String skuCode, Integer quantity, Throwable throwable) {
        LOGGER.info("Cannot check Inventory for sku code {}, Service is down. failure reason {}", skuCode,throwable.getMessage());
        return false;
    }
}
