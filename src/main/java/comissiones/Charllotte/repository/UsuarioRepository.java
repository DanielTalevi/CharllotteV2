package comissiones.Charllotte.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import comissiones.Charllotte.model.Usuario;

public interface UsuarioRepository
        extends JpaRepository<Usuario, Integer> {

    Optional<Usuario> findByEmail(String email);

    Optional<Usuario> findByCpf(String cpf);

    Optional<Usuario> findByEmailAndStatusTrue(String email);
}