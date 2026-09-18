document.addEventListener("DOMContentLoaded", function () {

    const newPassword =
        document.getElementById("newPassword");

    const confirmPassword =
        document.getElementById("confirmPassword");

    const strengthBar =
        document.getElementById("strengthBar");

    const strengthText =
        document.getElementById("strengthText");

    const matchMessage =
        document.getElementById("matchMessage");

    const submitButton =
        document.getElementById("changePasswordBtn");


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


    let passwordIsStrong = false;
    let passwordsMatch = false;



    /* =====================================================
                    PASSWORD REQUIREMENTS
    ===================================================== */

    function updateRequirement(element, valid) {

        if (!element) return;

        if (valid) {

            element.classList.add("valid");
            element.classList.remove("invalid");

        } else {

            element.classList.add("invalid");
            element.classList.remove("valid");

        }
    }



    /* =====================================================
                    PASSWORD STRENGTH
    ===================================================== */

    function checkPasswordStrength() {

        const password =
            newPassword.value;


        const hasLength =
            password.length >= 8;

        const hasUppercase =
            /[A-Z]/.test(password);

        const hasLowercase =
            /[a-z]/.test(password);

        const hasNumber =
            /[0-9]/.test(password);

        const hasSpecial =
            /[^A-Za-z0-9]/.test(password);


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

        if (hasLength) score++;
        if (hasUppercase) score++;
        if (hasLowercase) score++;
        if (hasNumber) score++;
        if (hasSpecial) score++;


        strengthBar.className = "";

        strengthText.className = "";


        if (password.length === 0) {

            strengthBar.style.width = "0";

            strengthText.textContent =
                "Enter password";

            passwordIsStrong = false;

        }

        else if (score <= 2) {

            strengthBar.classList.add("weak");

            strengthText.classList.add("weak");

            strengthText.textContent =
                "Weak";

            passwordIsStrong = false;

        }

        else if (score <= 4) {

            strengthBar.classList.add("medium");

            strengthText.classList.add("medium");

            strengthText.textContent =
                "Medium";

            passwordIsStrong = false;

        }

        else {

            strengthBar.classList.add("strong");

            strengthText.classList.add("strong");

            strengthText.textContent =
                "Strong";

            passwordIsStrong = true;

        }


        checkPasswordMatch();

        updateSubmitButton();
    }



    /* =====================================================
                    PASSWORD MATCH
    ===================================================== */

    function checkPasswordMatch() {

        const password =
            newPassword.value;

        const confirm =
            confirmPassword.value;


        if (confirm.length === 0) {

            matchMessage.textContent = "";

            matchMessage.className =
                "match-message";

            passwordsMatch = false;

            return;
        }


        if (password === confirm) {

            matchMessage.textContent =
                "✓ Passwords match";

            matchMessage.className =
                "match-message match";

            passwordsMatch = true;

        } else {

            matchMessage.textContent =
                "✕ Passwords do not match";

            matchMessage.className =
                "match-message no-match";

            passwordsMatch = false;

        }


        updateSubmitButton();
    }



    /* =====================================================
                    SUBMIT BUTTON
    ===================================================== */

    function updateSubmitButton() {

        submitButton.disabled =
            !(passwordIsStrong && passwordsMatch);

    }



    /* =====================================================
                    PASSWORD TOGGLE
    ===================================================== */

    const toggleButtons =
        document.querySelectorAll(
            ".password-toggle"
        );


    toggleButtons.forEach(function (button) {

        button.addEventListener(
            "click",
            function () {

                const targetId =
                    button.getAttribute(
                        "data-target"
                    );

                const input =
                    document.getElementById(
                        targetId
                    );

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

    });



    /* =====================================================
                    EVENT LISTENERS
    ===================================================== */

    newPassword.addEventListener(
        "input",
        checkPasswordStrength
    );


    confirmPassword.addEventListener(
        "input",
        checkPasswordMatch
    );


    /* =====================================================
                    INITIAL STATE
    ===================================================== */

    updateSubmitButton();

});