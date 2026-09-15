package Trabalho_de_Graduacao.Mesa_do_Campo_Back.Controller;

import Trabalho_de_Graduacao.Mesa_do_Campo_Back.Entities.Avaliacao;
import Trabalho_de_Graduacao.Mesa_do_Campo_Back.Service.AvaliacaoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/avaliacao")
public class AvaliacaoController {
    @Autowired
    private AvaliacaoService avaliacaoService;

    @GetMapping("/unique/{id}")
    public ResponseEntity<Avaliacao> getAvaliacaoById(@PathVariable("id") int id) {
        return ResponseEntity.ok(avaliacaoService.getById(id));
    }

    @GetMapping("/vendedor/{idVendedor}")
    public ResponseEntity<List<Avaliacao>> getAvaliacaoByIdVendedor(@PathVariable("idVendedor") int idVendedor) {
        return ResponseEntity.ok(avaliacaoService.getAllAvaliacoesByIdVendedor(idVendedor));
    }

    @GetMapping("/cliente/{idCliente}")
    public ResponseEntity<List<Avaliacao>> getAvaliacaoByCliente(@PathVariable("idCliente") int idCliente) {
        return ResponseEntity.ok(avaliacaoService.getAllAvaliacoesByIdCliente(idCliente));
    }

    @GetMapping("/auto")
    public ResponseEntity<List<Avaliacao>> getAvaliacaoByUsuario(@RequestAttribute("idUsuarioAuth") int idUsuarioAuth) {
        return ResponseEntity.ok(avaliacaoService.getAllAvaliacoesByIdVendedor(idUsuarioAuth));
    }

    @GetMapping("all")
    public ResponseEntity<List<Avaliacao>> getAllAvaliacoes() {
        return ResponseEntity.ok(avaliacaoService.getAllAvaliacoes());
    }

    @PostMapping("/create")
    public ResponseEntity<Avaliacao> createAvaliacao(@RequestBody Avaliacao avaliacao, @RequestAttribute("idUsuarioAuth") int idUsuarioAuth) {
        return ResponseEntity.ok(avaliacaoService.createAvaliacao(avaliacao, idUsuarioAuth));
    }

    @PutMapping("/update")
    public ResponseEntity<Avaliacao> updateAvaliacao(@RequestBody Avaliacao avaliacao, @RequestAttribute("idUsuarioAuth") int idUsuarioAuth) {
        return ResponseEntity.ok(avaliacaoService.updateAvaliacao(avaliacao, idUsuarioAuth));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteAvaliacao(@PathVariable("id") int id, @RequestAttribute("idUsuarioAuth") int idUsuarioAuth) {
        avaliacaoService.deleteAvaliacao(id, idUsuarioAuth);
        return ResponseEntity.ok().build();
    }
}
