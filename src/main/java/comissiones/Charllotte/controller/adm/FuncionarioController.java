package comissiones.Charllotte.controller.adm;

import java.util.List;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import comissiones.Charllotte.exception.ErroDePermissao;
import comissiones.Charllotte.config.AuthUtil;
import comissiones.Charllotte.model.Usuario;
import comissiones.Charllotte.service.UsuarioService;

import jakarta.servlet.http.HttpSession;

@Controller
@RequestMapping("/funcionario")
public class FuncionarioController {

    private final UsuarioService usuarioService;

    public FuncionarioController(
            UsuarioService usuarioService) {
        this.usuarioService = usuarioService;
    }

    @GetMapping
    public String funcionarios(
            HttpSession session,
            Model model) {

        /*
         * =====================================================
         * USUÁRIO LOGADO
         * =====================================================
         */

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


        /*
         * =====================================================
         * VERIFICAÇÃO DE ADMIN
         * =====================================================
         */

        if (!usuario.getAdmin()) {

            throw new ErroDePermissao(
                    "Você não possui permissão para acessar esta página."
            );
        }


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
         * FUNCIONÁRIOS
         * =====================================================
         */

        List<Usuario> funcionarios =
                usuarioService.listarTodos();


        /*
         * =====================================================
         * MODEL
         * =====================================================
         */

        model.addAttribute(
                "funcionarios",
                funcionarios
        );

        model.addAttribute(
                "totalFuncionarios",
                funcionarios.size()
        );


        /*
         * =====================================================
         * ATIVOS
         * =====================================================
         */

        long funcionariosAtivos =
                funcionarios.stream()
                        .filter(Usuario::getStatus)
                        .count();

        model.addAttribute(
                "funcionariosAtivos",
                funcionariosAtivos
        );


        /*
         * =====================================================
         * INATIVOS
         * =====================================================
         */

        long funcionariosInativos =
                funcionarios.stream()
                        .filter(usuarioFuncionario ->
                                !usuarioFuncionario.getStatus()
                        )
                        .count();

        model.addAttribute(
                "funcionariosInativos",
                funcionariosInativos
        );


        return "adm/funcionarios";
    }

    @PostMapping("/{id}/inativar")
    public String inativar(
            @PathVariable Integer id,
            HttpSession session) {

        String email = AuthUtil.getUsuarioLogado(session);

        if (email == null || email.isBlank()) {
            return "redirect:/login";
        }

        Usuario usuario = usuarioService.buscarPorEmail(email);

        if (usuario == null) {
            return "redirect:/login";
        }

        if (!usuario.getAdmin()) {
            throw new ErroDePermissao(
                    "Você não possui permissão para realizar esta ação."
            );
        }

        usuarioService.inativar(id);

        return "redirect:/funcionario";
    }

    @PostMapping("/{id}/ativar")
    public String ativar(
            @PathVariable Integer id,
            HttpSession session) {

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
                    "Você não possui permissão para realizar esta ação."
            );
        }

        usuarioService.ativar(id);

        return "redirect:/funcionario";
    }
}