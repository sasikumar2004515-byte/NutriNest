package com.nutrinest.serviceimpl;

import com.nutrinest.entity.Cart;
import com.nutrinest.entity.Product;
import com.nutrinest.entity.ProductVariant;
import com.nutrinest.entity.User;
import com.nutrinest.repository.CartRepository;
import com.nutrinest.repository.ProductRepository;
import com.nutrinest.repository.ProductVariantRepository;
import com.nutrinest.repository.UserRepository;
import com.nutrinest.service.CartService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional
public class CartServiceImpl implements CartService {

    private final CartRepository cartRepository;
    private final ProductRepository productRepository;
    private final UserRepository userRepository;
    private final ProductVariantRepository productVariantRepository;

    public CartServiceImpl(
            CartRepository cartRepository,
            ProductRepository productRepository,
            UserRepository userRepository,
            ProductVariantRepository productVariantRepository) {

        this.cartRepository = cartRepository;
        this.productRepository = productRepository;
        this.userRepository = userRepository;
        this.productVariantRepository = productVariantRepository;
    }

    // =========================================================
    // EXISTING ADD TO CART - BACKWARD COMPATIBILITY
    // =========================================================

    @Override
    public Cart addToCart(Long productId, String email) {
        return addToCart(productId, 1, email);
    }

    @Override
    public Cart addToCart(Long productId, Integer quantity, String email) {

        /*
         * Existing callers do not provide a variant.
         *
         * If the product has variants, select the first available
         * variant as the default variant.
         *
         * If the product has no variants, use the product itself.
         */
        Product product = getProduct(productId);

        List<ProductVariant> variants =
                productVariantRepository.findByProduct(product);

        if (!variants.isEmpty()) {
            ProductVariant defaultVariant = variants.get(0);

            return addToCart(
                    productId,
                    defaultVariant.getId(),
                    quantity,
                    email
            );
        }

        return addProductWithoutVariant(product, quantity, email);
    }

    // =========================================================
    // VARIANT-AWARE ADD TO CART
    // =========================================================

    @Override
    public Cart addToCart(
            Long productId,
            Long variantId,
            Integer quantity,
            String email) {

        User user = getUser(email);
        Product product = getProduct(productId);
        ProductVariant variant = getVariant(variantId);

        // Make sure variant belongs to the requested product.
        if (variant.getProduct() == null
                || !variant.getProduct().getId().equals(product.getId())) {

            throw new IllegalArgumentException(
                    "Selected variant does not belong to this product"
            );
        }

        // Validate quantity.
        if (quantity == null || quantity < 1) {
            quantity = 1;
        }

        // Variant stock is the source of truth.
        Integer variantStock = variant.getStock();

        if (variantStock == null || variantStock < quantity) {
            throw new IllegalArgumentException(
                    "Insufficient stock for selected variant"
            );
        }

        List<Cart> cartItems = cartRepository.findByUser(user);

        /*
         * IMPORTANT:
         *
         * Same product + same variant
         *      -> increase quantity
         *
         * Same product + different variant
         *      -> create a separate cart item
         */
        for (Cart cart : cartItems) {

            if (cart.getProduct() != null
                    && cart.getProduct().getId().equals(productId)
                    && cart.getProductVariant() != null
                    && cart.getProductVariant().getId().equals(variantId)) {

                int existingQuantity =
                        cart.getQuantity() == null ? 0 : cart.getQuantity();

                int newQuantity = existingQuantity + quantity;

                if (newQuantity > variantStock) {
                    throw new IllegalArgumentException(
                            "Insufficient stock for selected variant"
                    );
                }

                cart.setQuantity(newQuantity);
                cart.setProductVariant(variant);
                cart.setSelectedWeight(variant.getWeight());

                return cartRepository.save(cart);
            }
        }

        // Create a NEW cart item for this variant.
        Cart cart = new Cart();

        cart.setUser(user);
        cart.setProduct(product);
        cart.setProductVariant(variant);
        cart.setQuantity(quantity);
        cart.setSelectedWeight(variant.getWeight());

        return cartRepository.save(cart);
    }

    // =========================================================
    // PRODUCT WITHOUT VARIANTS
    // =========================================================

    private Cart addProductWithoutVariant(
            Product product,
            Integer quantity,
            String email) {

        User user = getUser(email);

        if (quantity == null || quantity < 1) {
            quantity = 1;
        }

        Integer stock = product.getStock();

        if (stock == null || stock < quantity) {
            throw new IllegalArgumentException("Insufficient stock");
        }

        List<Cart> cartItems = cartRepository.findByUser(user);

        for (Cart cart : cartItems) {

            if (cart.getProduct() != null
                    && cart.getProduct().getId().equals(product.getId())
                    && cart.getProductVariant() == null) {

                int existingQuantity =
                        cart.getQuantity() == null ? 0 : cart.getQuantity();

                int newQuantity = existingQuantity + quantity;

                if (newQuantity > stock) {
                    throw new IllegalArgumentException(
                            "Insufficient stock"
                    );
                }

                cart.setQuantity(newQuantity);

                return cartRepository.save(cart);
            }
        }

        Cart cart = new Cart();

        cart.setUser(user);
        cart.setProduct(product);
        cart.setQuantity(quantity);

        return cartRepository.save(cart);
    }

    // =========================================================
    // VIEW CART
    // =========================================================

    @Override
    @Transactional(readOnly = true)
    public List<Cart> getAllCartItems(String email) {

        User user = getUser(email);

        return cartRepository.findByUser(user);
    }

    // =========================================================
    // REMOVE
    // =========================================================

