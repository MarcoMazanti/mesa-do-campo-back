package Trabalho_de_Graduacao.Mesa_do_Campo_Back.Entities.DTO;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

public record ClienteDTO(int id, String nome, String cpfOrCnpj, String email, String telefone, Integer idEnderecoEntrega) {
    @JsonCreator
    public ClienteDTO(@JsonProperty("id") int id,
                      @JsonProperty("nome") String nome,
                      @JsonProperty("cpfOrCnpj") String cpfOrCnpj,
                      @JsonProperty("email") String email,
                      @JsonProperty("telefone") String telefone,
                      @JsonProperty("idEnderecoEntrega") Integer idEnderecoEntrega) {
        this.id = id;
        this.nome = nome;
        this.cpfOrCnpj = cpfOrCnpj;
        this.email = email;
        this.telefone = telefone;
        this.idEnderecoEntrega = idEnderecoEntrega;
    }
}
