package Trabalho_de_Graduacao.Mesa_do_Campo_Back.Controller;

import Trabalho_de_Graduacao.Mesa_do_Campo_Back.Entities.Endereco;
import Trabalho_de_Graduacao.Mesa_do_Campo_Back.Entities.External.ReturnModel;
import Trabalho_de_Graduacao.Mesa_do_Campo_Back.Service.EnderecoService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Tag(name = "Endereço")
@RestController
@RequestMapping("/api/endereco")
public class EnderecoController {
    @Autowired
    private EnderecoService enderecoService;

    @Operation(summary = "Busca um endereço por ID", description = "Só retorna o endereço se ele pertencer ao usuário autenticado.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Endereço encontrado.",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = ReturnModel.class),
                            examples = @ExampleObject(name = "Sucesso", value = """
                                    {
                                        "status": 200,
                                        "path": "/api/endereco/unique/3",
                                        "success": true,
                                        "quantity": 1,
                                        "itens": [
                                            { "id": 3, "idUsuario": 1, "cep": "19910000", "country": "Brasil", "state": "SP", "city": "Ourinhos", "adress": "Rua Alberto Zunta", "number": 493, "complement": "Apto 12" }
                                        ]
                                    }
                                    """))),
            @ApiResponse(responseCode = "401", description = "O endereço não pertence ao usuário autenticado.",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = ReturnModel.class),
                            examples = @ExampleObject(name = "Endereço de terceiro", value = """
                                    {
                                        "status": 401,
                                        "path": "/api/endereco/unique/9",
                                        "success": false,
                                        "quantity": 1,
                                        "errors": { "status": 401, "message": "O Usuário está tentando acessar um endereço que não pertence a ele.", "hour": "2026-09-20T14:30:00" }
                                    }
                                    """)))
    })
    @GetMapping("/unique/{id}")
    public ResponseEntity<Endereco> getEnderecoById(@PathVariable("id") int id, @RequestAttribute("idUsuarioAuth") int idUsuarioAuth) {
        return ResponseEntity.ok(enderecoService.getById(id, idUsuarioAuth));
    }

    @Operation(summary = "Lista os endereços do usuário autenticado", description = "Não recebe parâmetros — sempre retorna os endereços de quem está logado.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Lista retornada com sucesso (pode vir vazia).",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = ReturnModel.class),
                            examples = @ExampleObject(name = "Sucesso", value = """
                                    {
                                        "status": 200,
                                        "path": "/api/endereco/user",
                                        "success": true,
                                        "quantity": 1,
                                        "batch": { "quantity": 10, "loteAtual": 1, "loteTotal": 1 },
                                        "itens": [
                                            { "id": 3, "idUsuario": 1, "cep": "19910000", "country": "Brasil", "state": "SP", "city": "Ourinhos", "adress": "Rua Alberto Zunta", "number": 493, "complement": "Apto 12" }
                                        ]
                                    }
                                    """)))
    })
    @GetMapping("/user")
    public ResponseEntity<List<Endereco>> getEnderecoByIdUsuario(@RequestAttribute("idUsuarioAuth") int idUsuarioAuth) {
        return ResponseEntity.ok(enderecoService.getByUsuario(idUsuarioAuth));
    }

    @Operation(summary = "Cadastra um novo endereço",
            description = "O campo idUsuario do corpo é ignorado — o dono do endereço é sempre o usuário autenticado.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Endereço criado com sucesso.",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = ReturnModel.class),
                            examples = @ExampleObject(name = "Sucesso", value = """
                                    {
                                        "status": 200,
                                        "path": "/api/endereco/create",
                                        "success": true,
                                        "quantity": 1,
                                        "itens": [
                                            { "id": 4, "idUsuario": 1, "cep": "19910000", "country": "Brasil", "state": "SP", "city": "Ourinhos", "adress": "Rua Alberto Zunta", "number": 493, "complement": null }
                                        ]
                                    }
                                    """)))
    })
    @PostMapping("/create")
    public ResponseEntity<Endereco> createEndereco(@RequestBody Endereco endereco, @RequestAttribute("idUsuarioAuth") int idUsuarioAuth) {
        return ResponseEntity.ok(enderecoService.create(endereco, idUsuarioAuth));
    }

    @Operation(summary = "Atualiza um endereço existente",
            description = "Atualiza CEP, país, estado, cidade, rua, número e complemento de um endereço que pertença ao usuário autenticado.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Endereço atualizado com sucesso.",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = ReturnModel.class),
                            examples = @ExampleObject(name = "Sucesso", value = """
                                    {
                                        "status": 200,
                                        "path": "/api/endereco/update",
                                        "success": true,
                                        "quantity": 1,
                                        "itens": [
                                            { "id": 3, "idUsuario": 1, "cep": "19910000", "country": "Brasil", "state": "SP", "city": "Ourinhos", "adress": "Rua Alberto Zunta", "number": 500, "complement": "Casa" }
                                        ]
                                    }
                                    """))),
            @ApiResponse(responseCode = "401", description = "O endereço não existe ou não pertence ao usuário autenticado.",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = ReturnModel.class),
                            examples = @ExampleObject(name = "Endereço de terceiro", value = """
                                    {
                                        "status": 401,
                                        "path": "/api/endereco/update",
                                        "success": false,
                                        "quantity": 1,
                                        "errors": { "status": 401, "message": "O Usuário está tentando acessar um endereço que não pertence a ele.", "hour": "2026-09-20T14:30:00" }
                                    }
                                    """)))
    })
    @PutMapping("/update")
    public ResponseEntity<Endereco> updateEndereco(@RequestBody Endereco endereco, @RequestAttribute("idUsuarioAuth") int idUsuarioAuth) {
        return ResponseEntity.ok(enderecoService.update(endereco, idUsuarioAuth));
    }

    @Operation(summary = "Exclui um endereço",
            description = "Se o endereço excluído for o endereço de entrega atual do cliente, Cliente.idEnderecoEntrega é limpo (setado para null) automaticamente.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Endereço excluído com sucesso (sem corpo)."),
            @ApiResponse(responseCode = "401", description = "O endereço não existe ou não pertence ao usuário autenticado.",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = ReturnModel.class),
                            examples = @ExampleObject(name = "Endereço de terceiro", value = """
                                    {
                                        "status": 401,
                                        "path": "/api/endereco/9",
                                        "success": false,
                                        "quantity": 1,
                                        "errors": { "status": 401, "message": "O Usuário está tentando acessar um endereço que não pertence a ele.", "hour": "2026-09-20T14:30:00" }
                                    }
                                    """)))
    })
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteEndereco(@PathVariable("id") int id, @RequestAttribute("idUsuarioAuth") int idUsuarioAuth) {
        enderecoService.delete(id, idUsuarioAuth);
        return ResponseEntity.ok().build();
    }
}
