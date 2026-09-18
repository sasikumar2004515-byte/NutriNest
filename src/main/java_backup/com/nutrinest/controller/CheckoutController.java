package com.nutrinest.controller;

import com.nutrinest.entity.Address;
import com.nutrinest.entity.Cart;
import com.nutrinest.entity.Order;
import com.nutrinest.entity.OrderItem;
import com.nutrinest.entity.Product;
import com.nutrinest.entity.User;
import com.nutrinest.repository.AddressRepository;
import com.nutrinest.repository.CartRepository;
import com.nutrinest.repository.ProductRepository;
import com.nutrinest.repository.UserRepository;
import com.nutrinest.service.InvoiceService;
import com.nutrinest.service.NewsletterService;
import com.nutrinest.service.OrderService;

import jakarta.servlet.http.HttpSession;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.ArrayList;
import java.util.List;

@Controller
public class CheckoutController {

    private final UserRepository userRepository;
    private final CartRepository cartRepository;
    private final AddressRepository addressRepository;
    private final ProductRepository productRepository;
    private final OrderService orderService;
    private final InvoiceService invoiceService;
    private final NewsletterService newsletterService;

    public CheckoutController(
            UserRepository userRepository,
            CartRepository cartRepository,
            AddressRepository addressRepository,
            ProductRepository productRepository,
            OrderService orderService,
            InvoiceService invoiceService,
            NewsletterService newsletterService) {

        this.userRepository = userRepository;
        this.cartRepository = cartRepository;
        this.addressRepository = addressRepository;
        this.productRepository = productRepository;
        this.orderService = orderService;
        this.invoiceService = invoiceService;
        this.newsletterService = newsletterService;
    }

    // =========================================================
    // CHECKOUT PAGE
    // =========================================================

    @GetMapping("/checkout")
    public String checkout(
            Model model,
            Principal principal,
            HttpSession session) {

        if (principal == null) {
            return "redirect:/login";
        }

        User user = userRepository.findByEmail(principal.getName())
                .orElseThrow(() ->
                        new RuntimeException("User Not Found"));

        List<Address> addresses =
                addressRepository.findByUser(user);

        // =====================================================
        // CHECK NEWSLETTER SUBSCRIPTION
        // =====================================================

        boolean newsletterSubscribed =
                newsletterService.isSubscribed(principal.getName());

        // =====================================================
        // BUY NOW MODE
        // =====================================================

        Object buyNowProductIdObj =
                session.getAttribute("buyNowProductId");

        Object buyNowQuantityObj =
                session.getAttribute("buyNowQuantity");

        if (buyNowProductIdObj != null) {

            Long productId =
                    Long.valueOf(
                            buyNowProductIdObj.toString()
                    );

            Integer quantity = 1;

            if (buyNowQuantityObj != null) {

                quantity =
                        Integer.valueOf(
                                buyNowQuantityObj.toString()
                        );
            }

            Product product =
                    productRepository.findById(productId)
                            .orElseThrow(() ->
                                    new RuntimeException(
                                            "Product Not Found"
                                    ));

            // Temporary cart item for Buy Now
            Cart buyNowCart = new Cart();

            buyNowCart.setProduct(product);
            buyNowCart.setQuantity(quantity);
            buyNowCart.setSelectedWeight(
                    product.getWeight()
            );

            List<Cart> cartItems =
                    new ArrayList<>();

            cartItems.add(buyNowCart);

            // =================================================
            // PRICE CALCULATION
            // =================================================

            double subtotal =
                    product.getPrice() * quantity;

            double deliveryCharge =
                    subtotal >= 999 ? 0 : 99;

            // 2% Newsletter Discount
            double discount = 0;

            if (newsletterSubscribed) {
                discount = subtotal * 0.02;
            }

            double grandTotal =
                    subtotal - discount + deliveryCharge;

            // Prevent negative total
            if (grandTotal < 0) {
                grandTotal = 0;
            }

            // =================================================
            // MODEL
            // =================================================

            model.addAttribute(
                    "cartItems",
                    cartItems
            );

            model.addAttribute(
                    "addresses",
                    addresses
            );

            model.addAttribute(
                    "subtotal",
                    subtotal
            );

            model.addAttribute(
                    "deliveryCharge",
                    deliveryCharge
            );

            model.addAttribute(
                    "discount",
                    discount
            );

            model.addAttribute(
                    "newsletterSubscribed",
                    newsletterSubscribed
            );

            model.addAttribute(
                    "grandTotal",
                    grandTotal
            );

            model.addAttribute(
                    "buyNow",
                    true
            );

            return "checkout";
        }

        // =====================================================
        // NORMAL CART CHECKOUT
        // =====================================================

        List<Cart> cartItems =
                cartRepository.findByUser(user);

        if (cartItems.isEmpty()) {
            return "redirect:/cart";
        }

        // =====================================================
        // SUBTOTAL
        // =====================================================

        double subtotal = 0;

        for (Cart cart : cartItems) {

            if (cart.getProduct() != null) {

                subtotal +=
                        cart.getProduct().getPrice()
                                * cart.getQuantity();
            }
        }

        // =====================================================
        // DELIVERY
        // =====================================================

        double deliveryCharge =
                subtotal >= 999 ? 0 : 99;

        // =====================================================
        // NEWSLETTER 2% DISCOUNT
        // =====================================================

        double discount = 0;

        if (newsletterSubscribed) {
            discount = subtotal * 0.02;
        }

        // =====================================================
        // GRAND TOTAL
        // =====================================================

        double grandTotal =
                subtotal - discount + deliveryCharge;

        if (grandTotal < 0) {
            grandTotal = 0;
        }

        // =====================================================
        // MODEL
        // =====================================================

        model.addAttribute(
                "cartItems",
                cartItems
        );

        model.addAttribute(
                "addresses",
                addresses
        );

        model.addAttribute(
                "subtotal",
                subtotal
        );

        model.addAttribute(
                "deliveryCharge",
                deliveryCharge
        );

        model.addAttribute(
                "discount",
                discount
        );

        model.addAttribute(
                "newsletterSubscribed",
                newsletterSubscribed
        );

        model.addAttribute(
                "grandTotal",
                grandTotal
        );

        model.addAttribute(
                "buyNow",
                false
        );

        return "checkout";
    }


