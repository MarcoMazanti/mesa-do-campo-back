package Trabalho_de_Graduacao.Mesa_do_Campo_Back.Controller;

import Trabalho_de_Graduacao.Mesa_do_Campo_Back.Entities.DTO.SuporteDTO;
import Trabalho_de_Graduacao.Mesa_do_Campo_Back.Service.Security.HybridEncrypted;
import Trabalho_de_Graduacao.Mesa_do_Campo_Back.Service.SuporteService;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "Suporte")
@HybridEncrypted
@RestController
@RequestMapping("/api/suporte")
public class SuporteController {
    @Autowired
    private SuporteService suporteService;

    @PostMapping(consumes = {"multipart/form-data"})
    public ResponseEntity<Void> solicitarSuporte(@ModelAttribute SuporteDTO suporteDTO) {
        suporteService.solicitarSuporte(suporteDTO);
        return ResponseEntity.ok().build();
    }
}
