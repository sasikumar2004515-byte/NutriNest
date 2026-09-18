const addBtn = document.getElementById("showAddressForm");

const form = document.getElementById("newAddressForm");

addBtn.onclick = () => {

    form.classList.toggle("active");

};