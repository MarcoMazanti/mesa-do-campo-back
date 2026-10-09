package Trabalho_de_Graduacao.Mesa_do_Campo_Back.Repository;

import Trabalho_de_Graduacao.Mesa_do_Campo_Back.Entities.Enum.CategoriaProduto;
import Trabalho_de_Graduacao.Mesa_do_Campo_Back.Entities.Produto;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ProdutoRepository extends JpaRepository<Produto, Integer> {
    List<Produto> findAllByAtivoTrue();
    List<Produto> findAllByIdVendedorAndAtivoTrue(int idVendedor);
    List<Produto> findAllByCategoriaAndAtivoTrue(CategoriaProduto categoria);
    @Query(value = "SELECT * FROM produto WHERE nome ILIKE %:nome% AND ativo = true", nativeQuery = true)
    List<Produto> findAllByNomeAndAtivoTrue(String nome);
    @Query(value = "SELECT * FROM produto WHERE nome ILIKE %:nome% AND ativo = true AND id_vendedor = :idUsuarioAuth", nativeQuery = true)
    List<Produto> findAllByNomeAndAtivoTrueAndIdVendedor(String nome, int idUsuarioAuth);
}
