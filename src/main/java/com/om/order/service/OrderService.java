package com.om.order.service;

import com.om.order.client.InventoryClient;
import com.om.order.dto.OrderRequest;
import com.om.order.entity.Order;
import com.om.order.event.OrderPlacedEvent;
import com.om.order.repository.OrderRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

@Service
public class OrderService {

    Logger LOGGER = LoggerFactory.getLogger(OrderService.class);
    private final OrderRepository orderRepository;
    private final InventoryClient inventoryClient;
    private final KafkaTemplate<String, OrderPlacedEvent> kafkaTemplate;

    public OrderService(OrderRepository orderRepository, InventoryClient inventoryClient, KafkaTemplate kafkaTemplate) {
        this.orderRepository = orderRepository;
        this.inventoryClient = inventoryClient;
        this.kafkaTemplate = kafkaTemplate;
    }

    public OrderRequest placeOrder(OrderRequest orderRequest) {
        if(inventoryClient.isInStock(orderRequest.skuCode(),orderRequest.quantity()))
        {
            Order order = orderRepository.save(new Order(orderRequest.orderNumber(),
                    orderRequest.skuCode(),
                    orderRequest.price(),
                    orderRequest.quantity()));

            // send the message to kafka topic
            OrderPlacedEvent orderPlacedEvent = new OrderPlacedEvent(order.getOrderNumber(), "my new email");
            kafkaTemplate.send("order-placed", orderPlacedEvent);
            LOGGER.info("Order Placed Event sent to Kafka: {}", orderPlacedEvent);
            return new OrderRequest(order.getId(),
                    order.getOrderNumber(),
                    order.getSkuCode(),
                    order.getPrice(),
                    order.getQuantity());
        }else {
            throw new IllegalArgumentException("Product is not in stock, please try again later");
        }
    }
}
