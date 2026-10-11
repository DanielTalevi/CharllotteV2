document.addEventListener("DOMContentLoaded", function () {

    const modal =
        document.getElementById("modalCadastro");

    const btnAbrir =
        document.getElementById("btnAbrirCadastro");

    const btnFechar =
        document.getElementById("btnFecharCadastro");

    const btnCancelar =
        document.getElementById("btnCancelarCadastro");


    if (!modal || !btnAbrir) {
        return;
    }


    /* =================================================
       ABRIR
    ================================================= */

    function abrirModal() {

        modal.classList.add("aberto");

        document.body.style.overflow = "hidden";

        const primeiroCampo =
            document.getElementById("nome");

        if (primeiroCampo) {
            setTimeout(function () {
                primeiroCampo.focus();
            }, 200);
        }

    }


    /* =================================================
       FECHAR
    ================================================= */

    function fecharModal() {

        modal.classList.remove("aberto");

        document.body.style.overflow = "";

    }


    /* =================================================
       BOTÃO ADICIONAR
    ================================================= */

    btnAbrir.addEventListener(
        "click",
        abrirModal
    );


    /* =================================================
       BOTÃO X
    ================================================= */

    if (btnFechar) {

        btnFechar.addEventListener(
            "click",
            fecharModal
        );

    }


    /* =================================================
       BOTÃO CANCELAR
    ================================================= */

    if (btnCancelar) {

        btnCancelar.addEventListener(
            "click",
            fecharModal
        );

    }


    /* =================================================
       CLICAR FORA
    ================================================= */

    modal.addEventListener(
        "click",
        function (evento) {

            if (evento.target === modal) {
                fecharModal();
            }

        }
    );


    /* =================================================
       ESC
    ================================================= */

    document.addEventListener(
        "keydown",
        function (evento) {

            if (
                evento.key === "Escape" &&
                modal.classList.contains("aberto")
            ) {

                fecharModal();

            }

        }
    );


    /* =================================================
       MOSTRAR / ESCONDER SENHA
    ================================================= */

    const botoesSenha =
        document.querySelectorAll(
            ".btn-mostrar-senha"
        );


    botoesSenha.forEach(function (botao) {

        botao.addEventListener(
            "click",
            function () {

                const id =
                    botao.dataset.target;

                const input =
                    document.getElementById(id);

                if (!input) {
                    return;
                }


                if (input.type === "password") {

                    input.type = "text";

                    botao.innerHTML =
                        '<i data-lucide="eye-off"></i>';

                } else {

                    input.type = "password";

                    botao.innerHTML =
                        '<i data-lucide="eye"></i>';

                }


                if (
                    typeof lucide !== "undefined"
                ) {

                    lucide.createIcons();

                }

            }
        );

    });

        /* =================================================
       MÁSCARA DE CPF
    ================================================= */

    const campoCPF = document.getElementById("cpf");

    if (campoCPF) {
        campoCPF.addEventListener("input", function () {
            let cpf = campoCPF.value
                .replace(/\D/g, "")
                .slice(0, 11);

            if (cpf.length > 9) {
                cpf = cpf.replace(
                    /^(\d{3})(\d{3})(\d{3})(\d{1,2})$/,
                    "$1.$2.$3-$4"
                );
            } else if (cpf.length > 6) {
                cpf = cpf.replace(
                    /^(\d{3})(\d{3})(\d{1,3})$/,
                    "$1.$2.$3"
                );
            } else if (cpf.length > 3) {
                cpf = cpf.replace(
                    /^(\d{3})(\d{1,3})$/,
                    "$1.$2"
                );
            }

            campoCPF.value = cpf;
        });
    }
});