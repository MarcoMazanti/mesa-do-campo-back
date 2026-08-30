package Trabalho_de_Graduacao.Mesa_do_Campo_Back.Repository;

import Trabalho_de_Graduacao.Mesa_do_Campo_Back.Entities.Avaliacao;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface AvaliacaoRepository extends JpaRepository<Avaliacao, Integer> {
    List<Avaliacao> findAllByClienteId(int idCliente);
    List<Avaliacao> findAllByVendedorId(int idVendedor);
}
