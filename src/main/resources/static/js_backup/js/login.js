/* ==================================================
   NUTRINEST LOGIN JS
   Frontend behaviour only
   Spring Boot handles actual authentication
================================================== */

document.addEventListener("DOMContentLoaded", function () {

    initializeLogin();

});


/* ==================================================
   INITIALIZE
================================================== */

function initializeLogin() {

    togglePassword();

    loginValidation();

    rememberUser();

    googleLogin();

}


/* ==================================================
   SHOW / HIDE PASSWORD
================================================== */

function togglePassword() {

    const password = document.getElementById("password");

    const eye = document.querySelector(".toggle-password");


    if (!password || !eye) {

        console.log("Password toggle elements not found.");

        return;

    }


    function toggle() {

        if (password.type === "password") {

            password.type = "text";

            eye.classList.remove("fa-eye");

            eye.classList.add("fa-eye-slash");

            eye.setAttribute(
                "aria-label",
                "Hide password"
            );

        }

        else {

            password.type = "password";

            eye.classList.remove("fa-eye-slash");

            eye.classList.add("fa-eye");

            eye.setAttribute(
                "aria-label",
                "Show password"
            );

        }

    }


    /* Mouse click */

    eye.addEventListener("click", function (event) {

        event.preventDefault();

        toggle();

    });


    /* Keyboard */

    eye.addEventListener("keydown", function (event) {

        if (
            event.key === "Enter" ||
            event.key === " "
        ) {

            event.preventDefault();

            toggle();

        }

    });

}


/* ==================================================
   LOGIN FORM
   IMPORTANT:
   DO NOT PREVENT SUBMIT

   Spring Boot /login must receive:
   username
   password
================================================== */

function loginValidation() {

    const form = document.getElementById("loginForm");


    if (!form) {

        return;

    }


    form.addEventListener("submit", function (event) {

        const email =
            document.getElementById("email");

        const password =
            document.getElementById("password");

        const button =
            form.querySelector(".login-btn");

        const buttonText =
            form.querySelector(".login-btn-text");


        if (!email || !password) {

            return;

        }


        const emailValue =
            email.value.trim();

        const passwordValue =
            password.value.trim();


        /* Empty validation */

        if (
            emailValue === "" ||
            passwordValue === ""
        ) {

            event.preventDefault();

            alert("Please fill all fields.");

            return;

        }


        /* Email validation */

        const emailPattern =
            /^[^\s@]+@[^\s@]+\.[^\s@]+$/;


        if (!emailPattern.test(emailValue)) {

            event.preventDefault();

            alert("Please enter a valid email address.");

            email.focus();

            return;

        }


        /*
         * DO NOT:
         * event.preventDefault()
         *
         * DO NOT:
         * window.location.href
         *
         * Spring Boot should receive the form.
         */


        if (button) {

            button.disabled = true;

        }


        if (buttonText) {

            buttonText.textContent = "Signing In...";

        }

    });

}


/* ==================================================
   REMEMBER USER
================================================== */

function rememberUser() {

    const remember =
        document.getElementById("rememberMe");

    const email =
        document.getElementById("email");


    if (!remember || !email) {

        return;

    }


    /* Load saved email */

    const savedEmail =
        localStorage.getItem("rememberEmail");


    if (savedEmail) {

        email.value = savedEmail;

        remember.checked = true;

    }


    /* Save / remove */

    remember.addEventListener("change", function () {

        if (remember.checked) {

            const emailValue =
                email.value.trim();


            if (emailValue !== "") {

                localStorage.setItem(
                    "rememberEmail",
                    emailValue
                );

            }

        }

        else {

            localStorage.removeItem(
                "rememberEmail"
            );

        }

    });


    /*
     * If user checks Remember Me
     * and then types email, update storage.
     */

    email.addEventListener("input", function () {

        if (remember.checked) {

            localStorage.setItem(
                "rememberEmail",
                email.value.trim()
            );

        }

    });

}


/* ==================================================
   GOOGLE LOGIN
================================================== */

function googleLogin() {

    const googleBtn =
        document.querySelector(".google-btn");


    if (!googleBtn) {

        return;

    }


    googleBtn.addEventListener("click", function () {

        alert(
            "Google Login will be integrated with Spring Boot later."
        );

    });

}