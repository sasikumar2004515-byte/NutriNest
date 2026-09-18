package com.nutrinest.serviceimpl;

import com.nutrinest.entity.Product;
import com.nutrinest.entity.ProductVariant;
import com.nutrinest.repository.ProductVariantRepository;
import com.nutrinest.service.ProductVariantService;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ProductVariantServiceImpl implements ProductVariantService {

    private final ProductVariantRepository productVariantRepository;

    public ProductVariantServiceImpl(ProductVariantRepository productVariantRepository) {
        this.productVariantRepository = productVariantRepository;
    }

    @Override
    public List<ProductVariant> getVariants(Product product) {
        return productVariantRepository.findByProduct(product);
    }

    @Override
    public ProductVariant getVariant(Product product, String weight) {

        return productVariantRepository
                .findByProductAndWeight(product, weight)
                .orElseThrow(() -> new RuntimeException("Variant Not Found"));
    }
}