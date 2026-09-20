package comissiones.Charllotte.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import comissiones.Charllotte.model.ItemVenda;

public interface ItemVendaRepository
        extends JpaRepository<ItemVenda, Integer> {

    List<ItemVenda> findByVendaId(
            Integer idVenda);

    List<ItemVenda> findByProdutoId(
            Integer idProduto);
}