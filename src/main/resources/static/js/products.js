/* =========================================================
   NUTRINEST PRODUCTS PAGE
   WISHLIST + CART AJAX
========================================================= */

document.addEventListener("DOMContentLoaded", function () {

    /* =====================================================
       CSRF TOKEN
    ===================================================== */

    function getCsrfHeaders(form = null) {

        const headers = {};

        /*
         * 1. Try meta tag
         */
        const tokenMeta =
            document.querySelector(
                'meta[name="_csrf"]'
            );

        const headerMeta =
            document.querySelector(
                'meta[name="_csrf_header"]'
            );


        if (
            tokenMeta &&
            tokenMeta.content
        ) {

            const headerName =
                headerMeta &&
                headerMeta.content
                    ? headerMeta.content
                    : "X-CSRF-TOKEN";


            headers[headerName] =
                tokenMeta.content;

            return headers;
        }


        /*
         * 2. Try hidden CSRF input
         */
        if (form) {

            const csrfInput =
                form.querySelector(
                    'input[name="_csrf"]'
                );


            if (
                csrfInput &&
                csrfInput.value
            ) {

                headers["X-CSRF-TOKEN"] =
                    csrfInput.value;

                return headers;
            }

        }


        /*
         * 3. Search entire document
         */
        const globalCsrf =
            document.querySelector(
                'input[name="_csrf"]'
            );


        if (
            globalCsrf &&
            globalCsrf.value
        ) {

            headers["X-CSRF-TOKEN"] =
                globalCsrf.value;

        }


        return headers;
    }


    /* =====================================================
       WISHLIST
    ===================================================== */

    document
        .querySelectorAll(
            ".wishlist-btn, .wishlist"
        )
        .forEach(function (button) {

            /*
             * IMPORTANT
             * Prevent normal form submission.
             */
            button.addEventListener(
                "click",
                async function (event) {

                    event.preventDefault();
                    event.stopPropagation();


                    if (button.disabled) {
                        return;
                    }


                    const form =
                        button.closest("form");


                    if (!form) {

                        console.error(
                            "Wishlist form not found."
                        );

                        return;
                    }


                    let action =
                        form.getAttribute(
                            "action"
                        );


                    if (!action) {

                        console.error(
                            "Wishlist action missing."
                        );

                        return;
                    }


                    /*
                     * Convert relative URL into
                     * proper same-origin URL.
                     */
                    const requestUrl =
                        new URL(
                            action,
                            window.location.origin
                        ).pathname;


                    /*
                     * Make sure this is actually
                     * a wishlist request.
                     */
                    if (
                        !requestUrl.includes(
                            "/wishlist/add/"
                        ) &&
                        !requestUrl.includes(
                            "/wishlist/remove/"
                        )
                    ) {

                        console.error(
                            "Invalid wishlist URL:",
                            requestUrl
                        );

                        return;
                    }


                    button.disabled = true;


                    const icon =
                        button.querySelector("i");


                    const removing =
                        requestUrl.includes(
                            "/wishlist/remove/"
                        );


                    try {

                        const csrfHeaders =
                            getCsrfHeaders(form);


                        console.log(
                            "Wishlist URL:",
                            requestUrl
                        );

                        console.log(
                            "CSRF headers:",
                            csrfHeaders
                        );


                        /*
                         * CSRF missing
                         */
                        if (
                            Object.keys(
                                csrfHeaders
                            ).length === 0
                        ) {

                            throw new Error(
                                "CSRF token not found. Add CSRF meta tags to products.html."
                            );

                        }


                        const response =
                            await fetch(
                                requestUrl,
                                {
                                    method: "POST",

                                    credentials:
                                        "same-origin",

                                    headers: {
                                        ...csrfHeaders,

                                        "X-Requested-With":
                                            "XMLHttpRequest",

                                        "Accept":
                                            "text/html"
                                    }
                                }
                            );


                        console.log(
                            "Wishlist status:",
                            response.status
                        );


                        /*
                         * Authentication problem
                         */
                        if (
                            response.status === 401
                        ) {

                            window.location.href =
                                "/login";

                            return;
                        }


                        /*
                         * CSRF / server error
                         */
                        if (!response.ok) {

                            const errorText =
                                await response.text();

                            console.error(
                                "Wishlist server response:",
                                errorText
                            );

                            throw new Error(
                                "HTTP " +
                                response.status
                            );

                        }


                        /* =================================
                           REMOVE
                        ================================= */

                        if (removing) {

                            if (icon) {

                                icon.classList.remove(
                                    "fa-solid"
                                );

                                icon.classList.add(
                                    "fa-regular"
                                );

                            }


                            button.classList.remove(
                                "active"
                            );

                            button.classList.remove(
                                "wishlist-active"
                            );


                            button.setAttribute(
                                "title",
                                "Add to Wishlist"
                            );


                            /*
                             * Change next action
                             */
                            form.setAttribute(
                                "action",
                                requestUrl.replace(
                                    "/wishlist/remove/",
                                    "/wishlist/add/"
                                )
                            );


                            updateWishlistCount(
                                -1
                            );

                        }


                        /* =================================
                           ADD
                        ================================= */

                        else {

                            if (icon) {

                                icon.classList.remove(
                                    "fa-regular"
                                );

                                icon.classList.add(
                                    "fa-solid"
                                );

                            }


                            button.classList.add(
                                "active"
                            );

                            button.classList.add(
                                "wishlist-active"
                            );


                            button.setAttribute(
                                "title",
                                "Remove from Wishlist"
                            );


                            /*
                             * Change next action
                             */
                            form.setAttribute(
                                "action",
                                requestUrl.replace(
                                    "/wishlist/add/",
                                    "/wishlist/remove/"
                                )
                            );


                            updateWishlistCount(
                                1
                            );

                        }


                    }
                    catch (error) {

                        console.error(
                            "Wishlist error:",
                            error
                        );


                        /*
                         * IMPORTANT:
                         * Don't redirect to login for
                         * a generic request error.
                         */

                        alert(
                            "Unable to update wishlist. Please try again."
                        );

                    }
                    finally {

                        button.disabled =
                            false;

                    }

                }
            );

        });


    /* =====================================================
       WISHLIST COUNT
    ===================================================== */

    function updateWishlistCount(change) {

        const wishlistButtons =
            document.querySelectorAll(
                '.icon-btn[title="Wishlist"]'
            );


        wishlistButtons.forEach(
            function (wishlistButton) {

                const count =
                    wishlistButton.querySelector(
                        ".count"
                    );


                if (!count) {
                    return;
                }


                let current =
                    parseInt(
                        count.textContent,
                        10
                    );


                if (isNaN(current)) {
                    current = 0;
                }


                current += change;


                if (current < 0) {
                    current = 0;
                }


                count.textContent =
                    current;

            }
        );

    }


    /* =====================================================
       ADD TO CART
    ===================================================== */

    document
        .querySelectorAll(
            ".add-cart-btn"
        )
        .forEach(function (button) {

            /*
             * Never allow normal form submission.
             */
            button.setAttribute(
                "type",
                "button"
            );


            button.addEventListener(
                "click",
                async function (event) {

                    event.preventDefault();
                    event.stopPropagation();


                    if (button.disabled) {
                        return;
                    }


                    /*
                     * Product ID
                     */
                    let productId =
                        button.dataset.id ||
                        button.dataset.productId;


                    /*
                     * Try hidden input
                     */
                    if (!productId) {

                        const form =
                            button.closest("form");


                        if (form) {

                            const input =
                                form.querySelector(
                                    'input[name="productId"]'
                                );


                            if (input) {

                                productId =
                                    input.value;

                            }

                        }

                    }


                    if (!productId) {

                        console.error(
                            "Product ID missing."
                        );

                        alert(
                            "Product information missing."
                        );

                        return;
                    }


                    const form =
                        button.closest("form");


                    /*
                     * Quantity
                     */
                    let quantity = 1;


                    if (form) {

                        const quantityInput =
                            form.querySelector(
                                'input[name="quantity"]'
                            );


                        if (quantityInput) {

                            const q =
                                parseInt(
                                    quantityInput.value,
                                    10
                                );


                            if (
                                !isNaN(q) &&
                                q > 0
                            ) {

                                quantity = q;

                            }

                        }

                    }


                    button.disabled =
                        true;


                    const text =
                        button.querySelector(
                            ".cart-text"
                        );


                    const loading =
                        button.querySelector(
                            ".cart-loading"
                        );


                    const success =
                        button.querySelector(
                            ".cart-success"
                        );


                    if (text) {

                        text.style.display =
                            "none";

                    }


                    if (loading) {

                        loading.style.display =
                            "inline";

                    }


                    try {

                        const csrfHeaders =
                            getCsrfHeaders(form);


                        if (
                            Object.keys(
                                csrfHeaders
                            ).length === 0
                        ) {

                            throw new Error(
                                "CSRF token not found."
                            );

                        }


                        const body =
                            new URLSearchParams();


                        body.append(
                            "productId",
                            productId
                        );


                        body.append(
                            "quantity",
                            quantity
                        );


                        console.log(
                            "Adding product:",
                            productId
                        );


                        const response =
                            await fetch(
                                "/cart/add",
                                {

                                    method: "POST",

                                    credentials:
                                        "same-origin",

                                    headers: {

                                        ...csrfHeaders,

                                        "X-Requested-With":
                                            "XMLHttpRequest",

                                        "Content-Type":
                                            "application/x-www-form-urlencoded; charset=UTF-8"
                                    },

                                    body:
                                        body.toString()
                                }
                            );


                        console.log(
                            "Cart status:",
                            response.status
                        );


                        if (
                            response.status === 401
                        ) {

                            window.location.href =
                                "/login";

                            return;
                        }


                        const result =
                            (
                                await response.text()
                            ).trim();


                        console.log(
                            "Cart response:",
                            result
                        );


                        if (!response.ok) {

                            throw new Error(
                                "HTTP " +
                                response.status +
                                ": " +
                                result
                            );

                        }


                        /*
                         * SUCCESS:count
                         */
                        if (
                            result.startsWith(
                                "SUCCESS:"
                            )
                        ) {

                            const count =
                                parseInt(
                                    result.substring(
                                        8
                                    ),
                                    10
                                );


                            if (loading) {

                                loading.style.display =
                                    "none";

                            }


                            if (success) {

                                success.style.display =
                                    "inline";

                            }
                            else if (text) {

                                text.textContent =
                                    "Added";

                                text.style.display =
                                    "inline";

                            }


                            if (
                                !isNaN(count)
                            ) {

                                updateCartCount(
                                    count
                                );

                            }


                            setTimeout(
                                function () {

                                    button.disabled =
                                        false;


                                    if (success) {

                                        success.style.display =
                                            "none";

                                    }


                                    if (text) {

                                        text.style.display =
                                            "inline";

                                    }

                                },
                                1200
                            );

                        }
                        else {

                            throw new Error(
                                result ||
                                "Unable to add product."
                            );

                        }

                    }
                    catch (error) {

                        console.error(
                            "Cart error:",
                            error
                        );


                        if (loading) {

                            loading.style.display =
                                "none";

                        }


                        if (text) {

                            text.style.display =
                                "inline";

                        }


                        button.disabled =
                            false;


                        alert(
                            "Unable to add product. Please try again."
                        );

                    }

                }
            );

        });


    /* =====================================================
       CART COUNT
    ===================================================== */

    function updateCartCount(count) {

        const selectors = [

            '.icon-btn[title="Cart"] .count',

            '.cart-link .count',

            '.cart-icon .count',

            '.cart-count',

            '[data-cart-count]',

            'a[href="/cart"] .count',

            ".badge"

        ];


        selectors.forEach(
            function (selector) {

                document
                    .querySelectorAll(selector)
                    .forEach(
                        function (element) {

                            element.textContent =
                                count;

                        }
                    );

            }
        );

    }

});