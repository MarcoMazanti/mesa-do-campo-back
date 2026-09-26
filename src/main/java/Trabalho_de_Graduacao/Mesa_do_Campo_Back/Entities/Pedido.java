package Trabalho_de_Graduacao.Mesa_do_Campo_Back.Entities;

import Trabalho_de_Graduacao.Mesa_do_Campo_Back.Entities.Enum.StatusPedido;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PastOrPresent;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Schema(description = "\"Cabeçalho\" de um pedido de compra. Os produtos comprados ficam em ItemPedido, e o pagamento em Pagamento.")
@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity(name = "pedido")
public class Pedido {
    @Schema(description = "Identificador único do pedido, gerado pelo banco.", example = "5", accessMode = Schema.AccessMode.READ_ONLY)
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private int id;

    @Schema(description = "ID do cliente comprador. Só o próprio cliente pode consultar/alterar este pedido.", example = "1")
    @NotNull
    @Column(name = "id_cliente", nullable = false)
    private int idCliente;

    @Schema(description = "Valor total do pedido, soma dos itens no momento do checkout.", example = "45.90")
    @NotNull
    @Column(name = "preco_total", nullable = false, precision = 10, scale = 2)
    private BigDecimal precoTotal;

    @Schema(description = "Data e hora em que a compra foi realizada.", example = "2026-09-20T14:30:00")
    @NotNull
    @PastOrPresent
    @Column(name = "data_compra", nullable = false)
    private LocalDateTime dataCompra;

    @Schema(description = "Status atual do pedido.", example = "PROCESSANDO")
    @NotNull
    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    private StatusPedido status;

    public Pedido(int idCliente, BigDecimal precoTotal) {
        this.idCliente = idCliente;
        this.precoTotal = precoTotal;
        dataCompra = LocalDateTime.now();
        status = StatusPedido.AGUARDANDO_PAGAMENTO;
    }

    public Pedido(int idCliente, BigDecimal precoTotal, StatusPedido status) {
        this.idCliente = idCliente;
        this.precoTotal = precoTotal;
        this.status = status;
    }
}
