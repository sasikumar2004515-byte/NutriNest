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

import java.util.List;

@Service
public class CartServiceImpl implements CartService {

    private final CartRepository cartRepository;
    private final ProductRepository productRepository;
    private final UserRepository userRepository;
    private final ProductVariantRepository productVariantRepository;

    public CartServiceImpl(CartRepository cartRepository,
                           ProductRepository productRepository,
                           UserRepository userRepository,
                           ProductVariantRepository productVariantRepository) {

        this.cartRepository = cartRepository;
        this.productRepository = productRepository;
        this.userRepository = userRepository;
        this.productVariantRepository = productVariantRepository;
    }




    @Override
    public Cart addToCart(Long productId, String email) {
        return addToCart(productId, 1, email);
    }

    @Override
    public Cart addToCart(Long productId, Integer quantity, String email) {

        System.out.println("========== ADD TO CART ==========");
        System.out.println("Product ID : " + productId);
        System.out.println("Email      : " + email);

        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> {
                    System.out.println("❌ USER NOT FOUND");
                    return new RuntimeException("User Not Found");
                });

        System.out.println("✅ User Found : " + user.getEmail());

        Product product = productRepository.findById(productId)
                .orElseThrow(() -> {
                    System.out.println("❌ PRODUCT NOT FOUND");
                    return new RuntimeException("Product Not Found");
                });

        System.out.println("✅ Product Found : " + product.getId());

        if (quantity == null || quantity < 1) quantity = 1;
        if (product.getStock() == null || product.getStock() < quantity) {
            throw new IllegalArgumentException("Insufficient stock");
        }

        List<Cart> cartItems = cartRepository.findByUser(user);

        System.out.println("Existing Cart Size : " + cartItems.size());

        for (Cart cart : cartItems) {

            if (cart.getProduct().getId().equals(productId)) {

                System.out.println("Product already in cart. Increasing quantity.");

                int newQuantity = cart.getQuantity() + quantity;
                if (newQuantity > product.getStock()) {
                    throw new IllegalArgumentException("Insufficient stock");
                }
                cart.setQuantity(newQuantity);

                Cart savedCart = cartRepository.save(cart);

                System.out.println("✅ Quantity Updated");

                return savedCart;
            }
        }

        System.out.println("Creating New Cart Item...");

        Cart cart = new Cart();

        cart.setUser(user);
        cart.setProduct(product);
        cart.setQuantity(quantity);

        Cart savedCart = cartRepository.save(cart);

        System.out.println("✅ Cart Saved Successfully. Cart ID : " + savedCart.getId());

        return savedCart;
    }

    @Override
    public List<Cart> getAllCartItems(String email) {

        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User Not Found"));

        return cartRepository.findByUser(user);
    }

    @Override
    public void removeFromCart(Long cartId, String email) {

        User user = getUser(email);
        Cart cart = getOwnedCart(cartId, user);

        cartRepository.delete(cart);
    }

    @Override
    public void increaseQuantity(Long cartId, String email) {

        Cart cart = getOwnedCart(cartId, getUser(email));

        Integer stock = cart.getProduct().getStock();
        if (stock == null || cart.getQuantity() >= stock) {
            throw new IllegalArgumentException("Insufficient stock");
        }
        cart.setQuantity(cart.getQuantity() + 1);

        cartRepository.save(cart);
    }

    @Override
    public void decreaseQuantity(Long cartId, String email) {

        Cart cart = getOwnedCart(cartId, getUser(email));

        if (cart.getQuantity() > 1) {

            cart.setQuantity(cart.getQuantity() - 1);

            cartRepository.save(cart);

        } else {

            cartRepository.delete(cart);
        }
    }
    @Override
    public void changeWeight(Long cartId, String weight, String email) {

        Cart cart = getOwnedCart(cartId, getUser(email));

        ProductVariant variant = productVariantRepository
                .findByProductAndWeight(cart.getProduct(), weight)
                .orElseThrow(() -> new RuntimeException("Variant Not Found"));

        cart.setSelectedWeight(weight);

        cartRepository.save(cart);
    }

    private User getUser(String email) {
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User Not Found"));
    }

    private Cart getOwnedCart(Long cartId, User user) {
        return cartRepository.findByIdAndUser(cartId, user)
                .orElseThrow(() -> new RuntimeException("Cart Not Found"));
    }
}
