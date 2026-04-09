package Trabalho_de_Graduacao.Mesa_do_Campo_Back.Externals;

import Trabalho_de_Graduacao.Mesa_do_Campo_Back.Records.EnderecoCEP;
import Trabalho_de_Graduacao.Mesa_do_Campo_Back.Records.ErrorCEP;
import tools.jackson.databind.ObjectMapper;

import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

public class BrasilAPI {
    ObjectMapper mapper = new ObjectMapper();

    // GET brasilapi.com.br/api/cep/v1/{cep}
    public EnderecoCEP getCEP(String cep) {
        try {
            HttpClient client = HttpClient.newHttpClient();

            HttpRequest request = HttpRequest.newBuilder()
                    .uri(java.net.URI.create("https://brasilapi.com.br/api/cep/v1/" + cep))
                    .build();

            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

            if (response.statusCode() == 200) {
                return mapper.readValue(response.body(), EnderecoCEP.class);
            } else {
                ErrorCEP erro = mapper.readValue(response.body(), ErrorCEP.class);

                System.out.println("Erro ao buscar CEP: " + response.statusCode());
                System.out.println(erro.toString());
                return null;
            }
        } catch (Exception e) {
            return null;
        }
    }
}
