document.addEventListener("DOMContentLoaded", function () {

    /*
     * ========================================================
     * CALENDÁRIO PERSONALIZADO
     * ========================================================
     *
     * Funciona para:
     *
     * - dataInicio
     * - dataFim
     *
     * O usuário vê:
     *      22/09/2026
     *
     * O Spring recebe:
     *      2026-09-22
     */


    const campos = [
        {
            visual: document.getElementById("dataInicio"),
            valor: document.getElementById("dataInicioValue")
        },
        {
            visual: document.getElementById("dataFim"),
            valor: document.getElementById("dataFimValue")
        }
    ];


    let campoAtual = null;

    let dataAtual = new Date();

    let anoAtual = dataAtual.getFullYear();
    let mesAtual = dataAtual.getMonth();


    /*
     * ========================================================
     * CRIA CALENDÁRIO
     * ========================================================
     */

    const calendario = document.createElement("div");

    calendario.className = "calendario";

    calendario.innerHTML = `

        <div class="calendario-header">

            <button
                type="button"
                class="calendario-navegar"
                id="calendarioAnterior"
                aria-label="Mês anterior">

                <svg
                    xmlns="http://www.w3.org/2000/svg"
                    width="17"
                    height="17"
                    viewBox="0 0 24 24"
                    fill="none"
                    stroke="currentColor"
                    stroke-width="2"
                    stroke-linecap="round"
                    stroke-linejoin="round">

                    <path d="m15 18-6-6 6-6"></path>

                </svg>

            </button>


            <span
                class="calendario-mes"
                id="calendarioMes">
            </span>


            <button
                type="button"
                class="calendario-navegar"
                id="calendarioProximo"
                aria-label="Próximo mês">

                <svg
                    xmlns="http://www.w3.org/2000/svg"
                    width="17"
                    height="17"
                    viewBox="0 0 24 24"
                    fill="none"
                    stroke="currentColor"
                    stroke-width="2"
                    stroke-linecap="round"
                    stroke-linejoin="round">

                    <path d="m9 18 6-6-6-6"></path>

                </svg>

            </button>

        </div>


        <div class="calendario-semana">

            <span>Seg</span>
            <span>Ter</span>
            <span>Qua</span>
            <span>Qui</span>
            <span>Sex</span>
            <span>Sáb</span>
            <span>Dom</span>

        </div>


        <div
            class="calendario-dias"
            id="calendarioDias">
        </div>


        <div class="calendario-footer">

            <button
                type="button"
                class="calendario-limpar"
                id="calendarioLimpar">

                Limpar

            </button>


            <button
                type="button"
                class="calendario-hoje"
                id="calendarioHoje">

                Hoje

            </button>

        </div>
    `;


    document.body.appendChild(calendario);


    /*
     * ========================================================
     * ELEMENTOS
     * ========================================================
     */

    const elementoMes =
        document.getElementById("calendarioMes");

    const elementoDias =
        document.getElementById("calendarioDias");

    const botaoAnterior =
        document.getElementById("calendarioAnterior");

    const botaoProximo =
        document.getElementById("calendarioProximo");

    const botaoHoje =
        document.getElementById("calendarioHoje");

    const botaoLimpar =
        document.getElementById("calendarioLimpar");


    /*
     * ========================================================
     * NOMES DOS MESES
     * ========================================================
     */

    const meses = [
        "janeiro",
        "fevereiro",
        "março",
        "abril",
        "maio",
        "junho",
        "julho",
        "agosto",
        "setembro",
        "outubro",
        "novembro",
        "dezembro"
    ];


    /*
     * ========================================================
     * FORMATAR DATA
     * ========================================================
     */

    function formatarVisual(data) {

        const dia =
            String(data.getDate()).padStart(2, "0");

        const mes =
            String(data.getMonth() + 1).padStart(2, "0");

        const ano =
            data.getFullYear();

        return `${dia}/${mes}/${ano}`;
    }


    /*
     * ========================================================
     * FORMATAR PARA SPRING
     * ========================================================
     */

    function formatarSpring(data) {

        const ano =
            data.getFullYear();

        const mes =
            String(data.getMonth() + 1).padStart(2, "0");

        const dia =
            String(data.getDate()).padStart(2, "0");

        return `${ano}-${mes}-${dia}`;
    }


    /*
     * ========================================================
     * CONVERTER STRING DO SPRING
     * ========================================================
     *
     * Recebe:
     *
     * 2026-09-22
     *
     * Retorna Date.
     */

    function converterData(valor) {

        if (!valor) {
            return null;
        }


        const partes = valor.split("-");


        if (partes.length !== 3) {
            return null;
        }


        const ano = Number(partes[0]);
        const mes = Number(partes[1]) - 1;
        const dia = Number(partes[2]);


        const data = new Date(
            ano,
            mes,
            dia
        );


        if (isNaN(data.getTime())) {
            return null;
        }


        return data;
    }


    /*
     * ========================================================
     * COMPARAR DATAS
     * ========================================================
     */

    function mesmaData(data1, data2) {

        return (
            data1 &&
            data2 &&
            data1.getFullYear() === data2.getFullYear() &&
            data1.getMonth() === data2.getMonth() &&
            data1.getDate() === data2.getDate()
        );
    }


    /*
     * ========================================================
     * DATA DE HOJE
     * ========================================================
     */

    function hoje() {

        const agora = new Date();

        return new Date(
            agora.getFullYear(),
            agora.getMonth(),
            agora.getDate()
        );
    }


    /*
     * ========================================================
     * POSICIONAR CALENDÁRIO
     * ========================================================
     */

    function posicionarCalendario() {

        if (!campoAtual) {
            return;
        }


        const rect =
            campoAtual.visual.getBoundingClientRect();


        const largura =
            calendario.offsetWidth;


        let esquerda =
            rect.left;


        /*
         * Evita que o calendário
         * saia pela direita da tela.
         */

        if (esquerda + largura > window.innerWidth - 10) {

            esquerda =
                window.innerWidth - largura - 10;

        }


        /*
         * Se estiver muito perto da esquerda.
         */

        if (esquerda < 10) {
            esquerda = 10;
        }


        /*
         * Normalmente abre abaixo
         * do campo.
         */

        let topo =
            rect.bottom + 7;


        /*
         * Se não houver espaço embaixo,
         * abre acima.
         */

        if (
            topo + calendario.offsetHeight >
            window.innerHeight - 10
        ) {

            topo =
                rect.top -
                calendario.offsetHeight -
                7;

        }


        calendario.style.left =
            `${esquerda}px`;

        calendario.style.top =
            `${topo}px`;
    }


    /*
     * ========================================================
     * RENDERIZAR CALENDÁRIO
     * ========================================================
     */

    function renderizarCalendario() {

        elementoMes.textContent =
            `${meses[mesAtual]} ${anoAtual}`;


        elementoDias.innerHTML = "";


        /*
         * Primeiro dia do mês.
         *
         * JavaScript:
         *
         * domingo = 0
         * segunda = 1
         *
         * Nosso calendário começa na segunda.
         */

        const primeiroDia =
            new Date(
                anoAtual,
                mesAtual,
                1
            );


        let inicioSemana =
            primeiroDia.getDay();


        if (inicioSemana === 0) {
            inicioSemana = 6;
        } else {
            inicioSemana--;
        }


        /*
         * Quantidade de dias
         * no mês atual.
         */

        const quantidadeDias =
            new Date(
                anoAtual,
                mesAtual + 1,
                0
            ).getDate();


        /*
         * Dias do mês anterior.
         */

        const diasMesAnterior =
            new Date(
                anoAtual,
                mesAtual,
                0
            ).getDate();


        /*
         * Data selecionada.
         */

        let dataSelecionada = null;


        if (campoAtual) {

            dataSelecionada =
                converterData(
                    campoAtual.valor.value
                );

        }


        /*
         * Quantidade total de células.
         *
         * 42 garante sempre 6 linhas.
         */

        const totalCelulas = 42;


        for (
            let indice = 0;
            indice < totalCelulas;
            indice++
        ) {


            let dia;
            let mesDoDia = mesAtual;
            let anoDoDia = anoAtual;

            let outroMes = false;


            /*
             * MÊS ANTERIOR
             */

            if (indice < inicioSemana) {

                dia =
                    diasMesAnterior -
                    inicioSemana +
                    indice +
                    1;

                mesDoDia--;

                if (mesDoDia < 0) {

                    mesDoDia = 11;
                    anoDoDia--;

                }

                outroMes = true;

            }


            /*
             * MÊS ATUAL
             */

            else if (
                indice <
                inicioSemana + quantidadeDias
            ) {

                dia =
                    indice -
                    inicioSemana +
                    1;

            }


            /*
             * PRÓXIMO MÊS
             */

            else {

                dia =
                    indice -
                    inicioSemana -
                    quantidadeDias +
                    1;

                mesDoDia++;

                if (mesDoDia > 11) {

                    mesDoDia = 0;
                    anoDoDia++;

                }

                outroMes = true;

            }


            const dataDia =
                new Date(
                    anoDoDia,
                    mesDoDia,
                    dia
                );


            const botao =
                document.createElement("button");


            botao.type = "button";

            botao.className =
                "calendario-dia";


            botao.textContent =
                dia;


            /*
             * Dia de outro mês.
             */

            if (outroMes) {

                botao.classList.add(
                    "outro-mes"
                );

            }


            /*
             * Hoje.
             */

            if (
                mesmaData(
                    dataDia,
                    hoje()
                )
            ) {

                botao.classList.add(
                    "hoje"
                );

            }


            /*
             * Selecionado.
             */

            if (
                mesmaData(
                    dataDia,
                    dataSelecionada
                )
            ) {

                botao.classList.add(
                    "selecionado"
                );

            }


            /*
             * Clique.
             */

            botao.addEventListener(
                "click",
                function () {

                    selecionarData(
                        dataDia
                    );

                }
            );


            elementoDias.appendChild(
                botao
            );

        }


        /*
         * Depois de renderizar,
         * ajusta posição.
         */

        posicionarCalendario();

    }


    /*
     * ========================================================
     * SELECIONAR DATA
     * ========================================================
     */

    function selecionarData(data) {

        if (!campoAtual) {
            return;
        }


        /*
         * Campo que o usuário vê.
         */

        campoAtual.visual.value =
            formatarVisual(data);


        /*
         * Campo que o Spring recebe.
         */

        campoAtual.valor.value =
            formatarSpring(data);


        fecharCalendario();

    }


    /*
     * ========================================================
     * ABRIR CALENDÁRIO
     * ========================================================
     */

    function abrirCalendario(campo) {

        campoAtual = campo;


        /*
         * Se já existe uma data,
         * abrimos o calendário naquele mês.
         */

        const dataExistente =
            converterData(
                campo.valor.value
            );


        if (dataExistente) {

            anoAtual =
                dataExistente.getFullYear();

            mesAtual =
                dataExistente.getMonth();

        } else {

            const agora = hoje();

            anoAtual =
                agora.getFullYear();

            mesAtual =
                agora.getMonth();

        }


        renderizarCalendario();


        calendario.classList.add(
            "aberto"
        );


        /*
         * Pequeno timeout para
         * pegar a altura real do calendário.
         */

        requestAnimationFrame(
            posicionarCalendario
        );

    }


    /*
     * ========================================================
     * FECHAR CALENDÁRIO
     * ========================================================
     */

    function fecharCalendario() {

        calendario.classList.remove(
            "aberto"
        );

        campoAtual = null;

    }


    /*
     * ========================================================
     * LIMPAR DATA
     * ========================================================
     */

    function limparData() {

        if (!campoAtual) {
            return;
        }


        campoAtual.visual.value = "";

        campoAtual.valor.value = "";


        fecharCalendario();

    }


    /*
     * ========================================================
     * BOTÃO HOJE
     * ========================================================
     */

    botaoHoje.addEventListener(
        "click",
        function () {

            selecionarData(
                hoje()
            );

        }
    );


    /*
     * ========================================================
     * BOTÃO LIMPAR
     * ========================================================
     */

    botaoLimpar.addEventListener(
        "click",
        function () {

            limparData();

        }
    );


    /*
     * ========================================================
     * MÊS ANTERIOR
     * ========================================================
     */

    botaoAnterior.addEventListener(
        "click",
        function () {

            mesAtual--;


            if (mesAtual < 0) {

                mesAtual = 11;
                anoAtual--;

            }


            renderizarCalendario();

        }
    );


    /*
     * ========================================================
     * PRÓXIMO MÊS
     * ========================================================
     */

    botaoProximo.addEventListener(
        "click",
        function () {

            mesAtual++;


            if (mesAtual > 11) {

                mesAtual = 0;
                anoAtual++;

            }


            renderizarCalendario();

        }
    );


    /*
     * ========================================================
     * ABRIR AO CLICAR NOS CAMPOS
     * ========================================================
     */

    campos.forEach(
        function (campo) {

            if (!campo.visual || !campo.valor) {
                return;
            }


            /*
             * Carrega o valor que veio
             * do Spring.
             */

            const valorInicial =
                converterData(
                    campo.valor.value
                );


            if (valorInicial) {

                campo.visual.value =
                    formatarVisual(
                        valorInicial
                    );

            }


            /*
             * Clique.
             */

            campo.visual.addEventListener(
                "click",
                function (evento) {

                    evento.stopPropagation();

                    abrirCalendario(
                        campo
                    );

                }
            );


            /*
             * Também permite clicar
             * no ícone / container.
             */

            const container =
                campo.visual.closest(
                    ".data-input"
                );


            if (container) {

                container.addEventListener(
                    "click",
                    function (evento) {

                        if (
                            evento.target !==
                            campo.visual
                        ) {

                            evento.stopPropagation();

                            abrirCalendario(
                                campo
                            );

                        }

                    }
                );

            }

        }
    );


    /*
     * ========================================================
     * CLIQUE FORA
     * ========================================================
     */

    document.addEventListener(
        "click",
        function (evento) {

            if (!calendario.contains(evento.target)) {

                const clicouEmCampo =
                    campos.some(
                        function (campo) {

                            return (
                                campo.visual &&
                                campo.visual
                                    .closest(".data-input")
                                    ?.contains(evento.target)
                            );

                        }
                    );


                if (!clicouEmCampo) {

                    fecharCalendario();

                }

            }

        }
    );


    /*
     * ========================================================
     * ESC
     * ========================================================
     */

    document.addEventListener(
        "keydown",
        function (evento) {

            if (evento.key === "Escape") {

                fecharCalendario();

            }

        }
    );


    /*
     * ========================================================
     * REDIMENSIONAMENTO
     * ========================================================
     */

    window.addEventListener(
        "resize",
        function () {

            if (
                calendario.classList.contains(
                    "aberto"
                )
            ) {

                posicionarCalendario();

            }

        }
    );


    /*
     * ========================================================
     * SCROLL
     * ========================================================
     */

    window.addEventListener(
        "scroll",
        function () {

            if (
                calendario.classList.contains(
                    "aberto"
                )
            ) {

                posicionarCalendario();

            }

        },
        true
    );

});