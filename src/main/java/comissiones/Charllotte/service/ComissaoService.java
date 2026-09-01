package comissiones.Charllotte.service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;

import org.springframework.stereotype.Service;

import comissiones.Charllotte.model.Comissao;
import comissiones.Charllotte.model.ItemVenda;
import comissiones.Charllotte.model.Venda;
import comissiones.Charllotte.repository.ComissaoRepository;

@Service
public class ComissaoService {

    private final ComissaoRepository comissaoRepository;

    public ComissaoService(
            ComissaoRepository comissaoRepository) {

        this.comissaoRepository =
                comissaoRepository;
    }

    public Comissao gerarComissao(Venda venda) {

        BigDecimal valorComissao =
                BigDecimal.ZERO;

        BigDecimal valorBase =
                BigDecimal.ZERO;

        for (ItemVenda item : venda.getItens()) {

            BigDecimal subtotal =
                    item.getSubtotal();

            BigDecimal percentual =
                    item.getProduto()
                            .getPercentualComissao();

            BigDecimal comissaoItem =
                    subtotal
                            .multiply(percentual)
                            .divide(
                                BigDecimal.valueOf(100),
                                2,
                                RoundingMode.HALF_UP
                            );

            valorComissao =
                    valorComissao.add(
                            comissaoItem);

            valorBase =
                    valorBase.add(subtotal);
        }

        BigDecimal percentualEfetivo =
                BigDecimal.ZERO;

        if (valorBase.compareTo(
                BigDecimal.ZERO) > 0) {

            percentualEfetivo =
                    valorComissao
                            .multiply(
                                BigDecimal.valueOf(100))
                            .divide(
                                valorBase,
                                2,
                                RoundingMode.HALF_UP
                            );
        }

        Comissao comissao = new Comissao();

        comissao.setVenda(venda);
        comissao.setFuncionario(
                venda.getFuncionario());
        comissao.setPercentual(
                percentualEfetivo);
        comissao.setValor(valorComissao);
        comissao.setData(LocalDateTime.now());

        return comissaoRepository.save(comissao);
    }
}