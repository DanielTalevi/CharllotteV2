package comissiones.Charllotte.controller;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import comissiones.Charllotte.config.AuthUtil;
import comissiones.Charllotte.exception.ErroDePermissao;
import comissiones.Charllotte.model.Comissao;
import comissiones.Charllotte.model.Usuario;
import comissiones.Charllotte.model.Venda;
import comissiones.Charllotte.service.ComissaoService;
import comissiones.Charllotte.service.UsuarioService;
import comissiones.Charllotte.service.VendaService;

import jakarta.servlet.http.HttpSession;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
@RequestMapping("/vendas")
public class VendaController {

    private final VendaService vendaService;
    private final UsuarioService usuarioService;
    private final ComissaoService comissaoService;

    public VendaController(
            VendaService vendaService,
            UsuarioService usuarioService,
            ComissaoService comissaoService) {

        this.vendaService = vendaService;
        this.usuarioService = usuarioService;
        this.comissaoService = comissaoService;
    }

    /*
     * =========================================================
     * /vendas
     * =========================================================
     *
     * ADMIN:
     * -> acessa a tela de vendas administrativa
     *
     * FUNCIONÁRIO:
     * -> não possui tela própria de vendas
     * -> volta para o dashboard
     */
    @GetMapping
    public String vendas(
            HttpSession session,
            Model model) {

        Usuario usuario = obterUsuarioLogado(session);

        if (usuario == null) {
            return "redirect:/login";
        }

        /*
         * =====================================================
         * ADMIN
         * =====================================================
         */

        if (Boolean.TRUE.equals(usuario.getAdmin())) {

            List<Venda> vendas =
                    vendaService.listarTodas();

            prepararTelaAdmin(
                    vendas,
                    null,
                    null,
                    null,
                    usuario,
                    model
            );

            return "adm/vendas";
        }

        /*
         * =====================================================
         * FUNCIONÁRIO
         * =====================================================
         *
         * Funcionário não possui tela /vendas.
         * Seu acesso principal é pelo /home.
         */

        return "redirect:/home";
    }

    /*
     * =========================================================
     * VENDAS POR FUNCIONÁRIO
     * =========================================================
     *
     * SOMENTE ADMIN
     */
    @GetMapping("/funcionario")
    public String vendasPorFuncionario(
            @RequestParam(
                    required = false
            )
            Integer idFuncionario,

            @RequestParam(
                    required = false
            )
            LocalDate dataInicio,

            @RequestParam(
                    required = false
            )
            LocalDate dataFim,

            HttpSession session,
            Model model) {

        Usuario usuario = obterUsuarioLogado(session);

        if (usuario == null) {
            return "redirect:/login";
        }

        /*
         * =====================================================
         * FUNCIONÁRIO NÃO PODE ACESSAR
         * =====================================================
         */

        if (!Boolean.TRUE.equals(usuario.getAdmin())) {

            throw new ErroDePermissao(
                    "Você não possui permissão para acessar esta página."
            );
        }

        /*
         * =====================================================
         * BUSCA AS VENDAS
         * =====================================================
         */

        List<Venda> vendas;

        if (idFuncionario == null) {

            vendas = vendaService.listarTodas();

        } else {

            vendas = vendaService.listarPorFuncionario(
                    idFuncionario
            );
        }

        /*
         * =====================================================
         * FILTRO POR DATA
         * =====================================================
         */

        if (dataInicio != null || dataFim != null) {

            vendas = vendas.stream()
                    .filter(venda -> {

                        if (venda.getDataVenda() == null) {
                            return false;
                        }

                        LocalDate dataVenda =
                                venda.getDataVenda().toLocalDate();

                        /*
                         * DATA INICIAL
                         */
                        if (dataInicio != null
                                && dataVenda.isBefore(dataInicio)) {

                            return false;
                        }

                        /*
                         * DATA FINAL
                         */
                        if (dataFim != null
                                && dataVenda.isAfter(dataFim)) {

                            return false;
                        }

                        return true;
                    })
                    .toList();
        }

        /*
         * =====================================================
         * PREPARA TELA
         * =====================================================
         */

        prepararTelaAdmin(
                vendas,
                idFuncionario,
                dataInicio,
                dataFim,
                usuario,
                model
        );

        return "adm/vendas";
    }

    /*
     * =========================================================
     * PREPARA TELA ADMIN
     * =========================================================
     */
    private void prepararTelaAdmin(
            List<Venda> vendas,
            Integer idFuncionario,
            LocalDate dataInicio,
            LocalDate dataFim,
            Usuario usuario,
            Model model) {

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
         * TOTAL VENDIDO
         * =====================================================
         */

        BigDecimal totalVendido =
                vendaService.somarValorVendas(vendas);

        /*
         * =====================================================
         * TOTAL DE COMISSÃO
         * =====================================================
         */

        BigDecimal totalComissao;

        if (idFuncionario == null) {

            totalComissao =
                    comissaoService.somarTodasAsComissoes();

        } else {

            totalComissao =
                    comissaoService.somarComissaoPorFuncionario(
                            idFuncionario
                    );
        }

        /*
         * =====================================================
         * COMISSÃO POR VENDA
         * =====================================================
         */

        Map<Integer, BigDecimal> comissoesPorVenda =
                new HashMap<>();

        for (Venda venda : vendas) {

            Comissao comissao =
                    comissaoService.buscarPorVenda(
                            venda.getId()
                    );

            if (comissao != null
                    && comissao.getValor() != null) {

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
         * DADOS PARA O THYMELEAF
         * =====================================================
         */

        model.addAttribute(
                "vendas",
                vendas
        );

        model.addAttribute(
                "usuarios",
                usuarioService.listarFuncionariosDaVenda()
        );

        model.addAttribute(
                "idFuncionario",
                idFuncionario
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
                "comissoesPorVenda",
                comissoesPorVenda
        );

        model.addAttribute(
                "dataInicio",
                dataInicio
        );

        model.addAttribute(
                "dataFim",
                dataFim
        );
    }

    /*
     * =========================================================
     * USUÁRIO LOGADO
     * =========================================================
     */
    private Usuario obterUsuarioLogado(
            HttpSession session) {

        String email =
                AuthUtil.getUsuarioLogado(session);

        if (email == null || email.isBlank()) {
            return null;
        }

        return usuarioService.buscarPorEmail(email);
    }
}