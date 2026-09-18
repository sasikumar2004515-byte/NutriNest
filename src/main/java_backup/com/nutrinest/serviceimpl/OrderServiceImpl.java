package com.nutrinest.serviceimpl;

import com.nutrinest.entity.*;
import com.nutrinest.repository.*;
import com.nutrinest.service.OrderService;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

@Service
public class OrderServiceImpl implements OrderService {

    private final OrderRepository orderRepository;
    private final OrderItemRepository orderItemRepository;
    private final UserRepository userRepository;
    private final AddressRepository addressRepository;
    private final CartRepository cartRepository;
    private final ProductRepository productRepository;

    public OrderServiceImpl(
            OrderRepository orderRepository,
            OrderItemRepository orderItemRepository,
            UserRepository userRepository,
            AddressRepository addressRepository,
            CartRepository cartRepository,
            ProductRepository productRepository) {

        this.orderRepository = orderRepository;
        this.orderItemRepository = orderItemRepository;
        this.userRepository = userRepository;
        this.addressRepository = addressRepository;
        this.cartRepository = cartRepository;
        this.productRepository = productRepository;
    }


    // =========================================================
    // NORMAL CART CHECKOUT
    // =========================================================

    @Override
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

        List<Cart> cartItems = cartRepository.findByUser(user);

        if (cartItems.isEmpty()) {
            throw new RuntimeException("Cart is Empty");
        }

        double subtotal = 0;

        for (Cart cart : cartItems) {

            if (cart.getProduct().getStock()
                    < cart.getQuantity()) {

                throw new RuntimeException(
                        "Insufficient stock for "
                                + cart.getProduct().getProductName()
                );
            }

            subtotal += cart.getProduct().getPrice()
                    * cart.getQuantity();
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

        // Maximum allowed discount = 2%
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

            createOrderItem(order, cart);
        }

        // =====================================================
        // REDUCE STOCK
        // =====================================================

        for (Cart cart : cartItems) {

            Product product = cart.getProduct();

            product.setStock(
                    product.getStock()
                            - cart.getQuantity()
            );

            productRepository.save(product);
        }

        // =====================================================
        // CLEAR CART
        // =====================================================

        cartRepository.deleteByUser(user);

        return order;
    }


    // =========================================================
    // BUY NOW - SINGLE PRODUCT
    // =========================================================

    @Override
    public Order placeBuyNowOrder(
            String email,
            Long productId,
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

        Product product = productRepository
                .findById(productId)
                .orElseThrow(() ->
                        new RuntimeException("Product Not Found"));

        if (quantity == null || quantity < 1) {
            quantity = 1;
        }

        // =====================================================
        // STOCK CHECK
        // =====================================================

        if (product.getStock() < quantity) {

            throw new RuntimeException(
                    "Insufficient stock for "
                            + product.getProductName()
            );
        }

        // =====================================================
        // SUBTOTAL
        // =====================================================

        double subtotal =
                product.getPrice() * quantity;

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

        // Maximum allowed discount = 2%
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

        OrderItem item = new OrderItem();

        item.setOrder(order);

        item.setProduct(product);

        item.setProductName(
                product.getProductName()
        );

        item.setProductImage(
                product.getImageUrl()
        );

        item.setSelectedWeight(
                product.getWeight()
        );

        item.setPrice(
                product.getPrice()
        );

        item.setQuantity(quantity);

        item.setTotalPrice(
                product.getPrice() * quantity
        );

        orderItemRepository.save(item);

        // =====================================================
        // REDUCE STOCK
        // =====================================================

        product.setStock(
                product.getStock() - quantity
        );

        productRepository.save(product);

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

        order.setOrderStatus("PENDING");

        order.setPaymentMethod(
                paymentMethod
        );

        order.setPaymentStatus("PENDING");

        order.setSubtotal(subtotal);

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

        OrderItem item = new OrderItem();

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

        item.setPrice(
                cart.getProduct().getPrice()
        );

        item.setQuantity(
                cart.getQuantity()
        );

        item.setTotalPrice(
                cart.getProduct().getPrice()
                        * cart.getQuantity()
        );

        orderItemRepository.save(item);
    }


    // =========================================================
    // ORDER NUMBER
    // =========================================================

    private String generateOrderNumber() {

        String date = LocalDateTime.now()
                .format(
                        DateTimeFormatter.ofPattern(
                                "yyyyMMdd"
                        )
                );

        long count =
                orderRepository.count() + 1;

        return "NN"
                + date
                + String.format(
                "%04d",
                count
        );
    }


    // =========================================================
    // CUSTOMER ORDERS
    // =========================================================

    @Override
    public List<Order> getUserOrders(String email) {

        User user = userRepository.findByEmail(email)
                .orElseThrow(() ->
                        new RuntimeException("User Not Found"));

        return orderRepository
                .findByUserOrderByOrderDateDesc(user);
    }


    // =========================================================
    // ORDER DETAILS
    // =========================================================

    @Override
    public Order getOrder(
            String orderNumber,
            String email) {

        User user = userRepository.findByEmail(email)
                .orElseThrow(() ->
                        new RuntimeException("User Not Found"));

        Order order = orderRepository
                .findByOrderNumber(orderNumber)
                .orElseThrow(() ->
                        new RuntimeException("Order Not Found"));

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
    public void updateOrderStatus(
            Long orderId,
            String status) {

        Order order = orderRepository
                .findById(orderId)
                .orElseThrow(() ->
                        new RuntimeException(
                                "Order Not Found"
                        ));

        order.setOrderStatus(status);

        orderRepository.save(order);
    }
}