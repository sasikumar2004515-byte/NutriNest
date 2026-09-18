/* =========================================================
   NUTRINEST GLOBAL JAVASCRIPT
   ========================================================= */


/* =========================================================
   CSRF
   ========================================================= */

function getCsrfHeaders() {

    const tokenMeta =
        document.querySelector('meta[name="_csrf"]');

    const headerMeta =
        document.querySelector('meta[name="_csrf_header"]');

    if (!tokenMeta || !headerMeta) {
        console.warn("NutriNest: CSRF meta tags not found.");
        return {};
    }

    const token =
        tokenMeta.getAttribute("content");

    const header =
        headerMeta.getAttribute("content");

    if (!token || !header) {
        console.warn("NutriNest: Invalid CSRF meta tags.");
        return {};
    }

    return {
        [header]: token
    };
}


/* =========================================================
   HERO SLIDER
   ========================================================= */

document.addEventListener("DOMContentLoaded", function () {

    const heroSection =
        document.querySelector(
            ".nutri-nest-home-hero-section"
        );

    if (!heroSection) {
        return;
    }

    const heroSlides =
        heroSection.querySelectorAll(
            ".nutri-nest-home-hero-slide"
        );

    const heroDots =
        heroSection.querySelectorAll(
            ".nutri-nest-home-hero-dot"
        );

    const heroPrev =
        heroSection.querySelector(
            ".nutri-nest-home-hero-arrow-left"
        );

    const heroNext =
        heroSection.querySelector(
            ".nutri-nest-home-hero-arrow-right"
        );

    if (
        !heroSlides.length ||
        !heroDots.length ||
        !heroPrev ||
        !heroNext
    ) {
        return;
    }

    let currentSlide = 0;
    let heroInterval = null;

    let touchStartX = 0;
    let touchEndX = 0;


    /* =====================================================
       SHOW SLIDE
       ===================================================== */

    function showHeroSlide(index) {

        if (index < 0) {
            index = heroSlides.length - 1;
        }

        if (index >= heroSlides.length) {
            index = 0;
        }

        heroSlides.forEach(function (slide) {

            slide.classList.remove(
                "nutri-nest-home-hero-active"
            );

        });

        heroDots.forEach(function (dot) {

            dot.classList.remove(
                "nutri-nest-home-hero-dot-active"
            );

        });

        heroSlides[index].classList.add(
            "nutri-nest-home-hero-active"
        );

        if (heroDots[index]) {

            heroDots[index].classList.add(
                "nutri-nest-home-hero-dot-active"
            );

        }

        currentSlide = index;
    }


    /* =====================================================
       NEXT
       ===================================================== */

    function nextHeroSlide() {

        showHeroSlide(currentSlide + 1);

    }


    /* =====================================================
       PREVIOUS
       ===================================================== */

    function previousHeroSlide() {

        showHeroSlide(currentSlide - 1);

    }


    /* =====================================================
       START AUTO SLIDER
       ===================================================== */

    function startHeroSlider() {

        stopHeroSlider();

        heroInterval =
            setInterval(function () {

                nextHeroSlide();

            }, 5000);
    }


    /* =====================================================
       STOP AUTO SLIDER
       ===================================================== */

    function stopHeroSlider() {

        if (heroInterval !== null) {

            clearInterval(heroInterval);

            heroInterval = null;
        }
    }


    /* =====================================================
       NEXT BUTTON
       ===================================================== */

    heroNext.addEventListener(
        "click",
        function () {

            nextHeroSlide();

            startHeroSlider();

        }
    );


    /* =====================================================
       PREVIOUS BUTTON
       ===================================================== */

    heroPrev.addEventListener(
        "click",
        function () {

            previousHeroSlide();

            startHeroSlider();

        }
    );


    /* =====================================================
       DOTS
       ===================================================== */

    heroDots.forEach(
        function (dot, index) {

            dot.addEventListener(
                "click",
                function () {

                    showHeroSlide(index);

                    startHeroSlider();

                }
            );

        }
    );


    /* =====================================================
       MOUSE HOVER
       ===================================================== */

    heroSection.addEventListener(
        "mouseenter",
        stopHeroSlider
    );

    heroSection.addEventListener(
        "mouseleave",
        startHeroSlider
    );


    /* =====================================================
       MOBILE TOUCH START
       ===================================================== */

    heroSection.addEventListener(
        "touchstart",
        function (event) {

            touchStartX =
                event.changedTouches[0].screenX;

            stopHeroSlider();

        },
        { passive: true }
    );


    /* =====================================================
       MOBILE TOUCH END
       ===================================================== */

    heroSection.addEventListener(
        "touchend",
        function (event) {

            touchEndX =
                event.changedTouches[0].screenX;

            handleHeroSwipe();

            startHeroSlider();

        },
        { passive: true }
    );


    /* =====================================================
       SWIPE
       ===================================================== */

    function handleHeroSwipe() {

        const swipeDistance =
            touchEndX - touchStartX;

        if (Math.abs(swipeDistance) < 50) {
            return;
        }

        if (swipeDistance < 0) {

            nextHeroSlide();

        } else {

            previousHeroSlide();

        }
    }


    /* =====================================================
       KEYBOARD
       ===================================================== */

    document.addEventListener(
        "keydown",
        function (event) {

            const active =
                document.activeElement;

            const isInput =
                active &&
                (
                    active.tagName === "INPUT" ||
                    active.tagName === "TEXTAREA" ||
                    active.tagName === "SELECT"
                );

            if (isInput) {
                return;
            }

            if (event.key === "ArrowRight") {

                nextHeroSlide();

                startHeroSlider();
            }

            if (event.key === "ArrowLeft") {

                previousHeroSlide();

                startHeroSlider();
            }

        }
    );


    /* =====================================================
       INITIAL SLIDE
       ===================================================== */

    showHeroSlide(0);

    startHeroSlider();

});


