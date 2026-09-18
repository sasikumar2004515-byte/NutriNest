document.addEventListener("DOMContentLoaded", () => {

    const footerContainer = document.getElementById("footer");

    if (!footerContainer) return;

    fetch("footer.html")
        .then(response => {

            if (!response.ok) {
                throw new Error("Unable to load footer.");
            }

            return response.text();

        })
        .then(data => {

            footerContainer.innerHTML = data;

            activateFooterLink();

            updateFooterYear();

        })
        .catch(error => {

            console.error(error);

            footerContainer.innerHTML = `
                <div style="text-align:center;padding:20px;color:red;">
                    Footer could not be loaded.
                </div>
            `;

        });

});


function activateFooterLink() {

    const currentPage = window.location.pathname.split("/").pop();

    const footerLinks = document.querySelectorAll(".footer-links a");

    footerLinks.forEach(link => {

        const href = link.getAttribute("href");

        if (href === currentPage) {

            link.classList.add("footer-active");

        }

    });

}


function updateFooterYear() {

    const year = document.getElementById("footerYear");

    if (year) {

        year.textContent = new Date().getFullYear();

    }

}
const year = document.getElementById("footerYear");

if(year){

    year.textContent = new Date().getFullYear();

}