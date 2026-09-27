package Trabalho_de_Graduacao.Mesa_do_Campo_Back.Repository;

import Trabalho_de_Graduacao.Mesa_do_Campo_Back.Entities.Cliente;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ClienteRepository extends JpaRepository<Cliente, Integer> {
    Boolean existsByCpfCnpj(String cpfCnpj);
    List<Cliente> findAllByAtivoTrue();
    List<Cliente> findAllByNomeAndAtivoTrue(String nome);
    Optional<Cliente> findByIdAndAtivoTrue(int id);
    Optional<Cliente> findByEmailIgnoreCaseAndAtivoTrue(String email);
    Optional<Cliente> findByEmailIgnoreCase(String email);
}
