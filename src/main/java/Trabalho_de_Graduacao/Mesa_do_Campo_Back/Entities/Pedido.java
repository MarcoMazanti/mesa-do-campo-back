package Trabalho_de_Graduacao.Mesa_do_Campo_Back.Entities;

import Trabalho_de_Graduacao.Mesa_do_Campo_Back.Entities.Enum.StatusPedido;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PastOrPresent;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity(name = "pedido")
public class Pedido {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private int id;

    @NotNull
    @Column(name = "id_usuario", nullable = false)
    private int idUsuario;

    @NotNull
    @Column(name = "preco_total", nullable = false, precision = 10, scale = 2)
    private BigDecimal precoTotal;

    @NotNull
    @PastOrPresent
    @Column(name = "data_compra", nullable = false)
    private LocalDateTime dataCompra;

    @NotNull
    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    private StatusPedido status;

    public Pedido(int idUsuario, BigDecimal precoTotal) {
        this.idUsuario = idUsuario;
        this.precoTotal = precoTotal;
        dataCompra = LocalDateTime.now();
        status = StatusPedido.AGUARDANDO_PAGAMENTO;
    }

    public Pedido(int idUsuario, BigDecimal precoTotal, StatusPedido status) {
        this.idUsuario = idUsuario;
        this.precoTotal = precoTotal;
        this.status = status;
    }
}
