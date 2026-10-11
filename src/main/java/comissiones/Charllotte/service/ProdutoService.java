
package comissiones.Charllotte.service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import comissiones.Charllotte.model.Estoque;
import comissiones.Charllotte.model.Produto;
import comissiones.Charllotte.repository.EstoqueRepository;
import comissiones.Charllotte.repository.ProdutoRepository;

@Service
public class ProdutoService {

    private final ProdutoRepository produtoRepository;
    private final EstoqueRepository estoqueRepository;

    public ProdutoService(
            ProdutoRepository produtoRepository,
            EstoqueRepository estoqueRepository) {

        this.produtoRepository = produtoRepository;
        this.estoqueRepository = estoqueRepository;
    }

    // =====================================================
    // PRODUTO
    // =====================================================

    @Transactional
    public Produto salvar(Produto produto) {

        if (produto.getStatus() == null) {
            produto.setStatus(true);
        }

        if (produto.getQuantidadeEstoque() == null) {
            produto.setQuantidadeEstoque(BigDecimal.ZERO);
        }

        if (produto.getEstoqueMinimo() == null) {
            produto.setEstoqueMinimo(BigDecimal.ZERO);
        }

        Produto produtoSalvo = produtoRepository.save(produto);

        Estoque estoque = estoqueRepository
                .findByProdutoId(produtoSalvo.getId())
                .orElseGet(Estoque::new);

        estoque.setProduto(produtoSalvo);
        estoque.setQuantidade(
                produtoSalvo.getQuantidadeEstoque());
        estoque.setEstoqueMinimo(
                produtoSalvo.getEstoqueMinimo());
        estoque.setDataAtualizacao(LocalDateTime.now());

        estoqueRepository.save(estoque);

        return produtoSalvo;
    }

    public Produto buscarPorId(Integer id) {

        return produtoRepository.findById(id)
                .orElseThrow(() ->
                        new RuntimeException(
                                "Produto não encontrado"));
    }

    public List<Produto> listarTodos() {

        return produtoRepository.findAll();
    }

    public List<Produto> listarAtivos() {

        return produtoRepository.findByStatusTrue();
    }

    public List<Produto> pesquisar(String nome) {

        return produtoRepository
                .findByNomeContainingIgnoreCase(nome);
    }

    public void desativar(Integer id) {

        Produto produto = buscarPorId(id);

        produto.setStatus(false);

        produtoRepository.save(produto);
    }

    public void ativar(Integer id) {

        Produto produto = buscarPorId(id);

        produto.setStatus(true);

        produtoRepository.save(produto);
    }

    // =====================================================
    // ESTOQUE
    // =====================================================

    @Transactional
    public void entrada(
            Integer id,
            BigDecimal quantidade) {

        validarQuantidade(quantidade);

        Produto produto = buscarPorId(id);

        BigDecimal estoqueAtual =
                produto.getQuantidadeEstoque() != null
                        ? produto.getQuantidadeEstoque()
                        : BigDecimal.ZERO;

        BigDecimal novaQuantidade =
                estoqueAtual.add(quantidade);

        produto.setQuantidadeEstoque(novaQuantidade);

        produtoRepository.save(produto);

        sincronizarEstoque(produto, novaQuantidade);
    }

    @Transactional
    public void saida(
            Integer id,
            BigDecimal quantidade) {

        validarQuantidade(quantidade);

        Produto produto = buscarPorId(id);

        BigDecimal estoqueAtual =
                produto.getQuantidadeEstoque() != null
                        ? produto.getQuantidadeEstoque()
                        : BigDecimal.ZERO;

        if (estoqueAtual.compareTo(quantidade) < 0) {
            throw new RuntimeException(
                    "Estoque insuficiente");
        }

        BigDecimal novaQuantidade =
                estoqueAtual.subtract(quantidade);

        produto.setQuantidadeEstoque(novaQuantidade);

        produtoRepository.save(produto);

        sincronizarEstoque(produto, novaQuantidade);
    }

    private void sincronizarEstoque(
            Produto produto,
            BigDecimal quantidade) {

        Estoque estoque = estoqueRepository
                .findByProdutoId(produto.getId())
                .orElseGet(Estoque::new);

        estoque.setProduto(produto);
        estoque.setQuantidade(quantidade);

        if (estoque.getEstoqueMinimo() == null) {
            estoque.setEstoqueMinimo(
                    produto.getEstoqueMinimo() != null
                            ? produto.getEstoqueMinimo()
                            : BigDecimal.ZERO);
        }

        estoque.setDataAtualizacao(LocalDateTime.now());

        estoqueRepository.save(estoque);
    }

    // =====================================================
    // INDICADORES DO ESTOQUE
    // =====================================================

    public long totalProdutos() {

        return produtoRepository
                .findByStatusTrue()
                .size();
    }

    public long produtosBaixo() {

        return produtoRepository
                .findByStatusTrue()
                .stream()
                .filter(produto -> {
                    BigDecimal quantidade =
                            produto.getQuantidadeEstoque() != null
                                    ? produto.getQuantidadeEstoque()
                                    : BigDecimal.ZERO;

                    BigDecimal minimo =
                            produto.getEstoqueMinimo() != null
                                    ? produto.getEstoqueMinimo()
                                    : BigDecimal.ZERO;

                    return quantidade.compareTo(minimo) <= 0;
                })
                .count();
    }

    public BigDecimal valorEstoque() {

        BigDecimal valor =
                produtoRepository.calcularValorTotalEstoque();

        return valor != null
                ? valor
                : BigDecimal.ZERO;
    }

    // =====================================================
    // VALIDAÇÃO
    // =====================================================

    private void validarQuantidade(
            BigDecimal quantidade) {

        if (quantidade == null
                || quantidade.compareTo(BigDecimal.ZERO) <= 0) {

            throw new RuntimeException(
                    "A quantidade deve ser maior que zero");
        }
    }
}