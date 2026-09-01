package comissiones.Charllotte.service;

import java.util.List;

import org.springframework.stereotype.Service;

import comissiones.Charllotte.model.Usuario;
import comissiones.Charllotte.repository.UsuarioRepository;

@Service
public class UsuarioService {

    private final UsuarioRepository usuarioRepository;

    public UsuarioService(UsuarioRepository usuarioRepository) {
        this.usuarioRepository = usuarioRepository;
    }

    public Usuario salvar(Usuario usuario) {

        if (usuario.getStatus() == null) {
            usuario.setStatus(true);
        }

        return usuarioRepository.save(usuario);
    }

    public Usuario buscarPorId(Integer id) {

        return usuarioRepository.findById(id)
                .orElseThrow(() ->
                        new RuntimeException("Usuário não encontrado"));
    }

    public Usuario buscarPorEmail(String email) {

        return usuarioRepository.findByEmail(email)
                .orElseThrow(() ->
                        new RuntimeException("Usuário não encontrado"));
    }

    public List<Usuario> listarTodos() {
        return usuarioRepository.findAll();
    }

    public List<Usuario> listarAtivos() {

        return usuarioRepository.findAll()
                .stream()
                .filter(Usuario::getStatus)
                .toList();
    }

    public void desativar(Integer id) {

        Usuario usuario = buscarPorId(id);

        usuario.setStatus(false);

        usuarioRepository.save(usuario);
    }

    public void ativar(Integer id) {

        Usuario usuario = buscarPorId(id);

        usuario.setStatus(true);

        usuarioRepository.save(usuario);
    }
}