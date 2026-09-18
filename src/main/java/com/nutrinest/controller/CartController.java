package com.nutrinest.controller;

import com.nutrinest.entity.Cart;
import com.nutrinest.entity.ProductVariant;
import com.nutrinest.service.CartService;
import com.nutrinest.service.ProductVariantService;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.security.Principal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Controller
@RequestMapping("/cart")
public class CartController {

    private static final int MAX_CART_QUANTITY = 100;

    @Autowired
    private CartService cartService;

    @Autowired
    private ProductVariantService productVariantService;


    // =========================================================
    // ADD TO CART - PRODUCT ID PATH
    // =========================================================

    @PostMapping("/add/{productId}")
    public String addToCartByPath(
            @PathVariable Long productId,
            Principal principal,
            HttpServletRequest request) {

        if (principal == null) {
            return "redirect:/login";
        }

        cartService.addToCart(
                productId,
                principal.getName()
        );

        String referer =
                request.getHeader("Referer");

        if (isSafeLocalReferer(referer)) {

            return "redirect:" +
                    getLocalPath(referer);
        }

        return "redirect:/products";
    }


    // =========================================================
    // ADD TO CART - FORM / AJAX - VARIANT AWARE
    // =========================================================

    @PostMapping("/add")
    @ResponseBody
    public String addToCart(
            @RequestParam Long productId,
            @RequestParam(required = false) Long variantId,
            @RequestParam(defaultValue = "1") Integer quantity,
            Principal principal,
            HttpServletRequest request,
            HttpServletResponse response) throws IOException {

        // =====================================================
        // LOGIN CHECK
        // =====================================================

        if (principal == null) {
            return "LOGIN";
        }


        // =====================================================
        // QUANTITY VALIDATION
        // =====================================================

        if (quantity == null || quantity < 1) {
            return "ERROR:INVALID_QUANTITY";
        }

        if (quantity > MAX_CART_QUANTITY) {
            return "ERROR:MAX_QUANTITY_EXCEEDED";
        }


        // =====================================================
        // ADD PRODUCT TO CART
        // =====================================================

        try {

            if (variantId != null) {

                cartService.addToCart(
                        productId,
                        variantId,
                        quantity,
                        principal.getName()
                );

            } else {

                cartService.addToCart(
                        productId,
                        quantity,
                        principal.getName()
                );
            }

        } catch (IllegalArgumentException e) {

            return "ERROR:" +
                    (e.getMessage() == null
                            ? "Unable to add product to cart"
                            : e.getMessage());

        } catch (Exception e) {

            return "ERROR:Unable to add product to cart";
        }


        // =====================================================
        // AJAX REQUEST
        // =====================================================

        if ("XMLHttpRequest".equals(
                request.getHeader("X-Requested-With"))) {

            int count =
                    cartService
                            .getAllCartItems(
                                    principal.getName()
                            )
                            .size();

            return "SUCCESS:" + count;
        }


        // =====================================================
        // NORMAL HTML FORM REQUEST
        // =====================================================

        String referer =
                request.getHeader("Referer");

        if (isSafeLocalReferer(referer)) {

            response.sendRedirect(
                    getLocalPath(referer)
            );

            return "";
        }


        response.sendRedirect("/cart");

        return "";
    }


    // =========================================================
    // SAFE LOCAL REFERER CHECK
    // =========================================================

    private boolean isSafeLocalReferer(
            String referer) {

        if (
                referer == null ||
                        referer.isBlank()
        ) {
            return false;
        }

        try {

            java.net.URI uri =
                    java.net.URI.create(referer);

            String host =
                    uri.getHost();

            if (host == null) {
                return false;
            }

            return "localhost".equalsIgnoreCase(host)
                    || "127.0.0.1".equalsIgnoreCase(host);

        } catch (Exception e) {

            return false;
        }
    }


    // =========================================================
    // GET LOCAL PATH FROM REFERER
    // =========================================================

    private String getLocalPath(
            String referer) {

        try {

            java.net.URI uri =
                    java.net.URI.create(referer);

            String path =
                    uri.getRawPath();

            if (
                    path == null ||
                            path.isBlank()
            ) {
                return "/cart";
            }

            String query =
                    uri.getRawQuery();

            if (
                    query != null &&
                            !query.isBlank()
            ) {

                return path +
                        "?" +
                        query;
            }

            return path;

        } catch (Exception e) {

            return "/cart";
        }
    }


