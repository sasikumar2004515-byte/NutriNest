const registerForm = document.getElementById("registerForm");

if (registerForm) {
    registerForm.addEventListener("submit", function(event) {
        event.preventDefault();

        alert("Registration Successful!");

        window.location.href = "login.html";
    });
}