package Trabalho_de_Graduacao.Mesa_do_Campo_Back.Entities.DTO;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Corpo de POST /cliente/login. Atenção: o login é por e-mail, mas as chamadas seguintes usam Basic Auth com o NOME do cliente.")
public record LoginDTO(
        @Schema(description = "E-mail cadastrado.", example = "maria@email.com") String email,
        @Schema(description = "Senha em texto puro (comparada com o hash salvo no banco).", example = "SenhaForte#123") String senha) {
    @JsonCreator
    public LoginDTO(@JsonProperty("email") String email,
                    @JsonProperty("senha") String senha) {
        this.email = email;
        this.senha = senha;
    }
}
