package comissiones.Charllotte.service;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.stereotype.Service;

import comissiones.Charllotte.model.Fraude;
import comissiones.Charllotte.model.StatusFraude;
import comissiones.Charllotte.repository.FraudeRepository;

@Service
public class FraudeService {

    private final FraudeRepository fraudeRepository;

    public FraudeService(
            FraudeRepository fraudeRepository) {

        this.fraudeRepository =
                fraudeRepository;
    }

    public Fraude registrar(Fraude fraude) {

        if (fraude.getDataIdentificacao()
                == null) {

            fraude.setDataIdentificacao(
                    LocalDateTime.now());
        }

        if (fraude.getStatus() == null) {

            fraude.setStatus(
                    StatusFraude.PENDENTE);
        }

        return fraudeRepository.save(fraude);
    }

    public Fraude buscarPorId(Integer id) {

        return fraudeRepository.findById(id)
                .orElseThrow(() ->
                        new RuntimeException(
                                "Fraude não encontrada"));
    }

    public List<Fraude> listarTodas() {

        return fraudeRepository.findAll();
    }

    public List<Fraude> listarPendentes() {

        return fraudeRepository.findByStatus(
                StatusFraude.PENDENTE);
    }

    public void alterarStatus(
            Integer id,
            StatusFraude status) {

        Fraude fraude = buscarPorId(id);

        fraude.setStatus(status);

        fraudeRepository.save(fraude);
    }
}