package Trabalho_de_Graduacao.Mesa_do_Campo_Back.Controller;

import Trabalho_de_Graduacao.Mesa_do_Campo_Back.Entities.DTO.External.PublicKeyResponseDTO;
import Trabalho_de_Graduacao.Mesa_do_Campo_Back.Service.Security.CryptoService;
import org.springframework.http.CacheControl;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.concurrent.TimeUnit;

/** Endpoint público de bootstrap para o Web Crypto API do cliente. */
@RestController
@RequestMapping("/api/crypto")
public class CryptoController {
    private final CryptoService cryptoService;

    public CryptoController(CryptoService cryptoService) {
        this.cryptoService = cryptoService;
    }

    @GetMapping("/public-key")
    public ResponseEntity<PublicKeyResponseDTO> getPublicKey() {
        return ResponseEntity.ok()
                .cacheControl(CacheControl.maxAge(1, TimeUnit.HOURS).cachePublic())
                .body(new PublicKeyResponseDTO("RSA-OAEP-256", cryptoService.getPublicKeyBase64()));
    }
}
