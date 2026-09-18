package com.nutrinest.serviceimpl;

import com.nutrinest.entity.*;
import com.nutrinest.repository.*;
import com.nutrinest.service.OrderService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Set;
import java.util.UUID;

@Service
public class OrderServiceImpl implements OrderService {

    private final OrderRepository orderRepository;
    private final OrderItemRepository orderItemRepository;
    private final UserRepository userRepository;
    private final AddressRepository addressRepository;
    private final CartRepository cartRepository;
    private final ProductRepository productRepository;
    private final ProductVariantRepository productVariantRepository;


    public OrderServiceImpl(
            OrderRepository orderRepository,
            OrderItemRepository orderItemRepository,
            UserRepository userRepository,
            AddressRepository addressRepository,
            CartRepository cartRepository,
            ProductRepository productRepository,
            ProductVariantRepository productVariantRepository) {

        this.orderRepository = orderRepository;
        this.orderItemRepository = orderItemRepository;
        this.userRepository = userRepository;
        this.addressRepository = addressRepository;
        this.cartRepository = cartRepository;
        this.productRepository = productRepository;
        this.productVariantRepository = productVariantRepository;
    }


    // =========================================================
    // NORMAL CART CHECKOUT
    // =========================================================

    @Override
    @Transactional
    public Order placeOrder(
            String email,
            Long addressId,
            String paymentMethod,
            Double discount) {

        User user = userRepository.findByEmail(email)
                .orElseThrow(() ->
                        new RuntimeException("User Not Found"));


        Address address = addressRepository
                .findByIdAndUser(addressId, user)
                .orElseThrow(() ->
                        new RuntimeException("Address Not Found"));


        List<Cart> cartItems =
                cartRepository.findByUser(user);


        if (cartItems.isEmpty()) {
            throw new RuntimeException("Cart is Empty");
        }


        // =====================================================
        // SUBTOTAL + STOCK VALIDATION
        // =====================================================

        double subtotal = 0;


        for (Cart cart : cartItems) {

            Product product = cart.getProduct();

            if (product == null) {
                throw new RuntimeException(
                        "Product no longer available"
                );
            }


            if (cart.getQuantity() == null
                    || cart.getQuantity() < 1) {

                throw new RuntimeException(
                        "Invalid cart quantity"
                );
            }


            ProductVariant variant =
                    cart.getProductVariant();


            // =================================================
            // VARIANT PRODUCT
            // =================================================

            if (variant != null) {

                if (variant.getProduct() == null
                        || variant.getProduct().getId() == null
                        || !variant.getProduct()
                        .getId()
                        .equals(product.getId())) {

                    throw new RuntimeException(
                            "Invalid product variant"
                    );
                }


                Integer variantStock =
                        variant.getStock();


                if (variantStock == null
                        || variantStock < cart.getQuantity()) {

                    throw new RuntimeException(
                            "Insufficient stock for "
                                    + product.getProductName()
                                    + " - "
                                    + variant.getWeight()
                    );
                }


                if (variant.getPrice() == null
                        || variant.getPrice() < 0) {

                    throw new RuntimeException(
                            "Invalid variant price"
                    );
                }


                subtotal +=
                        variant.getPrice()
                                * cart.getQuantity();

            }

            // =================================================
            // NORMAL PRODUCT WITHOUT VARIANT
            // =================================================

            else {

                if (product.getStock() == null
                        || product.getStock()
                        < cart.getQuantity()) {

                    throw new RuntimeException(
                            "Insufficient stock for "
                                    + product.getProductName()
                    );
                }


                if (product.getPrice() == null
                        || product.getPrice() < 0) {

                    throw new RuntimeException(
                            "Invalid product price"
                    );
                }


                subtotal +=
                        product.getPrice()
                                * cart.getQuantity();
            }
        }


        // =====================================================
        // DELIVERY
        // =====================================================

        double deliveryCharge =
                subtotal >= 999 ? 0 : 99;


        // =====================================================
        // NEWSLETTER DISCOUNT
        // =====================================================

        if (discount == null || discount < 0) {
            discount = 0.0;
        }


        double maximumDiscount =
                subtotal * 0.02;


        if (discount > maximumDiscount) {
            discount = maximumDiscount;
        }


        // =====================================================
        // GRAND TOTAL
        // =====================================================

        double grandTotal =
                subtotal + deliveryCharge - discount;


        if (grandTotal < 0) {
            grandTotal = 0;
        }


        // =====================================================
        // CREATE ORDER
        // =====================================================

        Order order = createOrder(
                user,
                address,
                paymentMethod,
                subtotal,
                deliveryCharge,
                discount,
                grandTotal
        );


        // =====================================================
        // CREATE ORDER ITEMS
        // =====================================================

        for (Cart cart : cartItems) {

            createOrderItem(
                    order,
                    cart
            );
        }


        // =====================================================
        // REDUCE STOCK
        // =====================================================

        for (Cart cart : cartItems) {

            Product product =
                    cart.getProduct();

            ProductVariant variant =
                    cart.getProductVariant();


            // Variant stock
            if (variant != null) {

                int currentStock =
                        variant.getStock() == null
                                ? 0
                                : variant.getStock();

                variant.setStock(
                        currentStock
                                - cart.getQuantity()
                );

                productVariantRepository.save(
                        variant
                );
            }

            // Normal product stock
            else {

                int currentStock =
                        product.getStock() == null
                                ? 0
                                : product.getStock();

                product.setStock(
                        currentStock
                                - cart.getQuantity()
                );

                productRepository.save(
                        product
                );
            }
        }


        // =====================================================
        // CLEAR CART
        // =====================================================

        cartRepository.deleteByUser(user);


        return order;
    }


