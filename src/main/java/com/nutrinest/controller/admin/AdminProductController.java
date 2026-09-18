package com.nutrinest.controller.admin;

import com.nutrinest.entity.Category;
import com.nutrinest.entity.Product;
import com.nutrinest.entity.ProductVariant;
import com.nutrinest.service.CategoryService;
import com.nutrinest.service.FileUploadService;
import com.nutrinest.service.ProductService;
import com.nutrinest.service.ProductVariantService;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

@Controller
@RequestMapping("/admin/products")
public class AdminProductController {

    private final ProductService productService;
    private final CategoryService categoryService;
    private final FileUploadService fileUploadService;
    private final ProductVariantService productVariantService;


    // =========================================================
    // CONSTRUCTOR
    // =========================================================

    public AdminProductController(
            ProductService productService,
            CategoryService categoryService,
            FileUploadService fileUploadService,
            ProductVariantService productVariantService) {

        this.productService = productService;
        this.categoryService = categoryService;
        this.fileUploadService = fileUploadService;
        this.productVariantService = productVariantService;
    }


    // =========================================================
    // PRODUCT LIST
    // =========================================================

    @GetMapping
    public String productList(Model model) {

        model.addAttribute(
                "products",
                productService.getAllProducts()
        );

        return "admin/products";
    }


    // =========================================================
    // ADD PRODUCT PAGE
    // =========================================================

    @GetMapping("/new")
    public String showAddProductForm(Model model) {

        model.addAttribute(
                "product",
                new Product()
        );

        model.addAttribute(
                "categories",
                categoryService.findAll()
        );

        return "admin/add-product";
    }


    // =========================================================
    // SAVE PRODUCT
    // =========================================================

    @PostMapping("/save")
    public String saveProduct(
            @ModelAttribute Product product,

            @RequestParam("category")
            Long categoryId,

            @RequestParam(
                    value = "imageFile",
                    required = false
            )
            MultipartFile imageFile,

            @RequestParam("variantWeights")
            List<String> variantWeights,

            @RequestParam("variantPrices")
            List<Double> variantPrices,

            @RequestParam(
                    value = "variantMrps",
                    required = false
            )
            List<Double> variantMrps,

            @RequestParam("variantStocks")
            List<Integer> variantStocks)

            throws IOException {


        // =====================================================
        // VALIDATE VARIANTS
        // =====================================================

        if (variantWeights == null
                || variantPrices == null
                || variantStocks == null
                || variantWeights.isEmpty()
                || variantPrices.isEmpty()
                || variantStocks.isEmpty()) {

            throw new IllegalArgumentException(
                    "At least one product variant is required"
            );
        }


        int variantCount = Math.min(
                variantWeights.size(),
                Math.min(
                        variantPrices.size(),
                        variantStocks.size()
                )
        );


        if (variantCount == 0) {

            throw new IllegalArgumentException(
                    "At least one valid product variant is required"
            );
        }


        // =====================================================
        // FIRST VARIANT
        // =====================================================

        String firstWeight =
                variantWeights.get(0);

        Double firstPrice =
                variantPrices.get(0);

        Integer firstStock =
                variantStocks.get(0);


        Double firstMrp = null;

        if (variantMrps != null
                && !variantMrps.isEmpty()) {

            firstMrp =
                    variantMrps.get(0);
        }


        // =====================================================
        // VALIDATE FIRST VARIANT
        // =====================================================

        if (firstWeight == null
                || firstWeight.trim().isEmpty()
                || firstPrice == null
                || firstPrice < 0
                || firstStock == null
                || firstStock < 0) {

            throw new IllegalArgumentException(
                    "First product variant is invalid"
            );
        }


        if (firstMrp != null
                && firstMrp < 0) {

            throw new IllegalArgumentException(
                    "First product variant MRP is invalid"
            );
        }


        if (firstMrp != null
                && firstMrp < firstPrice) {

            throw new IllegalArgumentException(
                    "MRP cannot be lower than selling price"
            );
        }


        // =====================================================
        // CATEGORY
        // =====================================================

        Category category =
                categoryService.findById(categoryId);

        product.setCategory(category);


        // =====================================================
        // PRODUCT BASE VALUES
        // =====================================================

        product.setWeight(
                firstWeight.trim()
        );

        product.setPrice(
                firstPrice
        );

        product.setStock(
                firstStock
        );


        // =====================================================
        // PRODUCT MRP
        // =====================================================

        /*
         * Keep Product.mrp synchronized with
         * the first variant MRP.
         *
         * The actual variant MRP is stored inside
         * ProductVariant.mrp.
         */

        product.setMrp(
                firstMrp
        );


        // =====================================================
        // IMAGE
        // =====================================================

        if (imageFile != null
                && !imageFile.isEmpty()) {

            String imagePath =
                    fileUploadService.uploadFile(
                            imageFile
                    );

            product.setImageUrl(
                    imagePath
            );
        }


        // =====================================================
        // SAVE PRODUCT FIRST
        // =====================================================

        Product savedProduct =
                productService.saveProduct(
                        product
                );


        // =====================================================
        // SAVE ALL VARIANTS
        // =====================================================

        for (int i = 0; i < variantCount; i++) {

            String weight =
                    variantWeights.get(i);

            Double price =
                    variantPrices.get(i);

            Integer stock =
                    variantStocks.get(i);


            Double mrp = null;

            if (variantMrps != null
                    && i < variantMrps.size()) {

                mrp =
                        variantMrps.get(i);
            }


            // =================================================
            // VALIDATE VARIANT
            // =================================================

            if (weight == null
                    || weight.trim().isEmpty()
                    || price == null
                    || price < 0
                    || stock == null
                    || stock < 0) {

                continue;
            }


            if (mrp != null
                    && mrp < 0) {

                continue;
            }


            if (mrp != null
                    && mrp < price) {

                throw new IllegalArgumentException(
                        "MRP cannot be lower than selling price for "
                                + weight
                );
            }


            // =================================================
            // CREATE VARIANT
            // =================================================

            ProductVariant variant =
                    new ProductVariant();

            variant.setProduct(
                    savedProduct
            );

            variant.setWeight(
                    weight.trim()
            );

            variant.setPrice(
                    price
            );

            variant.setMrp(
                    mrp
            );

            variant.setStock(
                    stock
            );


            // =================================================
            // SAVE VARIANT
            // =================================================

            productVariantService.save(
                    variant
            );
        }


        return "redirect:/admin/products";
    }


