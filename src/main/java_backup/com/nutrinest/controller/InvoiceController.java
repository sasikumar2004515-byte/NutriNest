package com.nutrinest.controller;

import com.nutrinest.service.InvoiceService;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;

@Controller
public class InvoiceController {

    private final InvoiceService invoiceService;

    public InvoiceController(InvoiceService invoiceService) {
        this.invoiceService = invoiceService;
    }

    @GetMapping("/invoice/{orderNumber}")
    public void downloadInvoice(@PathVariable String orderNumber,
                                Principal principal,
                                HttpServletResponse response) throws Exception {

        invoiceService.downloadInvoice(
                orderNumber,
                principal.getName(),
                response
        );
    }
}