/* =========================================================
   WISHLIST AJAX
   ========================================================= */

document.addEventListener(
    "DOMContentLoaded",
    function () {

        const wishlistForms =
            document.querySelectorAll(
                ".wishlist-form"
            );

        if (!wishlistForms.length) {
            return;
        }

        wishlistForms.forEach(
            function (form) {

                const button =
                    form.querySelector(
                        ".wishlist-btn"
                    );

                if (!button) {
                    return;
                }


                button.addEventListener(
                    "click",
                    async function (event) {

                        event.preventDefault();

                        if (button.disabled) {
                            return;
                        }

                        const action =
                            form.getAttribute("action");

                        if (!action) {
                            console.error(
                                "NutriNest: Wishlist action missing."
                            );
                            return;
                        }

                        const icon =
                            button.querySelector("i");

                        button.disabled = true;


                        try {

                            const response =
                                await fetch(
                                    action,
                                    {
                                        method: "POST",

                                        credentials:
                                            "same-origin",

                                        headers: {
                                            ...getCsrfHeaders(),

                                            "X-Requested-With":
                                                "XMLHttpRequest"
                                        }
                                    }
                                );


                            if (
                                response.status === 401 ||
                                response.status === 403
                            ) {

                                window.location.href =
                                    "/login";

                                return;
                            }


                            if (!response.ok) {

                                throw new Error(
                                    "Wishlist request failed: " +
                                    response.status
                                );
                            }


                            const isAdd =
                                action.includes(
                                    "/wishlist/add/"
                                );


                            /* =================================================
                               ADD
                               ================================================= */

                            if (isAdd) {

                                if (icon) {

                                    icon.classList.remove(
                                        "fa-regular"
                                    );

                                    icon.classList.add(
                                        "fa-solid"
                                    );
                                }


                                button.classList.add(
                                    "wishlist-active"
                                );


                                button.setAttribute(
                                    "title",
                                    "Remove from Wishlist"
                                );


                                form.setAttribute(
                                    "action",
                                    action.replace(
                                        "/wishlist/add/",
                                        "/wishlist/remove/"
                                    )
                                );


                                updateWishlistCount(1);

                            }


                            /* =================================================
                               REMOVE
                               ================================================= */

                            else {

                                if (icon) {

                                    icon.classList.remove(
                                        "fa-solid"
                                    );

                                    icon.classList.add(
                                        "fa-regular"
                                    );
                                }


                                button.classList.remove(
                                    "wishlist-active"
                                );


                                button.setAttribute(
                                    "title",
                                    "Add to Wishlist"
                                );


                                form.setAttribute(
                                    "action",
                                    action.replace(
                                        "/wishlist/remove/",
                                        "/wishlist/add/"
                                    )
                                );


                                updateWishlistCount(-1);
                            }


                        } catch (error) {

                            console.error(
                                "NutriNest Wishlist Error:",
                                error
                            );

                        } finally {

                            button.disabled = false;

                        }

                    }
                );

            }
        );

    }
);


