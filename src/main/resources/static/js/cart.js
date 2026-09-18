// =========================================================
// CART PAGE JAVASCRIPT
// VARIANT + CSRF + UI
// =========================================================


// =========================================================
// CSRF HELPER
// =========================================================

function getCsrfHeaders() {

    let token = null;
    let header = null;


    // =====================================================
    // METHOD 1: META TAG
    // =====================================================

    const tokenMeta =
        document.querySelector(
            'meta[name="_csrf"]'
        );

    const headerMeta =
        document.querySelector(
            'meta[name="_csrf_header"]'
        );


    if (tokenMeta) {

        token =
            tokenMeta.getAttribute("content");
    }


    if (headerMeta) {

        header =
            headerMeta.getAttribute("content");
    }


    // =====================================================
    // METHOD 2: HIDDEN CSRF INPUT
    // =====================================================

    if (!token) {

        const csrfInput =
            document.querySelector(
                'input[name="_csrf"]'
            );


        if (csrfInput) {

            token =
                csrfInput.value;
        }
    }


    // =====================================================
    // DEFAULT SPRING SECURITY HEADER
    // =====================================================

    if (!header) {

        header =
            "X-CSRF-TOKEN";
    }


    const headers = {
        "X-Requested-With": "XMLHttpRequest"
    };


    // =====================================================
    // ADD CSRF TOKEN
    // =====================================================

    if (token) {

        headers[header] =
            token;
    }


    return headers;
}


// =========================================================
// DOM READY
// =========================================================

document.addEventListener(
    "DOMContentLoaded",
    function () {


        // =====================================================
        // COUPON
        // =====================================================

        const couponBtn =
            document.querySelector(
                ".cart-page .coupon-input button"
            );


        const couponInput =
            document.querySelector(
                ".cart-page .coupon-input input"
            );


        if (couponBtn) {

            couponBtn.addEventListener(
                "click",
                function (event) {

                    event.preventDefault();


                    if (!couponInput) {
                        return;
                    }


                    const coupon =
                        couponInput.value.trim();


                    if (coupon === "") {

                        alert(
                            "Please enter a coupon code."
                        );

                        couponInput.focus();

                        return;
                    }


                    alert(
                        "Coupon feature will be connected with backend."
                    );
                }
            );
        }


        // =====================================================
        // WISHLIST
        // =====================================================

        const wishlistButtons =
            document.querySelectorAll(
                ".cart-page .wishlist-btn"
            );


        wishlistButtons.forEach(
            function (button) {

                button.addEventListener(
                    "click",
                    function () {

                        this.classList.toggle(
                            "active"
                        );


                        if (
                            this.classList.contains(
                                "active"
                            )
                        ) {

                            this.innerHTML =
                                '<i class="fa-solid fa-heart"></i> Saved';

                        } else {

                            this.innerHTML =
                                '<i class="fa-regular fa-heart"></i> Save For Later';
                        }
                    }
                );
            }
        );


        // =====================================================
        // RECOMMENDED PRODUCTS
        // =====================================================

        const recommendButtons =
            document.querySelectorAll(
                ".cart-page .recommend-btn"
            );


        recommendButtons.forEach(
            function (button) {

                button.addEventListener(
                    "click",
                    function (event) {

                        event.preventDefault();


                        alert(
                            "This feature will be connected with backend."
                        );
                    }
                );
            }
        );


        // =====================================================
        // CART CARD ENTRANCE ANIMATION
        // =====================================================

        const cartCards =
            document.querySelectorAll(
                ".cart-page .cart-card"
            );


        cartCards.forEach(
            function (card, index) {

                card.style.opacity = "0";

                card.style.transform =
                    "translateY(20px)";


                setTimeout(
                    function () {

                        card.style.transition =
                            "opacity 0.45s ease, transform 0.45s ease";

                        card.style.opacity = "1";

                        card.style.transform =
                            "translateY(0)";

                    },
                    index * 100
                );
            }
        );


        // =====================================================
        // DELETE BUTTON ANIMATION
        // =====================================================

        const deleteButtons =
            document.querySelectorAll(
                ".cart-page .delete-btn"
            );


        deleteButtons.forEach(
            function (button) {

                button.addEventListener(
                    "click",
                    function () {

                        const card =
                            this.closest(
                                ".cart-card"
                            );


                        if (!card) {
                            return;
                        }


                        card.style.transition =
                            "opacity 0.3s ease, transform 0.3s ease";


                        card.style.opacity = "0";

                        card.style.transform =
                            "translateX(-50px)";
                    }
                );
            }
        );


        // =====================================================
        // FREE DELIVERY
        // =====================================================

        updateDeliveryProgress();

    }
);