    // =========================================================
    // BUY NOW
    // =========================================================

    @GetMapping("/checkout/buy-now/{productId}")
    public String buyNow(
            @PathVariable Long productId,
            @RequestParam(
                    value = "quantity",
                    defaultValue = "1"
            ) Integer quantity,
            Principal principal,
            HttpSession session) {

        if (principal == null) {
            return "redirect:/login";
        }

        Product product =
                productRepository.findById(productId)
                        .orElseThrow(() ->
                                new RuntimeException(
                                        "Product Not Found"
                                ));

        if (quantity == null || quantity < 1) {
            quantity = 1;
        }

        if (product.getStock() < quantity) {

            throw new RuntimeException(
                    "Insufficient stock"
            );
        }

        // =====================================================
        // STORE BUY NOW DATA
        // =====================================================

        session.setAttribute(
                "buyNowProductId",
                productId
        );

        session.setAttribute(
                "buyNowQuantity",
                quantity
        );

        // Clear Buy Again mode
        session.removeAttribute(
                "buyAgainOrderNumber"
        );

        return "redirect:/checkout";
    }


    // =========================================================
    // PLACE ORDER
    // =========================================================

    @PostMapping("/checkout/place-order")
    public String placeOrder(
            @RequestParam(required = false) Long addressId,
            @RequestParam String paymentMethod,
            Principal principal,
            HttpSession session) {

        if (principal == null) {
            return "redirect:/login";
        }

        // =====================================================
        // ADDRESS VALIDATION
        // =====================================================
        // Prevent 400 Bad Request when no address is selected.
        // Instead, return to checkout with a clear message.

        if (addressId == null) {
            return "redirect:/checkout?addressError=true";
        }

        String email = principal.getName();

        // =====================================================
        // CHECK NEWSLETTER SUBSCRIPTION AGAIN
        // =====================================================
        // Backend verification — user cannot manipulate
        // checkout HTML to get the discount.

        boolean newsletterSubscribed =
                newsletterService.isSubscribed(email);

        // =====================================================
        // BUY NOW ORDER
        // =====================================================

        Object buyNowProductIdObj =
                session.getAttribute(
                        "buyNowProductId"
                );

        Object buyNowQuantityObj =
                session.getAttribute(
                        "buyNowQuantity"
                );

        if (buyNowProductIdObj != null) {

            Long productId =
                    Long.valueOf(
                            buyNowProductIdObj.toString()
                    );

            Integer quantity = 1;

            if (buyNowQuantityObj != null) {

                quantity =
                        Integer.valueOf(
                                buyNowQuantityObj.toString()
                        );
            }

            // =================================================
            // CALCULATE BUY NOW DISCOUNT
            // =================================================

            Product product =
                    productRepository.findById(productId)
                            .orElseThrow(() ->
                                    new RuntimeException(
                                            "Product Not Found"
                                    ));

            double subtotal =
                    product.getPrice() * quantity;

            double discount = 0;

            if (newsletterSubscribed) {
                discount = subtotal * 0.02;
            }

            // =================================================
            // PLACE BUY NOW ORDER
            // =================================================

            Order order =
                    orderService.placeBuyNowOrder(
                            email,
                            productId,
                            quantity,
                            addressId,
                            paymentMethod,
                            discount
                    );

            // =================================================
            // CLEAR BUY NOW SESSION
            // =================================================

            session.removeAttribute(
                    "buyNowProductId"
            );

            session.removeAttribute(
                    "buyNowQuantity"
            );

            session.removeAttribute(
                    "buyAgainOrderNumber"
            );

            return "redirect:/order-success?orderNumber="
                    + order.getOrderNumber();
        }

        // =====================================================
        // NORMAL CART ORDER
        // =====================================================

        User user =
                userRepository.findByEmail(email)
                        .orElseThrow(() ->
                                new RuntimeException(
                                        "User Not Found"
                                ));

        List<Cart> cartItems =
                cartRepository.findByUser(user);

        if (cartItems.isEmpty()) {
            return "redirect:/cart";
        }

        // =====================================================
        // CALCULATE CART SUBTOTAL
        // =====================================================

        double subtotal = 0;

        for (Cart cart : cartItems) {

            if (cart.getProduct() != null) {

                subtotal +=
                        cart.getProduct().getPrice()
                                * cart.getQuantity();
            }
        }

        // =====================================================
        // NEWSLETTER DISCOUNT
        // =====================================================

        double discount = 0;

        if (newsletterSubscribed) {
            discount = subtotal * 0.02;
        }

        // =====================================================
        // PLACE NORMAL CART ORDER
        // =====================================================

        Order order =
                orderService.placeOrder(
                        email,
                        addressId,
                        paymentMethod,
                        discount
                );

        return "redirect:/order-success?orderNumber="
                + order.getOrderNumber();
    }


