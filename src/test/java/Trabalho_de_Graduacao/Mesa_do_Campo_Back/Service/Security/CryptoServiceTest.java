package Trabalho_de_Graduacao.Mesa_do_Campo_Back.Service.Security;

import Trabalho_de_Graduacao.Mesa_do_Campo_Back.Entities.DTO.External.EnvelopeResponseDTO;
import org.junit.jupiter.api.Test;

import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import javax.crypto.spec.GCMParameterSpec;
import javax.crypto.spec.OAEPParameterSpec;
import javax.crypto.spec.PSource;
import java.lang.reflect.Field;
import java.nio.charset.StandardCharsets;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.SecureRandom;
import java.security.spec.MGF1ParameterSpec;
import java.util.Base64;

import static org.junit.jupiter.api.Assertions.assertEquals;

class CryptoServiceTest {
    private static final OAEPParameterSpec OAEP_SHA_256 = new OAEPParameterSpec(
            "SHA-256", "MGF1", MGF1ParameterSpec.SHA256, PSource.PSpecified.DEFAULT);

    @Test
    void opensWrappedSessionKeyAndEncryptsAuthenticatedResponse() throws Exception {
        KeyPairGenerator rsaGenerator = KeyPairGenerator.getInstance("RSA");
        rsaGenerator.initialize(2048);
        KeyPair rsaKeyPair = rsaGenerator.generateKeyPair();

        CryptoService cryptoService = new CryptoService();
        Field privateKey = CryptoService.class.getDeclaredField("rsaPrivateKey");
        privateKey.setAccessible(true);
        privateKey.set(cryptoService, rsaKeyPair.getPrivate());

        KeyGenerator aesGenerator = KeyGenerator.getInstance("AES");
        aesGenerator.init(256);
        SecretKey originalAesKey = aesGenerator.generateKey();

        Cipher rsaCipher = Cipher.getInstance(CryptoService.RSA_TRANSFORMATION);
        rsaCipher.init(Cipher.ENCRYPT_MODE, rsaKeyPair.getPublic(), OAEP_SHA_256);
        String wrappedKey = Base64.getEncoder().encodeToString(rsaCipher.doFinal(originalAesKey.getEncoded()));
        SecretKey recoveredAesKey = cryptoService.descriptografarChaveAES(wrappedKey);

        assertEquals(Base64.getEncoder().encodeToString(originalAesKey.getEncoded()),
                Base64.getEncoder().encodeToString(recoveredAesKey.getEncoded()));

        byte[] aad = "RESPONSE:POST:/api/produto/create".getBytes(StandardCharsets.UTF_8);
        EnvelopeResponseDTO envelope = cryptoService.criptografarResposta("{\"ok\":true}", recoveredAesKey, aad);

        Cipher aesCipher = Cipher.getInstance(CryptoService.AES_TRANSFORMATION);
        aesCipher.init(Cipher.DECRYPT_MODE, recoveredAesKey,
                new GCMParameterSpec(128, Base64.getDecoder().decode(envelope.getIv())));
        aesCipher.updateAAD(aad);
        String payload = new String(aesCipher.doFinal(Base64.getDecoder().decode(envelope.getEncryptedData())),
                StandardCharsets.UTF_8);

        assertEquals("{\"ok\":true}", payload);
        assertEquals("v1", envelope.getVersion());
        assertEquals(CryptoService.ALGORITHM, envelope.getAlgorithm());
    }
}
