package Trabalho_de_Graduacao.Mesa_do_Campo_Back.Service.Security;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Exige o protocolo híbrido v1 para uma classe de controller ou para um endpoint.
 * Para endpoints sem corpo (GET/DELETE), a chave RSA-OAEP deve ser enviada no
 * cabeçalho {@value HybridCryptoContext#ENCRYPTED_KEY_HEADER}.
 */
@Target({ElementType.TYPE, ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
public @interface HybridEncrypted {
}
