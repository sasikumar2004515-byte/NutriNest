package com.nutrinest.repository;

import com.nutrinest.entity.Order;
import com.nutrinest.entity.OrderItem;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface OrderItemRepository extends JpaRepository<OrderItem, Long> {

    // Get all items of an order
    List<OrderItem> findByOrder(Order order);

    // Delete all items of an order
    void deleteByOrder(Order order);

}