/* =========================================================
   WISHLIST COUNT
   ========================================================= */

function updateWishlistCount(change) {

    const wishlistButton =
        document.querySelector(
            '.icon-btn[title="Wishlist"]'
        );

    if (!wishlistButton) {
        return;
    }

    const countElement =
        wishlistButton.querySelector(
            ".count"
        );

    if (!countElement) {
        return;
    }

    let count =
        parseInt(
            countElement.textContent,
            10
        );

    if (Number.isNaN(count)) {
        count = 0;
    }

    count += change;

    if (count < 0) {
        count = 0;
    }

    countElement.textContent =
        count;
}

/* =========================================================
   NUTRINEST — ADD TO CART
   ========================================================= */

document.addEventListener("DOMContentLoaded", function () {

    const cartButtons = document.querySelectorAll(".add-cart-btn");

    if (!cartButtons.length) {
        return;
    }

    cartButtons.forEach(function (button) {

        button.addEventListener("click", async function (event) {

            event.preventDefault();
            event.stopPropagation();

            const productId = button.dataset.id;

            if (!productId || button.disabled) {
                return;
            }

            /* -------------------------------------------------
               BUTTON ELEMENTS
            ------------------------------------------------- */

            const text = button.querySelector(".cart-text");
            const loading = button.querySelector(".cart-loading");
            const success = button.querySelector(".cart-success");

            /* -------------------------------------------------
               LOADING STATE
            ------------------------------------------------- */

            button.disabled = true;

            if (text) {
                text.style.display = "none";
            }

            if (loading) {
                loading.style.display = "inline";
            }

            if (success) {
                success.style.display = "none";
            }

            try {

                /* -------------------------------------------------
                   CSRF
                ------------------------------------------------- */

                const csrfHeaders = getCsrfHeaders();

                /* -------------------------------------------------
                   ADD PRODUCT TO CART
                ------------------------------------------------- */

                const response = await fetch(
                    "/cart/add",
                    {
                        method: "POST",

                        credentials: "same-origin",

                        headers: {
                            ...csrfHeaders,

                            "X-Requested-With":
                                "XMLHttpRequest",

                            "Content-Type":
                                "application/x-www-form-urlencoded; charset=UTF-8"
                        },

                        body: new URLSearchParams({
                            productId: productId,
                            quantity: "1"
                        }).toString()
                    }
                );

                /* -------------------------------------------------
                   AUTH CHECK
                ------------------------------------------------- */

                if (
                    response.status === 401 ||
                    response.status === 403
                ) {

                    window.location.href = "/login";
                    return;
                }

                /* -------------------------------------------------
                   SERVER RESPONSE
                ------------------------------------------------- */

                const result =
                    (await response.text()).trim();

                console.log(
                    "NutriNest Cart Response:",
                    result
                );

                /* -------------------------------------------------
                   LOGIN RESPONSE
                ------------------------------------------------- */

                if (result === "LOGIN") {

                    window.location.href = "/login";
                    return;
                }

                /* -------------------------------------------------
                   SUCCESS RESPONSE
                   Expected:
                   SUCCESS:1
                   SUCCESS:2
                   SUCCESS:3
                ------------------------------------------------- */

                if (!result.startsWith("SUCCESS:")) {

                    throw new Error(
                        result ||
                        "Unable to add product to cart"
                    );
                }

                /* -------------------------------------------------
                   GET REAL SERVER CART COUNT
                ------------------------------------------------- */

                const cartCount =
                    parseInt(
                        result.substring(
                            "SUCCESS:".length
                        ),
                        10
                    );

                if (!Number.isNaN(cartCount)) {

                    updateCartCount(cartCount);
                }

                /* -------------------------------------------------
                   SUCCESS UI
                ------------------------------------------------- */

                if (loading) {
                    loading.style.display = "none";
                }

                if (success) {
                    success.style.display = "inline";
                }

                /* -------------------------------------------------
                   RESTORE BUTTON
                ------------------------------------------------- */

                setTimeout(function () {

                    if (success) {
                        success.style.display = "none";
                    }

                    if (text) {
                        text.style.display = "inline";
                    }

                }, 1500);

            } catch (error) {

                console.error(
                    "NutriNest Add To Cart Error:",
                    error
                );

                if (loading) {
                    loading.style.display = "none";
                }

                if (success) {
                    success.style.display = "none";
                }

                if (text) {
                    text.style.display = "inline";
                }

            } finally {

                button.disabled = false;
            }

        });

    });

});


