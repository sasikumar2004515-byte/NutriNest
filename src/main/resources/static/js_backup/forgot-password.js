document.addEventListener("DOMContentLoaded", function () {

    const password =
        document.getElementById("password");

    const confirmPassword =
        document.getElementById("confirmPassword");

    const submitBtn =
        document.getElementById("submitBtn");

    const strengthBar =
        document.getElementById("strengthBar");

    const strengthText =
        document.getElementById("strengthText");

    const matchMessage =
        document.getElementById("matchMessage");


    /* =====================================================
       REQUIREMENTS
    ===================================================== */

    const lengthCheck =
        document.getElementById("lengthCheck");

    const uppercaseCheck =
        document.getElementById("uppercaseCheck");

    const lowercaseCheck =
        document.getElementById("lowercaseCheck");

    const numberCheck =
        document.getElementById("numberCheck");

    const specialCheck =
        document.getElementById("specialCheck");


    /* =====================================================
       PASSWORD TOGGLE
    ===================================================== */

    function setupPasswordToggle(
        input,
        button
    ) {

        if (!input || !button) {
            return;
        }

        button.addEventListener(
            "click",
            function () {

                const icon =
                    button.querySelector("i");

                if (input.type === "password") {

                    input.type = "text";

                    icon.classList.remove(
                        "fa-eye"
                    );

                    icon.classList.add(
                        "fa-eye-slash"
                    );

                    button.setAttribute(
                        "aria-label",
                        "Hide password"
                    );

                } else {

                    input.type = "password";

                    icon.classList.remove(
                        "fa-eye-slash"
                    );

                    icon.classList.add(
                        "fa-eye"
                    );

                    button.setAttribute(
                        "aria-label",
                        "Show password"
                    );

                }

            }
        );

    }


    setupPasswordToggle(
        password,
        document.getElementById(
            "passwordToggle"
        )
    );


    setupPasswordToggle(
        confirmPassword,
        document.getElementById(
            "confirmPasswordToggle"
        )
    );


    /* =====================================================
       REQUIREMENT UI
    ===================================================== */

    function updateRequirement(
        element,
        valid
    ) {

        if (!element) {
            return;
        }

        const icon =
            element.querySelector("i");

        if (valid) {

            element.classList.add(
                "valid"
            );

            element.classList.remove(
                "invalid"
            );

            icon.classList.remove(
                "fa-circle-xmark"
            );

            icon.classList.add(
                "fa-circle-check"
            );

        } else {

            element.classList.remove(
                "valid"
            );

            element.classList.add(
                "invalid"
            );

            icon.classList.remove(
                "fa-circle-check"
            );

            icon.classList.add(
                "fa-circle-xmark"
            );

        }

    }


    /* =====================================================
       PASSWORD STRENGTH
    ===================================================== */

    function checkPasswordStrength() {

        const value =
            password.value;


        const hasLength =
            value.length >= 8;

        const hasUppercase =
            /[A-Z]/.test(value);

        const hasLowercase =
            /[a-z]/.test(value);

        const hasNumber =
            /[0-9]/.test(value);

        const hasSpecial =
            /[^A-Za-z0-9]/.test(value);


        updateRequirement(
            lengthCheck,
            hasLength
        );

        updateRequirement(
            uppercaseCheck,
            hasUppercase
        );

        updateRequirement(
            lowercaseCheck,
            hasLowercase
        );

        updateRequirement(
            numberCheck,
            hasNumber
        );

        updateRequirement(
            specialCheck,
            hasSpecial
        );


        let score = 0;


        if (hasLength) {
            score++;
        }

        if (hasUppercase) {
            score++;
        }

        if (hasLowercase) {
            score++;
        }

        if (hasNumber) {
            score++;
        }

        if (hasSpecial) {
            score++;
        }


        /* ================================================
           EMPTY
        ================================================ */

        if (value.length === 0) {

            strengthBar.style.width = "0%";

            strengthText.textContent =
                "Password strength";

            strengthBar.className = "";

            return false;

        }


        /* ================================================
           WEAK
        ================================================ */

        if (score <= 2) {

            strengthBar.style.width =
                "33%";

            strengthBar.className =
                "weak";

            strengthText.textContent =
                "Weak password";

            return false;

        }


        /* ================================================
           MEDIUM
        ================================================ */

        if (score === 3 || score === 4) {

            strengthBar.style.width =
                "66%";

            strengthBar.className =
                "medium";

            strengthText.textContent =
                "Medium password";

            return false;

        }


        /* ================================================
           STRONG
        ================================================ */

        if (score === 5) {

            strengthBar.style.width =
                "100%";

            strengthBar.className =
                "strong";

            strengthText.textContent =
                "Strong password";

            return true;

        }


        return false;

    }


    /* =====================================================
       CONFIRM PASSWORD
    ===================================================== */

    function checkPasswordMatch() {

        const passwordValue =
            password.value;

        const confirmValue =
            confirmPassword.value;


        if (confirmValue.length === 0) {

            matchMessage.textContent = "";

            matchMessage.className =
                "match-message";

            return false;

        }


        if (
            passwordValue === confirmValue
        ) {

            matchMessage.textContent =
                "✓ Passwords match";

            matchMessage.className =
                "match-message match-success";

            return true;

        }


        matchMessage.textContent =
            "✕ Passwords do not match";

        matchMessage.className =
            "match-message match-error";

        return false;

    }


    /* =====================================================
       FINAL VALIDATION
    ===================================================== */

    function validateForm() {

        const strong =
            checkPasswordStrength();

        const matched =
            checkPasswordMatch();


        /*
         * STRONG + MATCH ONLY
         */

        submitBtn.disabled =
            !(strong && matched);

    }


    /* =====================================================
       PASSWORD INPUT
    ===================================================== */

    password.addEventListener(
        "input",
        function () {

            validateForm();

        }
    );


    /* =====================================================
       CONFIRM PASSWORD INPUT
    ===================================================== */

    confirmPassword.addEventListener(
        "input",
        function () {

            validateForm();

        }
    );


    /* =====================================================
       FORM SUBMIT PROTECTION
    ===================================================== */

    document
        .getElementById("forgotPasswordForm")
        .addEventListener(
            "submit",
            function (event) {

                const strong =
                    checkPasswordStrength();

                const matched =
                    checkPasswordMatch();


                if (
                    !strong ||
                    !matched
                ) {

                    event.preventDefault();

                    submitBtn.disabled = true;

                    return;

                }

            }
        );

});