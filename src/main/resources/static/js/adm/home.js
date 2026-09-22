document.addEventListener("DOMContentLoaded", function () {

    /*
     * =====================================================
     * ÍCONES LUCIDE
     * =====================================================
     */

    if (typeof lucide !== "undefined") {
        lucide.createIcons();
    }


    /*
     * =====================================================
     * ANIMAÇÃO DAS BARRAS DO GRÁFICO
     * =====================================================
     */

    const barras = document.querySelectorAll(".chart-bar");

    barras.forEach(function (barra) {

        const alturaFinal = barra.style.height;

        barra.style.height = "0%";

        setTimeout(function () {
            barra.style.height = alturaFinal;
        }, 150);

    });

});