    // =========================================================
    // BUY NOW - SINGLE PRODUCT / VARIANT
    // =========================================================

    @Override
    @Transactional
    public Order placeBuyNowOrder(
            String email,
            Long productId,
            Long variantId,
            Integer quantity,
            Long addressId,
            String paymentMethod,
            Double discount) {

        User user = userRepository.findByEmail(email)
                .orElseThrow(() ->
                        new RuntimeException("User Not Found"));


        Address address = addressRepository
                .findByIdAndUser(addressId, user)
                .orElseThrow(() ->
                        new RuntimeException("Address Not Found"));


        Product product =
                productRepository
                        .findById(productId)
                        .orElseThrow(() ->
                                new RuntimeException(
                                        "Product Not Found"
                                ));


        if (quantity == null || quantity < 1) {
            quantity = 1;
        }


        ProductVariant variant = null;


        // =====================================================
        // LOAD SELECTED VARIANT
        // =====================================================

        if (variantId != null) {

            variant =
                    productVariantRepository
                            .findById(variantId)
                            .orElseThrow(() ->
                                    new RuntimeException(
                                            "Product Variant Not Found"
                                    ));


            // Variant must belong to requested product
            if (variant.getProduct() == null
                    || variant.getProduct().getId() == null
                    || !variant.getProduct()
                    .getId()
                    .equals(product.getId())) {

                throw new RuntimeException(
                        "Invalid product variant"
                );
            }
        }


        // =====================================================
        // PRICE + STOCK
        // =====================================================

        double unitPrice;
        String selectedWeight;


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


            if (variant.getPrice() == null
                    || variant.getPrice() < 0) {

                throw new RuntimeException(
                        "Invalid variant price"
                );
            }


            unitPrice =
                    variant.getPrice();

            selectedWeight =
                    variant.getWeight();

        } else {

            if (product.getStock() == null
                    || product.getStock() < quantity) {

                throw new RuntimeException(
                        "Insufficient stock for "
                                + product.getProductName()
                );
            }


            if (product.getPrice() == null
                    || product.getPrice() < 0) {

                throw new RuntimeException(
                        "Invalid product price"
                );
            }


            unitPrice =
                    product.getPrice();

            selectedWeight =
                    product.getWeight();
        }


        // =====================================================
        // SUBTOTAL
        // =====================================================

        double subtotal =
                unitPrice * quantity;


        // =====================================================
        // DELIVERY
        // =====================================================

        double deliveryCharge =
                subtotal >= 999 ? 0 : 99;


        // =====================================================
        // NEWSLETTER DISCOUNT
        // =====================================================

        if (discount == null || discount < 0) {
            discount = 0.0;
        }


        double maximumDiscount =
                subtotal * 0.02;


        if (discount > maximumDiscount) {
            discount = maximumDiscount;
        }


        // =====================================================
        // GRAND TOTAL
        // =====================================================

        double grandTotal =
                subtotal + deliveryCharge - discount;


        if (grandTotal < 0) {
            grandTotal = 0;
        }


        // =====================================================
        // CREATE ORDER
        // =====================================================

        Order order = createOrder(
                user,
                address,
                paymentMethod,
                subtotal,
                deliveryCharge,
                discount,
                grandTotal
        );


        // =====================================================
        // CREATE ORDER ITEM
        // =====================================================

        OrderItem item =
                new OrderItem();


        item.setOrder(order);


        item.setProduct(product);


        item.setProductName(
                product.getProductName()
        );


        item.setProductImage(
                product.getImageUrl()
        );


        item.setSelectedWeight(
                selectedWeight
        );


        item.setPrice(
                unitPrice
        );


        item.setQuantity(
                quantity
        );


        item.setTotalPrice(
                unitPrice * quantity
        );


        orderItemRepository.save(item);


        // =====================================================
        // REDUCE STOCK
        // =====================================================

        if (variant != null) {

            int currentStock =
                    variant.getStock() == null
                            ? 0
                            : variant.getStock();

            variant.setStock(
                    currentStock - quantity
            );

            productVariantRepository.save(
                    variant
            );

        } else {

            int currentStock =
                    product.getStock() == null
                            ? 0
                            : product.getStock();

            product.setStock(
                    currentStock - quantity
            );

            productRepository.save(
                    product
            );
        }


