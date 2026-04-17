package Trabalho_de_Graduacao.Mesa_do_Campo_Back.Entities;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ItemPedido {
    @EmbeddedId
    private ItemPedidoPK id; // Chave composta
    @NotNull
    private int quantidade;
    @NotNull
    private double precoUnitario;
    private StatusPedido status;

    public ItemPedido(ItemPedidoPK id, int quantidade, double precoUnitario) {
        this.id = id;
        this.quantidade = quantidade;
        this.precoUnitario = precoUnitario;
        status = StatusPedido.PENDENTE;
    }
}
