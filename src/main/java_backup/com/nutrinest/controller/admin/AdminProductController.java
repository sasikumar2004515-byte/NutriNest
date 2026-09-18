package com.nutrinest.controller.admin;

import com.nutrinest.entity.Category;
import com.nutrinest.entity.Product;
import com.nutrinest.service.CategoryService;
import com.nutrinest.service.FileUploadService;
import com.nutrinest.service.ProductService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

@Controller
@RequestMapping("/admin/products")
public class AdminProductController {

    private final ProductService productService;
    private final CategoryService categoryService;
    private final FileUploadService fileUploadService;

    public AdminProductController(ProductService productService,
                                  CategoryService categoryService,
                                  FileUploadService fileUploadService) {
        this.productService = productService;
        this.categoryService = categoryService;
        this.fileUploadService = fileUploadService;
    }

    @GetMapping
    public String productList(Model model) {
        model.addAttribute("products", productService.getAllProducts());
        return "admin/products";
    }

    @GetMapping("/new")
    public String showAddProductForm(Model model) {
        model.addAttribute("product", new Product());
        model.addAttribute("categories", categoryService.findAll());
        return "admin/add-product";
    }

    @PostMapping("/save")
    public String saveProduct(@ModelAttribute Product product,
                              @RequestParam("category") Long categoryId,
                              @RequestParam("imageFile") MultipartFile imageFile)
            throws IOException {

        Category category = categoryService.findById(categoryId);
        product.setCategory(category);

        if (imageFile != null && !imageFile.isEmpty()) {
            String imagePath = fileUploadService.uploadFile(imageFile);
            product.setImageUrl(imagePath);
        }

        productService.saveProduct(product);

        return "redirect:/admin/products";
    }

    @GetMapping("/edit/{id}")
    public String editProduct(@PathVariable Long id, Model model) {

        Product product = productService.getProductById(id)
                .orElseThrow(() -> new RuntimeException("Product Not Found"));

        model.addAttribute("product", product);
        model.addAttribute("categories", categoryService.findAll());

        return "admin/edit-product";
    }

    @PostMapping("/update/{id}")
    public String updateProduct(@PathVariable Long id,
                                @ModelAttribute Product product,
                                @RequestParam("category") Long categoryId,
                                @RequestParam("imageFile") MultipartFile imageFile)
            throws IOException {

        Category category = categoryService.findById(categoryId);
        product.setCategory(category);

        // Existing product fetch
        Product existingProduct = productService.getProductById(id)
                .orElseThrow(() -> new RuntimeException("Product Not Found"));

        // New image selected
        if (imageFile != null && !imageFile.isEmpty()) {
            String imagePath = fileUploadService.uploadFile(imageFile);
            product.setImageUrl(imagePath);
        } else {
            // Keep old image
            product.setImageUrl(existingProduct.getImageUrl());
        }

        productService.updateProduct(id, product);

        return "redirect:/admin/products";
    }

    @GetMapping("/delete/{id}")
    public String deleteProduct(@PathVariable Long id) {

        Product product = productService.getProductById(id)
                .orElseThrow(() -> new RuntimeException("Product Not Found"));

        fileUploadService.deleteFile(product.getImageUrl());

        productService.deleteProduct(id);

        return "redirect:/admin/products";
    }


}