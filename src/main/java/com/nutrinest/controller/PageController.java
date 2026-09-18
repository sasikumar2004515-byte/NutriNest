package com.nutrinest.controller;

import com.nutrinest.entity.Category;
import com.nutrinest.entity.Product;
import com.nutrinest.entity.ProductVariant;
import com.nutrinest.entity.User;
import com.nutrinest.service.CategoryService;
import com.nutrinest.service.ProductService;
import com.nutrinest.service.ProductVariantService;
import com.nutrinest.service.UserService;
import com.nutrinest.service.WishlistService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import jakarta.servlet.http.HttpServletResponse;

import java.security.Principal;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@Controller
public class PageController {

    @Autowired
    private ProductService productService;

    @Autowired
    private WishlistService wishlistService;

    @Autowired
    private UserService userService;

    @Autowired
    private CategoryService categoryService;

    @Autowired
    private ProductVariantService productVariantService;

    /* =========================================================
       HOME
       ========================================================= */

    @GetMapping("/")
    public String home(
            Model model,
            Principal principal) {

        model.addAttribute(
                "products",
                productService.getActiveProducts()
        );

        model.addAttribute(
                "featuredProducts",
                productService.getFeaturedProducts()
        );

        model.addAttribute(
                "bestSellerProducts",
                productService.getBestSellerProducts()
        );

        model.addAttribute(
                "organicProducts",
                productService.getOrganicProducts()
        );

        model.addAttribute(
                "premiumProducts",
                productService.getPremiumProducts()
        );

        model.addAttribute(
                "giftProducts",
                productService.getGiftProducts()
        );

        model.addAttribute(
                "categories",
                categoryService.getActiveCategories()
        );

        /* =====================================================
           WISHLIST
           ===================================================== */

        if (principal != null) {

            User user = userService.findByEmail(
                    principal.getName()
            );

            model.addAttribute(
                    "wishlistCount",
                    wishlistService
                            .getWishlist(user)
                            .size()
            );

            model.addAttribute(
                    "wishlistProductIds",
                    wishlistService
                            .getWishlist(user)
                            .stream()
                            .map(wishlist ->
                                    wishlist.getProduct().getId()
                            )
                            .collect(Collectors.toSet())
            );

        } else {

            model.addAttribute(
                    "wishlistCount",
                    0
            );

            model.addAttribute(
                    "wishlistProductIds",
                    Collections.emptySet()
            );
        }

        return "index";
    }

    /* =========================================================
       PRODUCTS
       ========================================================= */

    @GetMapping("/products")
    public String products(Model model) {

        model.addAttribute(
                "products",
                productService.getActiveProducts()
        );

        model.addAttribute(
                "bestSellerProducts",
                productService.getBestSellerProducts()
        );

        model.addAttribute(
                "organicProducts",
                productService.getOrganicProducts()
        );

        model.addAttribute(
                "featuredProducts",
                productService.getFeaturedProducts()
        );

        model.addAttribute(
                "premiumProducts",
                productService.getPremiumProducts()
        );

        model.addAttribute(
                "giftProducts",
                productService.getGiftProducts()
        );

        return "products";
    }

    /* =========================================================
       PRODUCT DETAILS
       ========================================================= */

    @GetMapping("/products/{id}")
    public String productDetails(
            @PathVariable Long id,
            Model model,
            HttpServletResponse response,
            Principal principal) {

        Product product = productService
                .getProductById(id)
                .orElse(null);

        /* =====================================================
           PRODUCT NOT FOUND / INACTIVE
           ===================================================== */

        if (product == null ||
                !Boolean.TRUE.equals(product.getActive())) {

            response.setStatus(
                    HttpServletResponse.SC_NOT_FOUND
            );

            return "404";
        }

        /* =====================================================
           PRODUCT
           ===================================================== */

        model.addAttribute(
                "product",
                product
        );

        /* =====================================================
           PRODUCT VARIANTS
           ===================================================== */

        List<ProductVariant> variants =
                productVariantService.getVariants(product);

        model.addAttribute(
                "variants",
                variants
        );

        /* =====================================================
           DEFAULT VARIANT
           ===================================================== */

        ProductVariant defaultVariant = null;

        if (!variants.isEmpty()) {
            defaultVariant = variants.get(0);
        }

        model.addAttribute(
                "defaultVariant",
                defaultVariant
        );

        /* =====================================================
           WISHLIST STATUS
           ===================================================== */

        boolean inWishlist = false;

        if (principal != null) {

            try {

                inWishlist =
                        wishlistService.isProductInWishlist(
                                id,
                                principal.getName()
                        );

            } catch (RuntimeException ignored) {

                // A stale authenticated principal
                // must not break a public product page.

            }
        }

        model.addAttribute(
                "wishlistInWishlist",
                inWishlist
        );

        return "product-details";
    }

    /* =========================================================
       CATEGORIES
       ========================================================= */

    @GetMapping("/categories")
    public String categories(Model model) {

        List<Category> categories =
                categoryService.getActiveCategories();

        model.addAttribute(
                "categories",
                categories
        );

        return "categories";
    }

    /* =========================================================
       CATEGORY PRODUCTS
       ========================================================= */

    @GetMapping("/categories/{id}")
    public String categoryProducts(
            @PathVariable Long id,
            Model model,
            HttpServletResponse response) {

        Category category =
                categoryService
                        .getCategoryById(id)
                        .orElse(null);

        /* =====================================================
           CATEGORY NOT FOUND / INACTIVE
           ===================================================== */

        if (category == null ||
                !Boolean.TRUE.equals(category.getActive())) {

            response.setStatus(
                    HttpServletResponse.SC_NOT_FOUND
            );

            return "404";
        }

        /* =====================================================
           CATEGORY PRODUCTS
           ===================================================== */

        List<Product> products =
                productService.getProductsByCategory(
                        category
                );

        model.addAttribute(
                "category",
                category
        );

        model.addAttribute(
                "products",
                products
        );

        return "category-products";
    }

    /* =========================================================
       ABOUT
       ========================================================= */

    @GetMapping("/about")
    public String about() {

        return "about";
    }

    /* =========================================================
       CONTACT
       ========================================================= */

    @GetMapping("/contact")
    public String contact() {

        return "contact";
    }

    /* =========================================================
       TERMS
       ========================================================= */

    @GetMapping("/terms")
    public String terms() {

        return "terms";
    }
}