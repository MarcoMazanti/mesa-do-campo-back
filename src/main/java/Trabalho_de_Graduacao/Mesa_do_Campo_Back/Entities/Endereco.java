package Trabalho_de_Graduacao.Mesa_do_Campo_Back.Entities;

import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class Endereco {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;
    @NotNull
    private int idUsuario;
    @NotNull
    private String cep;
    @NotNull
    private String country;
    @NotNull
    private String state;
    @NotNull
    private String city;
    @NotNull
    private String adress;
    @NotNull
    private int number;
    private String complement;

    /*
     * Estado, cidade, rua, numero virão do front-end quando o usuário inserir o CEP, pois efetuará uma requisição GET
     * para o endpoint público abaixo:
     * brasilapi.com.br/api/cep/v1/{cep}
     */

    public Endereco(int id, int idUsuario, String cep, String country, String state, String city, String adress, int number) {
        this.id = id;
        this.idUsuario = idUsuario;
        this.cep = cep;
        this.country = country;
        this.state = state;
        this.city = city;
        this.adress = adress;
        this.number = number;
    }
}
