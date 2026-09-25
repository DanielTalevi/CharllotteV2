document.addEventListener("DOMContentLoaded", function () {

    const modal = document.getElementById("modalAlterarSenha");
    const btnAbrir = document.getElementById("btnAbrirAlterarSenha");
    const btnFechar = document.getElementById("btnFecharAlterarSenha");
    const btnCancelar = document.getElementById("btnCancelarAlterarSenha");

    if (!modal || !btnAbrir) {
        console.error("Modal ou botão de alterar senha não encontrado.");
        return;
    }


    // =====================================================
    // ABRIR MODAL
    // =====================================================

    btnAbrir.addEventListener("click", function () {

        modal.classList.add("ativo");

    });


    // =====================================================
    // FECHAR MODAL
    // =====================================================

    function fecharModal() {

        modal.classList.remove("ativo");

    }


    if (btnFechar) {

        btnFechar.addEventListener(
            "click",
            fecharModal
        );

    }


    if (btnCancelar) {

        btnCancelar.addEventListener(
            "click",
            fecharModal
        );

    }


    // =====================================================
    // CLICAR FORA DO MODAL
    // =====================================================

    modal.addEventListener("click", function (event) {

        if (event.target === modal) {

            fecharModal();

        }

    });


    // =====================================================
    // ESC
    // =====================================================

    document.addEventListener("keydown", function (event) {

        if (event.key === "Escape") {

            fecharModal();

        }

    });


    // =====================================================
    // MOSTRAR / OCULTAR SENHA
    // =====================================================

    const botoes = modal.querySelectorAll(".btn-mostrar-senha");

    botoes.forEach(function (botao) {

        botao.addEventListener("click", function () {

            const id = this.dataset.target;

            const input = document.getElementById(id);

            if (!input) {
                return;
            }


            if (input.type === "password") {

                input.type = "text";

                this.innerHTML =
                    '<i data-lucide="eye-off"></i>';

            } else {

                input.type = "password";

                this.innerHTML =
                    '<i data-lucide="eye"></i>';

            }


            if (typeof lucide !== "undefined") {

                lucide.createIcons();

            }

        });

    });

});