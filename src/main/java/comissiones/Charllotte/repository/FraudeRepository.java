package comissiones.Charllotte.repository;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import comissiones.Charllotte.model.Fraude;
import comissiones.Charllotte.model.StatusFraude;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface FraudeRepository
        extends JpaRepository<Fraude, Integer> {

    List<Fraude> findByStatus(
            StatusFraude status);

    List<Fraude> findByVendaId(
            Integer idVenda);
    @Query("""
    SELECT AVG(v.valorTotal)
    FROM Venda v
    WHERE v.funcionario.id = :idFuncionario
    AND v.dataVenda BETWEEN :inicio AND :fim
""")
    BigDecimal calcularMediaVendasFuncionario(
            @Param("idFuncionario") Integer idFuncionario,
            @Param("inicio") LocalDateTime inicio,
            @Param("fim") LocalDateTime fim);
}