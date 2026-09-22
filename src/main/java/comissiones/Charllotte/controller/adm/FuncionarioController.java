package comissiones.Charllotte.controller.adm;

import comissiones.Charllotte.config.AuthUtil;
import comissiones.Charllotte.exception.ErroDePermissao;
import comissiones.Charllotte.model.Usuario;
import comissiones.Charllotte.service.UsuarioService;

import jakarta.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/funcionario")
public class FuncionarioController {

    private final UsuarioService usuarioService;

    @Value("${senha.admin}")
    private String senhaAdminConfiguracao;

    public FuncionarioController(UsuarioService usuarioService) {
        this.usuarioService = usuarioService;
    }


    // =====================================================
    // LISTAR FUNCIONÁRIOS
    // =====================================================

    @GetMapping
    public String funcionarios(
            HttpSession session,
            Model model) {

        String email = AuthUtil.getUsuarioLogado(session);

        if (email == null || email.isBlank()) {
            return "redirect:/login";
        }

        Usuario usuario = usuarioService.buscarPorEmail(email);

        if (!Boolean.TRUE.equals(usuario.getAdmin())) {
            throw new ErroDePermissao(
                    "Você não possui permissão para acessar esta página."
            );
        }

        // =====================================================
        // LISTA DE FUNCIONÁRIOS
        // =====================================================

        model.addAttribute(
                "funcionarios",
                usuarioService.listarTodos()
        );

        // =====================================================
        // DADOS DOS CARDS
        // =====================================================

        model.addAttribute(
                "totalFuncionarios",
                usuarioService.contarFuncionarios()
        );

        model.addAttribute(
                "funcionariosAtivos",
                usuarioService.contarFuncionariosAtivos()
        );

        model.addAttribute(
                "funcionariosInativos",
                usuarioService.contarFuncionariosInativos()
        );

        // =====================================================
        // USUÁRIO LOGADO
        // =====================================================

        model.addAttribute(
                "usuarioLogado",
                usuario
        );

        return "adm/funcionarios";
    }


    // =====================================================
    // CADASTRAR FUNCIONÁRIO
    // =====================================================

    @PostMapping("/cadastrar")
    public String cadastrar(
            @RequestParam String nome,
            @RequestParam String cpf,
            @RequestParam String email,
            @RequestParam String senha,
            @RequestParam String confirmarSenha,
            @RequestParam(required = false) String senhaAdmin,
            HttpSession session) {

        // Verifica se existe usuário logado
        String emailLogado = AuthUtil.getUsuarioLogado(session);

        if (emailLogado == null || emailLogado.isBlank()) {
            return "redirect:/login";
        }


        // Busca o usuário que está tentando cadastrar
        Usuario usuarioLogado =
                usuarioService.buscarPorEmail(emailLogado);


        // Somente administrador pode cadastrar
        if (!usuarioLogado.getAdmin()) {
            throw new ErroDePermissao(
                    "Você não possui permissão para cadastrar funcionários."
            );
        }


        // Verifica se as senhas são iguais
        if (!senha.equals(confirmarSenha)) {
            throw new IllegalArgumentException(
                    "As senhas não coincidem."
            );
        }


        // Cria o novo usuário
        Usuario novoUsuario = new Usuario();

        novoUsuario.setNome(nome);
        novoUsuario.setCpf(cpf);
        novoUsuario.setEmail(email);
        novoUsuario.setSenha(senha);

        novoUsuario.setStatus(true);
        novoUsuario.setOnline(false);


        // =================================================
        // DEFINE SE SERÁ ADMINISTRADOR
        // =================================================

        if (senhaAdmin != null
                && !senhaAdmin.isBlank()
                && senhaAdmin.equals(senhaAdminConfiguracao)) {

            novoUsuario.setAdmin(true);

        } else {

            novoUsuario.setAdmin(false);
        }


        // Salva no banco
        usuarioService.salvar(novoUsuario);

        return "redirect:/funcionario";
    }


    // =====================================================
    // INATIVAR FUNCIONÁRIO
    // =====================================================

    @PostMapping("/{id}/inativar")
    public String inativar(
            @PathVariable Integer id,
            HttpSession session) {

        String email = AuthUtil.getUsuarioLogado(session);

        if (email == null || email.isBlank()) {
            return "redirect:/login";
        }

        Usuario usuario = usuarioService.buscarPorEmail(email);

        if (!usuario.getAdmin()) {
            throw new ErroDePermissao(
                    "Você não possui permissão para esta ação."
            );
        }

        usuarioService.inativar(id);

        return "redirect:/funcionario";
    }


    // =====================================================
    // ATIVAR FUNCIONÁRIO
    // =====================================================

    @PostMapping("/{id}/ativar")
    public String ativar(
            @PathVariable Integer id,
            HttpSession session) {

        String email = AuthUtil.getUsuarioLogado(session);

        if (email == null || email.isBlank()) {
            return "redirect:/login";
        }

        Usuario usuario = usuarioService.buscarPorEmail(email);

        if (!usuario.getAdmin()) {
            throw new ErroDePermissao(
                    "Você não possui permissão para esta ação."
            );
        }

        usuarioService.ativar(id);

        return "redirect:/funcionario";
    }
}