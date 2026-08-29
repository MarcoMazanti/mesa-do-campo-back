package Trabalho_de_Graduacao.Mesa_do_Campo_Back.Controller;

import Trabalho_de_Graduacao.Mesa_do_Campo_Back.Entities.Endereco;
import Trabalho_de_Graduacao.Mesa_do_Campo_Back.Service.EnderecoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/endereco")
public class EnderecoController {
    @Autowired
    private EnderecoService enderecoService;

    // Esse idUsuario vai ser incerido internamente quando for analisado a authenticacao do requerinte

    @GetMapping("/unique/{id}")
    public ResponseEntity<Endereco> getEnderecoById(@PathVariable("id") int id, @RequestParam("idUsuarioAuth") int idUsuarioAuth) {
        return ResponseEntity.ok(enderecoService.getById(id, idUsuarioAuth));
    }

    @GetMapping("/user/{idUsuario}")
    public ResponseEntity<List<Endereco>> getEnderecoByIdUsuario(@PathVariable("idUsuario") int idUsuario, @RequestParam("idUsuarioAuth") int idUsuarioAuth) {
        return ResponseEntity.ok(enderecoService.getByUsuario(idUsuario));
    }

    @PostMapping()
    public ResponseEntity<Endereco> createEndereco(@RequestBody Endereco endereco) {
        return ResponseEntity.ok(enderecoService.create(endereco));
    }

    @PutMapping()
    public ResponseEntity<Endereco> updateEndereco(@RequestBody Endereco endereco, @RequestParam("idUsuarioAuth") int idUsuarioAuth) {
        return ResponseEntity.ok(enderecoService.update(endereco, idUsuarioAuth));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteEndereco(@PathVariable("id") int id, @RequestParam("idUsuarioAuth") int idUsuarioAuth) {
        enderecoService.delete(id, idUsuarioAuth);
        return ResponseEntity.ok().build();
    }
}
