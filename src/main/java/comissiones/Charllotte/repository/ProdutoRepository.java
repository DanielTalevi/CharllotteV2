package comissiones.Charllotte.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import comissiones.Charllotte.model.Produto;

public interface ProdutoRepository
        extends JpaRepository<Produto, Integer> {

    List<Produto> findByStatusTrue();

    List<Produto> findByNomeContainingIgnoreCase(
            String nome);
}