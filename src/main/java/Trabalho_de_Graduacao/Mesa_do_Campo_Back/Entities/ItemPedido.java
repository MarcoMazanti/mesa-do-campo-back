package Trabalho_de_Graduacao.Mesa_do_Campo_Back.Entities;

import Trabalho_de_Graduacao.Mesa_do_Campo_Back.Entities.Enum.StatusItemPedido;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity(name = "item_pedido")
public class ItemPedido {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private int id;

    @NotNull
    @Column(name = "id_pedido", nullable = false)
    private int idPedido;

    @NotNull
    @Column(name = "id_produto", nullable = false)
    private int idProduto;

    @NotNull
    @Column(name = "quantidade", nullable = false)
    private int quantidade;

    @NotNull
    @Column(name = "preco_unit", precision = 10, scale = 2, nullable = false)
    private BigDecimal precoUnit;

    @NotNull
    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    private StatusItemPedido status;

    @Column(name = "data_compra")
    private LocalDateTime dataCompra;

    public ItemPedido(int idPedido, int idProduto, int quantidade, BigDecimal precoUnit, StatusItemPedido status, LocalDateTime dataCompra) {
        this.idPedido = idPedido;
        this.idProduto = idProduto;
        this.quantidade = quantidade;
        this.precoUnit = precoUnit;
        this.status = status;
        this.dataCompra = dataCompra;
    }

    public ItemPedido(int idPedido, int idProduto, int quantidade, BigDecimal precoUnit, LocalDateTime dataCompra) {
        this.idPedido = idPedido;
        this.idProduto = idProduto;
        this.quantidade = quantidade;
        this.precoUnit = precoUnit;
        this.status = StatusItemPedido.PENDENTE;
        this.dataCompra = dataCompra;
    }
}
