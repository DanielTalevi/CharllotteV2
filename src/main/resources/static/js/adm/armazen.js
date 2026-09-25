/* =====================================================
   CHARLLOTTE - ARMAZÉM
===================================================== */

document.addEventListener("DOMContentLoaded", () => {

    const modalProduto =
        document.getElementById("modalProduto");

    const modalMovimentacao =
        document.getElementById("modalMovimentacao");


    const formProduto =
        document.getElementById("formProduto");

    const formMovimentacao =
        document.getElementById("formMovimentacao");


    const linhas =
        Array.from(
            document.querySelectorAll(".produto-linha")
        );


    const filtroNome =
        document.getElementById("filtroNome");

    const filtroUnidade =
        document.getElementById("filtroUnidade");

    const filtroStatus =
        document.getElementById("filtroStatus");


    const contadorProdutos =
        document.getElementById("contadorProdutos");


    const semResultadoFiltro =
        document.getElementById("semResultadoFiltro");


    /* =================================================
       MODAL PRODUTO
    ================================================= */

    function abrirModalProduto() {

        formProduto.reset();

        document.getElementById("produtoId").value = "";

        document.getElementById("modalTitulo").textContent =
            "Novo Produto";

        modalProduto.classList.add("aberto");

    }


    function fecharModalProduto() {

        modalProduto.classList.remove("aberto");

    }


    document
        .getElementById("btnNovoProduto")
        ?.addEventListener(
            "click",
            abrirModalProduto
        );


    document
        .getElementById("fecharModal")
        ?.addEventListener(
            "click",
            fecharModalProduto
        );


    document
        .getElementById("cancelarModal")
        ?.addEventListener(
            "click",
            fecharModalProduto
        );


    /* =================================================
       EDITAR PRODUTO
    ================================================= */

    function editarProduto(linha) {

        const id =
            linha.dataset.id;

        const nome =
            linha.dataset.nome;

        const unidade =
            linha.dataset.unidade;

        const quantidade =
            linha.dataset.quantidade;

        const minimo =
            linha.dataset.minimo;

        const preco =
            linha.dataset.preco;

        const comissao =
            linha.dataset.comissao;


        document.getElementById("produtoId").value =
            id || "";


        document.getElementById("nome").value =
            nome || "";


        document.getElementById("unidade").value =
            unidade || "";


        document.getElementById("quantidadeEstoque").value =
            quantidade || 0;


        document.getElementById("estoqueMinimo").value =
            minimo || 0;


        document.getElementById("preco").value =
            preco || 0;


        const campoComissao =
            document.getElementById("percentualComissao");

        if (campoComissao) {

            campoComissao.value =
                comissao || 0;

        }


        document.getElementById("modalTitulo").textContent =
            "Editar Produto";


        modalProduto.classList.add("aberto");

    }


    /* =================================================
       MODAL MOVIMENTAÇÃO
    ================================================= */

    function abrirMovimentacao(
        linha,
        tipo
    ) {

        const id =
            linha.dataset.id;

        const nome =
            linha.dataset.nome;


        document.getElementById("movimentacaoId").value =
            id;


        document.getElementById("movimentacaoProduto").textContent =
            nome;


        document.getElementById("quantidadeMovimentacao").value =
            "";


        document.getElementById("observacaoMovimentacao").value =
            "";


        const titulo =
            document.getElementById(
                "movimentacaoTitulo"
            );


        if (tipo === "entrada") {

            titulo.textContent =
                "Entrada de Estoque";

            formMovimentacao.action =
                `/armazen/produtos/${id}/entrada`;

        } else {

            titulo.textContent =
                "Saída de Estoque";

            formMovimentacao.action =
                `/armazen/produtos/${id}/saida`;

        }


        modalMovimentacao.classList.add("aberto");

    }


    function fecharModalMovimentacao() {

        modalMovimentacao.classList.remove("aberto");

    }


    document
        .getElementById("fecharMovimentacao")
        ?.addEventListener(
            "click",
            fecharModalMovimentacao
        );


    document
        .getElementById("cancelarMovimentacao")
        ?.addEventListener(
            "click",
            fecharModalMovimentacao
        );


    /* =================================================
       EXCLUIR
    ================================================= */

    function excluirProduto(linha) {

        const id =
            linha.dataset.id;

        const nome =
            linha.dataset.nome;


        if (!id) {
            return;
        }


        const confirmar =
            window.confirm(
                `Deseja realmente excluir o produto "${nome}"?`
            );


        if (!confirmar) {
            return;
        }


        const form =
            document.createElement("form");

        form.method = "POST";

        form.action =
            `/armazen/produtos/${id}/excluir`;


        document.body.appendChild(form);

        form.submit();

    }


    /* =================================================
       AÇÕES DA TABELA
    ================================================= */

    linhas.forEach(linha => {

        linha
            .querySelectorAll("[data-acao]")
            .forEach(botao => {

                botao.addEventListener(
                    "click",
                    () => {

                        const acao =
                            botao.dataset.acao;


                        if (acao === "editar") {

                            editarProduto(linha);

                        }


                        if (acao === "entrada") {

                            abrirMovimentacao(
                                linha,
                                "entrada"
                            );

                        }


                        if (acao === "saida") {

                            abrirMovimentacao(
                                linha,
                                "saida"
                            );

                        }


                        if (acao === "excluir") {

                            excluirProduto(linha);

                        }

                    }
                );

            });

    });


    /* =================================================
       STATUS
    ================================================= */

    function obterStatus(linha) {

        const quantidade =
            Number(
                linha.dataset.quantidade || 0
            );


        const minimo =
            Number(
                linha.dataset.minimo || 0
            );


        if (quantidade <= 0) {

            return "ZERADO";

        }


        if (quantidade <= minimo) {

            return "BAIXO";

        }


        return "OK";

    }


    /* =================================================
       FILTROS
    ================================================= */

    function aplicarFiltros() {

        const nome =
            filtroNome.value
                .trim()
                .toLowerCase();


        const unidade =
            filtroUnidade.value
                .trim()
                .toLowerCase();


        const status =
            filtroStatus.value
                .trim()
                .toUpperCase();


        let quantidadeVisivel = 0;


        linhas.forEach(linha => {

            const nomeProduto =
                (
                    linha.dataset.nome || ""
                ).toLowerCase();


            const unidadeProduto =
                (
                    linha.dataset.unidade || ""
                ).trim()
                    .toLowerCase();


            const statusProduto =
                obterStatus(linha);


            const correspondeNome =
                !nome ||
                nomeProduto.includes(nome);


            const correspondeUnidade =
                !unidade ||
                unidade === "todas" ||
                unidadeProduto === unidade;


            const correspondeStatus =
                !status ||
                status === "TODOS" ||
                statusProduto === status;


            const mostrar =
                correspondeNome &&
                correspondeUnidade &&
                correspondeStatus;


            linha.style.display =
                mostrar
                    ? ""
                    : "none";


            if (mostrar) {

                quantidadeVisivel++;

            }

        });


        contadorProdutos.textContent =
            `${quantidadeVisivel} ${
                quantidadeVisivel === 1
                    ? "produto"
                    : "produtos"
            }`;


        if (
            linhas.length > 0 &&
            quantidadeVisivel === 0
        ) {

            semResultadoFiltro.classList.remove(
                "oculto"
            );

        } else {

            semResultadoFiltro.classList.add(
                "oculto"
            );

        }

    }


    filtroNome
        ?.addEventListener(
            "input",
            aplicarFiltros
        );


    filtroUnidade
        ?.addEventListener(
            "change",
            aplicarFiltros
        );


    filtroStatus
        ?.addEventListener(
            "change",
            aplicarFiltros
        );


    /* =================================================
       LIMPAR FILTROS
    ================================================= */

    document
        .getElementById("btnLimparFiltro")
        ?.addEventListener(
            "click",
            () => {

                filtroNome.value = "";

                filtroUnidade.value = "";

                filtroStatus.value = "";

                aplicarFiltros();

            }
        );


    /* =================================================
       FECHAR MODAIS CLICANDO FORA
    ================================================= */

    modalProduto
        ?.addEventListener(
            "click",
            event => {

                if (
                    event.target === modalProduto
                ) {

                    fecharModalProduto();

                }

            }
        );


    modalMovimentacao
        ?.addEventListener(
            "click",
            event => {

                if (
                    event.target === modalMovimentacao
                ) {

                    fecharModalMovimentacao();

                }

            }
        );


    /* =================================================
       ESC
    ================================================= */

    document.addEventListener(
        "keydown",
        event => {

            if (event.key !== "Escape") {
                return;
            }


            fecharModalProduto();

            fecharModalMovimentacao();

        }
    );


    /* =================================================
       CONTADOR INICIAL
    ================================================= */

    aplicarFiltros();

});