    // =========================================================
    // EDIT PRODUCT PAGE
    // =========================================================

    @GetMapping("/edit/{id}")
    public String editProduct(
            @PathVariable Long id,
            Model model) {

        Product product =
                productService.getProductById(id)
                        .orElseThrow(
                                () -> new RuntimeException(
                                        "Product Not Found"
                                )
                        );


        // =====================================================
        // LOAD VARIANTS
        // =====================================================

        List<ProductVariant> variants =
                productVariantService.getVariants(
                        product
                );


        // =====================================================
        // MODEL
        // =====================================================

        model.addAttribute(
                "product",
                product
        );

        model.addAttribute(
                "variants",
                variants
        );

        model.addAttribute(
                "categories",
                categoryService.findAll()
        );


        return "admin/edit-product";
    }


    // =========================================================
    // UPDATE PRODUCT
    // =========================================================

    @PostMapping("/update/{id}")
    public String updateProduct(
            @PathVariable Long id,

            @ModelAttribute Product product,

            @RequestParam("category")
            Long categoryId,

            @RequestParam(
                    value = "variantWeights",
                    required = false
            )
            List<String> variantWeights,

            @RequestParam(
                    value = "variantPrices",
                    required = false
            )
            List<Double> variantPrices,

            @RequestParam(
                    value = "variantMrps",
                    required = false
            )
            List<Double> variantMrps,

            @RequestParam(
                    value = "variantStocks",
                    required = false
            )
            List<Integer> variantStocks,

            @RequestParam(
                    value = "imageFile",
                    required = false
            )
            MultipartFile imageFile)

