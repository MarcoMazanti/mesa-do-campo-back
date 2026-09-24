package Trabalho_de_Graduacao.Mesa_do_Campo_Back.Entities;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Schema(description = "Conta de um cliente (comprador). Pode também ser vendedor, através de um registro em Vendedor com o mesmo id.")
@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity(name = "cliente")
public class Cliente {
    @Schema(description = "Identificador único do cliente, gerado pelo banco.", example = "1", accessMode = Schema.AccessMode.READ_ONLY)
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private int id;

    @Schema(description = "Nome completo do cliente. Também é o \"usuário\" usado no Basic Auth.", example = "Maria da Silva")
    @NotBlank
    @Size(max = 255, message = "O Nome do Cliente é Maior do que Esperado.")
    @Column(name = "nome", nullable = false)
    private String nome;

    @Schema(description = "CPF (11 dígitos) ou CNPJ (14 dígitos), apenas números. Imutável após o cadastro.", example = "12345678900")
    @NotBlank
    @Size(min = 11, max = 14, message = "Tamanho Inesperado para o CPF ou o CNPJ.")
    @Pattern(regexp = "^(\\d{11}|\\d{14})$", message = "Espera-se Apenas Números para o CPF ou o CNPJ.")
    @Column(name = "cpf_or_cnpj", nullable = false)
    private String cpfCnpj;

    @Schema(description = "E-mail do cliente, usado para login. Imutável após o cadastro.", example = "maria@email.com")
    @NotBlank
    @Size(max = 255, message = "Tamanho Inesperado para o E-mail.")
    @Email(message = "Insira o formato correto para o E-mail.")
    @Column(name = "email", nullable = false)
    private String email;

    @Schema(description = "Senha do cliente. É criptografada antes de ser salva e nunca retorna nas respostas (ClienteDTO não a expõe).",
            example = "SenhaForte#123", accessMode = Schema.AccessMode.WRITE_ONLY)
    @NotBlank
    @Size(max = 255)
    @Column(name = "senha", nullable = false)
    private String senha;

    @Schema(description = "Telefone do cliente, apenas números (8 a 14 dígitos).", example = "14999998888")
    @Size(max = 14, message = "Tamanho Inesperado para o Telefone.")
    @Pattern(regexp = "^\\d{8,14}$", message = "O telefone deve ter entre 8 e 11 dígitos.")
    @Column(name = "telefone")
    private String telefone;

    @Schema(description = "ID do Endereco usado como endereço de entrega do cliente. Nulo até que um endereço seja vinculado.",
            example = "3", nullable = true)
    @Column(name = "id_endereco_entrega")
    private Integer idEnderecoEntrega;

    public Cliente(int id, String nome, String cpfCnpj, String email, String senha, String telefone) {
        this.id = id;
        this.nome = nome;
        this.cpfCnpj = cpfCnpj;
        this.email = email;
        this.senha = senha;
        this.telefone = telefone;
        idEnderecoEntrega = null;
    }

    public Cliente(int id, String nome, String cpfCnpj, String email, String senha) {
        this.id = id;
        this.nome = nome;
        this.cpfCnpj = cpfCnpj;
        this.email = email;
        this.senha = senha;
        idEnderecoEntrega = null;
    }

    public Cliente(int id, String nome, String cpfCnpj, String email, String senha, Integer idEnderecoEntrega) {
        this.id = id;
        this.nome = nome;
        this.cpfCnpj = cpfCnpj;
        this.email = email;
        this.senha = senha;
        this.idEnderecoEntrega = idEnderecoEntrega;
    }

    public Cliente(String nome, String cpfCnpj, String email, String senha) {
        this.nome = nome;
        this.cpfCnpj = cpfCnpj;
        this.email = email;
        this.senha = senha;
        idEnderecoEntrega = null;
    }
}