// =========================================================
// CHANGE WEIGHT / VARIANT
// =========================================================

function changeWeight(select) {

    if (!select) {
        return;
    }


    // =====================================================
    // CART ID
    // =====================================================

    const cartId =
        select.dataset.cartId;


    // =====================================================
    // SELECTED OPTION
    // =====================================================

    const selectedOption =
        select.options[
            select.selectedIndex
        ];


    if (!cartId || !selectedOption) {

        console.error(
            "Cart ID or selected option is missing."
        );

        return;
    }


    // =====================================================
    // VARIANT ID
    // =====================================================

    const variantId =
        selectedOption.dataset.variantId;


    console.log(
        "Cart ID:",
        cartId
    );


    console.log(
        "Selected Variant ID:",
        variantId
    );


    console.log(
        "Selected Weight:",
        select.value
    );


    // =====================================================
    // LEGACY FALLBACK
    // =====================================================

    if (!variantId) {

        const weight =
            select.value;


        if (!weight) {
            return;
        }


        changeWeightLegacy(
            cartId,
            weight,
            select
        );

        return;
    }


    // =====================================================
    // CSRF
    // =====================================================

    const csrfHeaders =
        getCsrfHeaders();


    // =====================================================
    // DISABLE SELECT
    // =====================================================

    select.disabled = true;


    // =====================================================
    // SEND VARIANT REQUEST
    // =====================================================

    fetch(
        "/cart/change-variant",
        {
            method: "POST",

            credentials: "same-origin",

            headers: {
                ...csrfHeaders,

                "Content-Type":
                    "application/x-www-form-urlencoded; charset=UTF-8"
            },

            body:
                "cartId=" +
                encodeURIComponent(cartId) +

                "&variantId=" +
                encodeURIComponent(variantId)
        }
    )


    // =====================================================
    // RESPONSE
    // =====================================================

    .then(
        async function (response) {

            const result =
                (
                    await response.text()
                ).trim();


            console.log(
                "Variant HTTP status:",
                response.status
            );


            console.log(
                "Variant response:",
                result
            );


            // =================================================
            // 401
            // =================================================

            if (
                response.status === 401
            ) {

                throw new Error(
                    "Your login session has expired. Please login again."
                );
            }


            // =================================================
            // 403
            // =================================================

            if (
                response.status === 403
            ) {

                throw new Error(
                    "CSRF validation failed. Please refresh the page and try again."
                );
            }


            // =================================================
            // OTHER HTTP ERROR
            // =================================================

            if (!response.ok) {

                throw new Error(
                    result ||
                    "Failed to update product variant."
                );
            }


            // =================================================
            // PARSE JSON
            // =================================================

            let data;

            try {

                data =
                    JSON.parse(result);

            } catch (error) {

                console.error(
                    "JSON parse error:",
                    error
                );

                throw new Error(
                    "Invalid server response."
                );
            }


            return data;
        }
    )


    // =====================================================
    // RESULT
    // =====================================================

    .then(
        function (data) {

            // =================================================
            // LOGIN
            // =================================================

            if (
                data.status === "LOGIN"
            ) {

                throw new Error(
                    "Your login session has expired."
                );
            }


            // =================================================
            // ERROR
            // =================================================

            if (
                data.status !== "SUCCESS"
            ) {

                throw new Error(
                    data.message ||
                    "Unable to update variant."
                );
            }


            // =================================================
            // CURRENT CART CARD
            // =================================================

            const card =
                select.closest(
                    ".cart-card"
                );


            if (!card) {

                console.error(
                    "Cart card not found."
                );

                select.disabled = false;

                return;
            }


            // =================================================
            // NEW UNIT PRICE
            // =================================================

            const priceElement =
                card.querySelector(
                    ".new-price"
                );


            const sellingPrice =
                Number(
                    data.price || 0
                );


            if (priceElement) {

                priceElement.textContent =
                    "₹" +
                    sellingPrice.toLocaleString(
                        "en-IN",
                        {
                            maximumFractionDigits: 0
                        }
                    );
            }


            // =================================================
            // UPDATE MRP
            // =================================================

            const mrpElement =
                card.querySelector(
                    ".mrp-price"
                );


            const mrp =
                Number(
                    data.mrp || 0
                );


            if (mrpElement) {

                mrpElement.textContent =
                    mrp.toLocaleString(
                        "en-IN",
                        {
                            maximumFractionDigits: 0
                        }
                    );
            }


            // =================================================
            // UPDATE DISCOUNT
            // =================================================

            const discountElement =
                card.querySelector(
                    ".discount"
                );


            if (discountElement) {

                if (
                    mrp > 0 &&
                    sellingPrice > 0 &&
                    mrp > sellingPrice
                ) {

                    const discount =
                        Math.round(
                            (
                                (mrp - sellingPrice) /
                                mrp
                            ) * 100
                        );


                    discountElement.textContent =
                        discount + "% OFF";


                    discountElement.style.display =
                        "";

                } else {

                    discountElement.textContent =
                        "";

                    discountElement.style.display =
                        "none";
                }
            }


            // =================================================
            // ITEM TOTAL
            // =================================================

            const itemTotalElements =
                card.querySelectorAll(
                    ".item-total, .itemTotal"
                );


            itemTotalElements.forEach(
                function (element) {

                    element.textContent =
                        "₹" +
                        Number(
                            data.itemTotal || 0
                        ).toLocaleString(
                            "en-IN",
                            {
                                maximumFractionDigits: 0
                            }
                        );
                }
            );


            // =================================================
            // UPDATE ANY ELEMENT HAVING DATA CART ITEM TOTAL
            // =================================================

            const dataItemTotal =
                card.querySelector(
                    "[data-item-total]"
                );


            if (dataItemTotal) {

                dataItemTotal.textContent =
                    "₹" +
                    Number(
                        data.itemTotal || 0
                    ).toLocaleString(
                        "en-IN",
                        {
                            maximumFractionDigits: 0
                        }
                    );
            }


            // =================================================
            // SUBTOTAL
            // =================================================

            const subtotalElement =
                document.getElementById(
                    "subtotal"
                );


            if (subtotalElement) {

                subtotalElement.textContent =
                    "₹" +
                    Number(
                        data.subtotal || 0
                    ).toLocaleString(
                        "en-IN",
                        {
                            maximumFractionDigits: 0
                        }
                    );
            }


            // =================================================
            // DELIVERY CHARGE
            // =================================================

            const deliveryElements =
                document.querySelectorAll(
                    "#delivery, .delivery-charge, [data-delivery]"
                );


            deliveryElements.forEach(
                function (element) {

                    const delivery =
                        Number(
                            data.deliveryCharge ??
                            data.delivery ??
                            0
                        );


                    element.textContent =
                        delivery === 0
                            ? "FREE"
                            : "₹" +
                              delivery.toLocaleString(
                                  "en-IN",
                                  {
                                      maximumFractionDigits: 0
                                  }
                              );
                }
            );


            // =================================================
            // GRAND TOTAL
            // =================================================

            const grandTotalElement =
                document.getElementById(
                    "grandTotal"
                );


            if (grandTotalElement) {

                grandTotalElement.textContent =
                    "₹" +
                    Number(
                        data.grandTotal || 0
                    ).toLocaleString(
                        "en-IN",
                        {
                            maximumFractionDigits: 0
                        }
                    );
            }


            // =================================================
            // UPDATE SELECT DATA
            // =================================================

            selectedOption.dataset.variantId =
                data.variantId || variantId;


            selectedOption.dataset.price =
                data.price || 0;


            selectedOption.dataset.mrp =
                data.mrp || 0;


            // =================================================
            // UPDATE DELIVERY PROGRESS
            // =================================================

            updateDeliveryProgress();


            // =================================================
            // ENABLE SELECT
            // =================================================

            select.disabled = false;


            console.log(
                "Variant updated successfully:",
                data
            );
        }
    )


    // =====================================================
    // ERROR
    // =====================================================

    .catch(
        function (error) {

            console.error(
                "Variant update failed:",
                error
            );


            select.disabled = false;


            alert(
                error.message ||
                "Unable to update product variant. Please try again."
            );
        }
    );
}


