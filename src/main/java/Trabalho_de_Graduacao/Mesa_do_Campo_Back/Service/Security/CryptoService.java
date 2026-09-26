package Trabalho_de_Graduacao.Mesa_do_Campo_Back.Service.Security;

import Trabalho_de_Graduacao.Mesa_do_Campo_Back.Entities.DTO.External.EnvelopeResponseDTO;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.spec.GCMParameterSpec;
import javax.crypto.spec.OAEPParameterSpec;
import javax.crypto.spec.PSource;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.security.KeyFactory;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.SecureRandom;
import java.security.spec.MGF1ParameterSpec;
import java.security.spec.PKCS8EncodedKeySpec;
import java.util.Base64;

@Service
public class CryptoService {
    public static final String RSA_TRANSFORMATION = "RSA/ECB/OAEPWithSHA-256AndMGF1Padding";
    public static final String AES_TRANSFORMATION = "AES/GCM/NoPadding";
    public static final String ALGORITHM = "RSA-OAEP-256/A256GCM";
    private static final int GCM_TAG_LENGTH_BITS = 128;
    private static final int GCM_IV_LENGTH_BYTES = 12;
    private static final int AES_KEY_LENGTH_BYTES = 32;
    private static final OAEPParameterSpec OAEP_SHA_256 = new OAEPParameterSpec(
            "SHA-256", "MGF1", MGF1ParameterSpec.SHA256, PSource.PSpecified.DEFAULT);

    // Injeta a Chave Privada RSA do servidor
    @Value("${chave.privada}")
    private String privateKeyString;
    @Value("${chave.publica:}")
    private String publicKeyString;
    private PrivateKey rsaPrivateKey;
    private PublicKey rsaPublicKey;

    @PostConstruct
    public void init() throws Exception {
        rsaPrivateKey = loadPrivateKey(privateKeyString);
        rsaPublicKey = publicKeyString == null || publicKeyString.isBlank()
                ? KeyFactoryHolder.publicKeyFrom(rsaPrivateKey)
                : loadPublicKey(publicKeyString);
    }

    private PrivateKey loadPrivateKey(String privateKeyString) throws Exception {
        byte[] privateKeyBytes = Base64.getDecoder().decode(normalizePem(privateKeyString,
                "-----BEGIN PRIVATE KEY-----", "-----END PRIVATE KEY-----"));
        return KeyFactory.getInstance("RSA").generatePrivate(new PKCS8EncodedKeySpec(privateKeyBytes));
    }

    private PublicKey loadPublicKey(String publicKeyString) throws Exception {
        byte[] publicKeyBytes = Base64.getDecoder().decode(normalizePem(publicKeyString,
                "-----BEGIN PUBLIC KEY-----", "-----END PUBLIC KEY-----"));
        return KeyFactory.getInstance("RSA").generatePublic(new java.security.spec.X509EncodedKeySpec(publicKeyBytes));
    }

    private String normalizePem(String key, String beginMarker, String endMarker) {
        return key.replace(beginMarker, "").replace(endMarker, "").replaceAll("\\s", "");
    }

    // Abre o envelope para pegar a chave AES
    public SecretKey descriptografarChaveAES(String encryptedKeyBase64) throws CryptoException {
        if (encryptedKeyBase64 == null || encryptedKeyBase64.isBlank()) {
            throw new CryptoException("O campo encryptedKey é obrigatório.");
        }

        try {
            Cipher rsaCipher = Cipher.getInstance(RSA_TRANSFORMATION);
            rsaCipher.init(Cipher.DECRYPT_MODE, rsaPrivateKey, OAEP_SHA_256);

            byte[] aesKeyBytes = rsaCipher.doFinal(Base64.getDecoder().decode(encryptedKeyBase64));
            if (aesKeyBytes.length != AES_KEY_LENGTH_BYTES) {
                throw new CryptoException("A chave de sessão deve possuir 256 bits.");
            }
            return new SecretKeySpec(aesKeyBytes, "AES");
        } catch (CryptoException e) {
            throw e;
        } catch (Exception e) {
            throw new CryptoException("Não foi possível abrir a chave de sessão.", e);
        }
    }

    // Descriptografa os dados que vieram do front
    public String descriptografarDados(String encryptedData, String ivBase64, SecretKey aesKey, byte[] aad) throws CryptoException {
        if (encryptedData == null || encryptedData.isBlank() || ivBase64 == null || ivBase64.isBlank()) {
            throw new CryptoException("Os campos iv e encryptedData são obrigatórios.");
        }

        try {
            byte[] ivBytes = Base64.getDecoder().decode(ivBase64);
            if (ivBytes.length != GCM_IV_LENGTH_BYTES) {
                throw new CryptoException("O IV deve possuir 96 bits.");
            }

            Cipher aesCipher = Cipher.getInstance(AES_TRANSFORMATION);
            aesCipher.init(Cipher.DECRYPT_MODE, aesKey, new GCMParameterSpec(GCM_TAG_LENGTH_BITS, ivBytes));
            aesCipher.updateAAD(aad);
            byte[] descriptografado = aesCipher.doFinal(Base64.getDecoder().decode(encryptedData));

            return new String(descriptografado, StandardCharsets.UTF_8);
        } catch (CryptoException e) {
            throw e;
        } catch (Exception e) {
            throw new CryptoException("Não foi possível validar ou descriptografar a requisição.", e);
        }
    }

    // Criptografa a resposta para devolver ao front
    public EnvelopeResponseDTO criptografarResposta(String jsonPlano, SecretKey aesKey, byte[] aad) throws CryptoException {
        // Gera um novo IV para a resposta
        byte[] ivBytes = new byte[GCM_IV_LENGTH_BYTES];
        new SecureRandom().nextBytes(ivBytes);

        try {
            Cipher aesCipher = Cipher.getInstance(AES_TRANSFORMATION);
            aesCipher.init(Cipher.ENCRYPT_MODE, aesKey, new GCMParameterSpec(GCM_TAG_LENGTH_BITS, ivBytes));
            aesCipher.updateAAD(aad);

            byte[] dadosCriptografados = aesCipher.doFinal(jsonPlano.getBytes(StandardCharsets.UTF_8));

            // Monta o envelope de saída
            EnvelopeResponseDTO response = new EnvelopeResponseDTO();
            response.setVersion("v1");
            response.setAlgorithm(ALGORITHM);
            response.setIv(Base64.getEncoder().encodeToString(ivBytes));
            response.setEncryptedData(Base64.getEncoder().encodeToString(dadosCriptografados));

            return response;
        } catch (Exception e) {
            throw new CryptoException("Não foi possível criptografar a resposta.", e);
        }
    }

    public String getPublicKeyBase64() {
        return Base64.getEncoder().encodeToString(rsaPublicKey.getEncoded());
    }

    /** Obtém a chave pública no formato X.509 a partir da chave privada RSA. */
    private static final class KeyFactoryHolder {
        private static PublicKey publicKeyFrom(PrivateKey privateKey) {
            if (!(privateKey instanceof java.security.interfaces.RSAPrivateCrtKey rsaKey)) {
                throw new IllegalStateException("A chave privada configurada não é uma chave RSA CRT.");
            }
            try {
                java.security.spec.RSAPublicKeySpec publicKeySpec = new java.security.spec.RSAPublicKeySpec(
                        rsaKey.getModulus(), rsaKey.getPublicExponent());
                return KeyFactory.getInstance("RSA").generatePublic(publicKeySpec);
            } catch (Exception e) {
                throw new IllegalStateException("Não foi possível derivar a chave pública RSA.", e);
            }
        }
    }
}
