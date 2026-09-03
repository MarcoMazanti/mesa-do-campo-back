package Trabalho_de_Graduacao.Mesa_do_Campo_Back.Entities.DTO;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

public record LoginDTO(String email, String senha) {
    @JsonCreator
    public LoginDTO(@JsonProperty("email") String email,
                    @JsonProperty("senha") String senha) {
        this.email = email;
        this.senha = senha;
    }
}