// =========================================================
// LEGACY CHANGE WEIGHT
// =========================================================

function changeWeightLegacy(
    cartId,
    weight,
    select
) {

    if (!cartId || !weight) {
        return;
    }


    // =====================================================
    // CSRF
    // =====================================================

    const csrfHeaders =
        getCsrfHeaders();


    if (select) {

        select.disabled = true;
    }


    // =====================================================
    // REQUEST
    // =====================================================

    fetch(
        "/cart/change-weight",
        {

            method: "POST",

            credentials: "same-origin",

            headers: {
                ...csrfHeaders,

                "Content-Type":
                    "application/x-www-form-urlencoded; charset=UTF-8"
            },

            body:
                "cartId=" +
                encodeURIComponent(cartId) +

                "&weight=" +
                encodeURIComponent(weight)
        }
    )


    // =====================================================
    // RESPONSE
    // =====================================================

    .then(
        async function (response) {

            const result =
                (
                    await response.text()
                ).trim();


            console.log(
                "Weight HTTP status:",
                response.status
            );


            console.log(
                "Weight response:",
                result
            );


            if (
                response.status === 401
            ) {

                throw new Error(
                    "Your login session has expired. Please login again."
                );
            }


            if (
                response.status === 403
            ) {

                throw new Error(
                    "CSRF validation failed. Please refresh the page and try again."
                );
            }


            if (!response.ok) {

                throw new Error(
                    result ||
                    "Failed to update product weight."
                );
            }


            return result;
        }
    )


    // =====================================================
    // RESULT
    // =====================================================

    .then(
        function (result) {

            if (!result) {

                throw new Error(
                    "Empty server response."
                );
            }


            if (
                result === "LOGIN"
            ) {

                throw new Error(
                    "Your login session has expired."
                );
            }


            if (
                result.startsWith(
                    "ERROR"
                )
            ) {

                const message =
                    result
                        .substring(5)
                        .replace(
                            /^[:\s]+/,
                            ""
                        );


                throw new Error(
                    message ||
                    "Unable to update product weight."
                );
            }


            if (
                result !== "SUCCESS"
            ) {

                throw new Error(
                    "Unexpected server response: " +
                    result
                );
            }


            window.location.reload();
        }
    )


    // =====================================================
    // ERROR
    // =====================================================

    .catch(
        function (error) {

            console.error(
                "Weight update failed:",
                error
            );


            if (select) {

                select.disabled = false;
            }


            alert(
                error.message ||
                "Unable to update product weight. Please try again."
            );
        }
    );
}


