package com.nutrinest.controller.admin;

import com.nutrinest.service.UserService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/admin/customers")
public class AdminCustomerController {

    private final UserService userService;

    public AdminCustomerController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping
    public String customers(Model model) {

        model.addAttribute(
                "customers",
                userService.getAllUsers()
        );

        return "admin/customers";
    }
}