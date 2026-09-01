package comissiones.Charllotte.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import comissiones.Charllotte.model.StatusVenda;
import comissiones.Charllotte.model.Venda;

public interface VendaRepository
        extends JpaRepository<Venda, Integer> {

    List<Venda> findByFuncionarioId(
            Integer idFuncionario);

    List<Venda> findByStatus(
            StatusVenda status);
}