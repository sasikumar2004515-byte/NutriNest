package com.nutrinest.controller.admin;

import com.nutrinest.service.OrderService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/admin/orders")
public class AdminOrderController {

    private final OrderService orderService;

    public AdminOrderController(OrderService orderService) {
        this.orderService = orderService;
    }

    // Admin Orders List
    @GetMapping
    public String orders(Model model) {

        model.addAttribute(
                "orders",
                orderService.getAllOrders()
        );

        return "admin/orders";
    }

    // Update Order Status
    @PostMapping("/update-status/{id}")
    public String updateOrderStatus(
            @PathVariable Long id,
            @RequestParam String status) {

        orderService.updateOrderStatus(id, status);

        return "redirect:/admin/orders";
    }
}