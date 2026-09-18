package com.nutrinest.service;

import jakarta.servlet.http.HttpServletResponse;

public interface InvoiceService {

    void downloadInvoice(String orderNumber,
                         String email,
                         HttpServletResponse response) throws Exception;

}