package Trabalho_de_Graduacao.Mesa_do_Campo_Back.Entities;

import Trabalho_de_Graduacao.Mesa_do_Campo_Back.Entities.Enum.CategoriaProduto;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Schema(description = "Produto oferecido por um vendedor no catálogo.")
@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity(name = "produto")
public class Produto {
    @Schema(description = "Identificador único do produto, gerado pelo banco.", example = "10", accessMode = Schema.AccessMode.READ_ONLY)
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private int id;

    @Schema(description = "ID do vendedor dono do produto. Só o próprio vendedor pode criar/editar/excluir seus produtos.", example = "1")
    @NotNull
    @Column(name = "id_vendedor", nullable = false)
    private int idVendedor;

    @Schema(description = "Nome do produto.", example = "Cebola Roxa")
    @NotBlank
    @Size(max = 255)
    @Column(name = "nome", nullable = false)
    private String nome;

    @Schema(description = "Preço unitário. Este é sempre o valor usado no checkout — nunca um preço enviado pelo comprador.", example = "8.90")
    @NotNull
    @Column(name = "preco", nullable = false, precision = 10, scale = 2)
    private BigDecimal preco;

    @Schema(description = "Quantidade em estoque. Decrementada automaticamente a cada venda e devolvida em cancelamentos.", example = "50")
    @NotNull
    @Column(name = "quantidade", nullable = false)
    private int quantidade;

    @Schema(description = "Categoria do produto.", example = "VERDURAS")
    @NotNull
    @Column(name = "categoria", nullable = false)
    private CategoriaProduto categoria;

    @Schema(description = "Descrição livre do produto.", example = "Cebola roxa fresca, colhida na semana.")
    @Column(name = "descricao")
    private String descricao;

    public Produto(int idVendedor, String nome, BigDecimal preco, int quantidade, CategoriaProduto categoria) {
        this.idVendedor = idVendedor;
        this.nome = nome;
        this.preco = preco;
        this.quantidade = quantidade;
        this.categoria = categoria;
    }

    public Produto(int id, int idVendedor, String nome, BigDecimal preco, int quantidade) {
        this.id = id;
        this.idVendedor = idVendedor;
        this.nome = nome;
        this.preco = preco;
        this.quantidade = quantidade;
        categoria = CategoriaProduto.OUTROS;
    }

    public Produto(int idVendedor, String nome, BigDecimal preco) {
        this.idVendedor = idVendedor;
        this.nome = nome;
        this.preco = preco;
        quantidade = 0;
        categoria = CategoriaProduto.OUTROS;
    }
}
