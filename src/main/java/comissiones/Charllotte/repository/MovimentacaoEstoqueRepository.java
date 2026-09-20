package comissiones.Charllotte.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import comissiones.Charllotte.model.MovimentacaoEstoque;

public interface MovimentacaoEstoqueRepository
        extends JpaRepository<
                MovimentacaoEstoque,
                Integer> {

    List<MovimentacaoEstoque>
    findByProdutoId(Integer idProduto);

    List<MovimentacaoEstoque>
    findByFuncionarioId(Integer idFuncionario);
}