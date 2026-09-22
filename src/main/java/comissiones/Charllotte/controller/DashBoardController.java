package comissiones.Charllotte.controller;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import comissiones.Charllotte.config.AuthUtil;
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

    @GetMapping("/home")
    public String mostrarDashboard(
            HttpSession session,
            Model model) {

        String email = AuthUtil.getUsuarioLogado(session);

        if (email == null || email.isBlank()) {
            return "redirect:/login";
        }

        Usuario usuario = usuarioService.buscarPorEmail(email);

        if (usuario == null) {
            return "redirect:/login";
        }

        model.addAttribute("usuarioLogado", usuario);


        /*
         * =====================================================
         * DASHBOARD DO FUNCIONÁRIO
         * =====================================================
         */

        if (!usuario.getAdmin()) {

            List<Venda> minhasVendas =
                    vendaService.listarPorFuncionario(usuario.getId());

            /*
             * VENDAS REALIZADAS
             */

            List<Venda> minhasVendasRealizadas =
                    minhasVendas.stream()
                            .filter(venda ->
                                    venda.getStatus() == StatusVenda.REALIZADA
                            )
                            .toList();


            /*
             * DATA ATUAL
             */

            LocalDate hoje = LocalDate.now();

            int mesAtual = hoje.getMonthValue();

            int anoAtual = hoje.getYear();


            /*
             * VENDAS DE HOJE
             */

            List<Venda> vendasHoje =
                    minhasVendasRealizadas.stream()
                            .filter(venda ->
                                    venda.getDataVenda() != null &&
                                            venda.getDataVenda().toLocalDate().equals(hoje)
                            )
                            .toList();


            /*
             * VENDAS DO MÊS
             */

            List<Venda> vendasDoMes =
                    minhasVendasRealizadas.stream()
                            .filter(venda ->
                                    venda.getDataVenda() != null &&
                                            venda.getDataVenda().getMonthValue() == mesAtual &&
                                            venda.getDataVenda().getYear() == anoAtual
                            )
                            .toList();


            /*
             * TOTAIS
             */

            BigDecimal valorVendasHoje =
                    vendaService.somarValorVendas(vendasHoje);

            BigDecimal valorVendasMes =
                    vendaService.somarValorVendas(vendasDoMes);


            /*
             * COMISSÃO DO MÊS
             */

            BigDecimal comissaoMes =
                    comissaoService.somarComissaoPorFuncionario(
                            usuario.getId()
                    );


            /*
             * ÚLTIMAS VENDAS
             */

            List<Venda> vendasRecentes =
                    minhasVendasRealizadas.stream()
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
             * MODEL DO FUNCIONÁRIO
             */

            model.addAttribute(
                    "vendasHoje",
                    vendasHoje.size()
            );

            model.addAttribute(
                    "valorVendasHoje",
                    valorVendasHoje
            );

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
                    "dataAtual",
                    hoje
            );


            return "funcionario/Home";
        }


        /*
         * =====================================================
         * DASHBOARD DO ADMIN
         * =====================================================
         */

        List<Venda> todasVendas =
                vendaService.listarTodas();

        List<Venda> vendasRealizadas =
                vendaService.listarPorStatus(
                        StatusVenda.REALIZADA
                );

        List<Venda> vendasCanceladas =
                vendaService.listarPorStatus(
                        StatusVenda.CANCELADA
                );


        /*
         * =====================================================
         * TOTAIS
         * =====================================================
         */

        BigDecimal totalVendido =
                vendaService.somarValorVendas(todasVendas);

        BigDecimal totalComissao =
                comissaoService.somarTodasAsComissoes();


        /*
         * =====================================================
         * FUNCIONÁRIOS
         * =====================================================
         */

        int totalFuncionarios =
                usuarioService.listarTodos().size();


        /*
         * =====================================================
         * VENDAS RECENTES
         * =====================================================
         */

        List<Venda> vendasRecentes =
                todasVendas.stream()
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
         * GRÁFICO - VENDAS POR MÊS
         * =====================================================
         */

        int anoAtual = LocalDate.now().getYear();

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

        List<BigDecimal> vendasPorMes =
                new ArrayList<>();

        for (int mes = 1; mes <= 12; mes++) {

            List<Venda> vendasMes =
                    vendaService.vendasDoMes(
                            anoAtual,
                            mes
                    );

            BigDecimal totalMes =
                    vendaService.somarValorVendas(vendasMes);

            vendasPorMes.add(totalMes);
        }


        /*
         * =====================================================
         * ALTURA DO GRÁFICO
         * =====================================================
         */

        BigDecimal maiorVendaMes =
                vendasPorMes.stream()
                        .max(BigDecimal::compareTo)
                        .orElse(BigDecimal.ZERO);

        List<Integer> alturasGrafico =
                new ArrayList<>();

        for (BigDecimal valor : vendasPorMes) {

            if (maiorVendaMes.compareTo(BigDecimal.ZERO) == 0) {

                alturasGrafico.add(0);

            } else {

                int altura =
                        valor
                                .multiply(BigDecimal.valueOf(100))
                                .divide(
                                        maiorVendaMes,
                                        0,
                                        java.math.RoundingMode.HALF_UP
                                )
                                .intValue();

                if (altura < 4 &&
                        valor.compareTo(BigDecimal.ZERO) > 0) {

                    altura = 4;
                }

                alturasGrafico.add(altura);
            }
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


        return "adm/Home";
    }
}