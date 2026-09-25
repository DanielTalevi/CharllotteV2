package comissiones.Charllotte.controller;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import comissiones.Charllotte.config.AuthUtil;
import comissiones.Charllotte.model.Comissao;
import comissiones.Charllotte.model.StatusVenda;
import comissiones.Charllotte.model.Usuario;
import comissiones.Charllotte.model.Venda;
import comissiones.Charllotte.service.ComissaoService;
import comissiones.Charllotte.service.UsuarioService;
import comissiones.Charllotte.service.VendaService;

import jakarta.servlet.http.HttpSession;

@Controller
public class DashBoardController {

    private final UsuarioService usuarioService;
    private final VendaService vendaService;
    private final ComissaoService comissaoService;


    public DashBoardController(
            UsuarioService usuarioService,
            VendaService vendaService,
            ComissaoService comissaoService) {

        this.usuarioService = usuarioService;
        this.vendaService = vendaService;
        this.comissaoService = comissaoService;
    }


    // =========================================================
    // DASHBOARD
    // =========================================================

    @GetMapping("/home")
    public String mostrarDashboard(
            HttpSession session,
            Model model) {

        /*
         * =====================================================
         * BUSCA USUÁRIO LOGADO
         * =====================================================
         */

        String email =
                AuthUtil.getUsuarioLogado(session);


        if (email == null || email.isBlank()) {

            return "redirect:/login";
        }


        Usuario usuario =
                usuarioService.buscarPorEmail(email);


        if (usuario == null) {

            session.invalidate();

            return "redirect:/login";
        }


        /*
         * =====================================================
         * USUÁRIO LOGADO
         * =====================================================
         */

        model.addAttribute(
                "usuarioLogado",
                usuario
        );


        /*
         * =====================================================
         * FUNCIONÁRIO
         * =====================================================
         */

        if (!Boolean.TRUE.equals(usuario.getAdmin())) {

            prepararDashboardFuncionario(
                    usuario,
                    model
            );

            return "funcionario/Home";
        }


        /*
         * =====================================================
         * ADMIN
         * =====================================================
         */

        prepararDashboardAdmin(model);

        return "adm/Home";
    }


    // =========================================================
    // DASHBOARD DO FUNCIONÁRIO
    // =========================================================

