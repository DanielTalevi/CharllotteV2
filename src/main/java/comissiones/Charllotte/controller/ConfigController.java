package comissiones.Charllotte.controller;

import comissiones.Charllotte.model.Usuario;
import comissiones.Charllotte.service.UsuarioService;
import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
public class ConfigController {

    private final UsuarioService usuarioService;

    public ConfigController(UsuarioService usuarioService) {
        this.usuarioService = usuarioService;
    }


    // =====================================================
    // CONFIGURAÇÕES
    // =====================================================

    @GetMapping("/config")
    public String config(
            HttpSession session,
            Model model) {

        String email =
                (String) session.getAttribute("usuarioLogado");

        if (email == null || email.isBlank()) {
            return "redirect:/login";
        }

        Usuario usuario =
                usuarioService.buscarPorEmail(email);

        if (usuario == null) {
            return "redirect:/login";
        }

        model.addAttribute(
                "usuarioLogado",
                usuario
        );


        // =================================================
        // ADMIN
        // =================================================

        if (Boolean.TRUE.equals(usuario.getAdmin())) {
            return "adm/config";
        }


        // =================================================
        // FUNCIONÁRIO
        // =================================================

        return "funcionario/config";
    }


    // =====================================================
    // ALTERAR SENHA
    // =====================================================

    @PostMapping("/config/alterar-senha")
    public String alterarSenha(
            @RequestParam String senhaAtual,
            @RequestParam String novaSenha,
            @RequestParam String confirmarNovaSenha,
            HttpSession session,
            RedirectAttributes redirectAttributes) {

        String email =
                (String) session.getAttribute("usuarioLogado");

        if (email == null || email.isBlank()) {
            return "redirect:/login";
        }

        Usuario usuario =
                usuarioService.buscarPorEmail(email);

        if (usuario == null) {
            return "redirect:/login";
        }

        boolean alterou =
                usuarioService.alterarSenha(
                        usuario,
                        senhaAtual,
                        novaSenha,
                        confirmarNovaSenha
                );

        if (!alterou) {

            redirectAttributes.addFlashAttribute(
                    "erroSenha",
                    "Senha atual incorreta ou as novas senhas não conferem."
            );

            return "redirect:/config";
        }

        redirectAttributes.addFlashAttribute(
                "sucessoSenha",
                "Senha alterada com sucesso."
        );

        return "redirect:/config";
    }
}