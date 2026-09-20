package comissiones.Charllotte.repository;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import comissiones.Charllotte.model.StatusVenda;
import comissiones.Charllotte.model.Venda;

public interface VendaRepository
        extends JpaRepository<Venda, Integer> {

    List<Venda> findByFuncionarioId(
            Integer idFuncionario);

    List<Venda> findByStatus(
            StatusVenda status);

    @Query("""
        SELECT v
        FROM Venda v
        WHERE v.dataVenda >= :inicio
        AND v.dataVenda < :fim
        AND v.status = :status
        ORDER BY v.dataVenda
    """)
    List<Venda> findVendasEntreDatas(
            @Param("inicio") LocalDateTime inicio,
            @Param("fim") LocalDateTime fim,
            @Param("status") StatusVenda status);

    @Query("""
        SELECT COALESCE(SUM(v.valorTotal), 0)
        FROM Venda v
        WHERE v.dataVenda >= :inicio
        AND v.dataVenda < :fim
        AND v.status = :status
    """)
    BigDecimal somarVendasEntreDatas(
            @Param("inicio") LocalDateTime inicio,
            @Param("fim") LocalDateTime fim,
            @Param("status") StatusVenda status);
}