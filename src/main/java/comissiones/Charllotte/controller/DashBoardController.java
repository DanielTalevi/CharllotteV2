package comissiones.Charllotte.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

import comissiones.Charllotte.config.AuthUtil;
import comissiones.Charllotte.model.Usuario;
import comissiones.Charllotte.service.UsuarioService;

import jakarta.servlet.http.HttpSession;

@Controller
public class DashBoardController {

    private final UsuarioService usuarioService;

    public DashBoardController(UsuarioService usuarioService) {
        this.usuarioService = usuarioService;
    }

    @GetMapping("/home")
    public String mostrarDashboard(HttpSession session) {

        String email = AuthUtil.getUsuarioLogado(session);

        Usuario usuario = usuarioService.buscarPorEmail(email);

        if (usuario.getAdmin()) {
            return "adm/Home";
        }

        return "funcionario/Home";
    }
}