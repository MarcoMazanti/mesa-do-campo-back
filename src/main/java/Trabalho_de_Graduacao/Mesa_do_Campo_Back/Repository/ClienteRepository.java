package Trabalho_de_Graduacao.Mesa_do_Campo_Back.Repository;

import Trabalho_de_Graduacao.Mesa_do_Campo_Back.Entities.Cliente;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ClienteRepository extends JpaRepository<Cliente, Integer> {
    Boolean existsByCpfCnpj(String cpfCnpj);
    Boolean existsAnyByEmail(String email);
    List<Cliente> findAllByNome(String nome);
    Optional<Cliente> findByEmail(String email);
}
