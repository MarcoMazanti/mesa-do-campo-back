package Trabalho_de_Graduacao.Mesa_do_Campo_Back.Entities.DTO;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

public record LoginDTO(String nome, String senha) {
    @JsonCreator
    public LoginDTO(@JsonProperty("nome") String nome,
                    @JsonProperty("senha") String senha) {
        this.nome = nome;
        this.senha = senha;
    }
}
