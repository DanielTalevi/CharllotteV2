
package comissiones.Charllotte.service;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import comissiones.Charllotte.model.Estoque;
import comissiones.Charllotte.model.Produto;
import comissiones.Charllotte.repository.EstoqueRepository;
import comissiones.Charllotte.repository.ProdutoRepository;

@Service
public class EstoqueService {

    private final EstoqueRepository estoqueRepository;
    private final ProdutoRepository produtoRepository;

    public EstoqueService(
            EstoqueRepository estoqueRepository,
            ProdutoRepository produtoRepository) {

        this.estoqueRepository = estoqueRepository;
        this.produtoRepository = produtoRepository;
    }

    // =====================================================
    // CONSULTA E CADASTRO
    // =====================================================

    public Estoque buscarPorProduto(Integer idProduto) {

        return estoqueRepository
                .findByProdutoId(idProduto)
                .orElseThrow(() ->
                        new RuntimeException(
                                "Estoque não encontrado"));
    }

    @Transactional
    public Estoque salvar(Estoque estoque) {

        if (estoque.getProduto() == null
                || estoque.getProduto().getId() == null) {

            throw new IllegalArgumentException(
                    "É necessário informar um produto válido");
        }

        Produto produto = produtoRepository
                .findById(estoque.getProduto().getId())
                .orElseThrow(() ->
                        new RuntimeException(
                                "Produto não encontrado"));

        if (estoque.getQuantidade() == null) {
            estoque.setQuantidade(BigDecimal.ZERO);
        }

        if (estoque.getEstoqueMinimo() == null) {
            estoque.setEstoqueMinimo(BigDecimal.ZERO);
        }

        estoque.setProduto(produto);
        estoque.setDataAtualizacao(LocalDateTime.now());

        produto.setQuantidadeEstoque(estoque.getQuantidade());
        produto.setEstoqueMinimo(estoque.getEstoqueMinimo());

        produtoRepository.save(produto);

        return estoqueRepository.save(estoque);
    }

    // =====================================================
    // ENTRADA
    // =====================================================

    @Transactional
    public void adicionar(
            Integer idProduto,
            BigDecimal quantidade) {

        validarQuantidade(quantidade);

        Produto produto = buscarProduto(idProduto);
        Estoque estoque = buscarOuCriar(produto);

        BigDecimal quantidadeAtual =
                estoque.getQuantidade() != null
                        ? estoque.getQuantidade()
                        : BigDecimal.ZERO;

        BigDecimal novaQuantidade =
                quantidadeAtual.add(quantidade);

        estoque.setQuantidade(novaQuantidade);
        estoque.setDataAtualizacao(LocalDateTime.now());

        produto.setQuantidadeEstoque(novaQuantidade);

        estoqueRepository.save(estoque);
        produtoRepository.save(produto);
    }

    // =====================================================
    // SAÍDA
    // =====================================================

    @Transactional
    public void retirar(
            Integer idProduto,
            BigDecimal quantidade) {

        validarQuantidade(quantidade);

        Produto produto = buscarProduto(idProduto);
        Estoque estoque = buscarOuCriar(produto);

        BigDecimal quantidadeAtual =
                estoque.getQuantidade() != null
                        ? estoque.getQuantidade()
                        : BigDecimal.ZERO;

        if (quantidadeAtual.compareTo(quantidade) < 0) {
            throw new RuntimeException(
                    "Estoque insuficiente");
        }

        BigDecimal novaQuantidade =
                quantidadeAtual.subtract(quantidade);

        estoque.setQuantidade(novaQuantidade);
        estoque.setDataAtualizacao(LocalDateTime.now());

        produto.setQuantidadeEstoque(novaQuantidade);

        estoqueRepository.save(estoque);
        produtoRepository.save(produto);
    }

    // =====================================================
    // MÉTODOS AUXILIARES
    // =====================================================

    private Produto buscarProduto(Integer idProduto) {

        return produtoRepository.findById(idProduto)
                .orElseThrow(() ->
                        new RuntimeException(
                                "Produto não encontrado"));
    }

    private Estoque buscarOuCriar(Produto produto) {

        return estoqueRepository
                .findByProdutoId(produto.getId())
                .orElseGet(() -> {

                    Estoque estoque = new Estoque();

                    estoque.setProduto(produto);

                    estoque.setQuantidade(
                            produto.getQuantidadeEstoque() != null
                                    ? produto.getQuantidadeEstoque()
                                    : BigDecimal.ZERO);

                    estoque.setEstoqueMinimo(
                            produto.getEstoqueMinimo() != null
                                    ? produto.getEstoqueMinimo()
                                    : BigDecimal.ZERO);

                    estoque.setDataAtualizacao(LocalDateTime.now());

                    return estoque;
                });
    }

    private void validarQuantidade(BigDecimal quantidade) {

        if (quantidade == null
                || quantidade.compareTo(BigDecimal.ZERO) <= 0) {

            throw new IllegalArgumentException(
                    "A quantidade deve ser maior que zero");
        }
    }
}