/* =========================================================
   GET CSRF HEADERS
   ========================================================= */

function getCsrfHeaders() {

    const headers = {};

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
        headerMeta &&
        tokenMeta.content &&
        headerMeta.content
    ) {

        headers[headerMeta.content] =
            tokenMeta.content;
    }

    return headers;
}


/* =========================================================
   UPDATE CART COUNT
   ========================================================= */

function updateCartCount(count) {

    const numericCount =
        parseInt(count, 10);

    if (Number.isNaN(numericCount)) {
        return;
    }

    /*
     * Update only actual cart-count elements.
     * No generic ".badge" selector.
     */

    const selectors = [
        '.icon-btn[title="Cart"] .count',
        '.cart-link .count',
        '.cart-icon .count',
        '.cart-count',
        '[data-cart-count]',
        'a[href="/cart"] .count'
    ];

    const elements =
        document.querySelectorAll(
            selectors.join(",")
        );

    elements.forEach(function (element) {

        element.textContent =
            numericCount;

        element.style.display =
            "inline-flex";

        element.setAttribute(
            "aria-label",
            "Cart items: " + numericCount
        );

    });

    /*
     * If navbar cart exists but .count is missing,
     * create it.
     */

    const cartTargets =
        document.querySelectorAll(
            '.icon-btn[title="Cart"], ' +
            '.cart-link, ' +
            '.cart-icon, ' +
            'a[href="/cart"]'
        );

    cartTargets.forEach(function (cartTarget) {

        let countElement =
            cartTarget.querySelector(".count");

        if (!countElement) {

            countElement =
                document.createElement("span");

            countElement.className =
                "count";

            countElement.setAttribute(
                "aria-hidden",
                "true"
            );

            cartTarget.appendChild(
                countElement
            );
        }

        countElement.textContent =
            numericCount;

        countElement.style.display =
            "inline-flex";

        countElement.setAttribute(
            "aria-label",
            "Cart items: " + numericCount
        );

    });

}


/* =========================================================
   INITIAL CART COUNT
   ========================================================= */

document.addEventListener(
    "DOMContentLoaded",
    function () {

        /*
         * IMPORTANT:
         *
         * Do NOT read cart count from sessionStorage.
         *
         * Old code was doing:
         *
         * sessionStorage.getItem(
         *     "nutrinestCartCount"
         * );
         *
         * That caused old "1" to remain visible even
         * after the database cart became empty.
         *
         * Navbar HTML should provide the real initial
         * server-side count.
         *
         * If no server-side count exists, initialize
         * only the UI to 0.
         */

        const cartCountElements =
            document.querySelectorAll(
                '.icon-btn[title="Cart"] .count, ' +
                '.cart-link .count, ' +
                '.cart-icon .count, ' +
                '.cart-count, ' +
                '[data-cart-count], ' +
                'a[href="/cart"] .count'
            );

        cartCountElements.forEach(
            function (element) {

                /*
                 * Only initialize when the navbar has
                 * no meaningful server-side value.
                 */

                const currentValue =
                    parseInt(
                        element.textContent.trim(),
                        10
                    );

                if (Number.isNaN(currentValue)) {

                    element.textContent = "0";
                }

                element.style.display =
                    "inline-flex";
            }
        );

    }
);