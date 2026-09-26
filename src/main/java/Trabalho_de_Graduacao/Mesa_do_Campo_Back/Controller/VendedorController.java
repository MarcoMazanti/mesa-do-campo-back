package Trabalho_de_Graduacao.Mesa_do_Campo_Back.Controller;

import Trabalho_de_Graduacao.Mesa_do_Campo_Back.Entities.DTO.CadastroVendedorDTO;
import Trabalho_de_Graduacao.Mesa_do_Campo_Back.Entities.DTO.VendedorDTO;
import Trabalho_de_Graduacao.Mesa_do_Campo_Back.Entities.Vendedor;
import Trabalho_de_Graduacao.Mesa_do_Campo_Back.Service.VendedorService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

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

    @PostMapping("/create")
    public ResponseEntity<CadastroVendedorDTO> createVendedor(@RequestBody Vendedor vendedor,
                                                               @RequestParam("idUsuarioAuth") int idUsuarioAuth) {
        return ResponseEntity.ok(vendedorService.createVendedor(vendedor, idUsuarioAuth));
    }

    @PutMapping("/update")
    public ResponseEntity<VendedorDTO> updateVendedor(@RequestBody VendedorDTO vendedor,
                                                       @RequestParam("idUsuarioAuth") int idUsuarioAuth) {
        return ResponseEntity.ok(vendedorService.updateVendedor(vendedor, idUsuarioAuth));
    }

    @DeleteMapping("/{idAlvo}")
    public ResponseEntity<Map<String, String>> deleteVendedor(@PathVariable("idAlvo") int idAlvo,
                                                               @RequestParam("idUsuarioAuth") int idUsuarioAuth) {
        vendedorService.deleteVendedor(idAlvo, idUsuarioAuth);
        return ResponseEntity.ok(Map.of("mensagem", "Perfil de vendedor desativado com sucesso."));
    }
}
