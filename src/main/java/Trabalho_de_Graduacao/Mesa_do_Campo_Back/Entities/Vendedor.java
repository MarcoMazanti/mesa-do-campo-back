package Trabalho_de_Graduacao.Mesa_do_Campo_Back.Entities;

import jakarta.persistence.Id;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class Vendedor {
    @Id
    @NotNull
    private int id; // PK e FK apontando para a tabela Usuario
    private float avaliacao;
    private LocalDate dataAdmissao;

    public Vendedor(int id, float avaliacao) {
        this.id = id;
        this.avaliacao = avaliacao;
        this.dataAdmissao = LocalDate.now();
    }

    public Vendedor(int id, LocalDate dataAdmissao) {
        this.id = id;
        this.dataAdmissao = dataAdmissao;
        avaliacao = 0;
    }
}
