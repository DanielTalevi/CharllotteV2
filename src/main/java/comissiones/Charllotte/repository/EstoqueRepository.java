package comissiones.Charllotte.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import comissiones.Charllotte.model.Estoque;

public interface EstoqueRepository
        extends JpaRepository<Estoque, Integer> {

    Optional<Estoque> findByProdutoId(
            Integer idProduto);
}