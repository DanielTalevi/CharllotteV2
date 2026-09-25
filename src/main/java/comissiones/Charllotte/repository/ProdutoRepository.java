package comissiones.Charllotte.repository;

import java.math.BigDecimal;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import comissiones.Charllotte.model.Produto;

public interface ProdutoRepository
        extends JpaRepository<Produto, Integer> {

    List<Produto> findByStatusTrue();

    List<Produto> findByNomeContainingIgnoreCase(
            String nome);

    @Query("""
        SELECT COALESCE(
            SUM(p.quantidadeEstoque * p.preco),
            0
        )
        FROM Produto p
        WHERE p.status = true
    """)
    BigDecimal calcularValorTotalEstoque();
}