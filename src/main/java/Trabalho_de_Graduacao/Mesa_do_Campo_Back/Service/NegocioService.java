package Trabalho_de_Graduacao.Mesa_do_Campo_Back.Service;

import Trabalho_de_Graduacao.Mesa_do_Campo_Back.Entities.DTO.TopProdutoDTO;
import Trabalho_de_Graduacao.Mesa_do_Campo_Back.Entities.DTO.VendasMensaisDTO;
import Trabalho_de_Graduacao.Mesa_do_Campo_Back.Entities.Enum.CategoriaProduto;
import Trabalho_de_Graduacao.Mesa_do_Campo_Back.Entities.Enum.StatusPedido;
import Trabalho_de_Graduacao.Mesa_do_Campo_Back.Entities.ItemPedido;
import Trabalho_de_Graduacao.Mesa_do_Campo_Back.Entities.Produto;
import Trabalho_de_Graduacao.Mesa_do_Campo_Back.Exception.RequisicaoIncompletaException;
import Trabalho_de_Graduacao.Mesa_do_Campo_Back.Repository.VendedorRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.time.format.ResolverStyle;
import java.util.List;

@Service
public class NegocioService {
    @Autowired
    private VendedorRepository vendedorRepository;


    public List<TopProdutoDTO> getTopProdutos(int quant, int idUsuarioAuth) {
        return vendedorRepository.getTopProdutos(quant, idUsuarioAuth);
    }

    public List<VendasMensaisDTO> getVendasMensais(String dataInicio, String dataFim, int idUsuarioAuth) {
        if (dataInicio == null || dataInicio.trim().isEmpty() || dataFim == null || dataFim.trim().isEmpty()) {
            throw new RequisicaoIncompletaException("A data não pode ser nula ou vazia.");
        }
        String dataInicioFormatada = validarEFormatarData(dataInicio);
        String dataFimFormatada = validarEFormatarData(dataFim);

        return vendedorRepository.getVendasMensais(dataInicioFormatada, dataFimFormatada, idUsuarioAuth);
    }

    public List<ItemPedido> getItensVendidos(int id, StatusPedido status, CategoriaProduto categoria, int idUsuarioAuth) {
        return vendedorRepository.getItensVendidos(id, status, categoria.toString(), idUsuarioAuth);
    }

    public List<Produto> getProdutosPorNome(String nome, int idUsuarioAuth) {
        return vendedorRepository.getProdutosPorNome(nome, idUsuarioAuth);
    }

    private String validarEFormatarData(String dataString) {
        List<String> formatosAceitos = List.of(
                "dd/MM/uuuu",
                "uuuu-MM-dd",
                "dd-MM-uuuu",
                "dd.MM.uuuu",
                "dd/MM/uu"
        );

        for (String padrao : formatosAceitos) {
            try {
                DateTimeFormatter formatadorEntrada = DateTimeFormatter.ofPattern(padrao)
                        .withResolverStyle(ResolverStyle.STRICT);

                LocalDate dataValida = LocalDate.parse(dataString.trim(), formatadorEntrada);

                return dataValida.format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));
            } catch (DateTimeParseException e) {
                throw new IllegalArgumentException("Data inválida ou em formato não reconhecido: " + dataString);
            }
        }

        throw new IllegalArgumentException("Data inválida ou em formato não reconhecido: " + dataString);
    }
}
