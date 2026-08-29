package Trabalho_de_Graduacao.Mesa_do_Campo_Back.Entities;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

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
    private double preco;

    @NotNull
    @Column(name = "quantidade", nullable = false)
    private int quantidade;

    @Column(name = "descricao")
    private String descricao;

    public Produto(int id, int idVendedor, String nome, double preco, int quantidade) {
        this.id = id;
        this.idVendedor = idVendedor;
        this.nome = nome;
        this.preco = preco;
        this.quantidade = quantidade;
    }

    public Produto(int idVendedor, String nome, double preco) {
        this.idVendedor = idVendedor;
        this.nome = nome;
        this.preco = preco;
        quantidade = 0;
    }
}
