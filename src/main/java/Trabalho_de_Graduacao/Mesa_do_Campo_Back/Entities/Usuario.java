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
public class Usuario {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;
    @NotNull
    private String nome;
    @NotNull
    private String cpfOrCnpj;
    @NotNull
    private String email;
    @NotNull
    private String senha;
    private String telefone;
    private int idEnderecoEntrega;

    public Usuario(int id, String nome, String cpfOrCnpj, String email, String senha, String telefone) {
        this.id = id;
        this.nome = nome;
        this.cpfOrCnpj = cpfOrCnpj;
        this.email = email;
        this.senha = senha;
        this.telefone = telefone;
    }

    public Usuario(int id, String nome, String cpfOrCnpj, String email, String senha) {
        this.id = id;
        this.nome = nome;
        this.cpfOrCnpj = cpfOrCnpj;
        this.email = email;
        this.senha = senha;
    }

    public Usuario(int id, String nome, String cpfOrCnpj, String email, String senha, int idEnderecoEntrega) {
        this.id = id;
        this.nome = nome;
        this.cpfOrCnpj = cpfOrCnpj;
        this.email = email;
        this.senha = senha;
        this.idEnderecoEntrega = idEnderecoEntrega;
    }
}
