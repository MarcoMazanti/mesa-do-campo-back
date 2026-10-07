package Trabalho_de_Graduacao.Mesa_do_Campo_Back.Repository;

import Trabalho_de_Graduacao.Mesa_do_Campo_Back.Entities.DTO.TopProdutoDTO;
import Trabalho_de_Graduacao.Mesa_do_Campo_Back.Entities.DTO.VendasMensaisDTO;
import Trabalho_de_Graduacao.Mesa_do_Campo_Back.Entities.Enum.CategoriaProduto;
import Trabalho_de_Graduacao.Mesa_do_Campo_Back.Entities.Enum.StatusPedido;
import Trabalho_de_Graduacao.Mesa_do_Campo_Back.Entities.ItemPedido;
import Trabalho_de_Graduacao.Mesa_do_Campo_Back.Entities.Produto;
import Trabalho_de_Graduacao.Mesa_do_Campo_Back.Entities.Vendedor;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Repository
public interface VendedorRepository extends JpaRepository<Vendedor, Integer> {
    Optional<Vendedor> findByIdVendedor(int idVendedor);
    Optional<Vendedor> findByIdVendedorAndAtivoTrue(int idVendedor);
    List<Vendedor> findAllByAtivoTrue();
    @Query(value = "WITH base_agrupada AS (\n" +
            "    SELECT\n" +
            "        p.id,\n" +
            "        p.nome,\n" +
            "        SUM(ip.quantidade) AS quantidade,\n" +
            "        SUM(ip.preco_unit * ip.quantidade) AS valorVendido\n" +
            "    FROM item_pedido ip\n" +
            "    INNER JOIN produto p ON ip.id_produto = p.id\n" +
            "    WHERE p.id_vendedor = :idUsuarioAuth AND p.ativo = true\n" +
            "    GROUP BY p.id, p.nome\n" +
            "),\n" +
            "base_com_percentual AS (\n" +
            "    SELECT\n" +
            "        id,\n" +
            "        nome,\n" +
            "        quantidade,\n" +
            "        valorVendido,\n" +
            "        ROUND((valorVendido * 100.0 / SUM(valorVendido) OVER ()), 2) as percentual\n" +
            "    FROM base_agrupada\n" +
            ")\n" +
            "\n" +
            "(\n" +
            "    SELECT id, nome, quantidade, valorVendido, percentual\n" +
            "    FROM base_com_percentual\n" +
            "    ORDER BY valorVendido DESC\n" +
            "    LIMIT :quant\n" +
            ")\n" +
            "\n" +
            "UNION ALL\n" +
            "\n" +
            "(\n" +
            "    SELECT\n" +
            "        0 AS id,\n" +
            "        'TOTAL' AS nome,\n" +
            "        COALESCE(SUM(quantidade), 0) AS quantidade,\n" +
            "        COALESCE(SUM(valorVendido), 0) AS valorVendido,\n" +
            "        100.00 AS percentual\n" +
            "    FROM base_agrupada\n" +
            ")", nativeQuery = true)
    List<Object[]> getTopProdutos(
            @Param("quant") int quant,
            @Param("idUsuarioAuth") int idUsuarioAuth);

    @Query(value = "SELECT\n" +
            "   EXTRACT(YEAR FROM ip.data_compra) AS ano,\n" +
            "   EXTRACT(MONTH FROM ip.data_compra) AS mes,\n" +
            "   SUM(ip.preco_unit * ip.quantidade) AS valor_vendido\n" +
            "FROM item_pedido ip\n" +
            "   INNER JOIN produto p ON ip.id_produto = p.id\n" +
            "WHERE\n" +
            "   p.id_vendedor = :idUsuarioAuth AND\n" +
            "   p.ativo = TRUE AND\n" +
            "   ip.data_compra >= CAST(:dataInicio AS DATE) AND\n" +
            "   ip.data_compra < CAST(:dataFim AS DATE) + INTERVAL '1 day'\n" +
            "GROUP BY ano, mes\n" +
            "ORDER BY ano, mes", nativeQuery = true)
    List<Object[]> getVendasMensais(
            @Param("dataInicio") LocalDate dataInicio,
            @Param("dataFim") LocalDate dataFim,
            @Param("idUsuarioAuth") int idUsuarioAuth);

    @Query(value = "select\n" +
            "   ip.*\n" +
            "from item_pedido ip\n" +
            "   inner join produto p on ip.id_produto = p.id\n" +
            "where\n" +
            "   p.id_vendedor = :idUsuarioAuth and\n" +
            "   p.ativo = true and\n" +
            "   (p.id = :id or :id is null) and\n" +
            "   (ip.status = :categoria or :categoria is null) and\n" +
            "   (p.categoria = :categoria or :categoria is null)", nativeQuery = true)
    List<ItemPedido> getItensVendidos(
            @Param("id") int id,
            @Param("status") StatusPedido status,
            @Param("categoria") String categoria,
            @Param("idUsuarioAuth") int idUsuarioAuth);

    @Query(value = "select\n" +
            "   p.*\n" +
            "from produto p\n" +
            "where\n" +
            "   p.id_vendedor = :idUsuarioAuth and\n" +
            "   p.ativo = true and\n" +
            "   p.nome ILIKE CONCAT('%', :nome, '%')", nativeQuery = true)
    List<Produto> getProdutosPorNome(
            @Param("nome") String nome,
            @Param("idUsuarioAuth") int idUsuarioAuth);
}
