/*====================================
        NUTRINEST ANIMATION V3
====================================*/

document.addEventListener("DOMContentLoaded", () => {

    /* ==========================
            LOADER
    ========================== */

    const loader = document.querySelector(".loader");

    if(loader){

      window.addEventListener("load", () => {

          const loader = document.querySelector(".loader");

          if (loader) {

              setTimeout(() => {

                  loader.classList.add("hide");

              }, 1800);

          }

      });

    }


    /* ==========================
        SCROLL PROGRESS BAR
    ========================== */

    const progress = document.getElementById("scroll-progress");

    function updateProgress(){

        if(!progress) return;

        const scroll = window.scrollY;

        const height =
        document.documentElement.scrollHeight -
        window.innerHeight;

        const percent = (scroll / height) * 100;

        progress.style.width = percent + "%";

    }

    window.addEventListener("scroll", updateProgress);


    /* ==========================
          GLASS NAVBAR
    ========================== */

    const header = document.querySelector(".main-header");

    function navbarEffect(){

        if(!header) return;

        if(window.scrollY > 50){

            header.classList.add("scrolled");

        }else{

            header.classList.remove("scrolled");

        }

    }

    window.addEventListener("scroll", navbarEffect);

    navbarEffect();


    /* ==========================
        SCROLL REVEAL
    ========================== */

    const reveals = document.querySelectorAll(".reveal");

    function revealElements(){

        const trigger = window.innerHeight - 100;

        reveals.forEach(item=>{

            const top = item.getBoundingClientRect().top;

            if(top < trigger){

                item.classList.add("active");

            }

        });

    }

    window.addEventListener("scroll", revealElements);

    revealElements();


    /* ==========================
        BUTTON RIPPLE
    ========================== */

    document.querySelectorAll(".btn, button").forEach(btn=>{

        btn.addEventListener("click",function(e){

            const ripple=document.createElement("span");

            ripple.className="ripple";

            const rect=this.getBoundingClientRect();

            ripple.style.left=(e.clientX-rect.left)+"px";

            ripple.style.top=(e.clientY-rect.top)+"px";

            this.appendChild(ripple);

            setTimeout(()=>{

                ripple.remove();

            },600);

        });

    });

});

function revealOnScroll(){

    const elements = document.querySelectorAll(
        ".reveal, .reveal-left, .reveal-right, .reveal-zoom"
    );

    const trigger = window.innerHeight * 0.85;

    elements.forEach(el => {

        const top = el.getBoundingClientRect().top;

        if(top < trigger){
            el.classList.add("active");
        }

    });

}