    private void prepararDashboardFuncionario(
            Usuario usuario,
            Model model) {


        /*
         * =====================================================
         * BUSCA SOMENTE AS VENDAS DO FUNCIONÁRIO
         * =====================================================
         */

        List<Venda> minhasVendas =
                vendaService.listarPorFuncionario(
                        usuario.getId()
                );


        if (minhasVendas == null) {

            minhasVendas = new ArrayList<>();
        }


        /*
         * =====================================================
         * SOMENTE VENDAS REALIZADAS
         * =====================================================
         */

        List<Venda> minhasVendasRealizadas =
                minhasVendas.stream()
                        .filter(venda ->
                                venda != null
                                        &&
                                        venda.getStatus()
                                                == StatusVenda.REALIZADA
                        )
                        .toList();


        /*
         * =====================================================
         * DATA ATUAL
         * =====================================================
         */

        LocalDate hoje =
                LocalDate.now();


        int mesAtual =
                hoje.getMonthValue();


        int anoAtual =
                hoje.getYear();


        /*
         * =====================================================
         * VENDAS DE HOJE
         * =====================================================
         */

        List<Venda> vendasHoje =
                minhasVendasRealizadas.stream()
                        .filter(venda -> {

                            if (venda.getDataVenda() == null) {

                                return false;
                            }

                            return venda.getDataVenda()
                                    .toLocalDate()
                                    .equals(hoje);
                        })
                        .toList();


        /*
         * =====================================================
         * VENDAS DO MÊS
         * =====================================================
         */

        List<Venda> vendasDoMes =
                minhasVendasRealizadas.stream()
                        .filter(venda -> {

                            if (venda.getDataVenda() == null) {

                                return false;
                            }


                            return
                                    venda.getDataVenda()
                                            .getMonthValue()
                                            == mesAtual
                                            &&
                                            venda.getDataVenda()
                                                    .getYear()
                                                    == anoAtual;
                        })
                        .toList();


        /*
         * =====================================================
         * VALOR VENDAS HOJE
         * =====================================================
         */

        BigDecimal valorVendasHoje =
                vendaService.somarValorVendas(
                        vendasHoje
                );


        if (valorVendasHoje == null) {

            valorVendasHoje =
                    BigDecimal.ZERO;
        }


        /*
         * =====================================================
         * VALOR VENDAS DO MÊS
         * =====================================================
         */

        BigDecimal valorVendasMes =
                vendaService.somarValorVendas(
                        vendasDoMes
                );


        if (valorVendasMes == null) {

            valorVendasMes =
                    BigDecimal.ZERO;
        }


        /*
         * =====================================================
         * COMISSÃO DO FUNCIONÁRIO
         * =====================================================
         */

        BigDecimal comissaoMes =
                comissaoService
                        .somarComissaoPorFuncionario(
                                usuario.getId()
                        );


        if (comissaoMes == null) {

            comissaoMes =
                    BigDecimal.ZERO;
        }


        /*
         * =====================================================
         * ÚLTIMAS VENDAS
         * =====================================================
         */

        List<Venda> vendasRecentes =
                minhasVendasRealizadas.stream()
                        .filter(venda ->
                                venda != null
                        )
                        .sorted(
                                Comparator.comparing(
                                        Venda::getDataVenda,
                                        Comparator.nullsLast(
                                                Comparator.naturalOrder()
                                        )
                                ).reversed()
                        )
                        .limit(8)
                        .toList();


        /*
         * =====================================================
         * COMISSÃO DE CADA VENDA
         * =====================================================
         */

        Map<Integer, BigDecimal> comissoesPorVenda =
                new HashMap<>();


        for (Venda venda :
                vendasRecentes) {

            if (venda.getId() == null) {

                continue;
            }


            Comissao comissao =
                    comissaoService.buscarPorVenda(
                            venda.getId()
                    );


            if (
                    comissao != null
                            &&
                            comissao.getValor() != null
            ) {

                comissoesPorVenda.put(
                        venda.getId(),
                        comissao.getValor()
                );

            } else {

                comissoesPorVenda.put(
                        venda.getId(),
                        BigDecimal.ZERO
                );
            }
        }


        /*
         * =====================================================
         * MODEL DO FUNCIONÁRIO
         * =====================================================
         */

        model.addAttribute(
                "usuarioLogado",
                usuario
        );


        model.addAttribute(
                "vendasHoje",
                vendasHoje.size()
        );


        model.addAttribute(
                "valorVendasHoje",
                valorVendasHoje
        );


        /*
         * Nome principal usado no HTML
         */

        model.addAttribute(
                "vendasMes",
                vendasDoMes.size()
        );


        /*
         * Mantido para compatibilidade
         */

        model.addAttribute(
                "totalVendasMes",
                vendasDoMes.size()
        );


        model.addAttribute(
                "valorVendasMes",
                valorVendasMes
        );


        model.addAttribute(
                "comissaoMes",
                comissaoMes
        );


        model.addAttribute(
                "vendasRecentes",
                vendasRecentes
        );


        model.addAttribute(
                "comissoesPorVenda",
                comissoesPorVenda
        );


        model.addAttribute(
                "dataAtual",
                hoje
        );
    }


    // =========================================================
    // DASHBOARD DO ADMIN
    // =========================================================

