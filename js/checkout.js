const checkoutForm = document.getElementById("checkoutForm");

checkoutForm.addEventListener("submit", function(event){

    event.preventDefault();

    const fullName = document.querySelector('input[placeholder="Full Name"]').value.trim();
    const mobile = document.querySelector('input[placeholder="Mobile Number"]').value.trim();
    const address = document.querySelector('textarea').value.trim();

    if(fullName === "" || mobile === "" || address === ""){
        alert("Please fill all required fields.");
        return;
    }

    window.location.href = "order-success.html";

});