/*==================================
            CONTACT JS
==================================*/

document.addEventListener("DOMContentLoaded", () => {

    initializeContact();

});

/*==================================
          INITIALIZE
==================================*/

function initializeContact(){

    contactFormValidation();

    revealAnimation();

}

/*==================================
      CONTACT FORM VALIDATION
==================================*/

function contactFormValidation(){

    const form=document.getElementById("contactForm");

    if(!form) return;

    form.addEventListener("submit",(e)=>{

        e.preventDefault();

        const name=document.getElementById("name").value.trim();

        const email=document.getElementById("email").value.trim();

        const subject=document.getElementById("subject").value.trim();

        const message=document.getElementById("message").value.trim();

        if(name===""){

            alert("Please enter your name.");

            return;

        }

        if(!validateEmail(email)){

            alert("Please enter a valid email address.");

            return;

        }

        if(subject===""){

            alert("Please enter a subject.");

            return;

        }

        if(message.length<10){

            alert("Message should contain at least 10 characters.");

            return;

        }

        const button=document.querySelector(".send-btn");

        button.disabled=true;

        button.innerHTML="<i class='fa-solid fa-spinner fa-spin'></i> Sending...";

        setTimeout(()=>{

            alert("✅ Your message has been sent successfully.");

            form.reset();

            button.disabled=false;

            button.innerHTML="Send Message";

        },2000);

    });

}

/*==================================
        EMAIL VALIDATION
==================================*/

function validateEmail(email){

    return /^[^\s@]+@[^\s@]+\.[^\s@]+$/.test(email);

}

/*==================================
      SCROLL REVEAL ANIMATION
==================================*/

function revealAnimation(){

    const items=document.querySelectorAll(

        ".contact-info,.contact-form,.map-section,.info-card"

    );

    const observer=new IntersectionObserver((entries)=>{

        entries.forEach(entry=>{

            if(entry.isIntersecting){

                entry.target.classList.add("show");

            }

        });

    },{

        threshold:0.2

    });

    items.forEach(item=>{

        item.classList.add("hidden");

        observer.observe(item);

    });

}