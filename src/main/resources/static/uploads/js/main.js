window.addEventListener('DOMContentLoaded', function() {
    const isAdmin = document.body.dataset.isAdmin === "true";
    const isDesktop = window.innerWidth >= 992;
    if(isAdmin && isDesktop) {
        toggleNav();
    }
});
function toggleNav() {
  var sidebar = document.getElementById("mySidebar");
  var main = document.getElementById("main");
  sidebar.classList.toggle("show");
  main.classList.toggle("shifted");
}
document.getElementById("closeSidebarBtn").onclick = function() {
    document.getElementById("mySidebar").classList.remove("show");
    document.getElementById("main").classList.remove("shifted");
};