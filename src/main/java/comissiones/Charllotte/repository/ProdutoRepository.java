package comissiones.Charllotte.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ProdutoRepository extends JpaRepository<comissiones.Charllotte.Model.Produto, Integer> {
    Optional<comissiones.Charllotte.Model.Produto> findByCodigodoproduto(String codigodoproduto);

    boolean existsByCodigodoproduto(String codigodoproduto);
}

