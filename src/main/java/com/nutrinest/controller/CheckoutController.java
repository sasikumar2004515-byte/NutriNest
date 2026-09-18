package com.nutrinest.controller;

import com.nutrinest.entity.Address;
import com.nutrinest.entity.Cart;
import com.nutrinest.entity.Order;
import com.nutrinest.entity.OrderItem;
import com.nutrinest.entity.Product;
import com.nutrinest.entity.ProductVariant;
import com.nutrinest.entity.User;
import com.nutrinest.repository.AddressRepository;
import com.nutrinest.repository.CartRepository;
import com.nutrinest.repository.ProductRepository;
import com.nutrinest.repository.ProductVariantRepository;
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
    private final ProductVariantRepository productVariantRepository;
    private final OrderService orderService;
    private final InvoiceService invoiceService;
    private final NewsletterService newsletterService;

    public CheckoutController(
            UserRepository userRepository,
            CartRepository cartRepository,
            AddressRepository addressRepository,
            ProductRepository productRepository,
            ProductVariantRepository productVariantRepository,
            OrderService orderService,
            InvoiceService invoiceService,
            NewsletterService newsletterService) {

        this.userRepository = userRepository;
        this.cartRepository = cartRepository;
        this.addressRepository = addressRepository;
        this.productRepository = productRepository;
        this.productVariantRepository = productVariantRepository;
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

        Object buyNowVariantIdObj =
                session.getAttribute("buyNowVariantId");

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

            if (quantity < 1) {
                quantity = 1;
            }

            Product product =
                    productRepository.findById(productId)
                            .orElseThrow(() ->
                                    new RuntimeException(
                                            "Product Not Found"
                                    ));

            ProductVariant variant = null;

            // =================================================
            // LOAD SELECTED VARIANT
            // =================================================

            if (buyNowVariantIdObj != null) {

                Long variantId =
                        Long.valueOf(
                                buyNowVariantIdObj.toString()
                        );

                variant =
                        productVariantRepository.findById(variantId)
                                .orElseThrow(() ->
                                        new RuntimeException(
                                                "Selected variant not found"
                                        ));

                if (variant.getProduct() == null
                        || variant.getProduct().getId() == null
                        || !variant.getProduct().getId()
                        .equals(product.getId())) {

                    throw new RuntimeException(
                            "Invalid product variant"
                    );
                }

                if (variant.getStock() == null
                        || variant.getStock() < quantity) {

                    throw new RuntimeException(
                            "Insufficient stock for selected variant"
                    );
                }
            } else {

                // =================================================
                // PRODUCT WITHOUT VARIANT
                // =================================================

                if (product.getStock() == null
                        || product.getStock() < quantity) {

                    throw new RuntimeException(
                            "Insufficient stock"
                    );
                }
            }

            // =================================================
            // TEMPORARY CART ITEM
            // =================================================

            Cart buyNowCart = new Cart();

            buyNowCart.setProduct(product);
            buyNowCart.setQuantity(quantity);

            double unitPrice;

            if (variant != null) {

                buyNowCart.setProductVariant(variant);
                buyNowCart.setSelectedWeight(
                        variant.getWeight()
                );

                unitPrice =
                        variant.getPrice() != null
                                ? variant.getPrice()
                                : 0.0;

            } else {

                buyNowCart.setSelectedWeight(
                        product.getWeight()
                );

                unitPrice =
                        product.getPrice() != null
                                ? product.getPrice()
                                : 0.0;
            }

            List<Cart> cartItems =
                    new ArrayList<>();

            cartItems.add(buyNowCart);

            // =================================================
            // PRICE CALCULATION
            // =================================================

            double subtotal =
                    unitPrice * quantity;

            double deliveryCharge =
                    subtotal >= 999 ? 0 : 99;

            // 2% Newsletter Discount
            double discount = 0;

            if (newsletterSubscribed) {
                discount = subtotal * 0.02;
            }

            double grandTotal =
                    subtotal - discount + deliveryCharge;

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

            if (cart.getProduct() == null) {
                continue;
            }

            Integer quantity = cart.getQuantity();

            if (quantity == null || quantity < 1) {
                continue;
            }

            double unitPrice = getCartItemPrice(cart);

            subtotal +=
                    unitPrice * quantity;
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
            @RequestParam(
                    value = "variantId",
                    required = false
            ) Long variantId,
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

        // =====================================================
        // VARIANT STOCK VALIDATION
        // =====================================================

        if (variantId != null) {

            ProductVariant variant =
                    productVariantRepository.findById(variantId)
                            .orElseThrow(() ->
                                    new RuntimeException(
                                            "Selected variant not found"
                                    ));

            if (variant.getProduct() == null
                    || variant.getProduct().getId() == null
                    || !variant.getProduct().getId()
                    .equals(product.getId())) {

                throw new RuntimeException(
                        "Invalid product variant"
                );
            }

            if (variant.getStock() == null
                    || variant.getStock() < quantity) {

                throw new RuntimeException(
                        "Insufficient stock for selected variant"
                );
            }

        } else {

            // =================================================
            // NORMAL PRODUCT STOCK
            // =================================================

            if (product.getStock() == null
                    || product.getStock() < quantity) {

                throw new RuntimeException(
                        "Insufficient stock"
                );
            }
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

        if (variantId != null) {

            session.setAttribute(
                    "buyNowVariantId",
                    variantId
            );

        } else {

            session.removeAttribute(
                    "buyNowVariantId"
            );
        }

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

        if (addressId == null) {
            return "redirect:/checkout?addressError=true";
        }

        String email = principal.getName();

        // =====================================================
        // CHECK NEWSLETTER SUBSCRIPTION AGAIN
        // =====================================================

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

        Object buyNowVariantIdObj =
                session.getAttribute(
                        "buyNowVariantId"
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

            if (quantity < 1) {
                quantity = 1;
            }

            Long variantId = null;

            if (buyNowVariantIdObj != null) {
                variantId =
                        Long.valueOf(
                                buyNowVariantIdObj.toString()
                        );
            }

            // =================================================
            // LOAD PRODUCT
            // =================================================

            Product product =
                    productRepository.findById(productId)
                            .orElseThrow(() ->
                                    new RuntimeException(
                                            "Product Not Found"
                                    ));

            double unitPrice;

            // =================================================
            // VARIANT PRICE
            // =================================================

            if (variantId != null) {

                ProductVariant variant =
                        productVariantRepository.findById(
                                variantId
                        ).orElseThrow(() ->
                                new RuntimeException(
                                        "Selected variant not found"
                                ));

                if (variant.getProduct() == null
                        || variant.getProduct().getId() == null
                        || !variant.getProduct().getId()
                        .equals(product.getId())) {

                    throw new RuntimeException(
                            "Invalid product variant"
                    );
                }

                if (variant.getStock() == null
                        || variant.getStock() < quantity) {

                    throw new RuntimeException(
                            "Insufficient stock for selected variant"
                    );
                }

                unitPrice =
                        variant.getPrice() != null
                                ? variant.getPrice()
                                : 0.0;

            } else {

                // =================================================
                // PRODUCT PRICE
                // =================================================

                if (product.getStock() == null
                        || product.getStock() < quantity) {

                    throw new RuntimeException(
                            "Insufficient stock"
                    );
                }

                unitPrice =
                        product.getPrice() != null
                                ? product.getPrice()
                                : 0.0;
            }

            // =================================================
            // CALCULATE BUY NOW DISCOUNT
            // =================================================

            double subtotal =
                    unitPrice * quantity;

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
                            variantId,
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
                    "buyNowVariantId"
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

            if (cart.getProduct() == null) {
                continue;
            }

            Integer quantity = cart.getQuantity();

            if (quantity == null || quantity < 1) {
                throw new RuntimeException(
                        "Invalid cart quantity"
                );
            }

            double unitPrice =
                    getCartItemPrice(cart);

            subtotal +=
                    unitPrice * quantity;
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
        // FIRST ORDER ITEM
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

        Integer quantity =
                item.getQuantity();

        if (quantity == null || quantity < 1) {
            quantity = 1;
        }

        // =====================================================
        // TRY TO RESTORE ORIGINAL VARIANT
        // =====================================================

        ProductVariant variant = null;

        String selectedWeight =
                item.getSelectedWeight();

        if (selectedWeight != null
                && !selectedWeight.isBlank()) {

            variant =
                    productVariantRepository
                            .findByProductAndWeight(
                                    product,
                                    selectedWeight
                            )
                            .orElse(null);
        }

        // =====================================================
        // VARIANT STOCK
        // =====================================================

        if (variant != null) {

            if (variant.getStock() == null
                    || variant.getStock() < quantity) {

                throw new RuntimeException(
                        "Insufficient stock for "
                                + product.getProductName()
                                + " - "
                                + variant.getWeight()
                );
            }

            session.setAttribute(
                    "buyNowVariantId",
                    variant.getId()
            );

        } else {

            // =================================================
            // NORMAL PRODUCT STOCK
            // =================================================

            if (product.getStock() == null
                    || product.getStock() < quantity) {

                throw new RuntimeException(
                        "Insufficient stock for "
                                + product.getProductName()
                );
            }

            session.removeAttribute(
                    "buyNowVariantId"
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
                quantity
        );

        session.setAttribute(
                "buyAgainOrderNumber",
                orderNumber
        );

        return "redirect:/checkout";
    }


    // =========================================================
    // HELPER - GET CART ITEM PRICE
    // =========================================================

    private double getCartItemPrice(Cart cart) {

        if (cart == null) {
            return 0.0;
        }

        // =====================================================
        // VARIANT PRICE
        // =====================================================

        ProductVariant variant =
                cart.getProductVariant();

        if (variant != null
                && variant.getPrice() != null) {

            return variant.getPrice();
        }

        // =====================================================
        // PRODUCT PRICE
        // =====================================================

        Product product =
                cart.getProduct();

        if (product != null
                && product.getPrice() != null) {

            return product.getPrice();
        }

        return 0.0;
    }
}