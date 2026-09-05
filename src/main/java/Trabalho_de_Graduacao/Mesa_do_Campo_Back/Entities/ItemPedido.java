package Trabalho_de_Graduacao.Mesa_do_Campo_Back.Entities;

import Trabalho_de_Graduacao.Mesa_do_Campo_Back.Entities.Enum.StatusItemPedido;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

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
    @Column(name = "preco", precision = 10, scale = 2, nullable = false)
    private BigDecimal preco;

    @NotNull
    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    private StatusItemPedido status;

    public ItemPedido(int idPedido, int idProduto, int quantidade, BigDecimal preco, StatusItemPedido status) {
        this.idPedido = idPedido;
        this.idProduto = idProduto;
        this.quantidade = quantidade;
        this.preco = preco;
        this.status = status;
    }

    public ItemPedido(int idPedido, int idProduto, int quantidade, BigDecimal preco) {
        this.idPedido = idPedido;
        this.idProduto = idProduto;
        this.quantidade = quantidade;
        this.preco = preco;
        status = StatusItemPedido.PENDENTE;
    }
}
