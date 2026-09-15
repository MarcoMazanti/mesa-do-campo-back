package Trabalho_de_Graduacao.Mesa_do_Campo_Back.Controller;

import Trabalho_de_Graduacao.Mesa_do_Campo_Back.Entities.ChavePix;
import Trabalho_de_Graduacao.Mesa_do_Campo_Back.Service.ChavePixService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/chave-pix")
public class ChavePixController {
    @Autowired
    private ChavePixService chavePixService;

    @GetMapping("/unique/{id}")
    public ResponseEntity<ChavePix> getChavePixById(@PathVariable("id") int id, @RequestAttribute("idUsuarioAuth") int idUsuarioAuth) {
        return ResponseEntity.ok(chavePixService.getById(id, idUsuarioAuth));
    }

    @GetMapping("/auto")
    public ResponseEntity<List<ChavePix>> getChavePixByUsuario(@RequestAttribute("idUsuarioAuth") int idUsuarioAuth) {
        return ResponseEntity.ok(chavePixService.getAllChavePixByIdCliente(idUsuarioAuth));
    }

    @GetMapping("/active")
    public ResponseEntity<ChavePix> getChavePixActive(@RequestAttribute("idUsuarioAuth") int idUsuarioAuth) {
        return ResponseEntity.ok(chavePixService.getChavePixAtivo(idUsuarioAuth));
    }

    @PostMapping("/create")
    public ResponseEntity<ChavePix> createChavePix(@RequestBody ChavePix cartaoDebito, @RequestAttribute("idUsuarioAuth") int idUsuarioAuth) {
        return ResponseEntity.ok(chavePixService.createChavePix(cartaoDebito, idUsuarioAuth));
    }

    @PutMapping("/update")
    public ResponseEntity<ChavePix> updateChavePix(@RequestBody ChavePix cartaoDebito, @RequestAttribute("idUsuarioAuth") int idUsuarioAuth) {
        return ResponseEntity.ok(chavePixService.updateChavePix(cartaoDebito, idUsuarioAuth));
    }

    @PatchMapping("/active/{id}")
    public ResponseEntity<ChavePix> ativarChavePix(@PathVariable("id") int id, @RequestAttribute("idUsuarioAuth") int idUsuarioAuth) {
        return ResponseEntity.ok(chavePixService.ativarChavePix(id, idUsuarioAuth));
    }

    @PatchMapping("/deactivate/{id}")
    public ResponseEntity<Void> desativarChavePix(@PathVariable("id") int id, @RequestAttribute("idUsuarioAuth") int idUsuarioAuth) {
        chavePixService.desativarChavePix(id, idUsuarioAuth);
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletarChavePix(@PathVariable("id") int id, @RequestAttribute("idUsuarioAuth") int idUsuarioAuth) {
        chavePixService.deleteChavePix(id, idUsuarioAuth);
        return ResponseEntity.ok().build();
    }
}