        return order;
    }


    // =========================================================
    // CREATE ORDER
    // =========================================================

    private Order createOrder(
            User user,
            Address address,
            String paymentMethod,
            double subtotal,
            double deliveryCharge,
            double discount,
            double grandTotal) {

        Order order = new Order();


        order.setOrderNumber(
                generateOrderNumber()
        );


        order.setUser(user);


        order.setAddress(address);


        order.setOrderDate(
                LocalDateTime.now()
        );


        order.setOrderStatus(
                "PENDING"
        );


        order.setPaymentMethod(
                paymentMethod
        );


        order.setPaymentStatus(
                "PENDING"
        );


        order.setSubtotal(
                subtotal
        );


        order.setDeliveryCharge(
                deliveryCharge
        );


        order.setDiscount(
                discount
        );


        order.setGrandTotal(
                grandTotal
        );


        return orderRepository.save(order);
    }


    // =========================================================
    // CREATE ORDER ITEM FROM CART
    // =========================================================

    private void createOrderItem(
            Order order,
            Cart cart) {

        OrderItem item =
                new OrderItem();


        item.setOrder(order);


        item.setProduct(
                cart.getProduct()
        );


        item.setProductName(
                cart.getProduct().getProductName()
        );


        item.setProductImage(
                cart.getProduct().getImageUrl()
        );


        ProductVariant variant =
                cart.getProductVariant();


        double unitPrice;


        // =====================================================
        // VARIANT
        // =====================================================

        if (variant != null) {

            unitPrice =
                    variant.getPrice();


            item.setSelectedWeight(
                    variant.getWeight()
            );

        }

        // =====================================================
        // NORMAL PRODUCT
        // =====================================================

        else {

            unitPrice =
                    cart.getProduct().getPrice();


            if (cart.getSelectedWeight() != null
                    && !cart.getSelectedWeight().isEmpty()) {

                item.setSelectedWeight(
                        cart.getSelectedWeight()
                );

            } else {

                item.setSelectedWeight(
                        cart.getProduct().getWeight()
                );
            }
        }


        // =====================================================
        // PRICE SNAPSHOT
        // =====================================================

        item.setPrice(
                unitPrice
        );


        item.setQuantity(
                cart.getQuantity()
        );


        item.setTotalPrice(
                unitPrice * cart.getQuantity()
        );


        orderItemRepository.save(item);
    }


    // =========================================================
    // ORDER NUMBER
    // =========================================================

    private String generateOrderNumber() {
        String date = LocalDateTime.now()
                .format(DateTimeFormatter.ofPattern("yyyyMMdd"));

        String orderNumber;

        do {
            String randomPart = UUID.randomUUID()
                    .toString()
                    .replace("-", "")
                    .substring(0, 8)
                    .toUpperCase();

            orderNumber = "NN" + date + randomPart;
        } while (orderRepository.findByOrderNumber(orderNumber).isPresent());

        return orderNumber;
    }

    // =========================================================
    // CUSTOMER ORDERS
    // =========================================================

    @Override
    public List<Order> getUserOrders(
            String email) {

        User user =
                userRepository.findByEmail(email)
                        .orElseThrow(() ->
                                new RuntimeException(
                                        "User Not Found"
                                ));


        return orderRepository
                .findByUserOrderByOrderDateDesc(
                        user
                );
    }


    // =========================================================
    // ORDER DETAILS
    // =========================================================

    @Override
    public Order getOrder(
            String orderNumber,
            String email) {

        User user =
                userRepository.findByEmail(email)
                        .orElseThrow(() ->
                                new RuntimeException(
                                        "User Not Found"
                                ));


        Order order =
                orderRepository
                        .findByOrderNumber(orderNumber)
                        .orElseThrow(() ->
                                new RuntimeException(
                                        "Order Not Found"
                                ));


        if (!order.getUser().getId()
                .equals(user.getId())) {

            throw new RuntimeException(
                    "Unauthorized Access"
            );
        }


        return order;
    }


    // =========================================================
    // ADMIN - ALL ORDERS
    // =========================================================

    @Override
    public List<Order> getAllOrders() {

        return orderRepository
                .findAllByOrderByOrderDateDesc();
    }


    // =========================================================
    // UPDATE ORDER STATUS
    // =========================================================

    @Override
    @Transactional
    public void updateOrderStatus(Long orderId, String status) {

        if (status == null || status.trim().isEmpty()) {
            throw new IllegalArgumentException("Order status cannot be empty");
        }

        String normalizedStatus = status.trim().toUpperCase();

        Set<String> allowedStatuses = Set.of(
                "PENDING",
                "CONFIRMED",
                "PROCESSING",
                "SHIPPED",
                "OUT_FOR_DELIVERY",
                "DELIVERED",
                "CANCELLED"
        );

        if (!allowedStatuses.contains(normalizedStatus)) {
            throw new IllegalArgumentException(
                    "Invalid order status: " + status
            );
        }

        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new RuntimeException("Order not found"));

        order.setOrderStatus(normalizedStatus);
        orderRepository.save(order);
    }
    }