    @Override
    public void removeFromCart(Long cartId, String email) {

        User user = getUser(email);
        Cart cart = getOwnedCart(cartId, user);

        cartRepository.delete(cart);
    }

    // =========================================================
    // INCREASE QUANTITY
    // =========================================================

    @Override
    public void increaseQuantity(Long cartId, String email) {

        Cart cart = getOwnedCart(cartId, getUser(email));

        Integer stock = getAvailableStock(cart);

        int currentQuantity =
                cart.getQuantity() == null ? 0 : cart.getQuantity();

        if (currentQuantity >= stock) {
            throw new IllegalArgumentException(
                    "Insufficient stock"
            );
        }

        cart.setQuantity(currentQuantity + 1);

        cartRepository.save(cart);
    }

    // =========================================================
    // DECREASE QUANTITY
    // =========================================================

    @Override
    public void decreaseQuantity(Long cartId, String email) {

        Cart cart = getOwnedCart(cartId, getUser(email));

        int currentQuantity =
                cart.getQuantity() == null ? 1 : cart.getQuantity();

        if (currentQuantity > 1) {

            cart.setQuantity(currentQuantity - 1);

            cartRepository.save(cart);

        } else {

            cartRepository.delete(cart);
        }
    }

    // =========================================================
    // CHANGE VARIANT
    // =========================================================

    @Override
    public void changeVariant(
            Long cartId,
            Long variantId,
            String email) {

        User user = getUser(email);
        Cart cart = getOwnedCart(cartId, user);

        ProductVariant variant = getVariant(variantId);

        Product product = cart.getProduct();

        // Variant must belong to the same product.
        if (variant.getProduct() == null
                || product == null
                || !variant.getProduct().getId().equals(product.getId())) {

            throw new IllegalArgumentException(
                    "Selected variant does not belong to this product"
            );
        }

        Integer variantStock = variant.getStock();

        if (variantStock == null || variantStock < 1) {
            throw new IllegalArgumentException(
                    "Selected variant is out of stock"
            );
        }

        int currentQuantity =
                cart.getQuantity() == null ? 1 : cart.getQuantity();

        /*
         * If requested variant cannot support the existing
         * quantity, don't silently exceed its stock.
         */
        if (currentQuantity > variantStock) {
            throw new IllegalArgumentException(
                    "Selected variant has insufficient stock for current quantity"
            );
        }

        /*
         * If another cart row already contains this exact variant,
         * merge the quantities instead of creating duplicates.
         */
        List<Cart> cartItems = cartRepository.findByUser(user);

        for (Cart otherCart : cartItems) {

            if (otherCart.getId().equals(cart.getId())) {
                continue;
            }

            if (otherCart.getProduct() != null
                    && otherCart.getProduct().getId().equals(product.getId())
                    && otherCart.getProductVariant() != null
                    && otherCart.getProductVariant().getId().equals(variantId)) {

                int otherQuantity =
                        otherCart.getQuantity() == null
                                ? 0
                                : otherCart.getQuantity();

                int mergedQuantity = otherQuantity + currentQuantity;

                if (mergedQuantity > variantStock) {
                    throw new IllegalArgumentException(
                            "Insufficient stock for selected variant"
                    );
                }

                otherCart.setQuantity(mergedQuantity);
                otherCart.setSelectedWeight(variant.getWeight());
                otherCart.setProductVariant(variant);

                cartRepository.save(otherCart);
                cartRepository.delete(cart);

                return;
            }
        }

        cart.setProductVariant(variant);
        cart.setSelectedWeight(variant.getWeight());

        cartRepository.save(cart);
    }

    // =========================================================
    // LEGACY CHANGE WEIGHT
    // =========================================================

    @Override
    public void changeWeight(
            Long cartId,
            String weight,
            String email) {

        Cart cart = getOwnedCart(cartId, getUser(email));

        Product product = cart.getProduct();

        ProductVariant variant = productVariantRepository
                .findByProductAndWeight(product, weight)
                .orElseThrow(() ->
                        new IllegalArgumentException(
                                "Variant Not Found"
                        ));

        changeVariant(
                cartId,
                variant.getId(),
                email
        );
    }

    // =========================================================
    // HELPERS
    // =========================================================

    private User getUser(String email) {

        return userRepository.findByEmail(email)
                .orElseThrow(() ->
                        new IllegalArgumentException(
                                "User Not Found"
                        ));
    }

    private Product getProduct(Long productId) {

        return productRepository.findById(productId)
                .orElseThrow(() ->
                        new IllegalArgumentException(
                                "Product Not Found"
                        ));
    }

    private ProductVariant getVariant(Long variantId) {

        return productVariantRepository.findById(variantId)
                .orElseThrow(() ->
                        new IllegalArgumentException(
                                "Variant Not Found"
                        ));
    }

    private Cart getOwnedCart(
            Long cartId,
            User user) {

        return cartRepository.findByIdAndUser(cartId, user)
                .orElseThrow(() ->
                        new IllegalArgumentException(
                                "Cart Not Found"
                        ));
    }

    private Integer getAvailableStock(Cart cart) {

        if (cart.getProductVariant() != null) {

            Integer variantStock =
                    cart.getProductVariant().getStock();

            if (variantStock == null) {
                throw new IllegalArgumentException(
                        "Variant stock unavailable"
                );
            }

            return variantStock;
        }

        Integer productStock = cart.getProduct().getStock();

        if (productStock == null) {
            throw new IllegalArgumentException(
                    "Product stock unavailable"
            );
        }

        return productStock;
    }
}