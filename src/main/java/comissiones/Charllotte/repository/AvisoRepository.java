package comissiones.Charllotte.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import comissiones.Charllotte.model.Aviso;

public interface AvisoRepository
        extends JpaRepository<Aviso, Integer> {
}