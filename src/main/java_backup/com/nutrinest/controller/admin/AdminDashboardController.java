package com.nutrinest.controller.admin;

import com.nutrinest.service.CategoryService;
import com.nutrinest.service.ProductService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/admin")
public class AdminDashboardController {

    private final ProductService productService;
    private final CategoryService categoryService;

    public AdminDashboardController(ProductService productService,
                                    CategoryService categoryService) {
        this.productService = productService;
        this.categoryService = categoryService;
    }

    @GetMapping("/dashboard")
    public String dashboard(Model model) {

        model.addAttribute(
                "totalProducts",
                productService.getAllProducts().size()
        );

        model.addAttribute(
                "totalCategories",
                categoryService.getAllCategories().size()
        );

        // Orders and Customers will be connected later
        model.addAttribute("totalOrders", 0);
        model.addAttribute("totalCustomers", 0);

        return "admin/dashboard";
    }
}