package comissiones.Charllotte.controller.adm;

import java.math.BigDecimal;
import java.util.List;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import comissiones.Charllotte.model.Produto;
import comissiones.Charllotte.model.Usuario;
import comissiones.Charllotte.service.ProdutoService;
import comissiones.Charllotte.service.UsuarioService;

import jakarta.servlet.http.HttpSession;

@Controller
public class ArmazenController {

    private final ProdutoService produtoService;
    private final UsuarioService usuarioService;

    public ArmazenController(
            ProdutoService produtoService,
            UsuarioService usuarioService) {

        this.produtoService = produtoService;
        this.usuarioService = usuarioService;
    }


    // =====================================================
    // PÁGINA DO ESTOQUE
    // =====================================================

    @GetMapping("/armazen")
    public String armazen(
            HttpSession session,
            Model model) {

        String email =
                (String) session.getAttribute(
                        "usuarioLogado");

        if (email == null) {
            return "redirect:/login";
        }

        Usuario usuario =
                usuarioService.buscarPorEmail(email);

        if (usuario == null) {
            return "redirect:/login";
        }

        List<Produto> produtos =
                produtoService.listarAtivos();

        model.addAttribute(
                "usuarioLogado",
                usuario);

        model.addAttribute(
                "produtos",
                produtos);

        model.addAttribute(
                "totalProdutos",
                produtoService.totalProdutos());

        model.addAttribute(
                "produtosBaixo",
                produtoService.produtosBaixo());

        model.addAttribute(
                "valorEstoque",
                produtoService.valorEstoque());

        return "adm/armazen";
    }


    // =====================================================
    // SALVAR / EDITAR PRODUTO
    // =====================================================

    @PostMapping("/armazen/produtos/salvar")
    public String salvar(
            @ModelAttribute Produto produto,
            RedirectAttributes redirectAttributes) {

        try {

            produtoService.salvar(produto);

            redirectAttributes.addFlashAttribute(
                    "sucesso",
                    "Produto salvo com sucesso.");

        } catch (Exception e) {

            redirectAttributes.addFlashAttribute(
                    "erro",
                    e.getMessage());
        }

        return "redirect:/armazen";
    }


    // =====================================================
    // ENTRADA DE ESTOQUE
    // =====================================================

    @PostMapping(
            "/armazen/produtos/{id}/entrada")
    public String entrada(
            @PathVariable Integer id,
            @RequestParam BigDecimal quantidade,
            RedirectAttributes redirectAttributes) {

        try {

            produtoService.entrada(
                    id,
                    quantidade);

            redirectAttributes.addFlashAttribute(
                    "sucesso",
                    "Entrada registrada com sucesso.");

        } catch (Exception e) {

            redirectAttributes.addFlashAttribute(
                    "erro",
                    e.getMessage());
        }

        return "redirect:/armazen";
    }


    // =====================================================
    // SAÍDA DE ESTOQUE
    // =====================================================

    @PostMapping(
            "/armazen/produtos/{id}/saida")
    public String saida(
            @PathVariable Integer id,
            @RequestParam BigDecimal quantidade,
            RedirectAttributes redirectAttributes) {

        try {

            produtoService.saida(
                    id,
                    quantidade);

            redirectAttributes.addFlashAttribute(
                    "sucesso",
                    "Saída registrada com sucesso.");

        } catch (Exception e) {

            redirectAttributes.addFlashAttribute(
                    "erro",
                    e.getMessage());
        }

        return "redirect:/armazen";
    }


    // =====================================================
    // DESATIVAR PRODUTO
    // =====================================================

    @PostMapping(
            "/armazen/produtos/{id}/excluir")
    public String excluir(
            @PathVariable Integer id,
            RedirectAttributes redirectAttributes) {

        try {

            produtoService.desativar(id);

            redirectAttributes.addFlashAttribute(
                    "sucesso",
                    "Produto desativado com sucesso.");

        } catch (Exception e) {

            redirectAttributes.addFlashAttribute(
                    "erro",
                    e.getMessage());
        }

        return "redirect:/armazen";
    }
}