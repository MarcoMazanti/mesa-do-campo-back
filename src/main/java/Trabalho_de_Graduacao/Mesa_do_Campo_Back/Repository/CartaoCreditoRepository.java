package Trabalho_de_Graduacao.Mesa_do_Campo_Back.Repository;

import Trabalho_de_Graduacao.Mesa_do_Campo_Back.Entities.CartaoCredito;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CartaoCreditoRepository extends JpaRepository<CartaoCredito, Integer> {
    List<CartaoCredito> findAllByClienteId(int idCliente);
}
