package com.om.order.service;

import com.om.order.client.InventoryClient;
import com.om.order.dto.OrderRequest;
import com.om.order.entity.Order;
import com.om.order.repository.OrderRepository;
import org.springframework.stereotype.Service;

@Service
public class OrderService {

    private final OrderRepository orderRepository;
    private final InventoryClient inventoryClient;

    public OrderService(OrderRepository orderRepository, InventoryClient inventoryClient) {
        this.orderRepository = orderRepository;
        this.inventoryClient = inventoryClient;
    }

    public OrderRequest placeOrder(OrderRequest orderRequest) {
        if(inventoryClient.isInStock(orderRequest.skuCode(),orderRequest.quantity()))
        {
            Order order = orderRepository.save(new Order(orderRequest.orderNumber(),
                    orderRequest.skuCode(),
                    orderRequest.price(),
                    orderRequest.quantity()));
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
