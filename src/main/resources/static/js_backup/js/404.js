/*==================================
            404 JS
==================================*/

document.addEventListener("DOMContentLoaded", () => {

    initialize404();

});

/*==================================
        INITIALIZE
==================================*/

function initialize404(){

    animate404();

    homeButton();

    shopButton();

    searchProduct();

}

/*==================================
      PAGE ANIMATION
==================================*/

function animate404(){

    const content=document.querySelector(".error-content");

    const image=document.querySelector(".error-image");

    if(content){

        content.style.opacity="0";
        content.style.transform="translateY(40px)";

    }

    if(image){

        image.style.opacity="0";
        image.style.transform="translateY(40px)";

    }

    setTimeout(()=>{

        if(content){

            content.style.transition=".8s";
            content.style.opacity="1";
            content.style.transform="translateY(0)";

        }

        if(image){

            image.style.transition=".8s";
            image.style.opacity="1";
            image.style.transform="translateY(0)";

        }

    },300);

}

/*==================================
      HOME BUTTON
==================================*/

function homeButton(){

    const home=document.querySelector(".home-btn");

    if(!home) return;

    home.addEventListener("click",()=>{

        console.log("Redirecting to Home...");

    });

}

/*==================================
      SHOP BUTTON
==================================*/

function shopButton(){

    const shop=document.querySelector(".shop-btn");

    if(!shop) return;

    shop.addEventListener("click",()=>{

        console.log("Redirecting to Products...");

    });

}

/*==================================
      SEARCH
==================================*/

function searchProduct(){

    const input=document.querySelector(".search-box input");

    const button=document.querySelector(".search-box button");

    if(!input || !button) return;

    function search(){

        const keyword=input.value.trim();

        if(keyword===""){

            alert("Please enter a product name.");

            return;

        }

        window.location.href="search.html?query="+encodeURIComponent(keyword);

    }

    button.addEventListener("click",search);

    input.addEventListener("keypress",(e)=>{

        if(e.key==="Enter"){

            search();

        }

    });

}