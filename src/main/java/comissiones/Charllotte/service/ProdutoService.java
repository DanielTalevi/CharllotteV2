package comissiones.Charllotte.service;

import java.util.List;

import org.springframework.stereotype.Service;

import comissiones.Charllotte.model.Produto;
import comissiones.Charllotte.repository.ProdutoRepository;

@Service
public class ProdutoService {

    private final ProdutoRepository produtoRepository;

    public ProdutoService(
            ProdutoRepository produtoRepository) {

        this.produtoRepository = produtoRepository;
    }

    public Produto salvar(Produto produto) {

        if (produto.getStatus() == null) {
            produto.setStatus(true);
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
}