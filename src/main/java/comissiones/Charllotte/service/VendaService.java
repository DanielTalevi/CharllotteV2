package comissiones.Charllotte.service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import comissiones.Charllotte.model.ItemVenda;
import comissiones.Charllotte.model.Produto;
import comissiones.Charllotte.model.StatusVenda;
import comissiones.Charllotte.model.Usuario;
import comissiones.Charllotte.model.Venda;
import comissiones.Charllotte.repository.ProdutoRepository;
import comissiones.Charllotte.repository.VendaRepository;

@Service
public class VendaService {

    private final VendaRepository vendaRepository;

    private final ProdutoRepository produtoRepository;

    private final EstoqueService estoqueService;

    private final ComissaoService comissaoService;

    public VendaService(
            VendaRepository vendaRepository,
            ProdutoRepository produtoRepository,
            EstoqueService estoqueService,
            ComissaoService comissaoService) {

        this.vendaRepository = vendaRepository;
        this.produtoRepository = produtoRepository;
        this.estoqueService = estoqueService;
        this.comissaoService = comissaoService;
    }

    @Transactional
    public Venda realizarVenda(
            Usuario funcionario,
            List<ItemVenda> itens) {

        if (funcionario == null) {

            throw new IllegalArgumentException(
                    "Funcionário é obrigatório");
        }

        if (itens == null || itens.isEmpty()) {

            throw new IllegalArgumentException(
                    "A venda precisa ter itens");
        }

        Venda venda = new Venda();

        venda.setFuncionario(funcionario);

        venda.setDataVenda(
                LocalDateTime.now());

        venda.setStatus(
                StatusVenda.EM_ANDAMENTO);

        BigDecimal valorTotal =
                BigDecimal.ZERO;

        for (ItemVenda item : itens) {

            if (item.getQuantidade() == null ||
                    item.getQuantidade()
                        .compareTo(BigDecimal.ZERO) <= 0) {

                throw new IllegalArgumentException(
                        "Quantidade inválida");
            }

            Integer idProduto =
                    item.getProduto().getId();

            Produto produto =
                    produtoRepository
                        .findById(idProduto)
                        .orElseThrow(() ->
                            new RuntimeException(
                                "Produto não encontrado"));

            if (!produto.getStatus()) {

                throw new RuntimeException(
                        "Produto está inativo");
            }

            BigDecimal subtotal =
                    produto.getPreco()
                            .multiply(
                                item.getQuantidade());

            item.setVenda(venda);
            item.setProduto(produto);

            item.setPrecoUnitario(
                    produto.getPreco());

            item.setSubtotal(subtotal);

            valorTotal =
                    valorTotal.add(subtotal);

            estoqueService.retirar(
                    produto.getId(),
                    item.getQuantidade());

            venda.adicionarItem(item);
        }

        venda.setValorTotal(valorTotal);

        venda.setStatus(
                StatusVenda.REALIZADA);

        Venda vendaSalva =
                vendaRepository.save(venda);

        comissaoService.gerarComissao(
                vendaSalva);

        return vendaSalva;
    }

    public Venda buscarPorId(Integer id) {

        return vendaRepository.findById(id)
                .orElseThrow(() ->
                        new RuntimeException(
                                "Venda não encontrada"));
    }

    public List<Venda> listarTodas() {

        return vendaRepository.findAll();
    }

    public List<Venda> listarPorFuncionario(
            Integer idFuncionario) {

        return vendaRepository
                .findByFuncionarioId(
                        idFuncionario);
    }

    public List<Venda> listarPorStatus(
            StatusVenda status) {

        return vendaRepository
                .findByStatus(status);
    }

    @Transactional
    public void cancelar(Integer id) {

        Venda venda = buscarPorId(id);

        if (venda.getStatus()
                == StatusVenda.CANCELADA) {

            throw new RuntimeException(
                    "Venda já está cancelada");
        }

        if (venda.getStatus()
                != StatusVenda.REALIZADA) {

            throw new RuntimeException(
                    "Somente vendas realizadas podem ser canceladas");
        }

        for (ItemVenda item : venda.getItens()) {

            estoqueService.adicionar(
                    item.getProduto().getId(),
                    item.getQuantidade());
        }

        venda.setStatus(
                StatusVenda.CANCELADA);

        vendaRepository.save(venda);
    }
}