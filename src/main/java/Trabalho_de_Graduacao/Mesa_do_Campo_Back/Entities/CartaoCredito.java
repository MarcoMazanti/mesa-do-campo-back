package Trabalho_de_Graduacao.Mesa_do_Campo_Back.Entities;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity(name = "cartao_credito")
public class CartaoCredito {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private int id;

    @NotNull
    @Column(name = "id_cliente", nullable = false)
    private int idCliente;

    @NotNull
    @Column(name = "nome", nullable = false)
    private String nome;

    @NotNull
    @Column(name = "bandeira", nullable = false)
    private String bandeira;

    @NotNull
    @Column(name = "ultimos_digitos", nullable = false)
    private int ultimosDigitos;

    @NotNull
    @Column(name = "token_gateway", nullable = false)
    private String tokenGateway;

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