package comissiones.Charllotte.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import comissiones.Charllotte.model.AvisoFuncionario;
import comissiones.Charllotte.model.AvisoFuncionarioId;

public interface AvisoFuncionarioRepository
        extends JpaRepository<
                AvisoFuncionario,
                AvisoFuncionarioId> {

    List<AvisoFuncionario>
    findByFuncionarioId(Integer idFuncionario);

    List<AvisoFuncionario>
    findByFuncionarioIdAndLidoFalse(
            Integer idFuncionario);
}