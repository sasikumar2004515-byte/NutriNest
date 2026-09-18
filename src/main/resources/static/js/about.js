/*==================================
            ABOUT JS
==================================*/

document.addEventListener("DOMContentLoaded", () => {

    animateCounters();

    revealSections();

    smoothHoverEffects();

});

/*==================================
      SCROLL REVEAL ANIMATION
==================================*/

function revealSections(){

    const elements=document.querySelectorAll(

        ".story-image,.story-content,.mission-card,.feature-card,.team-card,.testimonial-card,.stat-box"

    );

    const observer=new IntersectionObserver((entries)=>{

        entries.forEach(entry=>{

            if(entry.isIntersecting){

                entry.target.classList.add("show");

            }

        });

    },{

        threshold:.2

    });

    elements.forEach(item=>{

        item.classList.add("hidden");

        observer.observe(item);

    });

}

/*==================================
      ANIMATED COUNTERS
==================================*/

function animateCounters(){

    const counters=document.querySelectorAll(".stat-box h2");

    let started=false;

    const statsSection=document.querySelector(".stats-section");

    if(!statsSection) return;

    const observer=new IntersectionObserver((entries)=>{

        entries.forEach(entry=>{

            if(entry.isIntersecting && !started){

                started=true;

                counters.forEach(counter=>{

                    const text=counter.innerText;

                    const target=parseInt(text.replace(/\D/g,""));

                    const suffix=text.replace(/[0-9]/g,"");

                    let value=0;

                    const speed=Math.max(15,2000/target);

                    const update=()=>{

                        if(value<target){

                            value++;

                            counter.innerText=value+suffix;

                            setTimeout(update,speed);

                        }
                        else{

                            counter.innerText=text;

                        }

                    };

                    update();

                });

            }

        });

    },{

        threshold:.5

    });

    observer.observe(statsSection);

}

/*==================================
      CARD HOVER EFFECT
==================================*/

function smoothHoverEffects(){

    const cards=document.querySelectorAll(

        ".mission-card,.feature-card,.team-card,.testimonial-card"

    );

    cards.forEach(card=>{

        card.addEventListener("mouseenter",()=>{

            card.style.transition=".3s";

        });

    });

}