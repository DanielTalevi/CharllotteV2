package comissiones.Charllotte.controller.adm;

import java.math.BigDecimal;
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


    // =========================================================
    // TODAS AS VENDAS
    // =========================================================

    @GetMapping
    public String vendas(
            HttpSession session,
            Model model) {

        String email =
                AuthUtil.getUsuarioLogado(session);

        if (email == null || email.isBlank()) {
            return "redirect:/login";
        }

        Usuario usuario =
                usuarioService.buscarPorEmail(email);

        if (usuario == null) {
            return "redirect:/login";
        }

        if (usuario.getAdmin()) {

            List<Venda> vendas =
                    vendaService.listarTodas();

            prepararTela(
                    vendas,
                    null,
                    usuario,
                    model
            );

            return "adm/vendas";
        }

        return "funcionario/vendas";
    }


    // =========================================================
    // VENDAS POR FUNCIONÁRIO
    // =========================================================

    @GetMapping("/funcionario")
    public String vendasPorFuncionario(
            @RequestParam(
                    required = false
            )
            Integer idFuncionario,

            HttpSession session,

            Model model) {

        String email =
                AuthUtil.getUsuarioLogado(session);

        if (email == null || email.isBlank()) {
            return "redirect:/login";
        }

        Usuario usuario =
                usuarioService.buscarPorEmail(email);

        if (usuario == null) {
            return "redirect:/login";
        }

        if (!usuario.getAdmin()) {
            throw new ErroDePermissao(
                    "Você não possui permissão para acessar esta página."
            );
        }

        List<Venda> vendas;

        if (idFuncionario == null) {

            vendas =
                    vendaService.listarTodas();

        } else {

            vendas =
                    vendaService.listarPorFuncionario(
                            idFuncionario
                    );
        }

        prepararTela(
                vendas,
                idFuncionario,
                usuario,
                model
        );

        return "adm/vendas";
    }


    // =========================================================
    // PREPARA OS DADOS DA TELA
    // =========================================================

    private void prepararTela(
            List<Venda> vendas,
            Integer idFuncionario,
            Usuario usuario,
            Model model) {

        // Usuário logado
        model.addAttribute(
                "usuarioLogado",
                usuario
        );


        // -----------------------------------------------------
        // TOTAL VENDIDO
        // -----------------------------------------------------

        BigDecimal totalVendido =
                vendaService.somarValorVendas(vendas);


        // -----------------------------------------------------
        // TOTAL DE COMISSÃO
        // -----------------------------------------------------

        BigDecimal totalComissao;

        if (idFuncionario == null) {

            totalComissao =
                    comissaoService.somarTodasAsComissoes();

        } else {

            totalComissao =
                    comissaoService
                            .somarComissaoPorFuncionario(
                                    idFuncionario
                            );
        }


        // -----------------------------------------------------
        // COMISSÃO DE CADA VENDA
        // -----------------------------------------------------

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


        // -----------------------------------------------------
        // DADOS PARA O THYMELEAF
        // -----------------------------------------------------

        model.addAttribute(
                "vendas",
                vendas
        );

        model.addAttribute(
                "usuarios",
                usuarioService
                        .listarFuncionariosDaVenda()
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
    }
}