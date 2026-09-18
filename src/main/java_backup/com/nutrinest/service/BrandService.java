package com.nutrinest.service;

import com.nutrinest.entity.Brand;

import java.util.List;
import java.util.Optional;

public interface BrandService {

    Brand saveBrand(Brand brand);

    List<Brand> getAllBrands();

    Optional<Brand> getBrandById(Long id);

    Brand updateBrand(Long id, Brand brand);

    void deleteBrand(Long id);

    List<Brand> getActiveBrands();

    Brand findById(Long id);
}