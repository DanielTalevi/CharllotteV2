package comissiones.Charllotte.service;

import java.util.List;

import org.springframework.stereotype.Service;

import comissiones.Charllotte.exception.UsuarioNaoEncontradoException;
import comissiones.Charllotte.model.Usuario;
import comissiones.Charllotte.repository.UsuarioRepository;

@Service
public class UsuarioService {

    private final UsuarioRepository usuarioRepository;

    public UsuarioService(UsuarioRepository usuarioRepository) {
        this.usuarioRepository = usuarioRepository;
    }


    // =====================================================
    // SALVAR USUÁRIO
    // =====================================================

    public Usuario salvar(Usuario usuario) {

        // Todo novo usuário começa ativo,
        // caso o status não tenha sido informado.
        if (usuario.getStatus() == null) {
            usuario.setStatus(true);
        }

        // Novo usuário começa offline.
        if (usuario.getOnline() == null) {
            usuario.setOnline(false);
        }

        // Caso o admin não tenha sido informado,
        // considera como funcionário comum.
        if (usuario.getAdmin() == null) {
            usuario.setAdmin(false);
        }

        return usuarioRepository.save(usuario);
    }


    // =====================================================
    // BUSCAR POR ID
    // =====================================================

    public Usuario buscarPorId(Integer id) {

        return usuarioRepository.findById(id)
                .orElseThrow(() ->
                        new UsuarioNaoEncontradoException(
                                "Usuário não encontrado."
                        )
                );
    }


    // =====================================================
    // BUSCAR POR EMAIL
    // =====================================================

    public Usuario buscarPorEmail(String email) {

        return usuarioRepository.findByEmail(email)
                .orElseThrow(() ->
                        new UsuarioNaoEncontradoException(
                                "Usuário não encontrado."
                        )
                );
    }


    // =====================================================
    // LISTAR TODOS
    // =====================================================

    public List<Usuario> listarTodos() {

        return usuarioRepository.findAll();
    }


    // =====================================================
    // LISTAR USUÁRIOS ATIVOS
    // =====================================================

    public List<Usuario> listarAtivos() {

        return usuarioRepository.findAll()
                .stream()
                .filter(usuario ->
                        Boolean.TRUE.equals(usuario.getStatus())
                )
                .toList();
    }


    // =====================================================
    // LISTAR FUNCIONÁRIOS
    // =====================================================
    // Retorna somente usuários que NÃO são administradores.
    // Inclui ativos e inativos.

    public List<Usuario> listarFuncionarios() {

        return usuarioRepository.findAll()
                .stream()
                .filter(usuario ->
                        !Boolean.TRUE.equals(usuario.getAdmin())
                )
                .toList();
    }


    // =====================================================
    // LISTAR FUNCIONÁRIOS ATIVOS
    // =====================================================
    // Somente funcionários comuns e ativos.

    public List<Usuario> listarFuncionariosAtivos() {

        return listarFuncionarios()
                .stream()
                .filter(usuario ->
                        Boolean.TRUE.equals(usuario.getStatus())
                )
                .toList();
    }


    // =====================================================
    // LISTAR FUNCIONÁRIOS INATIVOS
    // =====================================================
    // Somente funcionários comuns e inativos.

    public List<Usuario> listarFuncionariosInativos() {

        return listarFuncionarios()
                .stream()
                .filter(usuario ->
                        Boolean.FALSE.equals(usuario.getStatus())
                )
                .toList();
    }


    // =====================================================
    // FUNCIONÁRIOS DISPONÍVEIS PARA VENDA
    // =====================================================
    // Somente funcionários ativos e não administradores.

    public List<Usuario> listarFuncionariosDaVenda() {

        return usuarioRepository.findByStatusTrueAndAdminFalse();
    }


    // =====================================================
    // TOTAL DE FUNCIONÁRIOS
    // =====================================================

    public long contarFuncionarios() {

        return listarFuncionarios().size();
    }


    // =====================================================
    // TOTAL DE FUNCIONÁRIOS ATIVOS
    // =====================================================

    public long contarFuncionariosAtivos() {

        return listarFuncionariosAtivos().size();
    }


    // =====================================================
    // TOTAL DE FUNCIONÁRIOS INATIVOS
    // =====================================================

    public long contarFuncionariosInativos() {

        return listarFuncionariosInativos().size();
    }


    // =====================================================
    // INATIVAR FUNCIONÁRIO
    // =====================================================

    public void inativar(Integer id) {

        Usuario usuario =
                usuarioRepository.findById(id)
                        .orElseThrow(() ->
                                new UsuarioNaoEncontradoException(
                                        "Funcionário não encontrado."
                                )
                        );

        usuario.setStatus(false);

        // Quando o funcionário é inativado,
        // também deixa de estar online.
        usuario.setOnline(false);

        usuarioRepository.save(usuario);
    }


    // =====================================================
    // ATIVAR FUNCIONÁRIO
    // =====================================================

    public void ativar(Integer id) {

        Usuario usuario =
                usuarioRepository.findById(id)
                        .orElseThrow(() ->
                                new UsuarioNaoEncontradoException(
                                        "Funcionário não encontrado."
                                )
                        );

        usuario.setStatus(true);

        usuarioRepository.save(usuario);
    }
}