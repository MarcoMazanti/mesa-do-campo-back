package Trabalho_de_Graduacao.Mesa_do_Campo_Back.Entities;

import jakarta.persistence.Embeddable;
import lombok.Data;

// Construção da Chave Composta da tabela ItemPedido
@Data
@Embeddable
public class ItemPedidoPK {
    private int idPedido;
    private int idProduto;
}
