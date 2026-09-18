/*==================================
            SEARCH JS
==================================*/

document.addEventListener("DOMContentLoaded", () => {

    initializeSearch();

});

/*==================================
        INITIALIZE
==================================*/

function initializeSearch(){

    updatePrice();

    searchProducts();

    filterProducts();

    sortProducts();

}

/*==================================
        PRICE RANGE
==================================*/

function updatePrice(){

    const priceRange=document.getElementById("priceRange");

    const priceValue=document.getElementById("priceValue");

    if(!priceRange || !priceValue) return;

    priceValue.innerText=priceRange.value;

    priceRange.addEventListener("input",()=>{

        priceValue.innerText=priceRange.value;

        filterProducts();

    });

}

/*==================================
        SEARCH PRODUCT
==================================*/

function searchProducts(){

    const input=document.getElementById("searchInput");

    if(!input) return;

    input.addEventListener("keyup",()=>{

        filterProducts();

    });

}

/*==================================
        FILTER PRODUCTS
==================================*/

function filterProducts(){

    const keyword=document.getElementById("searchInput").value.toLowerCase();

    const maxPrice=parseInt(document.getElementById("priceRange").value);

    const products=document.querySelectorAll(".product-card");

    const noResults=document.querySelector(".no-results");

    let visibleProducts=0;

    products.forEach(product=>{

        const title=product.querySelector("h3").innerText.toLowerCase();

        const price=parseInt(

            product.querySelector("span").innerText.replace(/[₹,]/g,"")

        );

        if(title.includes(keyword) && price<=maxPrice){

            product.style.display="block";

            visibleProducts++;

        }

        else{

            product.style.display="none";

        }

    });

    if(visibleProducts===0){

        noResults.style.display="block";

    }

    else{

        noResults.style.display="none";

    }

}

/*==================================
        SORT PRODUCTS
==================================*/

function sortProducts(){

    const sort=document.getElementById("sortProducts");

    const grid=document.querySelector(".product-grid");

    if(!sort || !grid) return;

    sort.addEventListener("change",()=>{

        const cards=[...document.querySelectorAll(".product-card")];

        if(sort.value==="Price : Low to High"){

            cards.sort((a,b)=>{

                return getPrice(a)-getPrice(b);

            });

        }

        else if(sort.value==="Price : High to Low"){

            cards.sort((a,b)=>{

                return getPrice(b)-getPrice(a);

            });

        }

        cards.forEach(card=>{

            grid.appendChild(card);

        });

    });

}

/*==================================
        GET PRICE
==================================*/

function getPrice(card){

    return parseInt(

        card.querySelector("span").innerText.replace(/[₹,]/g,"")

    );

}