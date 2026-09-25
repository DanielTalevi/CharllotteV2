package comissiones.Charllotte.service;

import java.math.BigDecimal;
import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import comissiones.Charllotte.model.Produto;
import comissiones.Charllotte.repository.ProdutoRepository;

@Service
public class ProdutoService {

    private final ProdutoRepository produtoRepository;

    public ProdutoService(
            ProdutoRepository produtoRepository) {

        this.produtoRepository = produtoRepository;
    }


    // =====================================================
    // PRODUTO
    // =====================================================

    public Produto salvar(Produto produto) {

        if (produto.getStatus() == null) {
            produto.setStatus(true);
        }

        if (produto.getQuantidadeEstoque() == null) {
            produto.setQuantidadeEstoque(
                    BigDecimal.ZERO);
        }

        if (produto.getEstoqueMinimo() == null) {
            produto.setEstoqueMinimo(
                    BigDecimal.ZERO);
        }

        return produtoRepository.save(produto);
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
                produto.getQuantidadeEstoque();

        if (estoqueAtual == null) {
            estoqueAtual = BigDecimal.ZERO;
        }

        produto.setQuantidadeEstoque(
                estoqueAtual.add(quantidade));

        produtoRepository.save(produto);
    }


    @Transactional
    public void saida(
            Integer id,
            BigDecimal quantidade) {

        validarQuantidade(quantidade);

        Produto produto = buscarPorId(id);

        BigDecimal estoqueAtual =
                produto.getQuantidadeEstoque();

        if (estoqueAtual == null) {
            estoqueAtual = BigDecimal.ZERO;
        }

        if (estoqueAtual.compareTo(quantidade) < 0) {

            throw new RuntimeException(
                    "Estoque insuficiente");
        }

        produto.setQuantidadeEstoque(
                estoqueAtual.subtract(quantidade));

        produtoRepository.save(produto);
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
                .filter(produto ->
                        produto.getQuantidadeEstoque()
                                .compareTo(
                                        produto.getEstoqueMinimo()
                                ) <= 0)
                .count();
    }


    public BigDecimal valorEstoque() {

        BigDecimal valor =
                produtoRepository
                        .calcularValorTotalEstoque();

        if (valor == null) {
            return BigDecimal.ZERO;
        }

        return valor;
    }


    // =====================================================
    // VALIDAÇÃO
    // =====================================================

    private void validarQuantidade(
            BigDecimal quantidade) {

        if (quantidade == null ||
                quantidade.compareTo(
                        BigDecimal.ZERO) <= 0) {

            throw new RuntimeException(
                    "A quantidade deve ser maior que zero");
        }
    }
}