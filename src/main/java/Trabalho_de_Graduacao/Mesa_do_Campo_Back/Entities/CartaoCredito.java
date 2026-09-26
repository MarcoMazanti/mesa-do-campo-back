package Trabalho_de_Graduacao.Mesa_do_Campo_Back.Entities;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Schema(description = "Cartão de crédito salvo por um cliente. Não guarda o número completo — só os últimos dígitos e um token de gateway.")
@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity(name = "cartao_credito")
public class CartaoCredito {
    @Schema(description = "Identificador único do cartão, gerado pelo banco.", example = "4", accessMode = Schema.AccessMode.READ_ONLY)
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private int id;

    @Schema(description = "ID do cliente dono do cartão. Precisa ser o próprio usuário autenticado.", example = "1")
    @NotNull
    @Column(name = "id_cliente", nullable = false)
    private int idCliente;

    @Schema(description = "Apelido do cartão, definido pelo próprio cliente.", example = "Cartão principal")
    @NotNull
    @Column(name = "nome", nullable = false)
    private String nome;

    @Schema(description = "Bandeira do cartão.", example = "Visa")
    @NotNull
    @Column(name = "bandeira", nullable = false)
    private String bandeira;

    @Schema(description = "Últimos 4 dígitos do cartão (nunca o número completo).", example = "4321")
    @NotNull
    @Column(name = "ultimos_digitos", nullable = false)
    private int ultimosDigitos;

    @Schema(description = "Token de gateway representando o cartão tokenizado (o número completo nunca chega a ser armazenado).",
            example = "tok_a1b2c3d4e5")
    @NotNull
    @Column(name = "token_gateway", nullable = false)
    private String tokenGateway;

    @Schema(description = "Se este é o cartão padrão do cliente. Serializado como \"padrao\" no JSON (Jackson remove o prefixo \"is\" do getter isPadrao()).",
            example = "true")
    @Column(name = "is_padrao", columnDefinition = "BOOLEAN DEFAULT FALSE")
    private boolean isPadrao;

    public CartaoCredito(int idCliente, String nome, String bandeira, int ultimosDigitos, String tokenGateway, boolean isPadrao) {
        this.idCliente = idCliente;
        this.nome = nome;
        this.bandeira = bandeira;
        this.ultimosDigitos = ultimosDigitos;
        this.tokenGateway = tokenGateway;
        this.isPadrao = isPadrao;
    }

    public CartaoCredito(int idCliente, String nome, String bandeira, int ultimosDigitos, String tokenGateway) {
        this.idCliente = idCliente;
        this.nome = nome;
        this.bandeira = bandeira;
        this.ultimosDigitos = ultimosDigitos;
        this.tokenGateway = tokenGateway;
        isPadrao = false;
    }
}