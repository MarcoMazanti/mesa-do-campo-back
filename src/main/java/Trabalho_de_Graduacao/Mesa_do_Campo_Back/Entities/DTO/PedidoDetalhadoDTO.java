package Trabalho_de_Graduacao.Mesa_do_Campo_Back.Entities.DTO;

import Trabalho_de_Graduacao.Mesa_do_Campo_Back.Entities.ItemPedido;
import Trabalho_de_Graduacao.Mesa_do_Campo_Back.Entities.Pagamento;
import Trabalho_de_Graduacao.Mesa_do_Campo_Back.Entities.Pedido;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Schema(description = "Visão completa de um pedido: o \"cabeçalho\" (Pedido), seus itens (ItemPedido) e o pagamento associado. Retornado por GET /pedidos/detalhe/{id} e POST /pedidos/checkout.")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class PedidoDetalhadoDTO {
    @Schema(description = "Dados gerais do pedido.")
    private Pedido pedido;
    @Schema(description = "Itens que compõem o pedido.")
    private List<ItemPedido> itens;
    @Schema(description = "Pagamento do pedido. Pode ser nulo se o pedido ainda não tiver um pagamento registrado (ex: criado via POST /pedidos/create, sem passar pelo checkout).", nullable = true)
    private Pagamento pagamento;
}