    // =========================================================
    // ORDER SUCCESS
    // =========================================================

    @GetMapping("/order-success")
    public String orderSuccess(
            @RequestParam String orderNumber,
            Model model,
            Principal principal) {

        if (principal == null) {
            return "redirect:/login";
        }

        Order order =
                orderService.getOrder(
                        orderNumber,
                        principal.getName()
                );

        model.addAttribute(
                "order",
                order
        );

        return "order-success";
    }


    // =========================================================
    // MY ORDERS
    // =========================================================

    @GetMapping("/my-orders")
    public String myOrders(
            Model model,
            Principal principal) {

        if (principal == null) {
            return "redirect:/login";
        }

        List<Order> orders =
                orderService.getUserOrders(
                        principal.getName()
                );

        model.addAttribute(
                "orders",
                orders
        );

        return "my-orders";
    }


    // =========================================================
    // ORDER DETAILS
    // =========================================================

    @GetMapping("/my-orders/{orderNumber}")
    public String orderDetails(
            @PathVariable String orderNumber,
            Principal principal,
            Model model) {

        if (principal == null) {
            return "redirect:/login";
        }

        Order order =
                orderService.getOrder(
                        orderNumber,
                        principal.getName()
                );

        model.addAttribute(
                "order",
                order
        );

        return "order-details";
    }


    // =========================================================
    // TRACK ORDER
    // =========================================================

    @GetMapping("/my-orders/{orderNumber}/track")
    public String trackOrder(
            @PathVariable String orderNumber,
            Principal principal,
            Model model) {

        if (principal == null) {
            return "redirect:/login";
        }

        Order order =
                orderService.getOrder(
                        orderNumber,
                        principal.getName()
                );

        model.addAttribute(
                "order",
                order
        );

        return "track-order";
    }


    // =========================================================
    // BUY AGAIN
    // =========================================================

    @GetMapping("/my-orders/{orderNumber}/buy-again")
    public String buyAgain(
            @PathVariable String orderNumber,
            Principal principal,
            HttpSession session) {

        if (principal == null) {
            return "redirect:/login";
        }

        Order order =
                orderService.getOrder(
                        orderNumber,
                        principal.getName()
                );

        if (order.getOrderItems() == null
                || order.getOrderItems().isEmpty()) {

            throw new RuntimeException(
                    "Order has no items"
            );
        }

        // =====================================================
        // CURRENT BUY AGAIN IMPLEMENTATION
        // =====================================================

        OrderItem item =
                order.getOrderItems().get(0);

        if (item.getProduct() == null) {

            throw new RuntimeException(
                    "Product no longer available"
            );
        }

        Product product =
                productRepository.findById(
                        item.getProduct().getId()
                ).orElseThrow(() ->
                        new RuntimeException(
                                "Product Not Found"
                        ));

        if (product.getStock() < item.getQuantity()) {

            throw new RuntimeException(
                    "Insufficient stock for "
                            + product.getProductName()
            );
        }

        // =====================================================
        // STORE BUY AGAIN AS BUY NOW STYLE CHECKOUT
        // =====================================================

        session.setAttribute(
                "buyNowProductId",
                product.getId()
        );

        session.setAttribute(
                "buyNowQuantity",
                item.getQuantity()
        );

        session.setAttribute(
                "buyAgainOrderNumber",
                orderNumber
        );

        return "redirect:/checkout";
    }
}