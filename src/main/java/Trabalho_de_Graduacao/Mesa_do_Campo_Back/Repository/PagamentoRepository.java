package Trabalho_de_Graduacao.Mesa_do_Campo_Back.Repository;

import Trabalho_de_Graduacao.Mesa_do_Campo_Back.Entities.Pagamento;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface PagamentoRepository extends JpaRepository<Pagamento, Integer> {
    Optional<Pagamento> findByIdPedido(int idPedido);
    @Query("SELECT pag FROM pagamento pag JOIN pedido ped ON pag.idPedido = ped.id WHERE ped.idUsuario = :idUsuario")
    List<Pagamento> findAllByIdUsuario(@Param("idUsuario") int idUsuario);
}
