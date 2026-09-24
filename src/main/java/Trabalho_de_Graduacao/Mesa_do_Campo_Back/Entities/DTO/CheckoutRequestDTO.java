package Trabalho_de_Graduacao.Mesa_do_Campo_Back.Entities.DTO;

import Trabalho_de_Graduacao.Mesa_do_Campo_Back.Entities.Enum.TipoPagamento;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

import Trabalho_de_Graduacao.Mesa_do_Campo_Back.Entities.Enum.TipoPagamento;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Schema(description = "Corpo de POST /pedidos/checkout — finaliza a compra de uma vez: cria o Pedido, um ItemPedido para cada item e o Pagamento, de forma atômica.")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class CheckoutRequestDTO {
    @Schema(description = "Itens do carrinho a comprar.")
    @NotEmpty
    private List<ItemCheckoutDTO> itens;
    @Schema(description = "Forma de pagamento escolhida.", example = "PIX")
    @NotNull
    private TipoPagamento metodoPagamento;
}
