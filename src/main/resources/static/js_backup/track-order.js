/* ==================================
   TRACK ORDER JS
================================== */

document.addEventListener("DOMContentLoaded", () => {

    initializeTracking();

});


/* ==================================
   INITIALIZE
================================== */

function initializeTracking() {

    animateProgress();

    buttonEvents();

}


/* ==================================
   PROGRESS ANIMATION
================================== */

function animateProgress() {

    const steps =
        document.querySelectorAll(".tracking-step");

    if (!steps.length) return;

    steps.forEach((step, index) => {

        // Initial state
        step.style.opacity = "0";
        step.style.transform = "translateY(15px)";

        setTimeout(() => {

            step.style.opacity = "1";
            step.style.transform = "translateY(0)";

        }, index * 250);

    });

}


/* ==================================
   BUTTON EVENTS
================================== */

function buttonEvents() {

    const refresh =
        document.getElementById("refreshTracking");

    if (!refresh) return;


    refresh.addEventListener("click", () => {

        if (refresh.disabled) return;


        /* ------------------------------
           Loading State
        ------------------------------ */

        refresh.disabled = true;

        refresh.innerHTML = `
            <i class="fa-solid fa-rotate fa-spin"></i>
            <span>Refreshing...</span>
        `;


        /* ------------------------------
           Refresh Page
        ------------------------------ */

        setTimeout(() => {

            window.location.reload();

        }, 700);

    });

}


/* ==================================
   UPDATE ORDER STATUS
================================== */

function updateOrderStatus(status) {

    const current =
        document.querySelector(".current-status");

    if (!current) return;


    current.textContent = status;

}