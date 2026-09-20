package comissiones.Charllotte.controller;

import java.util.List;

import comissiones.Charllotte.config.AuthUtil;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import comissiones.Charllotte.model.Venda;
import comissiones.Charllotte.service.UsuarioService;
import comissiones.Charllotte.service.VendaService;
import jakarta.servlet.http.HttpSession;
import comissiones.Charllotte.model.Usuario;

@Controller
@RequestMapping("/vendas")
public class VendaController {

    private final VendaService vendaService;
    private final UsuarioService usuarioService;

    public VendaController(
            VendaService vendaService,
            UsuarioService usuarioService) {

        this.vendaService = vendaService;
        this.usuarioService = usuarioService;
    }

    @GetMapping
    public String vendas(
            HttpSession session,
            Model model) {

        String email = AuthUtil.getUsuarioLogado(session);
        Usuario usuario = usuarioService.buscarPorEmail(email);

        if (usuario.getAdmin()) {

            model.addAttribute(
                    "usuarios",
                    usuarioService.listarFuncionariosDaVenda());

            return "adm/vendas";
        }

        return "funcionario/vendas";
    }

    @GetMapping("/funcionario")
    public String vendasPorFuncionario(
            @RequestParam Integer idFuncionario,
            HttpSession session,
            Model model) {

        String email = AuthUtil.getUsuarioLogado(session);
        Usuario usuario = usuarioService.buscarPorEmail(email);

        // Funcionário comum não pode pesquisar outro funcionário
        if (!usuario.getAdmin()) {
            return "redirect:/vendas";
        }

        List<Venda> vendas =
                vendaService.listarPorFuncionario(idFuncionario);

        model.addAttribute("vendas", vendas);

        model.addAttribute(
                "usuarios",
                usuarioService.listarFuncionariosDaVenda());

        model.addAttribute(
                "idFuncionario",
                idFuncionario);

        return "adm/vendas";

    }
}