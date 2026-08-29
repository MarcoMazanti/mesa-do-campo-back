package Trabalho_de_Graduacao.Mesa_do_Campo_Back.Entities;

import Trabalho_de_Graduacao.Mesa_do_Campo_Back.Entities.Enum.StatusPagamento;
import Trabalho_de_Graduacao.Mesa_do_Campo_Back.Entities.Enum.TipoPagamento;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity(name = "pagamento")
public class Pagamento {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private int id;

    @NotNull
    @Column(name = "id_pedido", nullable = false)
    private int idPedido;

    @NotNull
    @Enumerated(EnumType.STRING)
    @Column(name = "metodo_pagamento", nullable = false)
    private TipoPagamento metodoPagamento;

    @NotNull
    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    private StatusPagamento status;

    @Column(name = "data_pagamento")
    private LocalDateTime dataPagamento;

    @NotNull
    @Column(name = "valor_pago", precision = 10, scale = 2, nullable = false)
    private double valorPago;

    public Pagamento(int idPedido, TipoPagamento metodoPagamento, StatusPagamento status, LocalDateTime dataPagamento, double valorPago) {
        this.idPedido = idPedido;
        this.metodoPagamento = metodoPagamento;
        this.status = status;
        this.dataPagamento = dataPagamento;
        this.valorPago = valorPago;
    }
}