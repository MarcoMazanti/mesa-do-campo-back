package Trabalho_de_Graduacao.Mesa_do_Campo_Back.Records;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

public record EnderecoCEP(String cep, String state, String city, String neighborhood, String street, String service) {
    @JsonCreator
    public EnderecoCEP(@JsonProperty("cep") String cep,
                       @JsonProperty("state") String state,
                       @JsonProperty("city") String city,
                       @JsonProperty("neighborhood") String neighborhood,
                       @JsonProperty("street") String street,
                       @JsonProperty("service") String service) {
        this.cep = cep;
        this.state = state;
        this.city = city;
        this.neighborhood = neighborhood;
        this.street = street;
        this.service = service;
    }
}
