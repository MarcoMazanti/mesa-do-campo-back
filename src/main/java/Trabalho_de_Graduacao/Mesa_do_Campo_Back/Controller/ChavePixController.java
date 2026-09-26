package Trabalho_de_Graduacao.Mesa_do_Campo_Back.Controller;

import Trabalho_de_Graduacao.Mesa_do_Campo_Back.Entities.ChavePix;
import Trabalho_de_Graduacao.Mesa_do_Campo_Back.Entities.DTO.External.ReturnModel;
import Trabalho_de_Graduacao.Mesa_do_Campo_Back.Service.ChavePixService;
import Trabalho_de_Graduacao.Mesa_do_Campo_Back.Service.Security.HybridEncrypted;
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

@Tag(name = "Chave PIX")
@SecurityRequirement(name = "basicAuth")
@RestController
@HybridEncrypted
@RequestMapping("/api/chave-pix")
public class ChavePixController {
    @Autowired
    private ChavePixService chavePixService;

    @Operation(summary = "Busca uma chave PIX por ID", description = "Só o dono da chave pode consultá-la.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Chave encontrada.",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = ReturnModel.class),
                            examples = @ExampleObject(name = "Sucesso", value = """
                                    {
                                        "status": 200,
                                        "path": "/api/chave-pix/unique/2",
                                        "success": true,
                                        "quantity": 1,
                                        "itens": [
                                            { "id": 2, "idCliente": 1, "nome": "Minha chave principal", "tipoChave": "EMAIL", "chave": "maria@email.com", "padrao": true }
                                        ]
                                    }
                                    """))),
            @ApiResponse(responseCode = "401", description = "A chave não pertence ao usuário autenticado.",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = ReturnModel.class),
                            examples = @ExampleObject(name = "Chave de terceiro", value = """
                                    {
                                        "status": 401,
                                        "path": "/api/chave-pix/unique/9",
                                        "success": false,
                                        "quantity": 1,
                                        "errors": { "status": 401, "message": "Apenas o cliente pode visualizar seus dados.", "hour": "2026-09-20T14:30:00" }
                                    }
                                    """))),
            @ApiResponse(responseCode = "404", description = "Não existe chave com o ID informado.",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = ReturnModel.class),
                            examples = @ExampleObject(name = "Não encontrada", value = """
                                    {
                                        "status": 404,
                                        "path": "/api/chave-pix/unique/999",
                                        "success": false,
                                        "quantity": 1,
                                        "errors": { "status": 404, "message": "Não foi eocontrado uma chave pix com o ID: 999", "hour": "2026-09-20T14:30:00" }
                                    }
                                    """)))
    })
    @GetMapping("/unique/{id}")
    public ResponseEntity<ChavePix> getChavePixById(@Parameter(description = "ID da chave.", example = "2") @PathVariable("id") int id, @RequestAttribute("idUsuarioAuth") int idUsuarioAuth) {
        return ResponseEntity.ok(chavePixService.getById(id, idUsuarioAuth));
    }

    @Operation(summary = "Lista as chaves PIX do usuário autenticado", description = "Retorna lista vazia se não houver nenhuma (não gera erro 404).")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Lista retornada com sucesso (pode vir vazia).",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = ReturnModel.class),
                            examples = @ExampleObject(name = "Sucesso", value = """
                                    {
                                        "status": 200,
                                        "path": "/api/chave-pix/auto",
                                        "success": true,
                                        "quantity": 1,
                                        "itens": [
                                            { "id": 2, "idCliente": 1, "nome": "Minha chave principal", "tipoChave": "EMAIL", "chave": "maria@email.com", "padrao": true }
                                        ]
                                    }
                                    """)))
    })
    @GetMapping("/auto")
    public ResponseEntity<List<ChavePix>> getChavePixByUsuario(@RequestAttribute("idUsuarioAuth") int idUsuarioAuth) {
        return ResponseEntity.ok(chavePixService.getAllChavePixByIdCliente(idUsuarioAuth));
    }

    @Operation(summary = "Busca a chave PIX padrão do usuário autenticado", description = "Chave marcada com padrao = true.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Chave padrão encontrada.",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = ReturnModel.class),
                            examples = @ExampleObject(name = "Sucesso", value = """
                                    {
                                        "status": 200,
                                        "path": "/api/chave-pix/active",
                                        "success": true,
                                        "quantity": 1,
                                        "itens": [
                                            { "id": 2, "idCliente": 1, "nome": "Minha chave principal", "tipoChave": "EMAIL", "chave": "maria@email.com", "padrao": true }
                                        ]
                                    }
                                    """))),
            @ApiResponse(responseCode = "404", description = "Não há chaves cadastradas, ou nenhuma delas está marcada como padrão.",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = ReturnModel.class),
                            examples = {
                                    @ExampleObject(name = "Nenhuma chave", value = """
                                            {
                                                "status": 404,
                                                "path": "/api/chave-pix/active",
                                                "success": false,
                                                "quantity": 1,
                                                "errors": { "status": 404, "message": "Não possui nenhuma chave pix cadastrada para este usuário.", "hour": "2026-09-20T14:30:00" }
                                            }
                                            """),
                                    @ExampleObject(name = "Nenhuma padrão definida", value = """
                                            {
                                                "status": 404,
                                                "path": "/api/chave-pix/active",
                                                "success": false,
                                                "quantity": 1,
                                                "errors": { "status": 404, "message": "Não possui nenhuma chave pix ativa para o cliente com o ID: 1", "hour": "2026-09-20T14:30:00" }
                                            }
                                            """)
                            }))
    })
    @GetMapping("/active")
    public ResponseEntity<ChavePix> getChavePixActive(@RequestAttribute("idUsuarioAuth") int idUsuarioAuth) {
        return ResponseEntity.ok(chavePixService.getChavePixAtivo(idUsuarioAuth));
    }

    @Operation(summary = "Cadastra uma nova chave PIX", description = "idCliente do corpo precisa ser o usuário autenticado. tipoChave: CPF, EMAIL, TELEFONE ou ALEATORIA.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Chave criada com sucesso.",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = ReturnModel.class),
                            examples = @ExampleObject(name = "Sucesso", value = """
                                    {
                                        "status": 200,
                                        "path": "/api/chave-pix/create",
                                        "success": true,
                                        "quantity": 1,
                                        "itens": [
                                            { "id": 3, "idCliente": 1, "nome": "Chave do celular", "tipoChave": "TELEFONE", "chave": "14999998888", "padrao": false }
                                        ]
                                    }
                                    """))),
            @ApiResponse(responseCode = "401", description = "idCliente do corpo diferente do usuário autenticado.",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = ReturnModel.class),
                            examples = @ExampleObject(name = "Chave para terceiro", value = """
                                    {
                                        "status": 401,
                                        "path": "/api/chave-pix/create",
                                        "success": false,
                                        "quantity": 1,
                                        "errors": { "status": 401, "message": "Não se pode criar uma chave para um terceiro.", "hour": "2026-09-20T14:30:00" }
                                    }
                                    """)))
    })
    @PostMapping("/create")
    public ResponseEntity<ChavePix> createChavePix(@RequestBody ChavePix chavePix, @RequestAttribute("idUsuarioAuth") int idUsuarioAuth) {
        return ResponseEntity.ok(chavePixService.createChavePix(chavePix, idUsuarioAuth));
    }

    @Operation(summary = "Atualiza uma chave PIX", description = "Atualiza nome, tipo e valor da chave. Só o dono da chave pode alterá-la.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Chave atualizada com sucesso.",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = ReturnModel.class),
                            examples = @ExampleObject(name = "Sucesso", value = """
                                    {
                                        "status": 200,
                                        "path": "/api/chave-pix/update",
                                        "success": true,
                                        "quantity": 1,
                                        "itens": [
                                            { "id": 2, "idCliente": 1, "nome": "Chave atualizada", "tipoChave": "EMAIL", "chave": "maria.nova@email.com", "padrao": true }
                                        ]
                                    }
                                    """))),
            @ApiResponse(responseCode = "401", description = "Chave de terceiro.",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = ReturnModel.class),
                            examples = @ExampleObject(name = "Chave de terceiro", value = """
                                    {
                                        "status": 401,
                                        "path": "/api/chave-pix/update",
                                        "success": false,
                                        "quantity": 1,
                                        "errors": { "status": 401, "message": "Não se pode criar uma chave para um terceiro.", "hour": "2026-09-20T14:30:00" }
                                    }
                                    """))),
            @ApiResponse(responseCode = "404", description = "Não existe chave com o ID informado.",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = ReturnModel.class),
                            examples = @ExampleObject(name = "Não encontrada", value = """
                                    {
                                        "status": 404,
                                        "path": "/api/chave-pix/update",
                                        "success": false,
                                        "quantity": 1,
                                        "errors": { "status": 404, "message": "Não foi encontrado a chave solicitada para atualizar.", "hour": "2026-09-20T14:30:00" }
                                    }
                                    """)))
    })
    @PutMapping("/update")
    public ResponseEntity<ChavePix> updateChavePix(@RequestBody ChavePix chavePix, @RequestAttribute("idUsuarioAuth") int idUsuarioAuth) {
        return ResponseEntity.ok(chavePixService.updateChavePix(chavePix, idUsuarioAuth));
    }

    @Operation(summary = "Define uma chave PIX como padrão", description = "Qualquer outra chave do cliente que estivesse marcada como padrão é desmarcada antes.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Chave definida como padrão.",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = ReturnModel.class),
                            examples = @ExampleObject(name = "Sucesso", value = """
                                    {
                                        "status": 200,
                                        "path": "/api/chave-pix/active/3",
                                        "success": true,
                                        "quantity": 1,
                                        "itens": [
                                            { "id": 3, "idCliente": 1, "nome": "Chave do celular", "tipoChave": "TELEFONE", "chave": "14999998888", "padrao": true }
                                        ]
                                    }
                                    """))),
            @ApiResponse(responseCode = "404", description = "Não existe chave com esse ID para o usuário autenticado.",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = ReturnModel.class),
                            examples = @ExampleObject(name = "Não encontrada", value = """
                                    {
                                        "status": 404,
                                        "path": "/api/chave-pix/active/999",
                                        "success": false,
                                        "quantity": 1,
                                        "errors": { "status": 404, "message": "Não foi encontrado uma chave pix com o ID: 999para o cliente com o ID: 1 ativar.", "hour": "2026-09-20T14:30:00" }
                                    }
                                    """)))
    })
    @PatchMapping("/active/{id}")
    public ResponseEntity<ChavePix> ativarChavePix(@Parameter(description = "ID da chave a marcar como padrão.", example = "3") @PathVariable("id") int id, @RequestAttribute("idUsuarioAuth") int idUsuarioAuth) {
        return ResponseEntity.ok(chavePixService.ativarChavePix(id, idUsuarioAuth));
    }

    @Operation(summary = "Remove a chave PIX como padrão", description = "Só desmarca padrao = false; a chave continua existindo.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Chave desmarcada como padrão (sem corpo)."),
            @ApiResponse(responseCode = "404", description = "Não existe chave com esse ID para o usuário autenticado.",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = ReturnModel.class),
                            examples = @ExampleObject(name = "Não encontrada", value = """
                                    {
                                        "status": 404,
                                        "path": "/api/chave-pix/deactivate/999",
                                        "success": false,
                                        "quantity": 1,
                                        "errors": { "status": 404, "message": "Não foi encontrado uma chave pix com o ID: 999para o cliente com o ID: 1 desativar.", "hour": "2026-09-20T14:30:00" }
                                    }
                                    """)))
    })
    @PatchMapping("/deactivate/{id}")
    public ResponseEntity<Void> desativarChavePix(@Parameter(description = "ID da chave a desmarcar.", example = "3") @PathVariable("id") int id, @RequestAttribute("idUsuarioAuth") int idUsuarioAuth) {
        chavePixService.desativarChavePix(id, idUsuarioAuth);
        return ResponseEntity.ok().build();
    }

    @Operation(summary = "Exclui uma chave PIX", description = "Só o dono da chave pode excluí-la.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Chave excluída com sucesso (sem corpo)."),
            @ApiResponse(responseCode = "401", description = "A chave não pertence ao usuário autenticado.",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = ReturnModel.class),
                            examples = @ExampleObject(name = "Chave de terceiro", value = """
                                    {
                                        "status": 401,
                                        "path": "/api/chave-pix/9",
                                        "success": false,
                                        "quantity": 1,
                                        "errors": { "status": 401, "message": "Não se pode deletar uma chave de terceiro", "hour": "2026-09-20T14:30:00" }
                                    }
                                    """))),
            @ApiResponse(responseCode = "404", description = "Não existe chave com o ID informado.",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = ReturnModel.class),
                            examples = @ExampleObject(name = "Não encontrada", value = """
                                    {
                                        "status": 404,
                                        "path": "/api/chave-pix/999",
                                        "success": false,
                                        "quantity": 1,
                                        "errors": { "status": 404, "message": "Não foi encontrado uma chave pix com o ID: 999para o cliente com o ID: 1 para deletar.", "hour": "2026-09-20T14:30:00" }
                                    }
                                    """)))
    })
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletarChavePix(@Parameter(description = "ID da chave a excluir.", example = "2") @PathVariable("id") int id, @RequestAttribute("idUsuarioAuth") int idUsuarioAuth) {
        chavePixService.deleteChavePix(id, idUsuarioAuth);
        return ResponseEntity.ok().build();
    }
}
