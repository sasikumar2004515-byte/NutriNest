document.addEventListener("DOMContentLoaded", function () {

    if (window.__nutrinestNavbarInitialized) {
        return;
    }

    window.__nutrinestNavbarInitialized = true;

    // ==========================================
    // ELEMENTS
    // ==========================================

    const menuBtn = document.getElementById("menuBtn");
    const sidebar = document.getElementById("sidebar");
    const closeSidebarBtn = document.getElementById("closeSidebar");
    const overlay = document.getElementById("overlay");

    const header = document.querySelector(".main-header");

    const searchBtn = document.getElementById("searchBtn");
    const searchExpand = document.getElementById("searchExpand");
    const searchInput = document.getElementById("searchInput");
    const searchClose = document.getElementById("searchClose");


    // ==========================================
    // SIDEBAR OPEN
    // ==========================================

    function openMenu(event) {

        if (event) {
            event.preventDefault();
            event.stopPropagation();
        }

        if (!sidebar || !overlay) {
            console.error("NutriNest: Sidebar or Overlay not found.");
            return;
        }

        sidebar.classList.add("active");
        overlay.classList.add("active");

        document.body.classList.add("sidebar-open");
        document.body.style.overflow = "hidden";
    }


    // ==========================================
    // SIDEBAR CLOSE
    // ==========================================

    function closeMenu(event) {

        if (event) {
            event.preventDefault();
            event.stopPropagation();
        }

        if (!sidebar || !overlay) {
            return;
        }

        sidebar.classList.remove("active");
        overlay.classList.remove("active");

        document.body.classList.remove("sidebar-open");
        document.body.style.overflow = "";
    }


    // ==========================================
    // MENU BUTTON
    // ==========================================

    if (menuBtn) {

        menuBtn.addEventListener("click", function (event) {
            openMenu(event);
        });

    } else {

        console.error("NutriNest: #menuBtn not found.");

    }


    // ==========================================
    // CLOSE BUTTON
    // ==========================================

    if (closeSidebarBtn) {

        closeSidebarBtn.addEventListener("click", function (event) {
            closeMenu(event);
        });

    }


    // ==========================================
    // OVERLAY
    // ==========================================

    if (overlay) {

        overlay.addEventListener("click", function (event) {
            closeMenu(event);
        });

    }


    // ==========================================
    // SIDEBAR LINKS
    // ==========================================

    if (sidebar) {

        const sidebarLinks = sidebar.querySelectorAll("a");

        sidebarLinks.forEach(function (link) {

            link.addEventListener("click", function () {
                closeMenu();
            });

        });

    }


    // ==========================================
    // ESC KEY
    // ==========================================

    document.addEventListener("keydown", function (event) {

        if (event.key === "Escape") {

            closeMenu();
            closeSearch();

        }

    });


    // ==========================================
    // SEARCH OPEN
    // ==========================================

    if (searchBtn) {

        searchBtn.addEventListener("click", function (event) {

            event.preventDefault();
            event.stopPropagation();

            if (!searchExpand) {
                return;
            }

            searchExpand.classList.add("active");

            if (searchInput) {
                setTimeout(function () {
                    searchInput.focus();
                }, 100);
            }

        });

    }


    // ==========================================
    // SEARCH CLOSE
    // ==========================================

    function closeSearch() {

        if (!searchExpand) {
            return;
        }

        searchExpand.classList.remove("active");

    }


    if (searchClose) {

        searchClose.addEventListener("click", function (event) {

            event.preventDefault();
            event.stopPropagation();

            if (searchInput) {
                searchInput.value = "";
            }

            closeSearch();

        });

    }


    // ==========================================
    // OUTSIDE CLICK - SEARCH ONLY
    // ==========================================

    document.addEventListener("click", function (event) {

        if (!searchExpand || !searchBtn) {
            return;
        }

        if (
            !searchExpand.contains(event.target) &&
            !searchBtn.contains(event.target)
        ) {

            closeSearch();

        }

    });


    // ==========================================
    // SCROLL
    // ==========================================

    window.addEventListener("scroll", function () {

        if (header) {

            if (window.scrollY > 50) {
                header.classList.add("scrolled");
            } else {
                header.classList.remove("scrolled");
            }

        }


        // Close search while scrolling

        if (
            searchExpand &&
            searchExpand.classList.contains("active")
        ) {

            if (searchInput) {
                searchInput.value = "";
            }

            closeSearch();

        }

    });


});
// ==========================================
// SEARCH SUBMIT
// ==========================================

if (searchInput) {

    searchInput.addEventListener("keydown", function (event) {

        if (event.key === "Enter") {

            event.preventDefault();
            event.stopPropagation();

            const keyword = searchInput.value.trim();

            // Empty search
            if (keyword.length === 0) {
                window.location.href = "/search";
                return;
            }

            // Go to search page with keyword
            window.location.href =
                "/search?keyword=" + encodeURIComponent(keyword);
        }

    });

}
