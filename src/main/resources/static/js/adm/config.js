/* =====================================================
   CHARLLOTTE - CONFIGURAÇÕES
===================================================== */


/* =====================================================
   CHAVE DO LOCAL STORAGE
===================================================== */

const CONFIG_KEY = "charllotte_config";


/* =====================================================
   CONFIGURAÇÕES PADRÃO
===================================================== */

const CONFIG_PADRAO = {

    tema: "claro",

    tamanhoFonte: "normal",

    notificacoes: true,

    confirmacoes: true

};


/* =====================================================
   INICIALIZAÇÃO
===================================================== */

document.addEventListener("DOMContentLoaded", function () {

    carregarConfiguracoes();

    configurarTema();

    configurarTamanhoFonte();

    configurarPreferencias();

});


/* =====================================================
   OBTER CONFIGURAÇÕES
===================================================== */

function obterConfiguracoes() {

    const configuracoesSalvas =
        localStorage.getItem(CONFIG_KEY);


    if (!configuracoesSalvas) {

        return {
            ...CONFIG_PADRAO
        };

    }


    try {

        return {
            ...CONFIG_PADRAO,
            ...JSON.parse(configuracoesSalvas)
        };

    } catch (erro) {

        console.error(
            "Erro ao carregar configurações:",
            erro
        );

        return {
            ...CONFIG_PADRAO
        };

    }

}


/* =====================================================
   SALVAR CONFIGURAÇÕES
===================================================== */

function salvarConfiguracoes(configuracoes) {

    localStorage.setItem(
        CONFIG_KEY,
        JSON.stringify(configuracoes)
    );

}


/* =====================================================
   CARREGAR CONFIGURAÇÕES
===================================================== */

function carregarConfiguracoes() {

    const configuracoes =
        obterConfiguracoes();


    /* =================================================
       TEMA
    ================================================= */

    const tema =
        document.getElementById("tema");


    if (tema) {

        tema.value =
            configuracoes.tema;

    }


    /* =================================================
       TAMANHO DA FONTE
    ================================================= */

    const tamanhoFonte =
        document.getElementById("tamanhoFonte");


    if (tamanhoFonte) {

        tamanhoFonte.value =
            configuracoes.tamanhoFonte;

    }


    /* =================================================
       NOTIFICAÇÕES
    ================================================= */

    const notificacoes =
        document.getElementById("notificacoes");


    if (notificacoes) {

        notificacoes.checked =
            configuracoes.notificacoes;

    }


    /* =================================================
       CONFIRMAÇÕES
    ================================================= */

    const confirmacoes =
        document.getElementById("confirmacoes");


    if (confirmacoes) {

        confirmacoes.checked =
            configuracoes.confirmacoes;

    }

}


/* =====================================================
   TEMA
===================================================== */

function configurarTema() {

    const selectTema =
        document.getElementById("tema");


    if (!selectTema) {

        return;

    }


    selectTema.addEventListener(
        "change",
        function () {

            const configuracoes =
                obterConfiguracoes();


            configuracoes.tema =
                this.value;


            salvarConfiguracoes(
                configuracoes
            );


            /*
             * O all.js é responsável
             * por aplicar o tema global.
             */

            if (
                typeof aplicarTema === "function"
            ) {

                aplicarTema();

            }

        }
    );

}


/* =====================================================
   TAMANHO DA FONTE
===================================================== */

function configurarTamanhoFonte() {

    const selectFonte =
        document.getElementById(
            "tamanhoFonte"
        );


    if (!selectFonte) {

        return;

    }


    selectFonte.addEventListener(
        "change",
        function () {

            const configuracoes =
                obterConfiguracoes();


            configuracoes.tamanhoFonte =
                this.value;


            salvarConfiguracoes(
                configuracoes
            );


            aplicarTamanhoFonte(
                this.value
            );

        }
    );


    const configuracoes =
        obterConfiguracoes();


    aplicarTamanhoFonte(
        configuracoes.tamanhoFonte
    );

}


/* =====================================================
   APLICAR TAMANHO DA FONTE
===================================================== */

function aplicarTamanhoFonte(
    tamanho
) {

    document.body.classList.remove(
        "fonte-grande"
    );


    if (tamanho === "grande") {

        document.body.classList.add(
            "fonte-grande"
        );

    }

}


/* =====================================================
   PREFERÊNCIAS
===================================================== */

function configurarPreferencias() {

    const notificacoes =
        document.getElementById(
            "notificacoes"
        );


    if (notificacoes) {

        notificacoes.addEventListener(
            "change",
            function () {

                const configuracoes =
                    obterConfiguracoes();


                configuracoes.notificacoes =
                    this.checked;


                salvarConfiguracoes(
                    configuracoes
                );

            }
        );

    }


    const confirmacoes =
        document.getElementById(
            "confirmacoes"
        );


    if (confirmacoes) {

        confirmacoes.addEventListener(
            "change",
            function () {

                const configuracoes =
                    obterConfiguracoes();


                configuracoes.confirmacoes =
                    this.checked;


                salvarConfiguracoes(
                    configuracoes
                );

            }
        );

    }

}