package com.nutrinest.repository;

import com.nutrinest.entity.Cart;
import com.nutrinest.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

public interface CartRepository extends JpaRepository<Cart, Long> {

    List<Cart> findByUser(User user);

    Optional<Cart> findByIdAndUser(Long id, User user);

    @Modifying
    @Transactional
    void deleteByUser(User user);

}
