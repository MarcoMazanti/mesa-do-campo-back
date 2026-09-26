package Trabalho_de_Graduacao.Mesa_do_Campo_Back.Entities.DTO;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Schema(description = "Um item do carrinho enviado no checkout: qual produto e em qual quantidade.")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ItemCheckoutDTO {
    @Schema(description = "ID do produto a comprar.", example = "10")
    @NotNull
    private Integer idProduto;
    @Schema(description = "Quantidade desejada. É validada contra o estoque disponível no momento do checkout.", example = "2")
    @NotNull
    @Positive
    private Integer quantidade;
}
