package Trabalho_de_Graduacao.Mesa_do_Campo_Back.Entities.DTO.External;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class EnvelopeRequestDTO {
    private String version;
    private String algorithm;
    private String encryptedKey; // Chave AES criptografada com RSA
    private String iv;           // nonce GCM de 96 bits em Base64
    private String encryptedData; // JSON criptografado com AES
}
