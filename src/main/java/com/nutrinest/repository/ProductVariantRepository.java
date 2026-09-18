package com.nutrinest.repository;

import com.nutrinest.entity.Product;
import com.nutrinest.entity.ProductVariant;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface ProductVariantRepository extends JpaRepository<ProductVariant, Long> {

    List<ProductVariant> findByProduct(Product product);

    Optional<ProductVariant> findByProductAndWeight(Product product, String weight);

}