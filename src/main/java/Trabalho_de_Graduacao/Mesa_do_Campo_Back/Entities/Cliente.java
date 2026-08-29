package Trabalho_de_Graduacao.Mesa_do_Campo_Back.Entities;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity(name = "cliente")
public class Cliente {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private int id;

    @NotBlank
    @Size(max = 255, message = "O Nome do Cliente é Maior do que Esperado.")
    @Column(name = "nome", nullable = false)
    private String nome;

    @NotBlank
    @Size(min = 11, max = 14, message = "Tamanho Inesperado para o CPF ou o CNPJ.")
    @Pattern(regexp = "^(\\d{11}|\\d{14})$", message = "Espera-se Apenas Números para o CPF ou o CNPJ.")
    @Column(name = "cpf_or_cnpj", nullable = false)
    private String cpfOrCnpj;

    @NotBlank
    @Size(max = 255, message = "Tamanho Inesperado para o E-mail.")
    @Email(message = "Insira o formato correto para o E-mail.")
    @Column(name = "email", nullable = false)
    private String email;

    @NotBlank
    @Size(max = 255)
    @Column(name = "senha", nullable = false)
    private String senha;

    @Size(max = 14, message = "Tamanho Inesperado para o Telefone.")
    @Pattern(regexp = "^\\d{8,14}$", message = "O telefone deve ter entre 8 e 11 dígitos.")
    @Column(name = "telefone")
    private String telefone;

    @Column(name = "id_endereco_entrega")
    private int idEnderecoEntrega;

    public Cliente(int id, String nome, String cpfOrCnpj, String email, String senha, String telefone) {
        this.id = id;
        this.nome = nome;
        this.cpfOrCnpj = cpfOrCnpj;
        this.email = email;
        this.senha = senha;
        this.telefone = telefone;
    }

    public Cliente(int id, String nome, String cpfOrCnpj, String email, String senha) {
        this.id = id;
        this.nome = nome;
        this.cpfOrCnpj = cpfOrCnpj;
        this.email = email;
        this.senha = senha;
    }

    public Cliente(int id, String nome, String cpfOrCnpj, String email, String senha, int idEnderecoEntrega) {
        this.id = id;
        this.nome = nome;
        this.cpfOrCnpj = cpfOrCnpj;
        this.email = email;
        this.senha = senha;
        this.idEnderecoEntrega = idEnderecoEntrega;
    }

    public Cliente(String nome, String cpfOrCnpj, String email, String senha) {
        this.nome = nome;
        this.cpfOrCnpj = cpfOrCnpj;
        this.email = email;
        this.senha = senha;
    }
}