            throws IOException {


        // =====================================================
        // LOAD EXISTING PRODUCT
        // =====================================================

        Product existingProduct =
                productService.getProductById(id)
                        .orElseThrow(
                                () -> new RuntimeException(
                                        "Product Not Found"
                                )
                        );


        // =====================================================
        // CATEGORY
        // =====================================================

        Category category =
                categoryService.findById(categoryId);

        existingProduct.setCategory(
                category
        );


        // =====================================================
        // BASIC PRODUCT DETAILS
        // =====================================================

        existingProduct.setProductName(
                product.getProductName()
        );

        existingProduct.setDescription(
                product.getDescription()
        );


        // =====================================================
        // IMAGE
        // =====================================================

        String oldImagePath =
                existingProduct.getImageUrl();

        String newImagePath = null;


        if (imageFile != null
                && !imageFile.isEmpty()) {

            newImagePath =
                    fileUploadService.uploadFile(
                            imageFile
                    );

            existingProduct.setImageUrl(
                    newImagePath
            );

        } else {

            existingProduct.setImageUrl(
                    oldImagePath
            );
        }


        // =====================================================
        // LOAD EXISTING VARIANTS
        // =====================================================

        List<ProductVariant> existingVariants =
                productVariantService.getVariants(
                        existingProduct
                );


        // =====================================================
        // VALIDATE VARIANT DATA
        // =====================================================

        if (existingVariants != null
                && !existingVariants.isEmpty()) {

            if (variantWeights == null
                    || variantPrices == null
                    || variantStocks == null) {

                throw new IllegalArgumentException(
                        "Variant information is missing"
                );
            }


            if (variantWeights.size()
                    != existingVariants.size()
                    || variantPrices.size()
                    != existingVariants.size()
                    || variantStocks.size()
                    != existingVariants.size()) {

                throw new IllegalArgumentException(
                        "Variant information is incomplete"
                );
            }
        }


        // =====================================================
        // UPDATE VARIANTS
        // =====================================================

        Double firstPrice = null;
        Double firstMrp = null;
        String firstWeight = null;
        Integer totalStock = 0;


        for (int i = 0;
             i < existingVariants.size();
             i++) {

            ProductVariant variant =
                    existingVariants.get(i);


            String weight =
                    variantWeights.get(i);

            Double price =
                    variantPrices.get(i);

            Integer stock =
                    variantStocks.get(i);


            Double mrp = null;

            if (variantMrps != null
                    && i < variantMrps.size()) {

                mrp =
                        variantMrps.get(i);
            }


            // =================================================
            // VALIDATE
            // =================================================

            if (weight == null
                    || weight.trim().isEmpty()) {

                throw new IllegalArgumentException(
                        "Variant weight cannot be empty"
                );
            }


            if (price == null
                    || price < 0) {

                throw new IllegalArgumentException(
                        "Variant selling price is invalid"
                );
            }


            if (stock == null
                    || stock < 0) {

                throw new IllegalArgumentException(
                        "Variant stock is invalid"
                );
            }


            if (mrp != null
                    && mrp < 0) {

                throw new IllegalArgumentException(
                        "Variant MRP is invalid"
                );
            }


            if (mrp != null
                    && mrp < price) {

                throw new IllegalArgumentException(
                        "MRP cannot be lower than selling price for "
                                + weight
                );
            }


            // =================================================
            // UPDATE VARIANT
            // =================================================

            variant.setProduct(
                    existingProduct
            );

            variant.setWeight(
                    weight.trim()
            );

            variant.setPrice(
                    price
            );

            variant.setMrp(
                    mrp
            );

            variant.setStock(
                    stock
            );


            // =================================================
            // SAVE VARIANT
            // =================================================

            productVariantService.save(
                    variant
            );


            // =================================================
            // FIRST VARIANT → PRODUCT BASE VALUES
            // =================================================

            if (i == 0) {

                firstWeight =
                        weight.trim();

                firstPrice =
                        price;

                firstMrp =
                        mrp;
            }


            totalStock += stock;
        }


        // =====================================================
        // SYNCHRONIZE PRODUCT BASE VALUES
        // =====================================================

        if (firstWeight != null) {

            existingProduct.setWeight(
                    firstWeight
            );
        }


        if (firstPrice != null) {

            existingProduct.setPrice(
                    firstPrice
            );
        }


        existingProduct.setMrp(
                firstMrp
        );


        existingProduct.setStock(
                totalStock
        );


        // =====================================================
        // UPDATE PRODUCT
        // =====================================================

        try {

            productService.updateProduct(
                    id,
                    existingProduct
            );

        } catch (RuntimeException ex) {

            // Remove newly uploaded image
            // if database update fails.

            if (newImagePath != null) {

                fileUploadService.deleteFile(
                        newImagePath
                );
            }

            throw ex;
        }


        // =====================================================
        // DELETE OLD IMAGE
        // =====================================================

        if (newImagePath != null
                && oldImagePath != null
                && !oldImagePath.isBlank()
                && !oldImagePath.equals(newImagePath)) {

            fileUploadService.deleteFile(
                    oldImagePath
            );
        }


        return "redirect:/admin/products";
    }


    // =========================================================
    // DELETE PRODUCT
    // =========================================================

    @GetMapping("/delete/{id}")
    public String deleteProduct(
            @PathVariable Long id) {

        productService.getProductById(id)
                .orElseThrow(
                        () -> new RuntimeException(
                                "Product Not Found"
                        )
                );


        /*
         * SOFT DELETE
         *
         * active = false
         *
         * Product record and image remain.
         */

        productService.deleteProduct(
                id
        );


        return "redirect:/admin/products";
    }
}