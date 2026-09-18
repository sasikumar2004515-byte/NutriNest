package com.nutrinest.service;

import com.nutrinest.entity.Category;

import java.util.List;
import java.util.Optional;

public interface CategoryService {

    Category saveCategory(Category category);

    List<Category> getAllCategories();

    Optional<Category> getCategoryById(Long id);

    Category updateCategory(Long id, Category category);

    void deleteCategory(Long id);

    List<Category> findAll();

    Category findById(Long id);

    // Customer side
    List<Category> getActiveCategories();
}