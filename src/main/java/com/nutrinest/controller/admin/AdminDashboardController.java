package com.nutrinest.controller.admin;

import com.nutrinest.service.CategoryService;
import com.nutrinest.service.ProductService;
import com.nutrinest.service.UserService;
import com.nutrinest.repository.OrderRepository;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/admin")
public class AdminDashboardController {

    private final ProductService productService;
    private final CategoryService categoryService;
    private final UserService userService;
    private final OrderRepository orderRepository;


    public AdminDashboardController(
            ProductService productService,
            CategoryService categoryService,
            UserService userService,
            OrderRepository orderRepository) {

        this.productService = productService;
        this.categoryService = categoryService;
        this.userService = userService;
        this.orderRepository = orderRepository;
    }


    // =====================================================
    // ADMIN DASHBOARD
    // =====================================================

    @GetMapping("/dashboard")
    public String dashboard(Model model) {

        // =================================================
        // ACTIVE PRODUCTS
        // =================================================

        long totalProducts =
                productService.getActiveProducts().size();


        // =================================================
        // ACTIVE CATEGORIES
        // =================================================

        long totalCategories =
                categoryService.getActiveCategories().size();


        // =================================================
        // TOTAL ORDERS
        // =================================================

        long totalOrders =
                orderRepository.count();


        // =================================================
        // TOTAL CUSTOMERS
        // =================================================

        long totalCustomers =
                userService.getAllUsers().size();


        // =================================================
        // SEND DATA TO DASHBOARD
        // =================================================

        model.addAttribute(
                "totalProducts",
                totalProducts
        );

        model.addAttribute(
                "totalCategories",
                totalCategories
        );

        model.addAttribute(
                "totalOrders",
                totalOrders
        );

        model.addAttribute(
                "totalCustomers",
                totalCustomers
        );


        return "admin/dashboard";
    }
}