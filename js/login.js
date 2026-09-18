const loginForm = document.getElementById("loginForm");

if (loginForm) {
    loginForm.addEventListener("submit", function(event) {

        event.preventDefault();

        const email = document.getElementById("email").value;
        const password = document.getElementById("password").value;

        if(email === "admin@gmail.com" && password === "12345"){
            alert("Login Successful!");
            window.location.href = "index.html";
        } else {
            alert("Invalid Email or Password");
        }

    });
}