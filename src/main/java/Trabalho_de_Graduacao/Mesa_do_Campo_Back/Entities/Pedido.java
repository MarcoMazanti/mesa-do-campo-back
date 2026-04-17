package Trabalho_de_Graduacao.Mesa_do_Campo_Back.Entities;

import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class Pedido {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;
    @NotNull
    private int idUsuario;
    @NotNull
    private double precoTotal;
    private LocalDateTime dataCompra;

    public Pedido(int idUsuario, double precoTotal) {
        this.idUsuario = idUsuario;
        this.precoTotal = precoTotal;
        dataCompra = LocalDateTime.now();
    }
}