    // =========================================================
    // VIEW CART
    // =========================================================

    @GetMapping
    public String viewCart(
            Model model,
            Principal principal) {

        // =====================================================
        // LOGIN CHECK
        // =====================================================

        if (principal == null) {
            return "redirect:/login";
        }


        String email =
                principal.getName();


        // =====================================================
        // GET CART ITEMS
        // =====================================================

        List<Cart> cartItems =
                cartService.getAllCartItems(email);


        // =====================================================
        // LOAD ACTUAL VARIANTS FOR EACH PRODUCT
        // =====================================================

        Map<Long, List<ProductVariant>> productVariants =
                new HashMap<>();


        for (Cart item : cartItems) {

            if (
                    item.getProduct() == null ||
                            item.getProduct().getId() == null
            ) {
                continue;
            }


            Long productId =
                    item.getProduct().getId();


            if (
                    !productVariants.containsKey(
                            productId
                    )
            ) {

                List<ProductVariant> variants =
                        productVariantService.getVariants(
                                item.getProduct()
                        );


                productVariants.put(
                        productId,
                        variants
                );
            }
        }


        // =====================================================
        // CALCULATE SUBTOTAL
        // =====================================================

        double subtotal =
                cartItems.stream()
                        .mapToDouble(item -> {

                            if (
                                    item.getProduct() == null
                            ) {
                                return 0.0;
                            }


                            double unitPrice =
                                    0.0;


                            // =================================
                            // VARIANT PRICE
                            // =================================

                            if (
                                    item.getProductVariant() != null &&
                                            item.getProductVariant().getPrice() != null
                            ) {

                                unitPrice =
                                        item.getProductVariant()
                                                .getPrice();
                            }


                            // =================================
                            // FALLBACK PRODUCT PRICE
                            // =================================

                            else if (
                                    item.getProduct().getPrice() != null
                            ) {

                                unitPrice =
                                        item.getProduct()
                                                .getPrice();
                            }


                            // =================================
                            // QUANTITY
                            // =================================

                            int quantity =
                                    item.getQuantity() == null
                                            ? 0
                                            : item.getQuantity();


                            return unitPrice *
                                    quantity;

                        })
                        .sum();


        // =====================================================
        // DELIVERY CHARGE
        // =====================================================

        double deliveryCharge =
                subtotal >= 999
                        ? 0.0
                        : 99.0;


        // =====================================================
        // GRAND TOTAL
        // =====================================================

        double grandTotal =
                subtotal +
                        deliveryCharge;


        // =====================================================
        // MODEL ATTRIBUTES
        // =====================================================

        model.addAttribute(
                "cartItems",
                cartItems
        );


        model.addAttribute(
                "productVariants",
                productVariants
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
                "delivery",
                deliveryCharge
        );


        model.addAttribute(
                "grandTotal",
                grandTotal
        );


        return "cart";
    }


    // =========================================================
    // INCREASE QUANTITY
    // =========================================================

    @PostMapping("/increase/{id}")
    public String increase(
            @PathVariable Long id,
            Principal principal) {

        if (principal == null) {
            return "redirect:/login";
        }


        // =====================================================
        // FIND CURRENT CART ITEM
        // =====================================================

        List<Cart> cartItems =
                cartService.getAllCartItems(
                        principal.getName()
                );

        Cart currentItem =
                cartItems.stream()
                        .filter(item ->
                                item.getId() != null &&
                                        item.getId().equals(id))
                        .findFirst()
                        .orElse(null);


        // =====================================================
        // CART ITEM VALIDATION
        // =====================================================

        if (currentItem == null) {
            return "redirect:/cart";
        }


        Integer currentQuantity =
                currentItem.getQuantity();


        if (
                currentQuantity == null ||
                        currentQuantity < 1
        ) {
            return "redirect:/cart";
        }


        // =====================================================
        // MAXIMUM QUANTITY VALIDATION
        // =====================================================

        if (currentQuantity >= MAX_CART_QUANTITY) {
            return "redirect:/cart";
        }


        // =====================================================
        // INCREASE QUANTITY
        // =====================================================

        cartService.increaseQuantity(
                id,
                principal.getName()
        );


        return "redirect:/cart";
    }


