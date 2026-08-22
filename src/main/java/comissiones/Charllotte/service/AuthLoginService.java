package comissiones.Charllotte.service;

import comissiones.Charllotte.model.Usuario;
import comissiones.Charllotte.repository.UsuarioRepository;
import org.springframework.stereotype.Service;
import java.util.Optional;

@Service
public class AuthLoginService {

    private final UsuarioRepository usuarioRepository;

    public AuthLoginService(UsuarioRepository usuarioRepository) {
        this.usuarioRepository = usuarioRepository;
    }

    public boolean authLogin(String email, String senha) {

        Optional<Usuario> resultado =
                usuarioRepository.findByEmail(email);

        if (resultado.isEmpty()) {
            return false;
        }

        Usuario usuario = resultado.get();

        return usuario.getPassword().equals(senha);
    }

}