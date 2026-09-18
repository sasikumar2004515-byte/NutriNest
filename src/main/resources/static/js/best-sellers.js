// ==========================
// BEST SELLER SLIDER
// ==========================

const slider = document.querySelector(".product-slider");
const nextBtn = document.querySelector(".next");
const prevBtn = document.querySelector(".prev");

const scrollAmount = 300;

// Next
nextBtn.addEventListener("click", () => {

    slider.scrollBy({
        left: scrollAmount,
        behavior: "smooth"
    });

});

// Previous
prevBtn.addEventListener("click", () => {

    slider.scrollBy({
        left: -scrollAmount,
        behavior: "smooth"
    });

});

// Auto Slide

setInterval(() => {

    if (
        slider.scrollLeft + slider.clientWidth >=
        slider.scrollWidth - 5
    ) {

        slider.scrollTo({
            left: 0,
            behavior: "smooth"
        });

    } else {

        slider.scrollBy({
            left: scrollAmount,
            behavior: "smooth"
        });

    }

}, 4000);