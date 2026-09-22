/* =========================================================
   CHARLLOTTE - JAVASCRIPT
   ========================================================= */


/* =========================================================
   TABS
   ========================================================= */

function openTab(tabName, button) {

    // Busca todos os conteúdos das abas
    const contents = document.querySelectorAll(".tab-content");

    // Busca todos os botões das abas
    const buttons = document.querySelectorAll(".tab");


    // Remove a classe active de todos os conteúdos
    contents.forEach(content => {
        content.classList.remove("active");
    });


    // Remove a classe active de todos os botões
    buttons.forEach(btn => {
        btn.classList.remove("active");
    });


    // Ativa o conteúdo escolhido
    const selectedContent = document.getElementById(tabName);

    if (selectedContent) {
        selectedContent.classList.add("active");
    }


    // Ativa o botão clicado
    if (button) {
        button.classList.add("active");
    }
}


/* =========================================================
   LUCIDE ICONS
   ========================================================= */

document.addEventListener("DOMContentLoaded", function () {

    if (typeof lucide !== "undefined") {
        lucide.createIcons();
    }

});