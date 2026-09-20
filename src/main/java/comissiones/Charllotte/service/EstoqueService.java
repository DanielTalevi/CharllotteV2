package comissiones.Charllotte.service;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import comissiones.Charllotte.model.Estoque;
import comissiones.Charllotte.repository.EstoqueRepository;

@Service
public class EstoqueService {

    private final EstoqueRepository estoqueRepository;

    public EstoqueService(
            EstoqueRepository estoqueRepository) {

        this.estoqueRepository = estoqueRepository;
    }

    public Estoque buscarPorProduto(
            Integer idProduto) {

        return estoqueRepository
                .findByProdutoId(idProduto)
                .orElseThrow(() ->
                        new RuntimeException(
                                "Estoque não encontrado"));
    }

    public Estoque salvar(Estoque estoque) {

        if (estoque.getQuantidade() == null) {
            estoque.setQuantidade(
                    BigDecimal.ZERO);
        }

        if (estoque.getEstoqueMinimo() == null) {
            estoque.setEstoqueMinimo(
                    BigDecimal.ZERO);
        }

        if (estoque.getDataAtualizacao() == null) {
            estoque.setDataAtualizacao(
                    LocalDateTime.now());
        }

        return estoqueRepository.save(estoque);
    }

    @Transactional
    public void adicionar(
            Integer idProduto,
            BigDecimal quantidade) {

        validarQuantidade(quantidade);

        Estoque estoque =
                buscarPorProduto(idProduto);

        estoque.setQuantidade(
                estoque.getQuantidade()
                        .add(quantidade));

        estoque.setDataAtualizacao(
                LocalDateTime.now());

        estoqueRepository.save(estoque);
    }

    @Transactional
    public void retirar(
            Integer idProduto,
            BigDecimal quantidade) {

        validarQuantidade(quantidade);

        Estoque estoque =
                buscarPorProduto(idProduto);

        if (estoque.getQuantidade()
                .compareTo(quantidade) < 0) {

            throw new RuntimeException(
                    "Estoque insuficiente");
        }

        estoque.setQuantidade(
                estoque.getQuantidade()
                        .subtract(quantidade));

        estoque.setDataAtualizacao(
                LocalDateTime.now());

        estoqueRepository.save(estoque);
    }

    private void validarQuantidade(
            BigDecimal quantidade) {

        if (quantidade == null ||
                quantidade.compareTo(
                        BigDecimal.ZERO) <= 0) {

            throw new IllegalArgumentException(
                    "A quantidade deve ser maior que zero");
        }
    }
}