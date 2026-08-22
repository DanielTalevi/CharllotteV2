package comissiones.Charllotte.Controller;

import comissiones.Charllotte.service.AuthLoginService;
import comissiones.Charllotte.service.AuthUtil;
import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
public class AuthLoginController {

    private final AuthLoginService authLoginService;

    public AuthLoginController(AuthLoginService authLoginService) {
        this.authLoginService = authLoginService;
    }

    // Abre a página de login
    @GetMapping("/login")
    public String mostrarLogin() {
        return "login"; // templates/login.html
    }

    // Processa o formulário
    @PostMapping("/login")
    public String login(@RequestParam String email,
                        @RequestParam String senha,
                        HttpSession session) {

        boolean sucesso = authLoginService.authLogin(email, senha);

        if (sucesso) {
            AuthUtil.marcarLogado(session, email);
            return "redirect:/home";
        }

        return "login";
    }
}