package com.om.order.service;

import com.om.order.client.InventoryClient;
import com.om.order.dto.OrderRequest;
import com.om.order.entity.Order;
import com.om.order.event.OrderPlacedEvent;
import com.om.order.repository.OrderRepository;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

@Service
public class OrderService {

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
            OrderPlacedEvent orderPlacedEvent = new OrderPlacedEvent(order.getOrderNumber(), "new email");
            kafkaTemplate.send("order-placed", orderPlacedEvent);
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
