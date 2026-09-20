package comissiones.Charllotte.service;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.stereotype.Service;

import comissiones.Charllotte.model.Aviso;
import comissiones.Charllotte.repository.AvisoRepository;

@Service
public class AvisoService {

    private final AvisoRepository avisoRepository;

    public AvisoService(
            AvisoRepository avisoRepository) {

        this.avisoRepository = avisoRepository;
    }

    public Aviso salvar(Aviso aviso) {

        if (aviso.getDataCriacao() == null) {

            aviso.setDataCriacao(
                    LocalDateTime.now());
        }

        return avisoRepository.save(aviso);
    }

    public Aviso buscarPorId(Integer id) {

        return avisoRepository.findById(id)
                .orElseThrow(() ->
                        new RuntimeException(
                                "Aviso não encontrado"));
    }

    public List<Aviso> listarTodos() {

        return avisoRepository.findAll();
    }
}