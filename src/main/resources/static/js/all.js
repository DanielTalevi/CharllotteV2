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

/* =====================================================
   CHARLLOTTE - FUNÇÕES GERAIS
===================================================== */

document.addEventListener("DOMContentLoaded", function () {

    aplicarTema();

    if (typeof lucide !== "undefined") {
        lucide.createIcons();
    }

});


/* =====================================================
   TEMA
===================================================== */
/* =====================================================
   CHARLLOTTE - FUNÇÕES GERAIS
===================================================== */

document.addEventListener("DOMContentLoaded", function () {

    aplicarTema();

    if (typeof lucide !== "undefined") {

        lucide.createIcons();

    }

});


/* =====================================================
   TEMA GLOBAL
===================================================== */

function aplicarTema() {

    const dados =
        localStorage.getItem("charllotte_config");


    let configuracoes = {

        tema: "claro"

    };


    if (dados) {

        try {

            configuracoes =
                JSON.parse(dados);

        } catch (erro) {

            console.error(
                "Erro ao carregar tema:",
                erro
            );

        }

    }


    if (configuracoes.tema === "escuro") {

        document.body.classList.add(
            "tema-escuro"
        );

    } else {

        document.body.classList.remove(
            "tema-escuro"
        );

    }

}

document.addEventListener("DOMContentLoaded", function () {

    aplicarTema();

    aplicarTamanhoFonte();

    if (typeof lucide !== "undefined") {
        lucide.createIcons();
    }

});


/* =====================================================
   TEMA
===================================================== */

function aplicarTema() {

    const dados =
        localStorage.getItem("charllotte_config");

    let configuracoes = {
        tema: "claro"
    };

    if (dados) {

        try {

            configuracoes = JSON.parse(dados);

        } catch (erro) {

            console.error(
                "Erro ao carregar configurações:",
                erro
            );

        }

    }

    if (configuracoes.tema === "escuro") {

        document.body.classList.add("tema-escuro");

    } else {

        document.body.classList.remove("tema-escuro");

    }

}


/* =====================================================
   TAMANHO DA FONTE
===================================================== */

function aplicarTamanhoFonte() {

    const dados =
        localStorage.getItem("charllotte_config");

    let configuracoes = {
        tamanhoFonte: "normal"
    };

    if (dados) {

        try {

            configuracoes = JSON.parse(dados);

        } catch (erro) {

            console.error(
                "Erro ao carregar tamanho da fonte:",
                erro
            );

        }

    }

    if (configuracoes.tamanhoFonte === "grande") {

        document.body.classList.add("fonte-grande");

    } else {

        document.body.classList.remove("fonte-grande");

    }

}
const profileCards = document.querySelectorAll(".profile-card");

let selectProfile = "frentista";

profileCards.forEach(card => {
    card.addEventListener("click", () => {

        profileCards.forEach(item => {
            item.classList.remove("selected");
        });

        card.classList.add("selected");

        selectProfile = card.dataset.profile;

        console.log("Perfil selecionado:", selectProfile);
    });
});
