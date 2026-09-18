
// =========================================================
// DOM READY
// =========================================================

document.addEventListener("DOMContentLoaded", function () {


    // =====================================================
    // COUPON
    // =====================================================

    const couponBtn =
        document.querySelector(".cart-page .coupon-input button");


    const couponInput =
        document.querySelector(".cart-page .coupon-input input");


    if (couponBtn) {

        couponBtn.addEventListener("click", function (event) {

            event.preventDefault();


            if (!couponInput) {
                return;
            }


            const coupon =
                couponInput.value.trim();


            if (coupon === "") {

                alert("Please enter a coupon code.");

                couponInput.focus();

                return;
            }


            alert(
                "Coupon feature will be connected with backend."
            );

        });

    }



    // =====================================================
    // WISHLIST
    // =====================================================

    const wishlistButtons =
        document.querySelectorAll(
            ".cart-page .wishlist-btn"
        );


    wishlistButtons.forEach(function (button) {


        button.addEventListener("click", function () {


            this.classList.toggle("active");


            if (this.classList.contains("active")) {

                this.innerHTML =
                    '<i class="fa-solid fa-heart"></i> Saved';

            } else {

                this.innerHTML =
                    '<i class="fa-regular fa-heart"></i> Save For Later';

            }

        });

    });



    // =====================================================
    // RECOMMENDED PRODUCTS
    // =====================================================

    const recommendButtons =
        document.querySelectorAll(
            ".cart-page .recommend-btn"
        );


    recommendButtons.forEach(function (button) {


        button.addEventListener("click", function (event) {

            event.preventDefault();


            alert(
                "This feature will be connected with backend."
            );

        });

    });



    // =====================================================
    // CART CARD ENTRANCE ANIMATION
    // =====================================================

    const cartCards =
        document.querySelectorAll(
            ".cart-page .cart-card"
        );


    cartCards.forEach(function (card, index) {


        card.style.opacity = "0";

        card.style.transform =
            "translateY(20px)";


        setTimeout(function () {


            card.style.transition =
                "opacity 0.45s ease, transform 0.45s ease";


            card.style.opacity = "1";


            card.style.transform =
                "translateY(0)";


        }, index * 100);

    });



    // =====================================================
    // DELETE BUTTON ANIMATION
    // =====================================================

    const deleteButtons =
        document.querySelectorAll(
            ".cart-page .delete-btn"
        );


    deleteButtons.forEach(function (button) {


        button.addEventListener("click", function () {


            const card =
                this.closest(".cart-card");


            if (!card) {
                return;
            }


            card.style.transition =
                "opacity 0.3s ease, transform 0.3s ease";


            card.style.opacity = "0";


            card.style.transform =
                "translateX(-50px)";

        });

    });



    // =====================================================
    // FREE DELIVERY PROGRESS
    // =====================================================

    updateDeliveryProgress();

});



// =========================================================
// CHANGE WEIGHT
// =========================================================
//
// BACKEND CONNECTION:
//
// POST /cart/change-weight
//
// Existing backend route is preserved.
// =========================================================

function changeWeight(select) {


    if (!select) {
        return;
    }


    const cartId =
        select.dataset.cartId;


    const weight =
        select.value;


    if (!cartId || !weight) {
        return;
    }



    fetch("/cart/change-weight", {

        method: "POST",


        headers: {

            ...getCsrfHeaders(),

            "Content-Type":
                "application/x-www-form-urlencoded; charset=UTF-8"

        },


        body:
            "cartId=" +
            encodeURIComponent(cartId) +
            "&weight=" +
            encodeURIComponent(weight)

    })


    .then(function (response) {


        if (!response.ok) {

            throw new Error(
                "Failed to update product weight."
            );

        }


        // Backend already updates the cart.
        // Reload gets fresh Thymeleaf values.

        window.location.reload();

    })


    .catch(function (error) {


        console.error(
            "Weight update failed:",
            error
        );


        alert(
            "Unable to update product weight. Please try again."
        );

    });

}



// =========================================================
// FREE DELIVERY PROGRESS
// =========================================================

function updateDeliveryProgress() {


    const subtotalElement =
        document.getElementById("subtotal");


    const progressFill =
        document.getElementById("progressFill");


    const deliveryMessage =
        document.getElementById("deliveryMessage");



    // =====================================================
    // EMPTY CART SAFETY
    // =====================================================

    if (
        !subtotalElement ||
        !progressFill ||
        !deliveryMessage
    ) {

        return;

    }



    // =====================================================
    // READ SUBTOTAL
    // =====================================================

    const subtotalText =
        subtotalElement.textContent || "";


    const cleanedSubtotal =
        subtotalText.replace(/[^\d.]/g, "");


    const subtotal =
        Number(cleanedSubtotal) || 0;



    // =====================================================
    // FREE DELIVERY LIMIT
    // =====================================================

    const FREE_DELIVERY_AMOUNT =
        999;



    // =====================================================
    // FREE DELIVERY UNLOCKED
    // =====================================================

    if (subtotal >= FREE_DELIVERY_AMOUNT) {


        progressFill.style.width =
            "100%";


        deliveryMessage.textContent =
            "🎉 Congratulations! FREE Delivery unlocked.";


        return;

    }



    // =====================================================
    // FREE DELIVERY NOT UNLOCKED
    // =====================================================

    const remaining =
        FREE_DELIVERY_AMOUNT - subtotal;


    let percentage =
        (subtotal / FREE_DELIVERY_AMOUNT) * 100;



    // Keep between 0 and 100

    percentage =
        Math.min(
            100,
            Math.max(0, percentage)
        );



    progressFill.style.width =
        percentage + "%";



    deliveryMessage.textContent =
        "Add ₹" +
        Math.ceil(remaining) +
        " more to unlock FREE Delivery.";

}
