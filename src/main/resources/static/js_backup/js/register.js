/*==================================
REGISTER JS
==================================*/

document.addEventListener("DOMContentLoaded", () => {
    initializeRegister();
});


/*==================================
INITIALIZE
==================================*/

function initializeRegister() {

    togglePassword();

    validateRegisterForm();

    passwordStrength();

    googleSignup();

}


/*==================================
SHOW / HIDE PASSWORD
==================================*/

function togglePassword() {

    const password = document.getElementById("password");
    const confirmPassword = document.getElementById("confirmPassword");
    const eyes = document.querySelectorAll(".toggle-password");

    if (!password || !confirmPassword || eyes.length === 0) {
        return;
    }

    /* Password eye */

    if (eyes[0]) {

        eyes[0].addEventListener("click", function () {

            if (password.type === "password") {

                password.type = "text";

                this.classList.remove("fa-eye");
                this.classList.add("fa-eye-slash");

                this.setAttribute(
                    "aria-label",
                    "Hide password"
                );

            } else {

                password.type = "password";

                this.classList.remove("fa-eye-slash");
                this.classList.add("fa-eye");

                this.setAttribute(
                    "aria-label",
                    "Show password"
                );

            }

        });

    }


    /* Confirm password eye */

    if (eyes[1]) {

        eyes[1].addEventListener("click", function () {

            if (confirmPassword.type === "password") {

                confirmPassword.type = "text";

                this.classList.remove("fa-eye");
                this.classList.add("fa-eye-slash");

                this.setAttribute(
                    "aria-label",
                    "Hide confirm password"
                );

            } else {

                confirmPassword.type = "password";

                this.classList.remove("fa-eye-slash");
                this.classList.add("fa-eye");

                this.setAttribute(
                    "aria-label",
                    "Show confirm password"
                );

            }

        });

    }

}


/*==================================
REGISTER VALIDATION
==================================*/

function validateRegisterForm() {

    const form = document.getElementById("registerForm");

    if (!form) return;

    form.addEventListener("submit", function (e) {

        const fullNameElement =
            document.getElementById("fullName");

        const emailElement =
            document.getElementById("email");

        const phoneElement =
            document.getElementById("phone");

        const passwordElement =
            document.getElementById("password");

        const confirmPasswordElement =
            document.getElementById("confirmPassword");


        if (
            !fullNameElement ||
            !emailElement ||
            !phoneElement ||
            !passwordElement ||
            !confirmPasswordElement
        ) {
            return;
        }


        const fullName =
            fullNameElement.value.trim();

        const email =
            emailElement.value.trim();

        const phone =
            phoneElement.value.trim();

        const password =
            passwordElement.value;

        const confirmPassword =
            confirmPasswordElement.value;


        /* Full name */

        if (fullName === "") {

            e.preventDefault();

            alert("Please enter your full name.");

            fullNameElement.focus();

            return;
        }


        /* Email */

        if (!validateEmail(email)) {

            e.preventDefault();

            alert("Please enter a valid email address.");

            emailElement.focus();

            return;
        }


        /* Phone */

        if (!/^[0-9]{10}$/.test(phone)) {

            e.preventDefault();

            alert(
                "Please enter a valid 10-digit mobile number."
            );

            phoneElement.focus();

            return;
        }


        /* Password */

        if (password.length < 6) {

            e.preventDefault();

            alert(
                "Password must be at least 6 characters."
            );

            passwordElement.focus();

            return;
        }


        /* Confirm password */

        if (password !== confirmPassword) {

            e.preventDefault();

            alert("Passwords do not match.");

            confirmPasswordElement.focus();

            return;
        }


        /* Valid -> allow Spring Boot submission */

        const button =
            document.querySelector(".register-btn");

        if (button) {

            button.innerHTML = "Creating Account...";

            button.disabled = true;

        }

        /*
         * IMPORTANT:
         * No e.preventDefault() here.
         *
         * Form will submit normally to:
         * POST /register
         */

    });

}


/*==================================
EMAIL VALIDATION
==================================*/

function validateEmail(email) {

    return /^[^\s@]+@[^\s@]+\.[^\s@]+$/.test(email);

}


/*==================================
PASSWORD STRENGTH
==================================*/

function passwordStrength() {

    const password =
        document.getElementById("password");

    const strength =
        document.getElementById("passwordStrength");

    if (!password || !strength) return;


    password.addEventListener("input", () => {

        const value = password.value;

        let score = 0;


        if (value.length >= 8)
            score++;

        if (/[A-Z]/.test(value))
            score++;

        if (/[a-z]/.test(value))
            score++;

        if (/[0-9]/.test(value))
            score++;

        if (/[@$!%*?&]/.test(value))
            score++;


        if (value.length === 0) {

            strength.innerHTML = "";

        }

        else if (score <= 2) {

            strength.innerHTML =
                "🔴 Weak Password";

            strength.style.color = "#d9534f";

        }

        else if (score <= 4) {

            strength.innerHTML =
                "🟡 Medium Password";

            strength.style.color = "#d88932";

        }

        else {

            strength.innerHTML =
                "🟢 Strong Password";

            strength.style.color = "#2e7d32";

        }

    });

}


/*==================================
GOOGLE SIGNUP
==================================*/

function googleSignup() {

    const googleBtn =
        document.querySelector(".google-btn");

    if (!googleBtn) return;


    googleBtn.addEventListener("click", () => {

        alert(
            "Google Sign Up will be integrated later."
        );

    });

}