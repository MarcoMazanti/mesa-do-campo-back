package Trabalho_de_Graduacao.Mesa_do_Campo_Back.Entities;

import Trabalho_de_Graduacao.Mesa_do_Campo_Back.Entities.Enum.CategoriaProduto;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity(name = "produto")
public class Produto {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private int id;

    @NotNull
    @Column(name = "id_vendedor", nullable = false)
    private int idVendedor;

    @NotBlank
    @Size(max = 255)
    @Column(name = "nome", nullable = false)
    private String nome;

    @NotNull
    @Column(name = "preco", nullable = false, precision = 10, scale = 2)
    private BigDecimal preco;

    @NotNull
    @Column(name = "quantidade", nullable = false)
    private int quantidade;

    @NotNull
    @Column(name = "categoria", nullable = false)
    private CategoriaProduto categoria;

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
