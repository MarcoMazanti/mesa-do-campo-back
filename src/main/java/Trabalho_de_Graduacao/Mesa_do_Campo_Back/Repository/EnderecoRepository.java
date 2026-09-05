package Trabalho_de_Graduacao.Mesa_do_Campo_Back.Repository;

import Trabalho_de_Graduacao.Mesa_do_Campo_Back.Entities.Endereco;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface EnderecoRepository extends JpaRepository<Endereco, Integer> {
    Boolean existsAnyByIdAndIdUsuario(int id, int idUsuario);
    Boolean existsByIdAndIdUsuario(int id, int idUsuario);
    List<Endereco> findAllByIdUsuario(int idUsuario);
}