package com.nutrinest.serviceimpl;

import com.nutrinest.entity.Brand;
import com.nutrinest.repository.BrandRepository;
import com.nutrinest.service.BrandService;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class BrandServiceImpl implements BrandService {

    private final BrandRepository brandRepository;

    public BrandServiceImpl(BrandRepository brandRepository) {
        this.brandRepository = brandRepository;
    }

    @Override
    public Brand saveBrand(Brand brand) {
        return brandRepository.save(brand);
    }

    @Override
    public List<Brand> getAllBrands() {
        return brandRepository.findAll();
    }

    @Override
    public Optional<Brand> getBrandById(Long id) {
        return brandRepository.findById(id);
    }

    @Override
    public Brand updateBrand(Long id, Brand brand) {

        Brand existingBrand = brandRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Brand not found"));

        existingBrand.setBrandName(brand.getBrandName());
        existingBrand.setDescription(brand.getDescription());
        existingBrand.setImageUrl(brand.getImageUrl());
        existingBrand.setActive(brand.getActive());

        return brandRepository.save(existingBrand);
    }

    @Override
    public void deleteBrand(Long id) {
        brandRepository.deleteById(id);
    }

    @Override
    public List<Brand> getActiveBrands() {
        return brandRepository.findByActiveTrue();
    }

    @Override
    public Brand findById(Long id) {
        return brandRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Brand not found"));
    }
}