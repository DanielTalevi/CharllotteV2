/* =====================================================
   CHARLLOTTE - REGISTRAR VENDA
===================================================== */

document.addEventListener("DOMContentLoaded", () => {

    const produtos =
        document.querySelectorAll(".produto-venda");

    const idProduto =
        document.getElementById("idProduto");

    const quantidade =
        document.getElementById("quantidade");

    const totalVenda =
        document.getElementById("totalVenda");

    const valorComissao =
        document.getElementById("valorComissao");

    const btnConfirmar =
        document.getElementById("btnConfirmarVenda");


    let produtoSelecionado = null;


    /* =================================================
       MODAL DE CONFIRMAÇÃO
    ================================================= */

    const modal =
        document.getElementById("modalConfirmacaoVenda");

    const btnFechar =
        document.getElementById("btnFecharConfirmacaoVenda");

    const btnCancelar =
        document.getElementById("btnCancelarConfirmacao");

    const btnFinalizar =
        document.getElementById("btnFinalizarVenda");

    const formVenda =
        document.getElementById("formVenda");


    /* =================================================
       DADOS DO MODAL
    ================================================= */

    const confirmacaoProduto =
        document.getElementById("confirmacaoProduto");

    const confirmacaoQuantidade =
        document.getElementById("confirmacaoQuantidade");

    const confirmacaoTotal =
        document.getElementById("confirmacaoTotal");

    const confirmacaoComissao =
        document.getElementById("confirmacaoComissao");


    let contador = null;


    /* =================================================
       SELECIONAR PRODUTO
    ================================================= */

    produtos.forEach(produto => {

        produto.addEventListener("click", () => {

            produtos.forEach(item => {

                item.classList.remove("selecionado");

            });


            produto.classList.add("selecionado");


            produtoSelecionado = {

                id: produto.dataset.id,

                nome: produto.dataset.nome,

                preco: Number(
                    produto.dataset.preco || 0
                ),

                comissao: Number(
                    produto.dataset.comissao || 0
                )

            };


            idProduto.value =
                produtoSelecionado.id;


            calcularVenda();

        });

    });


    /* =================================================
       CALCULAR
    ================================================= */

    function calcularVenda() {

        if (!produtoSelecionado) {

            totalVenda.textContent =
                "R$ 0,00";

            valorComissao.textContent =
                "R$ 0,00";

            btnConfirmar.disabled = true;

            return;

        }


        const qtd =
            Number(quantidade.value || 0);


        const total =
            qtd *
            produtoSelecionado.preco;


        const comissao =
            total *
            (
                produtoSelecionado.comissao /
                100
            );


        totalVenda.textContent =
            formatarMoeda(total);


        valorComissao.textContent =
            formatarMoeda(comissao);


        btnConfirmar.disabled =
            qtd <= 0;

    }


    /* =================================================
       QUANTIDADE
    ================================================= */

    quantidade
        ?.addEventListener(
            "input",
            calcularVenda
        );


    /* =================================================
       MOEDA
    ================================================= */

    function formatarMoeda(valor) {

        return valor.toLocaleString(
            "pt-BR",
            {
                style: "currency",
                currency: "BRL"
            }
        );

    }


    /* =================================================
       ABRIR MODAL
    ================================================= */

    btnConfirmar?.addEventListener(
        "click",
        () => {

            if (
                !produtoSelecionado ||
                Number(quantidade.value || 0) <= 0
            ) {

                return;

            }


            const qtd =
                Number(quantidade.value);


            const total =
                qtd *
                produtoSelecionado.preco;


            const comissao =
                total *
                (
                    produtoSelecionado.comissao /
                    100
                );


            /* -----------------------------------------
               PREENCHER MODAL
            ----------------------------------------- */

            confirmacaoProduto.textContent =
                produtoSelecionado.nome;


            confirmacaoQuantidade.textContent =
                qtd.toLocaleString(
                    "pt-BR",
                    {
                        minimumFractionDigits: 2,
                        maximumFractionDigits: 2
                    }
                );


            confirmacaoTotal.textContent =
                formatarMoeda(total);


            confirmacaoComissao.textContent =
                formatarMoeda(comissao);


            /* -----------------------------------------
               ABRIR
            ----------------------------------------- */

            modal.classList.add("ativo");


            /* -----------------------------------------
               INICIAR CONTADOR
            ----------------------------------------- */

            iniciarContador();

        }
    );


    /* =================================================
       CONTADOR DE 5 SEGUNDOS
    ================================================= */

    function iniciarContador() {

        clearInterval(contador);


        let segundos = 5;


        btnFinalizar.disabled = true;


        atualizarBotaoContador(segundos);


        contador = setInterval(() => {

            segundos--;


            if (segundos <= 0) {

                clearInterval(contador);

                contador = null;


                /* -------------------------------------
                   LIBERAR BOTÃO
                ------------------------------------- */

                btnFinalizar.disabled = false;


                btnFinalizar.innerHTML = `

                    <i data-lucide="check"></i>

                    Confirmar venda

                `;


                atualizarIcones();

                return;

            }


            atualizarBotaoContador(segundos);

        }, 1000);

    }


    /* =================================================
       ATUALIZAR BOTÃO DO CONTADOR
    ================================================= */

    function atualizarBotaoContador(segundos) {

        btnFinalizar.innerHTML = `

            Aguarde ${segundos}s

        `;

    }


    /* =================================================
       FECHAR MODAL
    ================================================= */

    function fecharModal() {

        clearInterval(contador);

        contador = null;


        modal.classList.remove("ativo");


        btnFinalizar.disabled = true;


        btnFinalizar.innerHTML = `

            <i data-lucide="check"></i>

            Confirmar venda

        `;


        atualizarIcones();

    }


    /* =================================================
       BOTÃO X
    ================================================= */

    btnFechar?.addEventListener(
        "click",
        fecharModal
    );


    /* =================================================
       BOTÃO CANCELAR
    ================================================= */

    btnCancelar?.addEventListener(
        "click",
        fecharModal
    );


    /* =================================================
       CLICAR FORA DO MODAL
    ================================================= */

    modal?.addEventListener(
        "click",
        event => {

            if (event.target === modal) {

                fecharModal();

            }

        }
    );


    /* =================================================
       CONFIRMAR VENDA
    ================================================= */

    btnFinalizar?.addEventListener(
        "click",
        () => {

            if (btnFinalizar.disabled) {

                return;

            }


            /*
             * SOMENTE AQUI O FORMULÁRIO É ENVIADO.
             *
             * Antes disso a venda não é registrada.
             */

            formVenda.submit();

        }
    );


    /* =================================================
       ESC PARA FECHAR
    ================================================= */

    document.addEventListener(
        "keydown",
        event => {

            if (
                event.key === "Escape" &&
                modal?.classList.contains("ativo")
            ) {

                fecharModal();

            }

        }
    );


    /* =================================================
       LUCIDE
    ================================================= */

    function atualizarIcones() {

        if (window.lucide) {

            lucide.createIcons();

        }

    }


    atualizarIcones();

});