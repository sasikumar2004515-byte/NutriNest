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

    public AdminBrandController(BrandService brandService,
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
            ) MultipartFile imageFile) throws IOException {

        if (imageFile != null && !imageFile.isEmpty()) {

            String imagePath =
                    fileUploadService.uploadFile(imageFile);

            brand.setImageUrl(imagePath);
        }

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
            ) MultipartFile imageFile) throws IOException {

        Brand existingBrand =
                brandService.findById(brand.getId());


        // If new image is selected
        if (imageFile != null && !imageFile.isEmpty()) {

            // Delete old image
            fileUploadService.deleteFile(
                    existingBrand.getImageUrl()
            );

            // Upload new image
            String imagePath =
                    fileUploadService.uploadFile(imageFile);

            brand.setImageUrl(imagePath);

        } else {

            // Keep existing image
            brand.setImageUrl(
                    existingBrand.getImageUrl()
            );
        }


        brandService.updateBrand(
                brand.getId(),
                brand
        );

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


        if (brand != null) {

            fileUploadService.deleteFile(
                    brand.getImageUrl()
            );
        }


        brandService.deleteBrand(id);

        return "redirect:/admin/brands";
    }
}