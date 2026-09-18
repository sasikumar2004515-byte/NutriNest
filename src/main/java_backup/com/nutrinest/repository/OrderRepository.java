package com.nutrinest.repository;

import com.nutrinest.entity.Order;
import com.nutrinest.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface OrderRepository extends JpaRepository<Order, Long> {

    // Customer Orders
    List<Order> findByUserOrderByOrderDateDesc(User user);

    // Find by Order Number
    Optional<Order> findByOrderNumber(String orderNumber);

    // Orders by Status (Admin)
    List<Order> findByOrderStatus(String orderStatus);

    // Latest Orders (Admin Dashboard)
    List<Order> findAllByOrderByOrderDateDesc();
}