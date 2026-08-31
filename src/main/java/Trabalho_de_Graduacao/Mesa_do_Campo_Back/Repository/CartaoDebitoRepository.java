package Trabalho_de_Graduacao.Mesa_do_Campo_Back.Repository;

import Trabalho_de_Graduacao.Mesa_do_Campo_Back.Entities.CartaoDebito;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CartaoDebitoRepository extends JpaRepository<CartaoDebito, Integer> {
    List<CartaoDebito> findAllByClienteId(int idCliente);
}