    private void prepararDashboardAdmin(
            Model model) {


        /*
         * =====================================================
         * TODAS AS VENDAS
         * =====================================================
         */

        List<Venda> todasVendas =
                vendaService.listarTodas();


        if (todasVendas == null) {

            todasVendas =
                    new ArrayList<>();
        }


        /*
         * =====================================================
         * VENDAS REALIZADAS
         * =====================================================
         */

        List<Venda> vendasRealizadas =
                vendaService.listarPorStatus(
                        StatusVenda.REALIZADA
                );


        if (vendasRealizadas == null) {

            vendasRealizadas =
                    new ArrayList<>();
        }


        /*
         * =====================================================
         * VENDAS CANCELADAS
         * =====================================================
         */

        List<Venda> vendasCanceladas =
                vendaService.listarPorStatus(
                        StatusVenda.CANCELADA
                );


        if (vendasCanceladas == null) {

            vendasCanceladas =
                    new ArrayList<>();
        }


        /*
         * =====================================================
         * TOTAL VENDIDO
         * =====================================================
         */

        BigDecimal totalVendido =
                vendaService.somarValorVendas(
                        todasVendas
                );


        if (totalVendido == null) {

            totalVendido =
                    BigDecimal.ZERO;
        }


        /*
         * =====================================================
         * TOTAL COMISSÃO
         * =====================================================
         */

        BigDecimal totalComissao =
                comissaoService
                        .somarTodasAsComissoes();


        if (totalComissao == null) {

            totalComissao =
                    BigDecimal.ZERO;
        }


        /*
         * =====================================================
         * FUNCIONÁRIOS
         * =====================================================
         */

        int totalFuncionarios =
                usuarioService
                        .listarTodos()
                        .size();


        /*
         * =====================================================
         * VENDAS RECENTES
         * =====================================================
         */

        List<Venda> vendasRecentes =
                todasVendas.stream()
                        .filter(venda ->
                                venda != null
                        )
                        .sorted(
                                Comparator.comparing(
                                        Venda::getDataVenda,
                                        Comparator.nullsLast(
                                                Comparator.naturalOrder()
                                        )
                                ).reversed()
                        )
                        .limit(5)
                        .toList();


        /*
         * =====================================================
         * ANO
         * =====================================================
         */

        int anoAtual =
                LocalDate.now().getYear();


        /*
         * =====================================================
         * MESES
         * =====================================================
         */

        String[] nomesMeses = {

                "JAN",
                "FEV",
                "MAR",
                "ABR",
                "MAI",
                "JUN",
                "JUL",
                "AGO",
                "SET",
                "OUT",
                "NOV",
                "DEZ"

        };


        /*
         * =====================================================
         * VENDAS POR MÊS
         * =====================================================
         */

        List<BigDecimal> vendasPorMes =
                new ArrayList<>();


        for (int mes = 1; mes <= 12; mes++) {

            List<Venda> vendasMes =
                    vendaService.vendasDoMes(
                            anoAtual,
                            mes
                    );


            if (vendasMes == null) {

                vendasMes =
                        new ArrayList<>();
            }


            BigDecimal totalMes =
                    vendaService.somarValorVendas(
                            vendasMes
                    );


            if (totalMes == null) {

                totalMes =
                        BigDecimal.ZERO;
            }


            vendasPorMes.add(
                    totalMes
            );
        }


        /*
         * =====================================================
         * MAIOR VALOR DO GRÁFICO
         * =====================================================
         */

        BigDecimal maiorVendaMes =
                vendasPorMes.stream()
                        .max(BigDecimal::compareTo)
                        .orElse(BigDecimal.ZERO);


        /*
         * =====================================================
         * ALTURA DAS BARRAS
         * =====================================================
         */

        List<Integer> alturasGrafico =
                new ArrayList<>();


        for (BigDecimal valor :
                vendasPorMes) {


            if (
                    maiorVendaMes
                            .compareTo(
                                    BigDecimal.ZERO
                            )
                            == 0
            ) {

                alturasGrafico.add(0);

                continue;
            }


            int altura =
                    valor
                            .multiply(
                                    BigDecimal.valueOf(100)
                            )
                            .divide(
                                    maiorVendaMes,
                                    0,
                                    java.math.RoundingMode.HALF_UP
                            )
                            .intValue();


            if (
                    altura < 4
                            &&
                            valor.compareTo(
                                    BigDecimal.ZERO
                            ) > 0
            ) {

                altura = 4;
            }


            alturasGrafico.add(
                    altura
            );
        }


        /*
         * =====================================================
         * MODEL DO ADMIN
         * =====================================================
         */

        model.addAttribute(
                "totalFuncionarios",
                totalFuncionarios
        );


        model.addAttribute(
                "totalVendas",
                vendasRealizadas.size()
        );


        model.addAttribute(
                "totalVendido",
                totalVendido
        );


        model.addAttribute(
                "totalComissao",
                totalComissao
        );


        model.addAttribute(
                "totalCanceladas",
                vendasCanceladas.size()
        );


        model.addAttribute(
                "vendasRecentes",
                vendasRecentes
        );


        model.addAttribute(
                "nomesMeses",
                nomesMeses
        );


        model.addAttribute(
                "vendasPorMes",
                vendasPorMes
        );


        model.addAttribute(
                "alturasGrafico",
                alturasGrafico
        );


        model.addAttribute(
                "anoAtual",
                anoAtual
        );


        model.addAttribute(
                "dataAtual",
                LocalDate.now()
        );
    }
}