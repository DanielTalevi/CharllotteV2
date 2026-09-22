package comissiones.Charllotte.controller.adm;

import comissiones.Charllotte.config.AuthUtil;
import comissiones.Charllotte.exception.ErroDePermissao;
import comissiones.Charllotte.model.Usuario;
import comissiones.Charllotte.model.Venda;
import comissiones.Charllotte.service.RelatorioService;
import comissiones.Charllotte.service.UsuarioService;
import comissiones.Charllotte.service.VendaService;

import jakarta.servlet.http.HttpSession;

import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.YearMonth;
import java.util.List;

@Controller
@RequestMapping("/relatorios")
public class RelatoriosController {

    private final UsuarioService usuarioService;
    private final RelatorioService relatorioService;
    private final VendaService vendaService;


    // =====================================================
    // CONSTRUTOR
    // =====================================================

    public RelatoriosController(
            UsuarioService usuarioService,
            RelatorioService relatorioService,
            VendaService vendaService) {

        this.usuarioService = usuarioService;
        this.relatorioService = relatorioService;
        this.vendaService = vendaService;
    }


    // =====================================================
    // PÁGINA DE RELATÓRIOS
    // =====================================================

    @GetMapping
    public String relatorios(
            HttpSession session,
            Model model) {

        String email =
                AuthUtil.getUsuarioLogado(session);


        if (email == null || email.isBlank()) {

            return "redirect:/login";
        }


        Usuario usuario =
                usuarioService.buscarPorEmail(email);


        // =============================================
        // PERMISSÃO
        // =============================================

        if (!usuario.getAdmin()) {

            throw new ErroDePermissao(
                    "Você não possui permissão para acessar esta página."
            );
        }


        // =============================================
        // MÊS ATUAL
        // =============================================

        YearMonth mesAtual =
                YearMonth.now();

        LocalDate inicioMes =
                mesAtual.atDay(1);

        LocalDate fimMes =
                mesAtual.atEndOfMonth();


        // =============================================
        // BUSCA VENDAS
        // =============================================

        List<Venda> vendas =
                vendaService.listarTodas();


        // =============================================
        // FILTRA VENDAS DO MÊS
        // =============================================

        List<Venda> vendasDoMes =
                vendas.stream()
                        .filter(venda ->
                                venda.getDataVenda() != null
                        )
                        .filter(venda -> {

                            LocalDate dataVenda =
                                    venda.getDataVenda()
                                            .toLocalDate();

                            return !dataVenda.isBefore(
                                    inicioMes
                            )
                                    && !dataVenda.isAfter(
                                    fimMes
                            );
                        })
                        .toList();


        // =============================================
        // FATURAMENTO MENSAL
        // =============================================

        BigDecimal faturamentoMensal =
                vendasDoMes.stream()
                        .map(Venda::getValorTotal)
                        .filter(valor ->
                                valor != null
                        )
                        .reduce(
                                BigDecimal.ZERO,
                                BigDecimal::add
                        );


        // =============================================
        // TOTAL DE VENDAS
        // =============================================

        int totalVendas =
                vendasDoMes.size();


        // =============================================
        // TICKET MÉDIO
        // =============================================

        BigDecimal ticketMedio =
                BigDecimal.ZERO;


        if (totalVendas > 0) {

            ticketMedio =
                    faturamentoMensal.divide(
                            BigDecimal.valueOf(
                                    totalVendas
                            ),
                            2,
                            RoundingMode.HALF_UP
                    );
        }


        // =============================================
        // DADOS DO THYMELEAF
        // =============================================

        model.addAttribute(
                "usuarioLogado",
                usuario
        );


        model.addAttribute(
                "faturamentoMensal",
                faturamentoMensal
        );


        model.addAttribute(
                "totalVendas",
                totalVendas
        );


        model.addAttribute(
                "ticketMedio",
                ticketMedio
        );


        model.addAttribute(
                "periodoRelatorio",
                formatarMes(mesAtual)
        );


        return "adm/relatorios";
    }


    // =====================================================
    // FORMATA MÊS
    // =====================================================

    private String formatarMes(
            YearMonth mes) {

        String[] meses = {

                "Janeiro",
                "Fevereiro",
                "Março",
                "Abril",
                "Maio",
                "Junho",
                "Julho",
                "Agosto",
                "Setembro",
                "Outubro",
                "Novembro",
                "Dezembro"
        };


        return meses[
                mes.getMonthValue() - 1
                ]
                + " "
                + mes.getYear();
    }


    // =====================================================
    // RELATÓRIO DE VENDAS
    // =====================================================

    @GetMapping("/vendas/pdf")
    public ResponseEntity<byte[]> gerarRelatorioVendas(
            HttpSession session) {

        verificarAdministrador(session);


        byte[] pdf =
                relatorioService.gerarRelatorioVendas();


        return ResponseEntity.ok()
                .header(
                        HttpHeaders.CONTENT_DISPOSITION,
                        "attachment; filename=relatorio-vendas.pdf"
                )
                .contentType(
                        MediaType.APPLICATION_PDF
                )
                .body(pdf);
    }


    // =====================================================
    // RELATÓRIO DE ESTOQUE
    // =====================================================

    @GetMapping("/estoque/pdf")
    public ResponseEntity<byte[]> gerarRelatorioEstoque(
            HttpSession session) {

        verificarAdministrador(session);


        byte[] pdf =
                relatorioService.gerarRelatorioEstoque();


        return ResponseEntity.ok()
                .header(
                        HttpHeaders.CONTENT_DISPOSITION,
                        "attachment; filename=relatorio-estoque.pdf"
                )
                .contentType(
                        MediaType.APPLICATION_PDF
                )
                .body(pdf);
    }


    // =====================================================
    // RELATÓRIO DE FUNCIONÁRIOS
    // =====================================================

    @GetMapping("/funcionarios/pdf")
    public ResponseEntity<byte[]> gerarRelatorioFuncionarios(
            HttpSession session) {

        verificarAdministrador(session);


        byte[] pdf =
                relatorioService
                        .gerarRelatorioFuncionarios();


        return ResponseEntity.ok()
                .header(
                        HttpHeaders.CONTENT_DISPOSITION,
                        "attachment; filename=relatorio-funcionarios.pdf"
                )
                .contentType(
                        MediaType.APPLICATION_PDF
                )
                .body(pdf);
    }


    // =====================================================
    // VERIFICA ADMINISTRADOR
    // =====================================================

    private void verificarAdministrador(
            HttpSession session) {

        String email =
                AuthUtil.getUsuarioLogado(session);


        if (email == null || email.isBlank()) {

            throw new ErroDePermissao(
                    "Usuário não está autenticado."
            );
        }


        Usuario usuario =
                usuarioService.buscarPorEmail(email);


        if (!usuario.getAdmin()) {

            throw new ErroDePermissao(
                    "Você não possui permissão para gerar relatórios."
            );
        }
    }
    @GetMapping("/comissoes/pdf")
    public ResponseEntity<byte[]> gerarRelatorioComissoes(
            HttpSession session) {

        verificarAdministrador(session);

        byte[] pdf =
                relatorioService
                        .gerarRelatorioComissoes();

        return ResponseEntity.ok()
                .header(
                        HttpHeaders.CONTENT_DISPOSITION,
                        "attachment; filename=relatorio-comissoes.pdf")
                .contentType(
                        MediaType.APPLICATION_PDF)
                .body(pdf);
    }
}