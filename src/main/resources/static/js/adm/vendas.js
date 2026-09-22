document.addEventListener("DOMContentLoaded", function () {

    criarGraficoVendas();

    atualizarResumoFiltro();

});



/* =====================================================
   RESUMO DO FILTRO
===================================================== */

function atualizarResumoFiltro() {

    const linhas = document.querySelectorAll(
        ".table-container tbody tr[data-data]"
    );


    let realizadas = 0;

    let canceladas = 0;

    const datas = [];


    linhas.forEach(function (linha) {

        const status = linha.dataset.status;

        const data = linha.dataset.data;


        /*
         * Conta vendas realizadas
         */

        if (status === "REALIZADA") {

            realizadas++;

        }


        /*
         * Conta vendas canceladas
         */

        if (status === "CANCELADA") {

            canceladas++;

        }


        /*
         * Guarda as datas
         */

        if (data) {

            const dataVenda = new Date(data);


            if (!isNaN(dataVenda.getTime())) {

                datas.push(dataVenda);

            }

        }

    });


    /*
     * Atualiza quantidade de realizadas
     */

    const elementoRealizadas =
        document.getElementById("vendasRealizadas");


    if (elementoRealizadas) {

        elementoRealizadas.textContent =
            realizadas;

    }


    /*
     * Atualiza quantidade de canceladas
     */

    const elementoCanceladas =
        document.getElementById("vendasCanceladas");


    if (elementoCanceladas) {

        elementoCanceladas.textContent =
            canceladas;

    }


    /*
     * Atualiza período
     */

    const elementoPeriodo =
        document.getElementById("periodoVendas");


    if (
        elementoPeriodo &&
        datas.length > 0
    ) {


        /*
         * Descobre a menor data
         */

        const menorData =
            new Date(
                Math.min(
                    ...datas.map(
                        data => data.getTime()
                    )
                )
            );


        /*
         * Descobre a maior data
         */

        const maiorData =
            new Date(
                Math.max(
                    ...datas.map(
                        data => data.getTime()
                    )
                )
            );


        /*
         * Formata as datas
         */

        const formatar =
            new Intl.DateTimeFormat(
                "pt-BR",
                {
                    month: "short",
                    year: "numeric"
                }
            );


        const inicio =
            formatar
                .format(menorData)
                .replace(".", "");


        const fim =
            formatar
                .format(maiorData)
                .replace(".", "");


        elementoPeriodo.textContent =
            `${inicio} — ${fim}`;

    }

}



/* =====================================================
   GRÁFICO DE VENDAS
===================================================== */

function criarGraficoVendas() {

    const grafico =
        document.getElementById(
            "graficoVendas"
        );


    /*
     * Se o gráfico não existir,
     * encerra.
     */

    if (!grafico) {

        return;

    }


    /*
     * Pega somente as linhas
     * que possuem venda.
     */

    const linhas =
        document.querySelectorAll(
            ".table-container tbody tr[data-data]"
        );


    const vendasPorMes = {};



    /* =================================================
       LER VENDAS
    ================================================= */

    linhas.forEach(function (linha) {

        const data =
            linha.dataset.data;


        const valor =
            parseFloat(
                linha.dataset.valor
            );


        const status =
            linha.dataset.status;


        /*
         * Ignora dados inválidos.
         */

        if (
            !data ||
            isNaN(valor)
        ) {

            return;

        }


        /*
         * Venda cancelada
         * não entra no gráfico.
         */

        if (
            status !== "REALIZADA"
        ) {

            return;

        }


        const dataVenda =
            new Date(data);


        if (
            isNaN(
                dataVenda.getTime()
            )
        ) {

            return;

        }


        const ano =
            dataVenda.getFullYear();


        const mes =
            dataVenda.getMonth();


        /*
         * Cria uma chave:
         *
         * 2026-07
         * 2026-08
         * 2026-09
         */

        const chave =
            `${ano}-${String(
                mes + 1
            ).padStart(2, "0")}`;


        /*
         * Se o mês ainda
         * não existe, cria.
         */

        if (
            !vendasPorMes[chave]
        ) {

            vendasPorMes[chave] = {

                ano: ano,

                mes: mes,

                valor: 0

            };

        }


        /*
         * Soma a venda.
         */

        vendasPorMes[chave].valor +=
            valor;

    });



    /* =================================================
       ORGANIZAR MESES
    ================================================= */

    let meses =
        Object.values(
            vendasPorMes
        );


    /*
     * Ordena por data.
     */

    meses.sort(function (a, b) {

        if (
            a.ano !== b.ano
        ) {

            return a.ano - b.ano;

        }


        return a.mes - b.mes;

    });


    /*
     * Mostra somente os
     * últimos 3 meses.
     */

    meses =
        meses.slice(-3);



    /* =================================================
       NENHUMA VENDA
    ================================================= */

    if (
        meses.length === 0
    ) {

        grafico.innerHTML = `

            <div class="grafico-vazio">

                Nenhuma venda realizada

            </div>

        `;

        return;

    }



    /* =================================================
       MAIOR VALOR
    ================================================= */

    const maiorValor =
        Math.max(
            ...meses.map(
                mes => mes.valor
            )
        );



    /* =================================================
       NOMES DOS MESES
    ================================================= */

    const nomesMeses = [

        "Jan",
        "Fev",
        "Mar",
        "Abr",
        "Mai",
        "Jun",
        "Jul",
        "Ago",
        "Set",
        "Out",
        "Nov",
        "Dez"

    ];



    /*
     * Limpa gráfico.
     */

    grafico.innerHTML = "";



    /* =================================================
       CRIAR BARRAS
    ================================================= */

    meses.forEach(function (item) {


        /*
         * Coluna
         */

        const coluna =
            document.createElement(
                "div"
            );


        coluna.classList.add(
            "grafico-coluna"
        );



        /*
         * Altura proporcional
         */

        let altura =
            (
                item.valor /
                maiorValor
            ) * 100;


        /*
         * Altura mínima
         */

        if (
            altura < 8
        ) {

            altura = 8;

        }



        /*
         * Área da barra
         */

        const barraArea =
            document.createElement(
                "div"
            );


        barraArea.classList.add(
            "grafico-barra-area"
        );



        /*
         * Barra
         */

        const barra =
            document.createElement(
                "div"
            );


        barra.classList.add(
            "grafico-barra"
        );


        barra.style.height =
            `${altura}%`;



        /*
         * Valor
         */

        const valor =
            document.createElement(
                "span"
            );


        valor.classList.add(
            "grafico-valor"
        );


        valor.textContent =
            formatarMoeda(
                item.valor
            );



        /*
         * Mês
         */

        const mes =
            document.createElement(
                "span"
            );


        mes.classList.add(
            "grafico-mes"
        );


        mes.textContent =
            nomesMeses[
                item.mes
                ];



        /*
         * Monta a barra
         */

        barraArea.appendChild(
            barra
        );


        coluna.appendChild(
            valor
        );


        coluna.appendChild(
            barraArea
        );


        coluna.appendChild(
            mes
        );


        grafico.appendChild(
            coluna
        );

    });

}



/* =====================================================
   FORMATAR MOEDA
===================================================== */

function formatarMoeda(valor) {

    return valor.toLocaleString(

        "pt-BR",

        {

            style: "currency",

            currency: "BRL",

            minimumFractionDigits: 0,

            maximumFractionDigits: 0

        }

    );

}