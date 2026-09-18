/*==================================
WISHLIST PAGE JS
==================================*/

document.addEventListener("DOMContentLoaded", () => {

    initializeWishlist();

});


/*==================================
INITIALIZE
==================================*/

function initializeWishlist(){

    removeWishlistItem();

    addToCart();

    buyNow();

}


/*==================================
REMOVE ITEM
==================================*/

function removeWishlistItem(){

    const removeButtons =
        document.querySelectorAll(".remove-btn");

    removeButtons.forEach(button => {

        button.addEventListener("click", async (event) => {

            event.preventDefault();

            const card =
                button.closest(".wishlist-card");

            if (!card) return;

            /*
             * Get product id from form
             */
            const form = button.closest("form");

            if (!form) return;

            const action = form.getAttribute("action");

            if (!action) return;

            try {

                button.disabled = true;

                const response = await fetch(action, {
                    method: "POST",
                    headers: getCsrfHeaders()
                });

                if (!response.ok) {
                    throw new Error("Remove failed");
                }

                /* Smooth remove */

                card.style.opacity = "0";
                card.style.transform = "scale(.8)";

                setTimeout(() => {

                    card.remove();

                    updateWishlistCount(-1);

                    checkEmptyWishlist();

                }, 300);

            } catch (error) {

                console.error(
                    "Wishlist remove error:",
                    error
                );

                button.disabled = false;

                alert(
                    "Unable to remove item. Please try again."
                );

            }

        });

    });

}


/*==================================
UPDATE WISHLIST COUNT
==================================*/

function updateWishlistCount(change){

    /*
     * Hero count
     */
    const heroCount =
        document.querySelector(".wishlist-hero h2");

    if (heroCount) {

        let count =
            parseInt(heroCount.textContent) || 0;

        count += change;

        if (count < 0) count = 0;

        heroCount.textContent = count;

    }


    /*
     * Toolbar count
     */
    const itemCount =
        document.querySelector(".wishlist-toolbar span");

    if (itemCount) {

        let countText =
            itemCount.textContent;

        let count =
            parseInt(countText) || 0;

        count += change;

        if (count < 0) count = 0;

        itemCount.textContent =
            count + " Items";

    }


    /*
     * Navbar wishlist count
     */
    const wishlistCounts =
        document.querySelectorAll(
            ".wishlist-count"
        );

    wishlistCounts.forEach(countElement => {

        let count =
            parseInt(countElement.textContent) || 0;

        count += change;

        if (count < 0) count = 0;

        countElement.textContent = count;

    });

}


/*==================================
ADD TO CART
==================================*/

function addToCart(){

    const cartButtons =
        document.querySelectorAll(".cart-btn");

    cartButtons.forEach(button => {

        button.addEventListener("click", () => {

            button.innerHTML = "✔ Added";

            button.style.background = "#43A047";

            setTimeout(() => {

                button.innerHTML = "Add to Cart";

            }, 1500);

        });

    });

}


/*==================================
BUY NOW
==================================*/

function buyNow(){

    const buyButtons =
        document.querySelectorAll(".buy-btn");

    buyButtons.forEach(button => {

        button.addEventListener("click", () => {

            window.location.href =
                "checkout.html";

        });

    });

}


/*==================================
EMPTY WISHLIST
==================================*/

function checkEmptyWishlist(){

    const cards =
        document.querySelectorAll(
            ".wishlist-card"
        );

    const grid =
        document.querySelector(".wishlist-grid");

    if (!grid) return;

    if (cards.length === 0){

        grid.innerHTML = `

            <div class="empty-wishlist">

                <i class="fa-solid fa-heart-crack"></i>

                <h2>Your Wishlist is Empty</h2>

                <p>
                    Save your favourite products here.
                </p>

                <a href="/products"
                   class="shop-btn">

                    Continue Shopping

                </a>

            </div>

        `;

    }

}
