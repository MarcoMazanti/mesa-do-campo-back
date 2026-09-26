package Trabalho_de_Graduacao.Mesa_do_Campo_Back.Entities;

import Trabalho_de_Graduacao.Mesa_do_Campo_Back.Entities.Enum.StatusItemPedido;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Schema(description = "Um item (produto + quantidade) dentro de um Pedido. O status é controlado pelo vendedor do produto, não pelo comprador.")
@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity(name = "item_pedido")
public class ItemPedido {
    @Schema(description = "Identificador único do item, gerado pelo banco.", example = "12", accessMode = Schema.AccessMode.READ_ONLY)
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private int id;

    @Schema(description = "ID do pedido ao qual este item pertence.", example = "5")
    @NotNull
    @Column(name = "id_pedido", nullable = false)
    private int idPedido;

    @Schema(description = "ID do produto comprado.", example = "10")
    @NotNull
    @Column(name = "id_produto", nullable = false)
    private int idProduto;

    @Schema(description = "Quantidade comprada deste produto.", example = "2")
    @NotNull
    @Column(name = "quantidade", nullable = false)
    private int quantidade;

    @Schema(description = "Preço unitário travado no momento da compra (não muda mesmo que o preço do produto mude depois).", example = "8.90")
    @NotNull
    @Column(name = "preco_unit", precision = 10, scale = 2, nullable = false)
    private BigDecimal precoUnit;

    @Schema(description = "Status de preparo/envio deste item específico.", example = "PENDENTE")
    @NotNull
    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    private StatusItemPedido status;

    @Schema(description = "Cópia de Pedido.dataCompra, gravada na criação do item. Existe aqui porque o vendedor não tem permissão de consultar o Pedido do comprador diretamente — sem isso não haveria como montar o relatório de vendas por mês em \"Meu Negócio\".",
            example = "2026-09-20T14:30:00")
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
