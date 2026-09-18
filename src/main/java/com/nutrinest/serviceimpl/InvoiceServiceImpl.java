
package com.nutrinest.serviceimpl;

import com.itextpdf.text.Document;
import com.itextpdf.text.Element;
import com.itextpdf.text.Font;
import com.itextpdf.text.FontFactory;
import com.itextpdf.text.PageSize;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.pdf.PdfWriter;

import com.nutrinest.entity.Order;
import com.nutrinest.entity.User;
import com.nutrinest.repository.UserRepository;
import com.nutrinest.service.InvoiceService;
import com.nutrinest.service.OrderService;
import com.itextpdf.text.Phrase;
import com.itextpdf.text.pdf.PdfPCell;
import com.itextpdf.text.pdf.PdfPTable;

import com.nutrinest.entity.OrderItem;

import jakarta.servlet.http.HttpServletResponse;

import org.springframework.stereotype.Service;

@Service
public class InvoiceServiceImpl implements InvoiceService {

    private final OrderService orderService;
    private final UserRepository userRepository;

    public InvoiceServiceImpl(OrderService orderService,
                              UserRepository userRepository) {

        this.orderService = orderService;
        this.userRepository = userRepository;
    }

    @Override
    public void downloadInvoice(String orderNumber,
                                String email,
                                HttpServletResponse response) throws Exception {

        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User Not Found"));

        Order order = orderService.getOrder(orderNumber, email);

        response.setContentType("application/pdf");
        response.setHeader(
                "Content-Disposition",
                "attachment; filename=Invoice-" + orderNumber + ".pdf"
        );

        Document document = new Document(PageSize.A4);
        PdfWriter.getInstance(document, response.getOutputStream());

        document.open();

        Font titleFont = FontFactory.getFont(
                FontFactory.HELVETICA_BOLD,
                22
        );

        Font headingFont = FontFactory.getFont(
                FontFactory.HELVETICA_BOLD,
                14
        );

        Font normalFont = FontFactory.getFont(
                FontFactory.HELVETICA,
                12
        );

        Paragraph title = new Paragraph("NutriNest Invoice", titleFont);
        title.setAlignment(Element.ALIGN_CENTER);

        document.add(title);
        document.add(new Paragraph(" "));

        // =========================
// ORDER DETAILS
// =========================

        document.add(new Paragraph(
                "Order Number : " + order.getOrderNumber(),
                headingFont));

        document.add(new Paragraph(
                "Order Date : " + order.getOrderDate(),
                normalFont));

        document.add(new Paragraph(
                "Customer : " + user.getFullName(),
                normalFont));

        document.add(new Paragraph(
                "Email : " + user.getEmail(),
                normalFont));

        document.add(new Paragraph(
                "Phone : " + user.getPhone(),
                normalFont));

        document.add(new Paragraph(" "));

// =========================
// DELIVERY ADDRESS
// =========================

        document.add(new Paragraph(
                "Delivery Address",
                headingFont));

        document.add(new Paragraph(
                order.getAddress().getFullName(),
                normalFont));

        document.add(new Paragraph(
                order.getAddress().getAddressLine1(),
                normalFont));

        if (order.getAddress().getAddressLine2() != null &&
                !order.getAddress().getAddressLine2().isBlank()) {

            document.add(new Paragraph(
                    order.getAddress().getAddressLine2(),
                    normalFont));
        }

        document.add(new Paragraph(
                order.getAddress().getCity() + ", "
                        + order.getAddress().getState(),
                normalFont));

        document.add(new Paragraph(
                "Pincode : "
                        + order.getAddress().getPincode(),
                normalFont));

        document.add(new Paragraph(
                "Phone : "
                        + order.getAddress().getPhone(),
                normalFont));

        document.add(new Paragraph(" "));

        // =========================
// PRODUCT TABLE
// =========================

        PdfPTable table = new PdfPTable(4);

        table.setWidthPercentage(100);

        table.setSpacingBefore(15);

        table.setWidths(new float[]{5, 2, 2, 2});

// Header

        table.addCell(new PdfPCell(new Phrase("Product")));
        table.addCell(new PdfPCell(new Phrase("Qty")));
        table.addCell(new PdfPCell(new Phrase("Price")));
        table.addCell(new PdfPCell(new Phrase("Total")));

// Products

        for (OrderItem item : order.getOrderItems()) {

            table.addCell(item.getProductName());

            table.addCell(String.valueOf(item.getQuantity()));

            table.addCell("Rs. " + item.getPrice());

            table.addCell("Rs. " + item.getTotalPrice());

        }

        document.add(table);

        document.add(new Paragraph(" "));

        // =========================
// TOTAL DETAILS
// =========================

        document.add(new Paragraph(
                "----------------------------------------------"));

        document.add(new Paragraph(
                "Subtotal          : Rs. " + order.getSubtotal(),
                normalFont));

        document.add(new Paragraph(
                "Delivery Charge : Rs. " + order.getDeliveryCharge(),
                normalFont));

        document.add(new Paragraph(
                "Discount         : Rs. " + order.getDiscount(),
                normalFont));

        document.add(new Paragraph(
                "Grand Total      : Rs. " + order.getGrandTotal(),
                headingFont));

        document.add(new Paragraph(" "));

// =========================
// PAYMENT DETAILS
// =========================

        document.add(new Paragraph(
                "Payment Method : " + order.getPaymentMethod(),
                normalFont));

        document.add(new Paragraph(
                "Payment Status : " + order.getPaymentStatus(),
                normalFont));

        document.add(new Paragraph(
                "Order Status : " + order.getOrderStatus(),
                normalFont));

        document.add(new Paragraph(" "));

// =========================
// THANK YOU
// =========================

        Paragraph thanks = new Paragraph(
                "Thank you for shopping with NutriNest!",
                headingFont);

        thanks.setAlignment(Element.ALIGN_CENTER);

        document.add(thanks);

        Paragraph footer = new Paragraph(
                "This is a computer generated invoice. No signature required.",
                normalFont);

        footer.setAlignment(Element.ALIGN_CENTER);

        document.add(footer);

// =========================
// CLOSE DOCUMENT
// =========================

        document.close();
    }
}