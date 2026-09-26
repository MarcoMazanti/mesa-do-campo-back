package Trabalho_de_Graduacao.Mesa_do_Campo_Back.Controller;

import Trabalho_de_Graduacao.Mesa_do_Campo_Back.Entities.DTO.VendedorDTO;
import Trabalho_de_Graduacao.Mesa_do_Campo_Back.Entities.External.ReturnModel;
import Trabalho_de_Graduacao.Mesa_do_Campo_Back.Entities.Vendedor;
import Trabalho_de_Graduacao.Mesa_do_Campo_Back.Service.VendedorService;
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

@Tag(name = "Vendedor")
@RestController
@RequestMapping("/api/vendedor")
public class VendedorController {
    @Autowired
    private VendedorService vendedorService;

    @Operation(summary = "Dados de vendedor do usuário autenticado",
            description = "Retorna 404 se o cliente autenticado ainda não tiver uma conta de vendedor.",
            security = @SecurityRequirement(name = "basicAuth"))
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Vendedor encontrado.",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = ReturnModel.class),
                            examples = @ExampleObject(name = "Sucesso", value = """
                                    {
                                        "status": 200,
                                        "path": "/api/vendedor/auto",
                                        "success": true,
                                        "quantity": 1,
                                        "itens": [
                                            { "idVendedor": 1, "nome": "Maria da Silva", "email": "maria@email.com", "telefone": "14999998888", "avaliacao": 4.5, "dataAdmissao": "2026-01-15" }
                                        ]
                                    }
                                    """))),
            @ApiResponse(responseCode = "404", description = "O cliente autenticado ainda não é vendedor (ou não existe mais o cliente associado).",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = ReturnModel.class),
                            examples = @ExampleObject(name = "Ainda não é vendedor", value = """
                                    {
                                        "status": 404,
                                        "path": "/api/vendedor/auto",
                                        "success": false,
                                        "quantity": 1,
                                        "errors": { "status": 404, "message": "Não foi encontrado nenhum vendedor com o ID: 1", "hour": "2026-09-20T14:30:00" }
                                    }
                                    """)))
    })
    @GetMapping("/auto")
    public ResponseEntity<VendedorDTO> getVendedor(@RequestAttribute("idUsuarioAuth") int idUsuarioAuth) {
        return ResponseEntity.ok(vendedorService.getByIdVendedor(idUsuarioAuth));
    }

    @Operation(summary = "Lista todos os vendedores", description = "Endpoint público, usado para exibir o nome do vendedor de um produto no catálogo.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Lista retornada com sucesso.",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = ReturnModel.class),
                            examples = @ExampleObject(name = "Sucesso", value = """
                                    {
                                        "status": 200,
                                        "path": "/api/vendedor/all",
                                        "success": true,
                                        "quantity": 1,
                                        "batch": { "quantity": 10, "loteAtual": 1, "loteTotal": 1 },
                                        "itens": [
                                            { "idVendedor": 1, "nome": "Maria da Silva", "email": "maria@email.com", "telefone": "14999998888", "avaliacao": 4.5, "dataAdmissao": "2026-01-15" }
                                        ]
                                    }
                                    """)))
    })
    @GetMapping("/all")
    public ResponseEntity<List<VendedorDTO>> getAllVendedores() {
        return ResponseEntity.ok(vendedorService.getAllVendedores());
    }

    @Operation(summary = "Cria uma conta de vendedor",
            description = "idVendedor precisa ser o id de um Cliente já cadastrado, e esse cliente ainda não pode ter uma conta de vendedor.",
            security = @SecurityRequirement(name = "basicAuth"))
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Conta de vendedor criada com sucesso.",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = ReturnModel.class),
                            examples = @ExampleObject(name = "Sucesso", value = """
                                    {
                                        "status": 200,
                                        "path": "/api/vendedor/create",
                                        "success": true,
                                        "quantity": 1,
                                        "itens": [
                                            { "idVendedor": 1, "nome": "Maria da Silva", "email": "maria@email.com", "telefone": "14999998888", "avaliacao": 0.0, "dataAdmissao": "2026-09-20" }
                                        ]
                                    }
                                    """))),
            @ApiResponse(responseCode = "401", description = "Já existe uma conta de vendedor com esse ID.",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = ReturnModel.class),
                            examples = @ExampleObject(name = "Já é vendedor", value = """
                                    {
                                        "status": 401,
                                        "path": "/api/vendedor/create",
                                        "success": false,
                                        "quantity": 1,
                                        "errors": { "status": 401, "message": "Já existe um vendedor com esse ID cadastrado.", "hour": "2026-09-20T14:30:00" }
                                    }
                                    """))),
            @ApiResponse(responseCode = "404", description = "Não existe cliente com o idVendedor informado.",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = ReturnModel.class),
                            examples = @ExampleObject(name = "Cliente inexistente", value = """
                                    {
                                        "status": 404,
                                        "path": "/api/vendedor/create",
                                        "success": false,
                                        "quantity": 1,
                                        "errors": { "status": 404, "message": "Não foi encontrado nenhum cliente com o ID: 99", "hour": "2026-09-20T14:30:00" }
                                    }
                                    """)))
    })
    @PostMapping("/create")
    public ResponseEntity<VendedorDTO> createVendedor(@RequestBody Vendedor vendedor) {
        return ResponseEntity.ok(vendedorService.createVendedor(vendedor));
    }

    @Operation(summary = "Atualiza os dados de vendedor",
            description = "Atualiza a avaliação (uso interno) e também sincroniza nome/e-mail/telefone com o Cliente correspondente. Só o próprio vendedor pode se atualizar.",
            security = @SecurityRequirement(name = "basicAuth"))
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Vendedor atualizado com sucesso.",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = ReturnModel.class),
                            examples = @ExampleObject(name = "Sucesso", value = """
                                    {
                                        "status": 200,
                                        "path": "/api/vendedor/update",
                                        "success": true,
                                        "quantity": 1,
                                        "itens": [
                                            { "idVendedor": 1, "nome": "Maria da Silva", "email": "maria@email.com", "telefone": "14999997777", "avaliacao": 4.5, "dataAdmissao": "2026-01-15" }
                                        ]
                                    }
                                    """))),
            @ApiResponse(responseCode = "401", description = "Tentativa de alterar os dados de outro vendedor.",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = ReturnModel.class),
                            examples = @ExampleObject(name = "Vendedor de terceiro", value = """
                                    {
                                        "status": 401,
                                        "path": "/api/vendedor/update",
                                        "success": false,
                                        "quantity": 1,
                                        "errors": { "status": 401, "message": "Apenas o vendedor pode alterar seus dados.", "hour": "2026-09-20T14:30:00" }
                                    }
                                    """))),
            @ApiResponse(responseCode = "404", description = "Não existe vendedor ou cliente com o ID informado.",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = ReturnModel.class),
                            examples = @ExampleObject(name = "Vendedor inexistente", value = """
                                    {
                                        "status": 404,
                                        "path": "/api/vendedor/update",
                                        "success": false,
                                        "quantity": 1,
                                        "errors": { "status": 404, "message": "Não foi encontrado nenhum vendedor com o ID: 1", "hour": "2026-09-20T14:30:00" }
                                    }
                                    """)))
    })
    @PutMapping("/update")
    public ResponseEntity<VendedorDTO> updateVendedor(@RequestBody VendedorDTO vendedor, @RequestAttribute("idUsuarioAuth") int idUsuarioAuth) {
        return ResponseEntity.ok(vendedorService.updateVendedor(vendedor, idUsuarioAuth));
    }

    @Operation(summary = "Exclui a conta de vendedor",
            description = "Só é permitido excluir a própria conta de vendedor (idAlvo precisa ser igual ao usuário autenticado). Não afeta a conta de Cliente.",
            security = @SecurityRequirement(name = "basicAuth"))
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Conta de vendedor excluída com sucesso (sem corpo)."),
            @ApiResponse(responseCode = "401", description = "Tentativa de excluir a conta de outro vendedor.",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = ReturnModel.class),
                            examples = @ExampleObject(name = "Vendedor de terceiro", value = """
                                    {
                                        "status": 401,
                                        "path": "/api/vendedor/2",
                                        "success": false,
                                        "quantity": 1,
                                        "errors": { "status": 401, "message": "Apenas o vendedor pode excluir seus dados.", "hour": "2026-09-20T14:30:00" }
                                    }
                                    """)))
    })
    @DeleteMapping("/{idAlvo}")
    public ResponseEntity<Void> deleteVendedor(@Parameter(description = "ID da própria conta de vendedor a excluir.", example = "1") @PathVariable("idAlvo") int idAlvo, @RequestAttribute("idUsuarioAuth") int idUsuarioAuth) {
        vendedorService.deleteVendedor(idAlvo, idUsuarioAuth);
        return ResponseEntity.ok().build();
    }
}
