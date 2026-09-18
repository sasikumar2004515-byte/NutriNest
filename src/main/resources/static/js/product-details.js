// ============================================================
// PRODUCT DETAILS
// VARIANT + QUANTITY + CART + BUY NOW
// ============================================================

document.addEventListener("DOMContentLoaded", function () {

    // ========================================================
    // BASIC ELEMENTS
    // ========================================================

    const decreaseBtn =
        document.getElementById("decreaseQuantity");

    const increaseBtn =
        document.getElementById("increaseQuantity");

    const quantityValue =
        document.getElementById("quantityValue");

    const mobileTotal =
        document.getElementById("mobileTotal");

    const buyNowBtn =
        document.getElementById("buyNowButton");

    const mobileBuyNowBtn =
        document.getElementById("mobileBuyNow");

    const cartQuantity =
        document.getElementById("cartQuantity");

    const mobileCartQuantity =
        document.getElementById("mobileCartQuantity");


    // ========================================================
    // VARIANT ELEMENTS
    // ========================================================

    /*
     * IMPORTANT
     * ----------
     * Product details page now uses:
     *
     * <select id="variantSelect">
     *
     * instead of multiple .pack-option buttons.
     */

    const variantSelect =
        document.getElementById("variantSelect");

    const selectedVariantId =
        document.getElementById("selectedVariantId");

    const selectedVariantPrice =
        document.getElementById("selectedVariantPrice");

    const selectedVariantStock =
        document.getElementById("selectedVariantStock");

    const selectedWeight =
        document.getElementById("selectedWeight");

    const cartVariantId =
        document.getElementById("cartVariantId");

    const mobileCartVariantId =
        document.getElementById("mobileCartVariantId");

    const currentPriceElement =
        document.querySelector(".current-price");


    // ========================================================
    // STATE
    // ========================================================

    let qty = 1;

    let unitPrice = 0;

    let maxStock = 0;

    let currentVariantId = null;


    // ========================================================
    // FORMAT PRICE
    // ========================================================

    function formatPrice(price) {

        return "₹" +
            Number(price || 0).toLocaleString(
                "en-IN",
                {
                    maximumFractionDigits: 0
                }
            );
    }


    // ========================================================
    // READ INITIAL PRICE
    // ========================================================

    if (currentPriceElement) {

        unitPrice =
            parseFloat(
                currentPriceElement.textContent
                    .replace(/[^\d.]/g, "")
            ) || 0;
    }


    // ========================================================
    // UPDATE PRICE
    // ========================================================

    function updatePrice(price) {

        unitPrice =
            Number(price) || 0;


        // Main product price

        if (currentPriceElement) {

            currentPriceElement.textContent =
                formatPrice(unitPrice);
        }


        // Optional selected variant price

        if (selectedVariantPrice) {

            selectedVariantPrice.textContent =
                formatPrice(unitPrice);
        }


        updateMobileTotal();
    }


    // ========================================================
    // UPDATE MOBILE TOTAL
    // ========================================================

    function updateMobileTotal() {

        if (mobileTotal) {

            mobileTotal.textContent =
                formatPrice(
                    unitPrice * qty
                );
        }
    }


    // ========================================================
    // UPDATE VARIANT IDS
    // ========================================================

    function updateVariantIds(variantId) {

        const value =
            variantId
                ? String(variantId)
                : "";


        if (selectedVariantId) {

            selectedVariantId.value =
                value;
        }


        if (cartVariantId) {

            cartVariantId.value =
                value;
        }


        if (mobileCartVariantId) {

            mobileCartVariantId.value =
                value;
        }
    }


    // ========================================================
    // UPDATE STOCK
    // ========================================================

    function updateStock(stock) {

        const parsedStock =
            parseInt(stock, 10);


        maxStock =
            Number.isFinite(parsedStock)
                && parsedStock > 0
                ? parsedStock
                : 0;


        // ====================================================
        // CURRENT QUANTITY VS STOCK
        // ====================================================

        if (
            maxStock > 0 &&
            qty > maxStock
        ) {

            qty = maxStock;
        }


        // ====================================================
        // OUT OF STOCK
        // ====================================================

        if (maxStock <= 0) {

            qty = 1;
        }


        // ====================================================
        // PLUS BUTTON
        // ====================================================

        if (increaseBtn) {

            increaseBtn.dataset.stock =
                String(maxStock);

            increaseBtn.disabled =
                maxStock <= 0 ||
                qty >= maxStock;
        }


        // ====================================================
        // MINUS BUTTON
        // ====================================================

        if (decreaseBtn) {

            decreaseBtn.disabled =
                qty <= 1;
        }


        // ====================================================
        // STOCK TEXT
        // ====================================================

        if (selectedVariantStock) {

            if (maxStock > 0) {

                selectedVariantStock.textContent =
                    maxStock +
                    " units available";

            } else {

                selectedVariantStock.textContent =
                    "Out of Stock";
            }
        }
    }


    // ========================================================
    // UPDATE QUANTITY
    // ========================================================

    function updateQuantity() {

        // ====================================================
        // DESKTOP QUANTITY TEXT
        // ====================================================

        if (quantityValue) {

            quantityValue.textContent =
                String(qty);
        }


        // ====================================================
        // DESKTOP CART QUANTITY
        // ====================================================

        if (cartQuantity) {

            cartQuantity.value =
                String(qty);
        }


        // ====================================================
        // MOBILE CART QUANTITY
        // ====================================================

        if (mobileCartQuantity) {

            mobileCartQuantity.value =
                String(qty);
        }


        // ====================================================
        // MOBILE TOTAL
        // ====================================================

        updateMobileTotal();


        // ====================================================
        // PLUS BUTTON
        // ====================================================

        if (increaseBtn) {

            increaseBtn.disabled =
                maxStock <= 0 ||
                qty >= maxStock;
        }


        // ====================================================
        // MINUS BUTTON
        // ====================================================

        if (decreaseBtn) {

            decreaseBtn.disabled =
                qty <= 1;
        }
    }


    // ========================================================
    // SELECT VARIANT FROM DROPDOWN
    // ========================================================

    function selectVariant(option) {

        if (!option) {
            return;
        }


        // ====================================================
        // READ DATA ATTRIBUTES
        // ====================================================

        const variantId =
            option.dataset.variantId || "";


        const variantPrice =
            option.dataset.price || "0";


        const variantStock =
            option.dataset.stock || "0";


        const variantWeight =
            option.dataset.weight || "";


        // ====================================================
        // VARIANT PRODUCT
        // ====================================================

        if (variantId) {

            currentVariantId =
                String(variantId);

        } else {

            currentVariantId = null;
        }


        // ====================================================
        // UPDATE ALL HIDDEN VARIANT IDS
        // ====================================================

        updateVariantIds(
            currentVariantId
        );


        // ====================================================
        // UPDATE PRICE
        // ====================================================

        updatePrice(
            parseFloat(
                variantPrice
            ) || 0
        );


        // ====================================================
        // UPDATE STOCK
        // ====================================================

        updateStock(
            variantStock
        );


        // ====================================================
        // UPDATE SELECTED WEIGHT
        // ====================================================

        if (
            selectedWeight &&
            variantWeight
        ) {

            selectedWeight.textContent =
                variantWeight;
        }


        // ====================================================
        // RESET QUANTITY
        // ====================================================

        qty = 1;

        updateQuantity();


        // ====================================================
        // STORE CURRENT SELECTION
        // ====================================================

        if (variantSelect) {

            variantSelect.dataset.selectedVariantId =
                currentVariantId || "";
        }
    }


    // ========================================================
    // DROPDOWN CHANGE EVENT
    // ========================================================

    if (variantSelect) {

        variantSelect.addEventListener(
            "change",
            function () {

                const option =
                    this.options[
                        this.selectedIndex
                    ];


                selectVariant(option);
            }
        );
    }


    // ========================================================
    // INITIAL VARIANT
    // ========================================================

    if (variantSelect) {

        const selectedOption =
            variantSelect.options[
                variantSelect.selectedIndex
            ];


        if (selectedOption) {

            selectVariant(
                selectedOption
            );
        }

    } else {

        // ====================================================
        // PRODUCT WITHOUT VARIANT SELECT
        // ====================================================

        currentVariantId = null;

        updateVariantIds("");


        const productStock =
            increaseBtn
                ? parseInt(
                    increaseBtn.dataset.stock || "0",
                    10
                )
                : 0;


        maxStock =
            Number.isFinite(productStock)
                ? Math.max(
                    productStock,
                    0
                )
                : 0;


        qty = 1;

        updateQuantity();
    }


    // ========================================================
    // DECREASE QUANTITY
    // ========================================================

    if (decreaseBtn) {

        decreaseBtn.addEventListener(
            "click",
            function () {

                if (qty > 1) {

                    qty--;

                    updateQuantity();
                }
            }
        );
    }


    // ========================================================
    // INCREASE QUANTITY
    // ========================================================

    if (increaseBtn) {

        increaseBtn.addEventListener(
            "click",
            function () {

                if (
                    maxStock > 0 &&
                    qty < maxStock
                ) {

                    qty++;

                    updateQuantity();
                }
            }
        );
    }


    // ========================================================
    // ADD TO CART - KEEP VARIANT ID SYNCHRONIZED
    // ========================================================

    const cartForm =
        document.querySelector(
            ".cart-form"
        );


    if (cartForm) {

        cartForm.addEventListener(
            "submit",
            function (event) {

                // --------------------------------------------
                // Update quantity
                // --------------------------------------------

                if (cartQuantity) {

                    cartQuantity.value =
                        String(qty);
                }


                // --------------------------------------------
                // Update variant ID
                // --------------------------------------------

                if (
                    cartVariantId &&
                    currentVariantId
                ) {

                    cartVariantId.value =
                        currentVariantId;
                }


                // --------------------------------------------
                // Variant validation
                // --------------------------------------------

                if (
                    variantSelect &&
                    variantSelect.options.length > 0
                ) {

                    const selectedOption =
                        variantSelect.options[
                            variantSelect.selectedIndex
                        ];


                    if (
                        selectedOption &&
                        selectedOption.dataset.variantId
                    ) {

                        currentVariantId =
                            String(
                                selectedOption
                                    .dataset
                                    .variantId
                            );


                        updateVariantIds(
                            currentVariantId
                        );
                    }
                }


                // --------------------------------------------
                // Stock validation
                // --------------------------------------------

                if (maxStock <= 0) {

                    event.preventDefault();

                    alert(
                        "Selected variant is out of stock."
                    );

                    return;
                }


                if (qty > maxStock) {

                    event.preventDefault();

                    alert(
                        "Selected quantity is not available."
                    );

                    return;
                }
            }
        );
    }


    // ========================================================
    // BUY NOW
    // ========================================================

    function handleBuyNow() {

        const productId =
            buyNowBtn
                ? buyNowBtn.dataset.productId
                : null;


        // ====================================================
        // PRODUCT ID VALIDATION
        // ====================================================

        if (!productId) {

            console.error(
                "Product ID not found."
            );

            return;
        }


        // ====================================================
        // GET CURRENT DROPDOWN VARIANT
        // ====================================================

        if (variantSelect) {

            const selectedOption =
                variantSelect.options[
                    variantSelect.selectedIndex
                ];


            if (selectedOption) {

                const variantId =
                    selectedOption.dataset.variantId;


                if (variantId) {

                    currentVariantId =
                        String(variantId);

                    updateVariantIds(
                        currentVariantId
                    );
                }
            }
        }


        // ====================================================
        // VARIANT VALIDATION
        // ====================================================

        if (
            variantSelect &&
            variantSelect.options.length > 0 &&
            !currentVariantId
        ) {

            alert(
                "Please select a pack size."
            );

            return;
        }


        // ====================================================
        // STOCK VALIDATION
        // ====================================================

        if (maxStock <= 0) {

            alert(
                "Selected variant is out of stock."
            );

            return;
        }


        if (qty > maxStock) {

            alert(
                "Selected quantity is not available."
            );

            return;
        }


        // ====================================================
        // BUILD CHECKOUT URL
        // ====================================================

        let checkoutUrl =
            "/checkout/buy-now/" +
            encodeURIComponent(
                productId
            ) +
            "?quantity=" +
            encodeURIComponent(
                qty
            );


        // ====================================================
        // PASS VARIANT ID
        // ====================================================

        if (currentVariantId) {

            checkoutUrl +=
                "&variantId=" +
                encodeURIComponent(
                    currentVariantId
                );
        }


        // ====================================================
        // GO TO CHECKOUT
        // ====================================================

        window.location.href =
            checkoutUrl;
    }


    // ========================================================
    // DESKTOP BUY NOW
    // ========================================================

    if (buyNowBtn) {

        buyNowBtn.addEventListener(
            "click",
            handleBuyNow
        );
    }


    // ========================================================
    // MOBILE BUY NOW
    // ========================================================

    if (mobileBuyNowBtn) {

        mobileBuyNowBtn.addEventListener(
            "click",
            handleBuyNow
        );
    }


    // ========================================================
    // INITIAL TOTAL
    // ========================================================

    updateQuantity();

});


// ============================================================
// IMAGE CHANGE
// ============================================================

function changeImage(img) {

    const mainImage =
        document.getElementById(
            "productImage"
        );


    if (
        mainImage &&
        img
    ) {

        mainImage.src =
            img.src;
    }


    document
        .querySelectorAll(".thumb")
        .forEach(
            function (image) {

                image.classList.remove(
                    "active"
                );
            }
        );


    if (img) {

        img.classList.add(
            "active"
        );
    }
}