package comissiones.Charllotte.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import comissiones.Charllotte.model.Comissao;

public interface ComissaoRepository
        extends JpaRepository<Comissao, Integer> {

    List<Comissao> findByFuncionarioId(
            Integer idFuncionario);

    List<Comissao> findByVendaId(
            Integer idVenda);
}