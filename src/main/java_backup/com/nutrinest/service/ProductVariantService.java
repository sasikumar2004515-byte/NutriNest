package com.nutrinest.service;

import com.nutrinest.entity.Product;
import com.nutrinest.entity.ProductVariant;

import java.util.List;

public interface ProductVariantService {

    List<ProductVariant> getVariants(Product product);

    ProductVariant getVariant(Product product, String weight);
}