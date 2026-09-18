package com.nutrinest.controller.admin;

import com.nutrinest.entity.Brand;
import com.nutrinest.service.BrandService;
import com.nutrinest.service.FileUploadService;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

@Controller
@RequestMapping("/admin/brands")
public class AdminBrandController {

    private final BrandService brandService;
    private final FileUploadService fileUploadService;

    public AdminBrandController(
            BrandService brandService,
            FileUploadService fileUploadService) {

        this.brandService = brandService;
        this.fileUploadService = fileUploadService;
    }


    // =========================
    // BRAND LIST
    // =========================

    @GetMapping
    public String brands(Model model) {

        model.addAttribute(
                "brands",
                brandService.getAllBrands()
        );

        return "admin/brands";
    }


    // =========================
    // ADD BRAND PAGE
    // =========================

    @GetMapping("/new")
    public String showAddBrandForm(Model model) {

        model.addAttribute(
                "brand",
                new Brand()
        );

        return "admin/add-brand";
    }


    // =========================
    // SAVE BRAND
    // =========================

    @PostMapping("/save")
    public String saveBrand(
            @ModelAttribute Brand brand,
            @RequestParam(
                    value = "imageFile",
                    required = false
            )
            MultipartFile imageFile)
            throws IOException {


        // =========================
        // UPLOAD BRAND IMAGE
        // =========================

        if (imageFile != null
                && !imageFile.isEmpty()) {

            String imagePath =
                    fileUploadService.uploadFile(imageFile);

            brand.setImageUrl(imagePath);
        }


        // =========================
        // SAVE BRAND
        // =========================

        brandService.saveBrand(brand);


        return "redirect:/admin/brands";
    }


    // =========================
    // EDIT BRAND PAGE
    // =========================

    @GetMapping("/edit/{id}")
    public String editBrand(
            @PathVariable Long id,
            Model model) {

        Brand brand =
                brandService.findById(id);

        if (brand == null) {
            throw new RuntimeException(
                    "Brand Not Found"
            );
        }

        model.addAttribute(
                "brand",
                brand
        );

        return "admin/edit-brand";
    }


    // =========================
    // UPDATE BRAND
    // =========================

    @PostMapping("/update")
    public String updateBrand(
            @ModelAttribute Brand brand,
            @RequestParam(
                    value = "imageFile",
                    required = false
            )
            MultipartFile imageFile)
            throws IOException {


        // =========================
        // LOAD EXISTING BRAND
        // =========================

        Brand existingBrand =
                brandService.findById(
                        brand.getId()
                );

        if (existingBrand == null) {
            throw new RuntimeException(
                    "Brand Not Found"
            );
        }


        // =========================
        // EXISTING IMAGE
        // =========================

        String oldImagePath =
                existingBrand.getImageUrl();

        String newImagePath = null;


        // =========================
        // IMAGE UPDATE
        // =========================

        if (imageFile != null
                && !imageFile.isEmpty()) {

            /*
             * Upload new image FIRST.
             *
             * Old image remains untouched until
             * the database update succeeds.
             */
            newImagePath =
                    fileUploadService.uploadFile(imageFile);

            brand.setImageUrl(
                    newImagePath
            );

        } else {

            /*
             * No new image selected.
             * Keep existing image.
             */
            brand.setImageUrl(
                    oldImagePath
            );
        }


        // =========================
        // UPDATE BRAND
        // =========================

        try {

            brandService.updateBrand(
                    brand.getId(),
                    brand
            );

        } catch (RuntimeException ex) {

            /*
             * Database update failed.
             *
             * Remove the newly uploaded image
             * so an orphan file is not left behind.
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
         * Delete the old image only after the
         * database update succeeds.
         */
        if (newImagePath != null
                && oldImagePath != null
                && !oldImagePath.isBlank()
                && !oldImagePath.equals(newImagePath)) {

            fileUploadService.deleteFile(
                    oldImagePath
            );
        }


        return "redirect:/admin/brands";
    }


    // =========================
    // DELETE BRAND
    // =========================

    @GetMapping("/delete/{id}")
    public String deleteBrand(
            @PathVariable Long id) {

        Brand brand =
                brandService.findById(id);


        if (brand == null) {
            throw new RuntimeException(
                    "Brand Not Found"
            );
        }


        // =========================
        // DELETE BRAND IMAGE
        // =========================

        fileUploadService.deleteFile(
                brand.getImageUrl()
        );


        // =========================
        // DELETE BRAND
        // =========================

        brandService.deleteBrand(id);


        return "redirect:/admin/brands";
    }
}