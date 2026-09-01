package comissiones.Charllotte.service;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.stereotype.Service;

import comissiones.Charllotte.model.Mensagem;
import comissiones.Charllotte.repository.MensagemRepository;

@Service
public class MensagemService {

    private final MensagemRepository mensagemRepository;

    public MensagemService(
            MensagemRepository mensagemRepository) {

        this.mensagemRepository =
                mensagemRepository;
    }

    public Mensagem enviar(Mensagem mensagem) {

        if (mensagem.getDataEnvio() == null) {

            mensagem.setDataEnvio(
                    LocalDateTime.now());
        }

        return mensagemRepository.save(mensagem);
    }

    public List<Mensagem> listarPorFuncionario(
            Integer idFuncionario) {

        return mensagemRepository
                .findByFuncionarioId(
                        idFuncionario);
    }
}