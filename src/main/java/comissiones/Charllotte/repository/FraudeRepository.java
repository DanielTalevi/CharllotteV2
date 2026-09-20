package comissiones.Charllotte.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import comissiones.Charllotte.model.Fraude;
import comissiones.Charllotte.model.StatusFraude;

public interface FraudeRepository
        extends JpaRepository<Fraude, Integer> {

    List<Fraude> findByStatus(
            StatusFraude status);

    List<Fraude> findByVendaId(
            Integer idVenda);
}