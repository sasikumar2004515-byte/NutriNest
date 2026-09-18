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

    public CategoryController(
            CategoryService categoryService,
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
            @RequestParam(
                    value = "imageFile",
                    required = false
            )
            MultipartFile imageFile)
            throws IOException {


        // =========================
        // UPLOAD CATEGORY IMAGE
        // =========================

        if (imageFile != null
                && !imageFile.isEmpty()) {

            String imagePath =
                    fileUploadService.uploadFile(imageFile);

            category.setImageUrl(imagePath);
        }


        // =========================
        // SAVE CATEGORY
        // =========================

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

        if (category == null) {
            throw new RuntimeException(
                    "Category Not Found"
            );
        }

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
            @RequestParam(
                    value = "imageFile",
                    required = false
            )
            MultipartFile imageFile)
            throws IOException {


        // =========================
        // LOAD EXISTING CATEGORY
        // =========================

        Category existingCategory =
                categoryService.findById(
                        category.getId()
                );

        if (existingCategory == null) {
            throw new RuntimeException(
                    "Category Not Found"
            );
        }


        // =========================
        // EXISTING IMAGE
        // =========================

        String oldImagePath =
                existingCategory.getImageUrl();

        String newImagePath = null;


        // =========================
        // IMAGE UPDATE
        // =========================

        if (imageFile != null
                && !imageFile.isEmpty()) {

            /*
             * Upload the new image FIRST.
             *
             * Old image is kept safe until the
             * database update succeeds.
             */
            newImagePath =
                    fileUploadService.uploadFile(imageFile);

            category.setImageUrl(
                    newImagePath
            );

        } else {

            /*
             * No new image selected.
             * Keep existing image.
             */
            category.setImageUrl(
                    oldImagePath
            );
        }


        // =========================
        // UPDATE CATEGORY
        // =========================

        try {

            categoryService.updateCategory(
                    category.getId(),
                    category
            );

        } catch (RuntimeException ex) {

            /*
             * Database update failed.
             *
             * Remove the newly uploaded image
             * and keep the old image.
             */
            if (newImagePath != null) {

                fileUploadService.deleteFile(
                        newImagePath
                );
            }

            throw ex;
        }


        // =========================
        // DELETE OLD IMAGE
        // =========================

        /*
         * Delete old image ONLY after successful
         * database update.
         */
        if (newImagePath != null
                && oldImagePath != null
                && !oldImagePath.isBlank()
                && !oldImagePath.equals(newImagePath)) {

            fileUploadService.deleteFile(
                    oldImagePath
            );
        }


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

        if (category == null) {
            throw new RuntimeException(
                    "Category Not Found"
            );
        }


        // =========================
        // DELETE CATEGORY IMAGE
        // =========================

        fileUploadService.deleteFile(
                category.getImageUrl()
        );


        // =========================
        // DELETE CATEGORY
        // =========================

        categoryService.deleteCategory(id);


        return "redirect:/admin/categories";
    }
}