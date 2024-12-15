package com.om.order.service;

import com.om.order.dto.OrderRequest;
import com.om.order.entity.Order;
import com.om.order.repository.OrderRepository;
import org.springframework.stereotype.Service;

@Service
public class OrderService {

    private final OrderRepository orderRepository;

    public OrderService(OrderRepository orderRepository) {
        this.orderRepository = orderRepository;
    }

    public OrderRequest placeOrder(OrderRequest orderRequest) {
        Order order = orderRepository.save(new Order(orderRequest.orderNumber(),
                orderRequest.skuCode(),
                orderRequest.price(),
                orderRequest.quantity()));
        return new OrderRequest(order.getId(),
                order.getOrderNumber(),
                order.getSkuCode(),
                order.getPrice(),
                order.getQuantity());
    }
}
