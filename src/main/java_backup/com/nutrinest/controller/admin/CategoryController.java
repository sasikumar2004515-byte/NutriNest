package com.nutrinest.controller.admin;

import com.nutrinest.entity.Category;
import com.nutrinest.service.CategoryService;
import com.nutrinest.service.FileUploadService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

@Controller
@RequestMapping("/admin/categories")
public class CategoryController {

    private final CategoryService categoryService;
    private final FileUploadService fileUploadService;

    public CategoryController(CategoryService categoryService,
                              FileUploadService fileUploadService) {
        this.categoryService = categoryService;
        this.fileUploadService = fileUploadService;
    }


    // =========================
    // CATEGORY LIST
    // =========================

    @GetMapping
    public String listCategories(Model model) {

        model.addAttribute(
                "categories",
                categoryService.findAll()
        );

        return "admin/categories";
    }


    // =========================
    // ADD CATEGORY PAGE
    // =========================

    @GetMapping("/new")
    public String showAddCategoryForm(Model model) {

        model.addAttribute(
                "category",
                new Category()
        );

        return "admin/add-category";
    }


    // =========================
    // SAVE CATEGORY
    // =========================

    @PostMapping("/save")
    public String saveCategory(
            @ModelAttribute Category category,
            @RequestParam(value = "imageFile", required = false)
            MultipartFile imageFile)
            throws IOException {

        // Upload category image
        if (imageFile != null && !imageFile.isEmpty()) {

            String imagePath =
                    fileUploadService.uploadFile(imageFile);

            category.setImageUrl(imagePath);
        }

        categoryService.saveCategory(category);

        return "redirect:/admin/categories";
    }


    // =========================
    // EDIT CATEGORY PAGE
    // =========================

    @GetMapping("/edit/{id}")
    public String editCategory(
            @PathVariable Long id,
            Model model) {

        Category category =
                categoryService.findById(id);

        model.addAttribute(
                "category",
                category
        );

        return "admin/edit-category";
    }


    // =========================
    // UPDATE CATEGORY
    // =========================

    @PostMapping("/update")
    public String updateCategory(
            @ModelAttribute Category category,
            @RequestParam(value = "imageFile", required = false)
            MultipartFile imageFile)
            throws IOException {

        Category existingCategory =
                categoryService.findById(category.getId());

        // New image uploaded
        if (imageFile != null && !imageFile.isEmpty()) {

            // Delete old image
            fileUploadService.deleteFile(
                    existingCategory.getImageUrl()
            );

            // Upload new image
            String imagePath =
                    fileUploadService.uploadFile(imageFile);

            category.setImageUrl(imagePath);

        } else {

            // Keep existing image
            category.setImageUrl(
                    existingCategory.getImageUrl()
            );
        }

        categoryService.updateCategory(
                category.getId(),
                category
        );

        return "redirect:/admin/categories";
    }


    // =========================
    // DELETE CATEGORY
    // =========================

    @GetMapping("/delete/{id}")
    public String deleteCategory(
            @PathVariable Long id) {

        Category category =
                categoryService.findById(id);

        // Delete category image
        if (category != null) {

            fileUploadService.deleteFile(
                    category.getImageUrl()
            );
        }

        categoryService.deleteCategory(id);

        return "redirect:/admin/categories";
    }

}