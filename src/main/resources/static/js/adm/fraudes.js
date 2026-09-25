document.addEventListener("DOMContentLoaded", function () {


    /* =====================================================
       ELEMENTOS
    ===================================================== */

    const tabela =
        document.getElementById("tabelaFraudes");

    const linhas =
        Array.from(
            tabela.querySelectorAll(
                "tr[data-id]"
            )
        );


    const filtroStatus =
        document.getElementById("filtroStatus");

    const filtroTipo =
        document.getElementById("filtroTipo");

    const dataInicio =
        document.getElementById("dataInicioValue");

    const dataFim =
        document.getElementById("dataFimValue");

    const btnFiltrar =
        document.getElementById("btnFiltrar");

    const btnLimpar =
        document.getElementById("btnLimpar");

    const semFraudes =
        document.getElementById("semFraudes");



    /* =====================================================
       RESUMOS
    ===================================================== */

    const totalFraudes =
        document.getElementById("totalFraudes");

    const fraudesPendentes =
        document.getElementById("fraudesPendentes");

    const fraudesAnalise =
        document.getElementById("fraudesAnalise");

    const fraudesResolvidas =
        document.getElementById("fraudesResolvidas");


    const cardTotalFraudes =
        document.getElementById("cardTotalFraudes");

    const cardPendentes =
        document.getElementById("cardPendentes");

    const cardAnalise =
        document.getElementById("cardAnalise");

    const cardResolvidas =
        document.getElementById("cardResolvidas");



    /* =====================================================
       MODAL
    ===================================================== */

    const modal =
        document.getElementById("modalFraude");

    const fecharModal =
        document.getElementById("fecharModal");

    const btnFecharModal =
        document.getElementById("btnFecharModal");



    /* =====================================================
       FORMATAR VALOR
    ===================================================== */

    function formatarValor(valor) {

        if (
            valor === null ||
            valor === undefined ||
            valor === ""
        ) {
            return "—";
        }


        const numero =
            Number(valor);


        if (isNaN(numero)) {
            return "—";
        }


        return numero.toLocaleString(
            "pt-BR",
            {
                style: "currency",
                currency: "BRL"
            }
        );

    }



    /* =====================================================
       FORMATAR TIPO
    ===================================================== */

    function formatarTipo(tipo) {

        if (!tipo) {
            return "Não informado";
        }


        switch (tipo) {

            case "VALOR_ELEVADO":
                return "Valor elevado";

            case "VALOR_FORA_PADRAO":
                return "Valor fora do padrão";

            case "SEM_FUNCIONARIO":
                return "Sem funcionário";

            case "STATUS_INVALIDO":
                return "Status inválido";

            default:
                return tipo
                    .replaceAll("_", " ")
                    .toLowerCase()
                    .replace(
                        /^\w/,
                        letra => letra.toUpperCase()
                    );

        }

    }



    /* =====================================================
       FORMATAR STATUS
    ===================================================== */

    function formatarStatus(status) {

        if (!status) {
            return "Sem status";
        }


        switch (status) {

            case "PENDENTE":
                return "Pendente";

            case "EM_ANALISE":
                return "Em análise";

            case "RESOLVIDA":
                return "Resolvida";

            default:
                return status
                    .replaceAll("_", " ")
                    .toLowerCase()
                    .replace(
                        /^\w/,
                        letra => letra.toUpperCase()
                    );

        }

    }



    /* =====================================================
       FORMATAR DATA
    ===================================================== */

    function formatarData(data) {

        if (!data) {
            return "—";
        }


        const valor =
            data.substring(0, 10);


        const partes =
            valor.split("-");


        if (partes.length !== 3) {
            return data;
        }


        return `${partes[2]}/${partes[1]}/${partes[0]}`;

    }



    /* =====================================================
       ATUALIZAR RESUMO
    ===================================================== */

    function atualizarResumo(linhasVisiveis) {

        let total = 0;
        let pendentes = 0;
        let analise = 0;
        let resolvidas = 0;


        linhasVisiveis.forEach(
            function (linha) {

                const status =
                    linha.dataset.status;


                total++;


                if (status === "PENDENTE") {
                    pendentes++;
                }


                if (status === "EM_ANALISE") {
                    analise++;
                }


                if (status === "RESOLVIDA") {
                    resolvidas++;
                }

            }
        );


        totalFraudes.textContent =
            total;

        fraudesPendentes.textContent =
            pendentes;

        fraudesAnalise.textContent =
            analise;

        fraudesResolvidas.textContent =
            resolvidas;


        cardTotalFraudes.textContent =
            total;

        cardPendentes.textContent =
            pendentes;

        cardAnalise.textContent =
            analise;

        cardResolvidas.textContent =
            resolvidas;

    }



    /* =====================================================
       FILTRAR
    ===================================================== */

    function filtrar() {

        const statusSelecionado =
            filtroStatus.value;

        const tipoSelecionado =
            filtroTipo.value;

        const inicio =
            dataInicio.value;

        const fim =
            dataFim.value;


        const linhasVisiveis = [];


        linhas.forEach(
            function (linha) {

                const status =
                    linha.dataset.status || "";

                const tipo =
                    linha.dataset.tipo || "";

                const data =
                    linha.dataset.data || "";


                /*
                 * A data da LocalDateTime
                 * começa com yyyy-MM-dd.
                 */

                const dataVenda =
                    data.substring(0, 10);


                let mostrar = true;


                /* STATUS */

                if (
                    statusSelecionado &&
                    status !== statusSelecionado
                ) {

                    mostrar = false;

                }


                /* TIPO */

                if (
                    tipoSelecionado &&
                    tipo !== tipoSelecionado
                ) {

                    mostrar = false;

                }


                /* DATA INICIAL */

                if (
                    inicio &&
                    dataVenda < inicio
                ) {

                    mostrar = false;

                }


                /* DATA FINAL */

                if (
                    fim &&
                    dataVenda > fim
                ) {

                    mostrar = false;

                }


                linha.style.display =
                    mostrar ? "" : "none";


                if (mostrar) {

                    linhasVisiveis.push(
                        linha
                    );

                }

            }
        );


        semFraudes.style.display =
            linhasVisiveis.length === 0
                ? ""
                : "none";


        atualizarResumo(
            linhasVisiveis
        );

    }



    /* =====================================================
       LIMPAR FILTROS
    ===================================================== */

    function limparFiltros() {

        filtroStatus.value = "";

        filtroTipo.value = "";

        dataInicio.value = "";

        dataFim.value = "";


        filtrar();

    }



    /* =====================================================
       ABRIR MODAL
    ===================================================== */

    function abrirModal(linha) {

        const venda =
            linha.dataset.venda || "—";

        const funcionario =
            linha.dataset.funcionario ||
            "Não informado";

        const valor =
            linha.dataset.valor;

        const tipo =
            linha.dataset.tipo;

        const data =
            linha.dataset.data;

        const descricao =
            linha.dataset.descricao ||
            "Nenhuma descrição informada.";

        const status =
            linha.dataset.status;


        document.getElementById(
            "modalTitulo"
        ).textContent =
            `Fraude #${linha.dataset.id}`;


        document.getElementById(
            "modalVenda"
        ).textContent =
            venda === "—"
                ? "Sem venda"
                : `#${venda}`;


        document.getElementById(
            "modalFuncionario"
        ).textContent =
            funcionario;


        document.getElementById(
            "modalValor"
        ).textContent =
            formatarValor(valor);


        document.getElementById(
            "modalTipo"
        ).textContent =
            formatarTipo(tipo);


        document.getElementById(
            "modalData"
        ).textContent =
            formatarData(data);


        document.getElementById(
            "modalDescricao"
        ).textContent =
            descricao;


        document.getElementById(
            "modalStatus"
        ).textContent =
            formatarStatus(status);


        modal.classList.add(
            "aberto"
        );

    }



    /* =====================================================
       FECHAR MODAL
    ===================================================== */

    function fechar() {

        modal.classList.remove(
            "aberto"
        );

    }



    /* =====================================================
       BOTÃO VISUALIZAR
    ===================================================== */

    linhas.forEach(
        function (linha) {

            const botao =
                linha.querySelector(
                    ".btn-visualizar"
                );


            if (!botao) {
                return;
            }


            botao.addEventListener(
                "click",
                function () {

                    abrirModal(
                        linha
                    );

                }
            );

        }
    );



    /* =====================================================
       EVENTOS DOS FILTROS
    ===================================================== */

    btnFiltrar.addEventListener(
        "click",
        filtrar
    );


    btnLimpar.addEventListener(
        "click",
        limparFiltros
    );



    /* =====================================================
       MODAL
    ===================================================== */

    fecharModal.addEventListener(
        "click",
        fechar
    );


    btnFecharModal.addEventListener(
        "click",
        fechar
    );


    modal.addEventListener(
        "click",
        function (evento) {

            if (
                evento.target === modal
            ) {

                fechar();

            }

        }
    );



    /* =====================================================
       ESC
    ===================================================== */

    document.addEventListener(
        "keydown",
        function (evento) {

            if (
                evento.key === "Escape"
            ) {

                fechar();

            }

        }
    );



    /* =====================================================
       INICIALIZAÇÃO
    ===================================================== */

    filtrar();


    if (
        window.lucide
    ) {

        lucide.createIcons();

    }

});