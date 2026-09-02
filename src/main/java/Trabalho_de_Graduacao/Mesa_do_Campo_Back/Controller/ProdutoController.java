package Trabalho_de_Graduacao.Mesa_do_Campo_Back.Controller;

import Trabalho_de_Graduacao.Mesa_do_Campo_Back.Entities.Enum.CategoriaProduto;
import Trabalho_de_Graduacao.Mesa_do_Campo_Back.Entities.Produto;
import Trabalho_de_Graduacao.Mesa_do_Campo_Back.Service.ProdutoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/produto")
public class ProdutoController {
    @Autowired
    private ProdutoService produtoService;

    @GetMapping("/unique/{id}")
    public ResponseEntity<Produto> getProdutoById(@PathVariable("id") int id) {
        return ResponseEntity.ok(produtoService.getById(id));
    }

    @GetMapping("/vendedor/{idVendedor}")
    public ResponseEntity<List<Produto>> getProdutoByVendedor(@PathVariable("idVendedor") int idVendedor) {
        return ResponseEntity.ok(produtoService.getAllProdutosByIdVendedor(idVendedor));
    }

    @GetMapping("/all")
    public ResponseEntity<List<Produto>> getAllProduto() {
        return ResponseEntity.ok(produtoService.getAllProdutos());
    }

    @GetMapping("/categoria/{categoria}")
    public ResponseEntity<List<Produto>> getProdutoByCategoria(@PathVariable("categoria") String categoria) {
        return ResponseEntity.ok(produtoService.getAllProdutosByCategoria(CategoriaProduto.valueOf(categoria.toUpperCase())));
    }

    @PostMapping("/create")
    public ResponseEntity<Produto> createProduto(@RequestBody Produto produto, @RequestAttribute("idUsuarioAuth") int idUsuarioAuth) {
        return ResponseEntity.ok(produtoService.createProduto(produto, idUsuarioAuth));
    }

    @PutMapping("/update")
    public ResponseEntity<Produto> updateProduto(@RequestBody Produto produto, @RequestAttribute("idUsuarioAuth") int idUsuarioAuth) {
        return ResponseEntity.ok(produtoService.updateProduto(produto, idUsuarioAuth));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteProduto(@PathVariable("id") int id, @RequestAttribute("idUsuarioAuth") int idUsuarioAuth) {
        produtoService.deleteProduto(id, idUsuarioAuth);
        return ResponseEntity.ok().build();
    }

}
