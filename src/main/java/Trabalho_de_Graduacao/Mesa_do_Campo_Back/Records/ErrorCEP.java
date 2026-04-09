package Trabalho_de_Graduacao.Mesa_do_Campo_Back.Records;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.Arrays;

public record ErrorCEP(String name, String message, String type, Object[] errors) {
    @JsonCreator
    public ErrorCEP(@JsonProperty("name") String name,
                    @JsonProperty("message") String message,
                    @JsonProperty("type") String type,
                    @JsonProperty("errors") Object[] errors) {
        this.name = name;
        this.message = message;
        this.type = type;
        this.errors = errors;
    }

    @Override
    public String toString() {
        return "ErrorCEP{" +
                "name='" + name + '\'' +
                ", message='" + message + '\'' +
                ", type='" + type + '\'' +
                ", errors=" + Arrays.toString(errors) +
                '}';
    }
}
