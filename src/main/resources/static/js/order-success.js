/*=================================
      ORDER SUCCESS JS
=================================*/

document.addEventListener("DOMContentLoaded", () => {

    generateOrderId();

    launchConfetti();

    setupButtons();

});


/*=================================
      ORDER ID
=================================*/

function generateOrderId() {

    const orderElement = document.querySelector(".info-box strong");

    if (!orderElement) return;

    const randomNumber = Math.floor(100000 + Math.random() * 900000);

    orderElement.textContent = "#NN" + randomNumber;

}


/*=================================
      BUTTON EVENTS
=================================*/

function setupButtons() {

    const invoiceBtn = document.querySelector(".invoice-btn");

    const trackBtn = document.querySelector(".track-btn");

    const homeBtn = document.querySelector(".home-btn");


    if(invoiceBtn){

        invoiceBtn.addEventListener("click", () => {

            alert("Invoice download will be available soon.");

        });

    }


    if(trackBtn){

        trackBtn.addEventListener("click", () => {

            trackBtn.style.transform = "scale(.97)";

            setTimeout(()=>{

                trackBtn.style.transform="scale(1)";

            },150);

        });

    }


    if(homeBtn){

        homeBtn.addEventListener("click",(e)=>{

            homeBtn.style.opacity=".8";

        });

    }

}


/*=================================
      SIMPLE CONFETTI
=================================*/

function launchConfetti(){

    for(let i=0;i<120;i++){

        createConfetti();

    }

}

function createConfetti(){

    const confetti=document.createElement("span");

    confetti.className="confetti-piece";

    confetti.style.left=Math.random()*100+"vw";

    confetti.style.animationDuration=
        (Math.random()*3+2)+"s";

    confetti.style.opacity=Math.random();

    confetti.style.transform=
        `rotate(${Math.random()*360}deg)`;

    const colors=[

        "#2E7D32",

        "#66BB6A",

        "#FFD54F",

        "#81C784",

        "#43A047"

    ];

    confetti.style.background=
        colors[Math.floor(Math.random()*colors.length)];

    document.body.appendChild(confetti);

    setTimeout(()=>{

        confetti.remove();

    },5000);

}