package Trabalho_de_Graduacao.Mesa_do_Campo_Back.Controller;

import Trabalho_de_Graduacao.Mesa_do_Campo_Back.Entities.Avaliacao;
import Trabalho_de_Graduacao.Mesa_do_Campo_Back.Entities.External.ReturnModel;
import Trabalho_de_Graduacao.Mesa_do_Campo_Back.Service.AvaliacaoService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Tag(name = "Avaliação")
@RestController
@RequestMapping("/api/avaliacao")
public class AvaliacaoController {
    @Autowired
    private AvaliacaoService avaliacaoService;

    @Operation(summary = "Busca uma avaliação por ID", description = "Endpoint público.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Avaliação encontrada.",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = ReturnModel.class),
                            examples = @ExampleObject(name = "Sucesso", value = """
                                    {
                                        "status": 200,
                                        "path": "/api/avaliacao/unique/7",
                                        "success": true,
                                        "quantity": 1,
                                        "itens": [
                                            { "id": 7, "idVendedor": 1, "idCliente": 2, "nota": 4.5, "descricao": "Produtos sempre frescos e entrega rápida!" }
                                        ]
                                    }
                                    """))),
            @ApiResponse(responseCode = "404", description = "Não existe avaliação com o ID informado.",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = ReturnModel.class),
                            examples = @ExampleObject(name = "Não encontrado", value = """
                                    {
                                        "status": 404,
                                        "path": "/api/avaliacao/unique/999",
                                        "success": false,
                                        "quantity": 1,
                                        "errors": { "status": 404, "message": "Não foi encontrado nenhuma avaliação com o ID: 999", "hour": "2026-09-20T14:30:00" }
                                    }
                                    """)))
    })
    @GetMapping("/unique/{id}")
    public ResponseEntity<Avaliacao> getAvaliacaoById(@PathVariable("id") int id) {
        return ResponseEntity.ok(avaliacaoService.getById(id));
    }

    @Operation(summary = "Lista as avaliações recebidas por um vendedor", description = "Endpoint público.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Lista retornada com sucesso.",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = ReturnModel.class),
                            examples = @ExampleObject(name = "Sucesso", value = """
                                    {
                                        "status": 200,
                                        "path": "/api/avaliacao/vendedor/1",
                                        "success": true,
                                        "quantity": 1,
                                        "itens": [
                                            { "id": 7, "idVendedor": 1, "idCliente": 2, "nota": 4.5, "descricao": "Produtos sempre frescos e entrega rápida!" }
                                        ]
                                    }
                                    """))),
            @ApiResponse(responseCode = "404", description = "Esse vendedor não tem nenhuma avaliação.",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = ReturnModel.class),
                            examples = @ExampleObject(name = "Nenhuma avaliação", value = """
                                    {
                                        "status": 404,
                                        "path": "/api/avaliacao/vendedor/5",
                                        "success": false,
                                        "quantity": 1,
                                        "errors": { "status": 404, "message": "Não foi encontrado nenhuma avaliação relacionadas ao vendedor de ID: 5", "hour": "2026-09-20T14:30:00" }
                                    }
                                    """)))
    })
    @GetMapping("/vendedor/{idVendedor}")
    public ResponseEntity<List<Avaliacao>> getAvaliacaoByIdVendedor(@PathVariable("idVendedor") int idVendedor) {
        return ResponseEntity.ok(avaliacaoService.getAllAvaliacoesByIdVendedor(idVendedor));
    }

    @Operation(summary = "Lista as avaliações feitas por um cliente", description = "Endpoint público.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Lista retornada com sucesso.",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = ReturnModel.class),
                            examples = @ExampleObject(name = "Sucesso", value = """
                                    {
                                        "status": 200,
                                        "path": "/api/avaliacao/cliente/2",
                                        "success": true,
                                        "quantity": 1,
                                        "itens": [
                                            { "id": 7, "idVendedor": 1, "idCliente": 2, "nota": 4.5, "descricao": "Produtos sempre frescos e entrega rápida!" }
                                        ]
                                    }
                                    """))),
            @ApiResponse(responseCode = "404", description = "Esse cliente não fez nenhuma avaliação.",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = ReturnModel.class),
                            examples = @ExampleObject(name = "Nenhuma avaliação", value = """
                                    {
                                        "status": 404,
                                        "path": "/api/avaliacao/cliente/3",
                                        "success": false,
                                        "quantity": 1,
                                        "errors": { "status": 404, "message": "Não foi encontrado nenhuma avaliação enviada pelo cliente de ID: 3", "hour": "2026-09-20T14:30:00" }
                                    }
                                    """)))
    })
    @GetMapping("/cliente/{idCliente}")
    public ResponseEntity<List<Avaliacao>> getAvaliacaoByCliente(@PathVariable("idCliente") int idCliente) {
        return ResponseEntity.ok(avaliacaoService.getAllAvaliacoesByIdCliente(idCliente));
    }

    @Operation(summary = "Avaliações recebidas pelo vendedor autenticado",
            description = "Atalho para getAllAvaliacoesByIdVendedor usando o próprio ID autenticado.",
            security = @SecurityRequirement(name = "basicAuth"))
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Lista retornada com sucesso.",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = ReturnModel.class),
                            examples = @ExampleObject(name = "Sucesso", value = """
                                    {
                                        "status": 200,
                                        "path": "/api/avaliacao/auto",
                                        "success": true,
                                        "quantity": 1,
                                        "itens": [
                                            { "id": 7, "idVendedor": 1, "idCliente": 2, "nota": 4.5, "descricao": "Produtos sempre frescos e entrega rápida!" }
                                        ]
                                    }
                                    """))),
            @ApiResponse(responseCode = "404", description = "O usuário autenticado ainda não recebeu nenhuma avaliação.",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = ReturnModel.class),
                            examples = @ExampleObject(name = "Nenhuma avaliação", value = """
                                    {
                                        "status": 404,
                                        "path": "/api/avaliacao/auto",
                                        "success": false,
                                        "quantity": 1,
                                        "errors": { "status": 404, "message": "Não foi encontrado nenhuma avaliação relacionadas ao vendedor de ID: 1", "hour": "2026-09-20T14:30:00" }
                                    }
                                    """)))
    })
    @GetMapping("/auto")
    public ResponseEntity<List<Avaliacao>> getAvaliacaoByUsuario(@RequestAttribute("idUsuarioAuth") int idUsuarioAuth) {
        return ResponseEntity.ok(avaliacaoService.getAllAvaliacoesByIdVendedor(idUsuarioAuth));
    }

    @Operation(summary = "Lista todas as avaliações", description = "Endpoint público.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Lista retornada com sucesso.",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = ReturnModel.class),
                            examples = @ExampleObject(name = "Sucesso", value = """
                                    {
                                        "status": 200,
                                        "path": "/api/avaliacao/all",
                                        "success": true,
                                        "quantity": 1,
                                        "itens": [
                                            { "id": 7, "idVendedor": 1, "idCliente": 2, "nota": 4.5, "descricao": "Produtos sempre frescos e entrega rápida!" }
                                        ]
                                    }
                                    """))),
            @ApiResponse(responseCode = "404", description = "Não há nenhuma avaliação registrada no sistema.",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = ReturnModel.class),
                            examples = @ExampleObject(name = "Nenhuma avaliação", value = """
                                    {
                                        "status": 404,
                                        "path": "/api/avaliacao/all",
                                        "success": false,
                                        "quantity": 1,
                                        "errors": { "status": 404, "message": "Não foi encontrado nenhuma avaliação registrada.", "hour": "2026-09-20T14:30:00" }
                                    }
                                    """)))
    })
    @GetMapping("all")
    public ResponseEntity<List<Avaliacao>> getAllAvaliacoes() {
        return ResponseEntity.ok(avaliacaoService.getAllAvaliacoes());
    }

    @Operation(summary = "Cria uma avaliação sobre um vendedor",
            description = "idCliente do corpo precisa ser o usuário autenticado. Não é permitido se autoavaliar (idVendedor == idCliente).",
            security = @SecurityRequirement(name = "basicAuth"))
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Avaliação criada com sucesso.",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = ReturnModel.class),
                            examples = @ExampleObject(name = "Sucesso", value = """
                                    {
                                        "status": 200,
                                        "path": "/api/avaliacao/create",
                                        "success": true,
                                        "quantity": 1,
                                        "itens": [
                                            { "id": 8, "idVendedor": 1, "idCliente": 2, "nota": 5.0, "descricao": "Recomendo!" }
                                        ]
                                    }
                                    """))),
            @ApiResponse(responseCode = "401", description = "idCliente diferente do autenticado, ou tentativa de autoavaliação.",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = ReturnModel.class),
                            examples = {
                                    @ExampleObject(name = "ID de terceiro", value = """
                                            {
                                                "status": 401,
                                                "path": "/api/avaliacao/create",
                                                "success": false,
                                                "quantity": 1,
                                                "errors": { "status": 401, "message": "Não é permitido utilizar um ID distinto do seu.", "hour": "2026-09-20T14:30:00" }
                                            }
                                            """),
                                    @ExampleObject(name = "Autoavaliação", value = """
                                            {
                                                "status": 401,
                                                "path": "/api/avaliacao/create",
                                                "success": false,
                                                "quantity": 1,
                                                "errors": { "status": 401, "message": "Não é permitido se auto avaliar.", "hour": "2026-09-20T14:30:00" }
                                            }
                                            """)
                            }))
    })
    @PostMapping("/create")
    public ResponseEntity<Avaliacao> createAvaliacao(@RequestBody Avaliacao avaliacao, @RequestAttribute("idUsuarioAuth") int idUsuarioAuth) {
        return ResponseEntity.ok(avaliacaoService.createAvaliacao(avaliacao, idUsuarioAuth));
    }

    @Operation(summary = "Atualiza nota/comentário de uma avaliação",
            description = "Só o próprio autor da avaliação pode editá-la.",
            security = @SecurityRequirement(name = "basicAuth"))
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Avaliação atualizada com sucesso.",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = ReturnModel.class),
                            examples = @ExampleObject(name = "Sucesso", value = """
                                    {
                                        "status": 200,
                                        "path": "/api/avaliacao/update",
                                        "success": true,
                                        "quantity": 1,
                                        "itens": [
                                            { "id": 7, "idVendedor": 1, "idCliente": 2, "nota": 5.0, "descricao": "Melhorou ainda mais!" }
                                        ]
                                    }
                                    """))),
            @ApiResponse(responseCode = "401", description = "idCliente diferente do autenticado, ou tentativa de autoavaliação.",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = ReturnModel.class),
                            examples = @ExampleObject(name = "Avaliação de terceiro", value = """
                                    {
                                        "status": 401,
                                        "path": "/api/avaliacao/update",
                                        "success": false,
                                        "quantity": 1,
                                        "errors": { "status": 401, "message": "Não é permitido utilizar um ID distinto do seu.", "hour": "2026-09-20T14:30:00" }
                                    }
                                    """))),
            @ApiResponse(responseCode = "404", description = "Não existe avaliação com o ID informado.",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = ReturnModel.class),
                            examples = @ExampleObject(name = "Não encontrada", value = """
                                    {
                                        "status": 404,
                                        "path": "/api/avaliacao/update",
                                        "success": false,
                                        "quantity": 1,
                                        "errors": { "status": 404, "message": "Não foi encontrado a avaliação com o ID: 999 para atualizar.", "hour": "2026-09-20T14:30:00" }
                                    }
                                    """)))
    })
    @PutMapping("/update")
    public ResponseEntity<Avaliacao> updateAvaliacao(@RequestBody Avaliacao avaliacao, @RequestAttribute("idUsuarioAuth") int idUsuarioAuth) {
        return ResponseEntity.ok(avaliacaoService.updateAvaliacao(avaliacao, idUsuarioAuth));
    }

    @Operation(summary = "Exclui uma avaliação", description = "Só o próprio autor da avaliação pode excluí-la.",
            security = @SecurityRequirement(name = "basicAuth"))
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Avaliação excluída com sucesso (sem corpo)."),
            @ApiResponse(responseCode = "401", description = "Tentativa de excluir a avaliação de outro cliente.",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = ReturnModel.class),
                            examples = @ExampleObject(name = "Avaliação de terceiro", value = """
                                    {
                                        "status": 401,
                                        "path": "/api/avaliacao/7",
                                        "success": false,
                                        "quantity": 1,
                                        "errors": { "status": 401, "message": "Não é permitido deletar uma avaliação diferente do seu.", "hour": "2026-09-20T14:30:00" }
                                    }
                                    """))),
            @ApiResponse(responseCode = "404", description = "Não existe avaliação com o ID informado.",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = ReturnModel.class),
                            examples = @ExampleObject(name = "Não encontrada", value = """
                                    {
                                        "status": 404,
                                        "path": "/api/avaliacao/999",
                                        "success": false,
                                        "quantity": 1,
                                        "errors": { "status": 404, "message": "Não foi encontrado nenhuma avaliação com o ID: 999 para excluir.", "hour": "2026-09-20T14:30:00" }
                                    }
                                    """)))
    })
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteAvaliacao(@PathVariable("id") int id, @RequestAttribute("idUsuarioAuth") int idUsuarioAuth) {
        avaliacaoService.deleteAvaliacao(id, idUsuarioAuth);
        return ResponseEntity.ok().build();
    }
}
