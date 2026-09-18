/*==================================
        PROFILE PAGE JS
==================================*/

document.addEventListener("DOMContentLoaded", () => {

    initializeProfile();

});

/*==================================
        INITIALIZE
==================================*/

function initializeProfile(){

    profileImageUpload();

    saveProfile();

    sidebarNavigation();

    logoutUser();

}

/*==================================
      PROFILE IMAGE
==================================*/

function profileImageUpload(){

    const image = document.querySelector(".profile-image img");

    if(!image) return;

    image.addEventListener("click",()=>{

        const input=document.createElement("input");

        input.type="file";

        input.accept="image/*";

        input.click();

        input.onchange=(event)=>{

            const file=event.target.files[0];

            if(file){

                const reader=new FileReader();

                reader.onload=function(e){

                    image.src=e.target.result;

                };

                reader.readAsDataURL(file);

            }

        };

    });

}

/*==================================
        SAVE PROFILE
==================================*/

function saveProfile(){

    const form=document.getElementById("profileForm");

    if(!form) return;

    form.addEventListener("submit",(e)=>{

        e.preventDefault();

        const button=document.querySelector(".save-profile-btn");

        button.innerHTML="✔ Saved Successfully";

        button.style.background="#43A047";

        setTimeout(()=>{

            button.innerHTML="Save Changes";

            button.style.background="linear-gradient(135deg,#2E7D32,#43A047)";

        },2000);

    });

}

/*==================================
      SIDEBAR MENU
==================================*/

function sidebarNavigation(){

    const menu=document.querySelectorAll(".profile-menu li");

    menu.forEach(item=>{

        item.addEventListener("click",()=>{

            menu.forEach(li=>li.classList.remove("active"));

            item.classList.add("active");

            const text=item.textContent.trim();

            switch(text){

                case "My Orders":

                    window.location.href="orders.html";

                    break;

                case "Saved Addresses":

                    window.location.href="checkout.html";

                    break;

                case "Wishlist":

                    window.location.href="wishlist.html";

                    break;

                case "Logout":

                    logoutUser();

                    break;

            }

        });

    });

}

/*==================================
          LOGOUT
==================================*/

function logoutUser(){

    const logout=document.querySelector(".profile-menu li:last-child");

    if(!logout) return;

    logout.addEventListener("click",(e)=>{

        e.preventDefault();

        const confirmLogout=confirm("Are you sure you want to logout?");

        if(confirmLogout){

            localStorage.removeItem("user");

            window.location.href="login.html";

        }

    });

}