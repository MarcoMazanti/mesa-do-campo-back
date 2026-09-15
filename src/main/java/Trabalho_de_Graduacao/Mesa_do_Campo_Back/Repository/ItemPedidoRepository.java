package Trabalho_de_Graduacao.Mesa_do_Campo_Back.Repository;

import Trabalho_de_Graduacao.Mesa_do_Campo_Back.Entities.ItemPedido;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ItemPedidoRepository extends JpaRepository<ItemPedido, Integer> {
    List<ItemPedido> findAllByIdPedido(int idPedido);
    @Query("SELECT ip FROM item_pedido ip JOIN produto p ON ip.idProduto = p.id WHERE p.idVendedor = :idVendedor")
    List<ItemPedido> findAllByIdVendedor(@Param("idVendedor") int idVendedor);
}
