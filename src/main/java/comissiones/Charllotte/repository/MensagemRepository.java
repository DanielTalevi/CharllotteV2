package comissiones.Charllotte.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import comissiones.Charllotte.model.Mensagem;

public interface MensagemRepository
        extends JpaRepository<Mensagem, Integer> {

    List<Mensagem>
    findByFuncionarioId(Integer idFuncionario);
}