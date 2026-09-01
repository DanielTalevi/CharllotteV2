package comissiones.Charllotte.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import comissiones.Charllotte.config.AuthUtil;
import comissiones.Charllotte.model.Usuario;
import comissiones.Charllotte.service.AuthLoginService;

import jakarta.servlet.http.HttpSession;

@Controller
public class AuthLoginController {

    private final AuthLoginService authLoginService;

    public AuthLoginController(AuthLoginService authLoginService) {
        this.authLoginService = authLoginService;
    }

    @GetMapping("/login")
    public String mostrarLogin() {
        return "login";
    }

    @PostMapping("/login")
    public String login(
            @RequestParam String email,
            @RequestParam String senha,
            HttpSession session) {

        Usuario usuario = authLoginService.authLogin(email, senha);

        if (usuario == null) {
            return "login";
        }

        AuthUtil.marcarLogado(session, usuario.getEmail());

        if (usuario.getAdmin()) {
            return "redirect:/HM";
        }

        return "redirect:/home";
    }
}