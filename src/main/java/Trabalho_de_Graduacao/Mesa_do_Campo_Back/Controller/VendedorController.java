package Trabalho_de_Graduacao.Mesa_do_Campo_Back.Controller;

import Trabalho_de_Graduacao.Mesa_do_Campo_Back.Entities.DTO.VendedorDTO;
import Trabalho_de_Graduacao.Mesa_do_Campo_Back.Entities.Vendedor;
import Trabalho_de_Graduacao.Mesa_do_Campo_Back.Service.VendedorService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/vendedor")
public class VendedorController {
    @Autowired
    private VendedorService vendedorService;

    @GetMapping("/auto")
    public ResponseEntity<VendedorDTO> getVendedor(@RequestParam("idUsuarioAuth") int idUsuarioAuth) {
        return ResponseEntity.ok(vendedorService.getByIdVendedor(idUsuarioAuth));
    }

    @GetMapping("/all")
    public ResponseEntity<List<VendedorDTO>> getAllVendedores() {
        return ResponseEntity.ok(vendedorService.getAllVendedores());
    }

    @PostMapping()
    public ResponseEntity<VendedorDTO> createVendedor(@RequestBody Vendedor vendedor) {
        return ResponseEntity.ok(vendedorService.createVendedor(vendedor));
    }

    @PutMapping()
    public ResponseEntity<VendedorDTO> updateVendedor(@RequestBody VendedorDTO vendedor, @RequestParam("idUsuarioAuth") int idUsuarioAuth) {
        return ResponseEntity.ok(vendedorService.updateVendedor(vendedor, idUsuarioAuth));
    }

    @DeleteMapping("/{idAlvo}")
    public ResponseEntity<Void> deleteVendedor(@PathVariable("idAlvo") int idAlvo, @RequestParam("idUsuarioAuth") int idUsuarioAuth) {
        vendedorService.deleteVendedor(idAlvo, idUsuarioAuth);
        return ResponseEntity.ok().build();
    }
}
