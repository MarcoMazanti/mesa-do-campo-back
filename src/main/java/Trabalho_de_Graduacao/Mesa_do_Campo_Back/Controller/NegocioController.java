package Trabalho_de_Graduacao.Mesa_do_Campo_Back.Controller;

import Trabalho_de_Graduacao.Mesa_do_Campo_Back.Entities.DTO.TopProdutoDTO;
import Trabalho_de_Graduacao.Mesa_do_Campo_Back.Entities.DTO.VendasMensaisDTO;
import Trabalho_de_Graduacao.Mesa_do_Campo_Back.Entities.Enum.CategoriaProduto;
import Trabalho_de_Graduacao.Mesa_do_Campo_Back.Entities.Enum.StatusPedido;
import Trabalho_de_Graduacao.Mesa_do_Campo_Back.Entities.ItemPedido;
import Trabalho_de_Graduacao.Mesa_do_Campo_Back.Entities.Produto;
import Trabalho_de_Graduacao.Mesa_do_Campo_Back.Service.NegocioService;
import Trabalho_de_Graduacao.Mesa_do_Campo_Back.Service.Security.HybridEncrypted;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Tag(name = "Negócio")
@HybridEncrypted
@RestController
@RequestMapping("/api/negocio")
public class NegocioController {
    @Autowired
    private NegocioService negocioService;

    @GetMapping("/top_produtos/{quant}")
    public ResponseEntity<List<TopProdutoDTO>> getTopProdutos(@PathVariable("quant") int quant, @RequestAttribute("idUsuarioAuth") int idUsuarioAuth) {
        List<TopProdutoDTO> topProdutos = negocioService.getTopProdutos(idUsuarioAuth, quant);
        return ResponseEntity.ok(topProdutos);
    }

    @GetMapping("/vendas_mensais")
    public ResponseEntity<List<VendasMensaisDTO>> getVendasMes(@RequestParam("dataInicio") String dataInicio, @RequestParam("dataFim") String dataFim, @RequestAttribute("idUsuarioAuth") int idUsuarioAuth) {
        List<VendasMensaisDTO> vendasMes = negocioService.getVendasMensais(dataInicio, dataFim, idUsuarioAuth);
        return ResponseEntity.ok(vendasMes);
    }

    @GetMapping("/itens_vendidos")
    public ResponseEntity<List<ItemPedido>> getItensVendidos(@RequestParam("id") int id, @RequestParam("status") StatusPedido status, @RequestParam("categoria") CategoriaProduto categoria, @RequestAttribute("idUsuarioAuth") int idUsuarioAuth) {
        List<ItemPedido> itensVendidos = negocioService.getItensVendidos(id, status, categoria, idUsuarioAuth);
        return ResponseEntity.ok(itensVendidos);
    }

    @GetMapping("/produto/{nome}")
    public ResponseEntity<List<Produto>> getProdutosPorNome(@PathVariable("nome") String nome, @RequestAttribute("idUsuarioAuth") int idUsuarioAuth) {
        List<Produto> produtos = negocioService.getProdutosPorNome(nome, idUsuarioAuth);
        return ResponseEntity.ok(produtos);
    }
}
