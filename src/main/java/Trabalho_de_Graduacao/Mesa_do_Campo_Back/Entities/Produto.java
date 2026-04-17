package Trabalho_de_Graduacao.Mesa_do_Campo_Back.Entities;

import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class Produto {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;
    @NotNull
    private int idVendedor;
    @NotNull
    private String nome;
    @NotNull
    private double preco;
    @NotNull
    private int quantidade;
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
