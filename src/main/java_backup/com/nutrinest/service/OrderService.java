package com.nutrinest.service;

import com.nutrinest.entity.Order;

import java.util.List;

public interface OrderService {

    // Normal Cart Checkout
    Order placeOrder(
            String email,
            Long addressId,
            String paymentMethod,
            Double discount
    );

    // Buy Now - Single Product
    Order placeBuyNowOrder(
            String email,
            Long productId,
            Integer quantity,
            Long addressId,
            String paymentMethod,
            Double discount
    );

    // My Orders
    List<Order> getUserOrders(String email);

    // Order Details
    Order getOrder(
            String orderNumber,
            String email
    );

    // Admin
    List<Order> getAllOrders();

    void updateOrderStatus(
            Long orderId,
            String status
    );
}