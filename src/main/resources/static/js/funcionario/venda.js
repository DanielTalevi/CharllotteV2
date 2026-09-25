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

});