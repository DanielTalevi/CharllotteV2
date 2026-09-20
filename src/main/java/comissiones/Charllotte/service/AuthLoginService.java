package comissiones.Charllotte.service;

import org.springframework.stereotype.Service;

import comissiones.Charllotte.model.Usuario;

@Service
public class AuthLoginService {

    private final UsuarioService usuarioService;

    public AuthLoginService(UsuarioService usuarioService) {
        this.usuarioService = usuarioService;
    }

    public Usuario authLogin(String email, String senha) {

        try {

            Usuario usuario = usuarioService.buscarPorEmail(email);

            if (!usuario.getStatus()) {
                return null;
            }

            if (!usuario.getSenha().equals(senha)) {
                return null;
            }

            return usuario;

        } catch (RuntimeException e) {

            return null;
        }
    }
}