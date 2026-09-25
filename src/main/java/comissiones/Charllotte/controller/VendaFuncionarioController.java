package comissiones.Charllotte.controller;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import comissiones.Charllotte.config.AuthUtil;
import comissiones.Charllotte.model.ItemVenda;
import comissiones.Charllotte.model.Produto;
import comissiones.Charllotte.model.Usuario;
import comissiones.Charllotte.service.ProdutoService;
import comissiones.Charllotte.service.UsuarioService;
import comissiones.Charllotte.service.VendaService;

import jakarta.servlet.http.HttpSession;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/funcionario")
public class VendaFuncionarioController {

    private final ProdutoService produtoService;
    private final UsuarioService usuarioService;
    private final VendaService vendaService;

    public VendaFuncionarioController(
            ProdutoService produtoService,
            UsuarioService usuarioService,
            VendaService vendaService) {

        this.produtoService = produtoService;
        this.usuarioService = usuarioService;
        this.vendaService = vendaService;
    }


    // =====================================================
    // ABRIR TELA DE REGISTRAR VENDA
    // =====================================================

    @GetMapping("/venda")
    public String registrarVenda(
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

        // Admin não utiliza essa tela
        if (Boolean.TRUE.equals(usuario.getAdmin())) {
            return "redirect:/vendas";
        }

        List<Produto> produtos =
                produtoService.listarAtivos();

        model.addAttribute(
                "usuarioLogado",
                usuario
        );

        model.addAttribute(
                "produtos",
                produtos
        );

        return "funcionario/venda";
    }


    // =====================================================
    // REALIZAR VENDA
    // =====================================================

    @PostMapping("/venda")
    public String realizarVenda(
            @RequestParam List<Integer> produtoIds,
            @RequestParam List<BigDecimal> quantidades,
            HttpSession session,
            RedirectAttributes redirectAttributes) {

        String email =
                AuthUtil.getUsuarioLogado(session);

        if (email == null || email.isBlank()) {
            return "redirect:/login";
        }

        Usuario funcionario =
                usuarioService.buscarPorEmail(email);

        if (funcionario == null) {
            return "redirect:/login";
        }

        // Admin não pode usar a tela de funcionário
        if (Boolean.TRUE.equals(funcionario.getAdmin())) {
            return "redirect:/vendas";
        }


        // =================================================
        // VALIDAÇÃO
        // =================================================

        if (produtoIds == null
                || quantidades == null
                || produtoIds.isEmpty()
                || quantidades.isEmpty()
                || produtoIds.size() != quantidades.size()) {

            redirectAttributes.addFlashAttribute(
                    "erroVenda",
                    "Informe os produtos e as quantidades corretamente."
            );

            return "redirect:/funcionario/venda";
        }


        try {

            List<ItemVenda> itens =
                    new ArrayList<>();


            // =============================================
            // MONTA OS ITENS DA VENDA
            // =============================================

            for (int i = 0; i < produtoIds.size(); i++) {

                Integer idProduto =
                        produtoIds.get(i);

                BigDecimal quantidade =
                        quantidades.get(i);


                if (idProduto == null) {

                    throw new IllegalArgumentException(
                            "Produto inválido."
                    );
                }


                if (quantidade == null
                        || quantidade.compareTo(
                        BigDecimal.ZERO) <= 0) {

                    throw new IllegalArgumentException(
                            "A quantidade deve ser maior que zero."
                    );
                }


                Produto produto =
                        produtoService.buscarPorId(
                                idProduto
                        );


                if (!Boolean.TRUE.equals(
                        produto.getStatus())) {

                    throw new IllegalArgumentException(
                            "O produto "
                                    + produto.getNome()
                                    + " está inativo."
                    );
                }


                ItemVenda item =
                        new ItemVenda();

                item.setProduto(produto);

                item.setQuantidade(
                        quantidade
                );


                itens.add(item);
            }


            // =============================================
            // REALIZA A VENDA
            // =============================================

            vendaService.realizarVenda(
                    funcionario,
                    itens
            );


            redirectAttributes.addFlashAttribute(
                    "sucessoVenda",
                    "Venda registrada com sucesso!"
            );


        } catch (Exception e) {

        e.printStackTrace();

        redirectAttributes.addFlashAttribute(
                "erroVenda",
                "Erro ao registrar venda: " + e.getMessage()
        );
    }


        return "redirect:/funcionario/venda";
    }
}