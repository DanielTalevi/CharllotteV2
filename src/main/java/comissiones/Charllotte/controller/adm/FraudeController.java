package comissiones.Charllotte.controller.adm;

import java.util.List;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import comissiones.Charllotte.model.Fraude;
import comissiones.Charllotte.model.Usuario;
import comissiones.Charllotte.service.FraudeService;
import comissiones.Charllotte.service.UsuarioService;

import jakarta.servlet.http.HttpSession;

@Controller
public class FraudeController {

    private final FraudeService fraudeService;
    private final UsuarioService usuarioService;

    public FraudeController(
            FraudeService fraudeService,
            UsuarioService usuarioService) {

        this.fraudeService = fraudeService;
        this.usuarioService = usuarioService;
    }


    @GetMapping("/fraudes")
    public String listarFraudes(
            HttpSession session,
            Model model) {

        String email =
                (String) session.getAttribute("usuarioLogado");

        if (email == null) {
            return "redirect:/login";
        }

        Usuario usuario =
                usuarioService.buscarPorEmail(email);

        if (usuario == null) {
            return "redirect:/login";
        }

        List<Fraude> fraudes =
                fraudeService.listarTodas();

        System.out.println("==============================");
        System.out.println("TOTAL DE FRAUDES: " + fraudes.size());

        for (Fraude fraude : fraudes) {
            System.out.println(
                    "Fraude ID: " + fraude.getId()
                            + " | Tipo: " + fraude.getTipo()
                            + " | Status: " + fraude.getStatus()
            );
        }

        System.out.println("==============================");

        List<Fraude> fraudesRecentes =
                fraudeService.listarRecentes(5);

        model.addAttribute(
                "usuarioLogado",
                usuario
        );

        model.addAttribute(
                "fraudes",
                fraudes
        );

        model.addAttribute(
                "fraudesRecentes",
                fraudesRecentes
        );

        return "adm/fraudes";
    }
}