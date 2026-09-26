package Trabalho_de_Graduacao.Mesa_do_Campo_Back.Entities;

import Trabalho_de_Graduacao.Mesa_do_Campo_Back.Entities.Enum.StatusPagamento;
import Trabalho_de_Graduacao.Mesa_do_Campo_Back.Entities.Enum.TipoPagamento;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Schema(description = "Pagamento associado a um Pedido. Simulado: como não há gateway real, criar um pagamento já o registra como APROVADO.")
@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity(name = "pagamento")
public class Pagamento {
    @Schema(description = "Identificador único do pagamento, gerado pelo banco.", example = "5", accessMode = Schema.AccessMode.READ_ONLY)
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private int id;

    @Schema(description = "ID do pedido pago. Relação 1 para 1 — um pedido não pode ter mais de um pagamento.", example = "5")
    @NotNull
    @Column(name = "id_pedido", nullable = false)
    private int idPedido;

    @Schema(description = "Forma de pagamento escolhida.", example = "PIX")
    @NotNull
    @Enumerated(EnumType.STRING)
    @Column(name = "metodo_pagamento", nullable = false)
    private TipoPagamento metodoPagamento;

    @Schema(description = "Status do pagamento.", example = "APROVADO")
    @NotNull
    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    private StatusPagamento status;

    @Schema(description = "Data e hora em que o pagamento foi aprovado. Nulo enquanto o status não for APROVADO.", example = "2026-09-20T14:31:00")
    @Column(name = "data_pagamento")
    private LocalDateTime dataPagamento;

    @Schema(description = "Valor pago, igual ao Pedido.precoTotal no momento da criação do pagamento.", example = "45.90")
    @NotNull
    @Column(name = "valor_pago", precision = 10, scale = 2, nullable = false)
    private BigDecimal valorPago;

    public Pagamento(int idPedido, TipoPagamento metodoPagamento, StatusPagamento status, LocalDateTime dataPagamento, BigDecimal valorPago) {
        this.idPedido = idPedido;
        this.metodoPagamento = metodoPagamento;
        this.status = status;
        this.dataPagamento = dataPagamento;
        this.valorPago = valorPago;
    }
}