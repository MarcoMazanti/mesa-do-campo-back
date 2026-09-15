package Trabalho_de_Graduacao.Mesa_do_Campo_Back.Entities.DTO;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ItemCheckoutDTO {
    @NotNull
    private Integer idProduto;
    @NotNull
    @Positive
    private Integer quantidade;
}
