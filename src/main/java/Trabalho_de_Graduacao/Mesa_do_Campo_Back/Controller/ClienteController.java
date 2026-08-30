package Trabalho_de_Graduacao.Mesa_do_Campo_Back.Controller;

import Trabalho_de_Graduacao.Mesa_do_Campo_Back.Entities.Cliente;
import Trabalho_de_Graduacao.Mesa_do_Campo_Back.Entities.DTO.ClienteDTO;
import Trabalho_de_Graduacao.Mesa_do_Campo_Back.Entities.DTO.LoginDTO;
import Trabalho_de_Graduacao.Mesa_do_Campo_Back.Exception.RequisicaoIncompletaException;
import Trabalho_de_Graduacao.Mesa_do_Campo_Back.Service.ClienteService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/cliente")
public class ClienteController {
    @Autowired
    private ClienteService clienteService;

    @GetMapping("/auto")
    public ResponseEntity<ClienteDTO> getCliente(@RequestAttribute("idUsuarioAuth") int idUsuarioAuth) {
        return ResponseEntity.ok(clienteService.getById(idUsuarioAuth));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ClienteDTO> getClienteById(@PathVariable("id") int id) {
        return ResponseEntity.ok(clienteService.getById(id));
    }

    @GetMapping("/all")
    public ResponseEntity<List<ClienteDTO>> getAllClientes() {
        return ResponseEntity.ok(clienteService.getAllClientes());
    }

    @PostMapping("/create")
    public ResponseEntity<ClienteDTO> createCliente(@RequestBody Cliente cliente) {
        return ResponseEntity.ok(clienteService.createCliente(cliente));
    }

    @PostMapping("/login")
    public ResponseEntity<Void> login(@RequestBody LoginDTO loginDTO) {
        clienteService.login(loginDTO);
        return ResponseEntity.ok().build();
    }

    @PutMapping("/update")
    public ResponseEntity<ClienteDTO> updateCliente(@RequestBody ClienteDTO clienteDto) {
        return ResponseEntity.ok(clienteService.updateCliente(clienteDto));
    }

    @PatchMapping("/change/senha")
    public ResponseEntity<ClienteDTO> updateSenha(@RequestBody Map<String, String> body, @RequestAttribute("idUsuarioAuth") int idUsuarioAuth) {
        String senha = body.get("senha");
        if (senha.isBlank()) throw new RequisicaoIncompletaException("Não foi informada a senha.");

        return ResponseEntity.ok(clienteService.updateSenha(senha, idUsuarioAuth));
    }

    @PatchMapping("/change/endereco/{idEndereco}")
    public ResponseEntity<ClienteDTO> updateEndereco(@PathVariable("idEndereco") int idEndereco, @RequestAttribute("idUsuarioAuth") int idUsuarioAuth) {
        return ResponseEntity.ok(clienteService.updateEndereco(idEndereco, idUsuarioAuth));
    }

    @DeleteMapping("/{idAlvo}")
    public ResponseEntity<Void> deleteCliente(@PathVariable("idAlvo") int idAlvo, @RequestAttribute("idUsuarioAuth") int idUsuarioAuth) {
        clienteService.delete(idAlvo, idUsuarioAuth);
        return ResponseEntity.ok().build();
    }
}