    // =========================================================
    // DECREASE QUANTITY
    // =========================================================

    @PostMapping("/decrease/{id}")
    public String decrease(
            @PathVariable Long id,
            Principal principal) {

        if (principal == null) {
            return "redirect:/login";
        }


        cartService.decreaseQuantity(
                id,
                principal.getName()
        );


        return "redirect:/cart";
    }


    // =========================================================
    // REMOVE ITEM
    // =========================================================

    @PostMapping("/remove/{id}")
    public String remove(
            @PathVariable Long id,
            Principal principal) {

        if (principal == null) {
            return "redirect:/login";
        }


        cartService.removeFromCart(
                id,
                principal.getName()
        );


        return "redirect:/cart";
    }


    // =========================================================
    // CHANGE VARIANT - AJAX
    // =========================================================

    @PostMapping("/change-variant")
    @ResponseBody
    public Map<String, Object> changeVariant(
            @RequestParam Long cartId,
            @RequestParam Long variantId,
            Principal principal) {

        Map<String, Object> response =
                new HashMap<>();


        // =====================================================
        // LOGIN CHECK
        // =====================================================

        if (principal == null) {

            response.put(
                    "status",
                    "LOGIN"
            );

            return response;
        }


        try {

            // =================================================
            // UPDATE VARIANT IN DATABASE
            // =================================================

            cartService.changeVariant(
                    cartId,
                    variantId,
                    principal.getName()
            );


            // =================================================
            // RELOAD CART DATA
            // =================================================

            List<Cart> cartItems =
                    cartService.getAllCartItems(
                            principal.getName()
                    );


            // =================================================
            // FIND UPDATED CART ITEM
            // =================================================

            Cart updatedCartItem =
                    null;


            for (Cart item : cartItems) {

                if (
                        item.getId() != null &&
                                item.getId().equals(cartId)
                ) {

                    updatedCartItem =
                            item;

                    break;
                }
            }


            // =================================================
            // SAFETY CHECK
            // =================================================

            if (updatedCartItem == null) {

                response.put(
                        "status",
                        "ERROR"
                );

                response.put(
                        "message",
                        "Cart item not found"
                );

                return response;
            }


            // =================================================
            // GET AUTHORITATIVE SELLING PRICE
            // =================================================

            double unitPrice =
                    0.0;


            if (
                    updatedCartItem.getProductVariant() != null &&
                            updatedCartItem
                                    .getProductVariant()
                                    .getPrice() != null
            ) {

                unitPrice =
                        updatedCartItem
                                .getProductVariant()
                                .getPrice();

            } else if (
                    updatedCartItem.getProduct() != null &&
                            updatedCartItem
                                    .getProduct()
                                    .getPrice() != null
            ) {

                unitPrice =
                        updatedCartItem
                                .getProduct()
                                .getPrice();
            }


            // =================================================
            // GET AUTHORITATIVE MRP
            // =================================================

            double unitMrp =
                    0.0;


            if (
                    updatedCartItem.getProductVariant() != null &&
                            updatedCartItem
                                    .getProductVariant()
                                    .getMrp() != null
            ) {

                unitMrp =
                        updatedCartItem
                                .getProductVariant()
                                .getMrp();

            } else if (
                    updatedCartItem.getProduct() != null &&
                            updatedCartItem
                                    .getProduct()
                                    .getMrp() != null
            ) {

                unitMrp =
                        updatedCartItem
                                .getProduct()
                                .getMrp();
            }


            // =================================================
            // CALCULATE DISCOUNT PERCENTAGE
            // =================================================

            int discountPercent =
                    0;


            if (
                    unitMrp > 0 &&
                            unitPrice > 0 &&
                            unitMrp > unitPrice
            ) {

                discountPercent =
                        (int) Math.round(
                                (
                                        (unitMrp - unitPrice)
                                                / unitMrp
                                ) * 100
                        );
            }


            // =================================================
            // QUANTITY
            // =================================================

            int quantity =
                    updatedCartItem.getQuantity() == null
                            ? 0
                            : updatedCartItem.getQuantity();


            // =================================================
            // ITEM TOTAL
            // =================================================

            double itemTotal =
                    unitPrice *
                            quantity;


            // =================================================
            // CALCULATE COMPLETE CART TOTALS
            // =================================================

            double subtotal =
                    0.0;


            for (Cart item : cartItems) {

                if (
                        item.getProduct() == null
                ) {
                    continue;
                }


                double price =
                        0.0;


                // =============================================
                // VARIANT PRICE
                // =============================================

                if (
                        item.getProductVariant() != null &&
                                item.getProductVariant().getPrice() != null
                ) {

                    price =
                            item.getProductVariant()
                                    .getPrice();

                } else if (
                        item.getProduct().getPrice() != null
                ) {

                    price =
                            item.getProduct()
                                    .getPrice();
                }


                int itemQuantity =
                        item.getQuantity() == null
                                ? 0
                                : item.getQuantity();


                subtotal +=
                        price *
                                itemQuantity;
            }


            // =================================================
            // DELIVERY
            // =================================================

            double deliveryCharge =
                    subtotal >= 999
                            ? 0.0
                            : 99.0;


            // =================================================
            // GRAND TOTAL
            // =================================================

            double grandTotal =
                    subtotal +
                            deliveryCharge;


            // =================================================
            // VARIANT DETAILS
            // =================================================

            String weight =
                    "";


            if (
                    updatedCartItem.getProductVariant() != null &&
                            updatedCartItem
                                    .getProductVariant()
                                    .getWeight() != null
            ) {

                weight =
                        updatedCartItem
                                .getProductVariant()
                                .getWeight();
            }


            // =================================================
            // SUCCESS RESPONSE
            // =================================================

            response.put(
                    "status",
                    "SUCCESS"
            );


            response.put(
                    "cartId",
                    cartId
            );


            response.put(
                    "variantId",
                    updatedCartItem
                            .getProductVariant() != null
                            ? updatedCartItem
                            .getProductVariant()
                            .getId()
                            : variantId
            );


            response.put(
                    "weight",
                    weight
            );


            // =================================================
            // SELLING PRICE
            // =================================================

            response.put(
                    "price",
                    unitPrice
            );


            // =================================================
            // MRP
            // =================================================

            response.put(
                    "mrp",
                    unitMrp
            );


            // =================================================
            // DISCOUNT
            // =================================================

            response.put(
                    "discount",
                    discountPercent
            );


            // =================================================
            // QUANTITY
            // =================================================

            response.put(
                    "quantity",
                    quantity
            );


            // =================================================
            // ITEM TOTAL
            // =================================================

            response.put(
                    "itemTotal",
                    itemTotal
            );


            // =================================================
            // SUBTOTAL
            // =================================================

            response.put(
                    "subtotal",
                    subtotal
            );


            // =================================================
            // DELIVERY
            // =================================================

            response.put(
                    "delivery",
                    deliveryCharge
            );


            response.put(
                    "deliveryCharge",
                    deliveryCharge
            );


            // =================================================
            // GRAND TOTAL
            // =================================================

            response.put(
                    "grandTotal",
                    grandTotal
            );


            return response;


        } catch (IllegalArgumentException e) {

            response.put(
                    "status",
                    "ERROR"
            );


            response.put(
                    "message",
                    e.getMessage() == null
                            ? "Unable to change variant"
                            : e.getMessage()
            );


            return response;


        } catch (Exception e) {

            response.put(
                    "status",
                    "ERROR"
            );


            response.put(
                    "message",
                    "Unable to change variant"
            );


            return response;
        }
    }


    // =========================================================
    // CHANGE WEIGHT - LEGACY COMPATIBILITY
    // =========================================================

    @PostMapping("/change-weight")
    @ResponseBody
    public String changeWeight(
            @RequestParam Long cartId,
            @RequestParam String weight,
            Principal principal) {

        if (principal == null) {
            return "LOGIN";
        }


        try {

            cartService.changeWeight(
                    cartId,
                    weight,
                    principal.getName()
            );


            return "SUCCESS";


        } catch (IllegalArgumentException e) {

            return "ERROR:" +
                    (e.getMessage() == null
                            ? "Unable to change weight"
                            : e.getMessage());


        } catch (Exception e) {

            return "ERROR:Unable to change weight";
        }
    }
}