package Trabalho_de_Graduacao.Mesa_do_Campo_Back.Entities.DTO;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Resultado do cadastro de cliente. Informa se uma conta desativada foi reativada.")
public record CadastroClienteDTO(
        @Schema(description = "Dados públicos da conta criada ou reativada.") ClienteDTO cliente,
        @Schema(description = "Verdadeiro quando o e-mail já pertencia a uma conta desativada e ela foi reativada.", example = "false") boolean contaReativada) {
}
