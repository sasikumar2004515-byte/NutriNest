package com.nutrinest.repository;

import com.nutrinest.entity.Brand;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface BrandRepository extends JpaRepository<Brand, Long> {

    List<Brand> findByActiveTrue();

    Optional<Brand> findByBrandName(String brandName);
}