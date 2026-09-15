package Trabalho_de_Graduacao.Mesa_do_Campo_Back.Entities.DTO;

import Trabalho_de_Graduacao.Mesa_do_Campo_Back.Entities.Enum.TipoPagamento;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CheckoutRequestDTO {
    @NotEmpty
    private List<ItemCheckoutDTO> itens;
    @NotNull
    private TipoPagamento metodoPagamento;
}
