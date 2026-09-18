// ================================
// PRODUCT DETAILS - QUANTITY
// ================================

document.addEventListener("DOMContentLoaded", function () {

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

    let qty = 1;

    // ================================
    // MAX STOCK
    // ================================

    const maxStock =
        increaseBtn
            ? parseInt(
                increaseBtn.dataset.stock || "999"
            )
            : 999;


    // ================================
    // PRODUCT PRICE
    // ================================

    const priceElement =
        document.querySelector(".current-price");

    let unitPrice = 0;

    if (priceElement) {

        unitPrice =
            parseFloat(
                priceElement.textContent
                    .replace(/[^\d.]/g, "")
            ) || 0;
    }


    // ================================
    // UPDATE QUANTITY
    // ================================

    function updateQuantity() {

        if (quantityValue) {
            quantityValue.textContent = qty;
        }

        const cartQuantity = document.getElementById("cartQuantity");
        if (cartQuantity) cartQuantity.value = qty;

        if (mobileTotal) {

            mobileTotal.textContent =
                "₹" +
                Math.round(unitPrice * qty);
        }
    }


    // ================================
    // DECREASE
    // ================================

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


    // ================================
    // INCREASE
    // ================================

    if (increaseBtn) {

        increaseBtn.addEventListener(
            "click",
            function () {

                if (qty < maxStock) {

                    qty++;

                    updateQuantity();
                }
            }
        );
    }


    // ================================
    // BUY NOW
    // ================================

    function handleBuyNow() {

        const productId =
            buyNowBtn
                ? buyNowBtn.dataset.productId
                : null;

        if (!productId) {

            console.error(
                "Product ID not found"
            );

            return;
        }

        // Direct Buy Now → Checkout
        window.location.href =
            "/checkout/buy-now/"
            + productId
            + "?quantity="
            + qty;
    }


    // Desktop Buy Now
    if (buyNowBtn) {

        buyNowBtn.addEventListener(
            "click",
            handleBuyNow
        );
    }


    // Mobile Buy Now
    if (mobileBuyNowBtn) {

        mobileBuyNowBtn.addEventListener(
            "click",
            handleBuyNow
        );
    }


    // ================================
    // INITIAL TOTAL
    // ================================

    updateQuantity();

});


// ================================
// IMAGE CHANGE
// ================================

function changeImage(img) {

    const mainImage =
        document.getElementById(
            "productImage"
        );

    if (mainImage) {

        mainImage.src = img.src;
    }

    document
        .querySelectorAll(".thumb")
        .forEach(function (image) {

            image.classList.remove("active");
        });

    img.classList.add("active");
}
