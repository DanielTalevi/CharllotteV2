package comissiones.Charllotte.service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

import org.springframework.stereotype.Service;

import comissiones.Charllotte.model.Fraude;
import comissiones.Charllotte.model.StatusFraude;
import comissiones.Charllotte.model.Venda;
import comissiones.Charllotte.repository.FraudeRepository;
import comissiones.Charllotte.repository.VendaRepository;

@Service
public class FraudeService {

    private final FraudeRepository fraudeRepository;
    private final VendaRepository vendaRepository;

    public FraudeService(
            FraudeRepository fraudeRepository,
            VendaRepository vendaRepository) {

        this.fraudeRepository =
                fraudeRepository;

        this.vendaRepository =
                vendaRepository;
    }

    // =====================================================
    // REGISTRAR
    // =====================================================

    public Fraude registrar(Fraude fraude) {

        if (fraude.getDataIdentificacao() == null) {

            fraude.setDataIdentificacao(
                    LocalDateTime.now());
        }

        if (fraude.getStatus() == null) {

            fraude.setStatus(
                    StatusFraude.PENDENTE);
        }

        return fraudeRepository.save(fraude);
    }

    // =====================================================
    // BUSCAR POR ID
    // =====================================================

    public Fraude buscarPorId(Integer id) {

        return fraudeRepository.findById(id)
                .orElseThrow(() ->
                        new RuntimeException(
                                "Fraude não encontrada"));
    }

    // =====================================================
    // LISTAR TODAS
    // =====================================================

    public List<Fraude> listarTodas() {

        return fraudeRepository.findAll();
    }

    // =====================================================
    // LISTAR PENDENTES
    // =====================================================

    public List<Fraude> listarPendentes() {

        return fraudeRepository.findByStatus(
                StatusFraude.PENDENTE);
    }

    // =====================================================
    // LISTAR FRAUDES DE UMA VENDA
    // =====================================================

    public List<Fraude> listarPorVenda(
            Integer idVenda) {

        return fraudeRepository.findByVendaId(
                idVenda);
    }

    // =====================================================
    // ALTERAR STATUS
    // =====================================================

    public void alterarStatus(
            Integer id,
            StatusFraude status) {

        Fraude fraude =
                buscarPorId(id);

        fraude.setStatus(status);

        fraudeRepository.save(fraude);
    }

    // =====================================================
    // ANALISAR VENDA
    // =====================================================

    public void analisarVenda(Venda venda) {

        System.out.println("=================================");
        System.out.println("ANALISANDO VENDA PARA FRAUDE");
        System.out.println("ID: " + venda.getId());
        System.out.println("VALOR: " + venda.getValorTotal());
        System.out.println("FUNCIONÁRIO: " +
                (venda.getFuncionario() != null
                        ? venda.getFuncionario().getNome()
                        : "NULL"));
        System.out.println("STATUS: " + venda.getStatus());
        System.out.println("=================================");

        if (venda == null) {
            return;
        }

        analisarValorVenda(venda);

        analisarVendaSemFuncionario(venda);

        analisarStatusVenda(venda);

        analisarValorForaDoPadrao(venda);
    }

    // =====================================================
    // REGRA 1
    // VALOR MUITO ALTO
    // =====================================================

    private void analisarValorVenda(
            Venda venda) {

        BigDecimal valor =
                venda.getValorTotal();

        if (valor == null) {
            return;
        }

        if (valor.compareTo(
                new BigDecimal("5000")) > 0) {

            registrarSeNaoExistir(
                    venda,
                    "VALOR_ELEVADO",
                    "A venda possui valor superior a R$ 5.000,00.");
        }
    }

    // =====================================================
    // REGRA 2
    // VENDA SEM FUNCIONÁRIO
    // =====================================================

    private void analisarVendaSemFuncionario(
            Venda venda) {

        if (venda.getFuncionario() == null) {

            registrarSeNaoExistir(
                    venda,
                    "SEM_FUNCIONARIO",
                    "A venda não possui funcionário associado.");
        }
    }

    // =====================================================
    // REGRA 3
    // STATUS DA VENDA
    // =====================================================

    private void analisarStatusVenda(
            Venda venda) {

        if (venda.getStatus() == null) {

            registrarSeNaoExistir(
                    venda,
                    "STATUS_INVALIDO",
                    "A venda não possui um status definido.");
        }
    }

    // =====================================================
    // REGRA 4
    // VALOR FORA DO PADRÃO DO FUNCIONÁRIO
    // =====================================================

    private void analisarValorForaDoPadrao(
            Venda venda) {

        if (venda.getFuncionario() == null) {
            return;
        }

        if (venda.getValorTotal() == null) {
            return;
        }

        if (venda.getDataVenda() == null) {
            return;
        }

        Integer idFuncionario =
                venda.getFuncionario().getId();

        LocalDateTime fim =
                venda.getDataVenda();

        LocalDateTime inicio =
                fim.minusDays(30);

        BigDecimal media =
                vendaRepository.calcularMediaVendasFuncionario(
                        idFuncionario,
                        inicio,
                        fim);

        if (media == null ||
                media.compareTo(BigDecimal.ZERO) <= 0) {

            return;
        }

        BigDecimal limite =
                media.multiply(
                        new BigDecimal("3"));

        if (venda.getValorTotal()
                .compareTo(limite) > 0) {

            registrarSeNaoExistir(
                    venda,
                    "VALOR_FORA_PADRAO",
                    "O valor desta venda é mais de três vezes "
                            + "a média das vendas do funcionário "
                            + "nos últimos 30 dias.");
        }
    }

    // =====================================================
    // EVITA DUPLICAR FRAUDE
    // =====================================================

    private void registrarSeNaoExistir(
            Venda venda,
            String tipo,
            String descricao) {

        if (venda.getId() == null) {
            return;
        }

        List<Fraude> fraudes =
                fraudeRepository.findByVendaId(
                        venda.getId());

        boolean existe =
                fraudes.stream()
                        .anyMatch(f ->
                                tipo.equals(
                                        f.getTipo())
                                        &&
                                        f.getStatus()
                                                == StatusFraude.PENDENTE);

        if (existe) {
            return;
        }

        Fraude fraude =
                new Fraude();

        fraude.setVenda(venda);

        fraude.setTipo(tipo);

        fraude.setDescricao(
                descricao);

        fraude.setDataIdentificacao(
                LocalDateTime.now());

        fraude.setStatus(
                StatusFraude.PENDENTE);

        fraudeRepository.save(fraude);
    }
    public List<Fraude> listarRecentes(int limite) {

        return fraudeRepository
                .findAll(
                        org.springframework.data.domain.Sort
                                .by(
                                        org.springframework.data.domain.Sort.Direction.DESC,
                                        "dataIdentificacao"
                                )
                )
                .stream()
                .limit(limite)
                .toList();
    }

}