// =========================================================
// FREE DELIVERY PROGRESS
// =========================================================

function updateDeliveryProgress() {

    const subtotalElement =
        document.getElementById(
            "subtotal"
        );


    const progressFill =
        document.getElementById(
            "progressFill"
        );


    const deliveryMessage =
        document.getElementById(
            "deliveryMessage"
        );


    // =====================================================
    // SAFETY
    // =====================================================

    if (
        !subtotalElement ||
        !progressFill ||
        !deliveryMessage
    ) {

        return;
    }


    // =====================================================
    // SUBTOTAL
    // =====================================================

    const subtotalText =
        subtotalElement.textContent || "";


    const cleanedSubtotal =
        subtotalText.replace(
            /[^\d.]/g,
            ""
        );


    const subtotal =
        Number(cleanedSubtotal) || 0;


    // =====================================================
    // FREE DELIVERY LIMIT
    // =====================================================

    const FREE_DELIVERY_AMOUNT =
        999;


    // =====================================================
    // FREE DELIVERY AVAILABLE
    // =====================================================

    if (
        subtotal >=
        FREE_DELIVERY_AMOUNT
    ) {

        progressFill.style.width =
            "100%";


        deliveryMessage.textContent =
            "🎉 Congratulations! FREE Delivery unlocked.";

        return;
    }


    // =====================================================
    // REMAINING
    // =====================================================

    const remaining =
        FREE_DELIVERY_AMOUNT -
        subtotal;


    let percentage =
        (
            subtotal /
            FREE_DELIVERY_AMOUNT
        ) * 100;


    // =====================================================
    // LIMIT
    // =====================================================

    percentage =
        Math.min(
            100,
            Math.max(
                0,
                percentage
            )
        );


    progressFill.style.width =
        percentage + "%";


    deliveryMessage.textContent =
        "Add ₹" +
        Math.ceil(remaining) +
        " more to unlock FREE Delivery.";
}


// =========================================================
// UPDATE CART SUBTOTAL
// =========================================================

function updateCartSubtotal() {

    let subtotal = 0;


    document
        .querySelectorAll(
            ".cart-card"
        )
        .forEach(
            function (card) {

                const priceElement =
                    card.querySelector(
                        ".new-price"
                    );


                if (!priceElement) {
                    return;
                }


                const priceText =
                    priceElement.textContent
                        .replace(
                            /[^\d.]/g,
                            ""
                        );


                const price =
                    Number(priceText) || 0;


                const quantityElement =
                    card.querySelector(
                        ".quantity-value, .quantity, input[type='number']"
                    );


                let quantity = 1;


                if (quantityElement) {

                    if (
                        quantityElement.tagName ===
                        "INPUT"
                    ) {

                        quantity =
                            parseInt(
                                quantityElement.value || "1",
                                10
                            );

                    } else {

                        quantity =
                            parseInt(
                                quantityElement.textContent || "1",
                                10
                            );
                    }
                }


                if (
                    !Number.isFinite(quantity) ||
                    quantity < 1
                ) {

                    quantity = 1;
                }


                subtotal +=
                    price * quantity;
            }
        );


    const subtotalElement =
        document.getElementById(
            "subtotal"
        );


    if (subtotalElement) {

        subtotalElement.textContent =
            "₹" +
            subtotal.toLocaleString(
                "en-IN",
                {
                    maximumFractionDigits: 0
                }
            );
    }


    updateDeliveryProgress();
}