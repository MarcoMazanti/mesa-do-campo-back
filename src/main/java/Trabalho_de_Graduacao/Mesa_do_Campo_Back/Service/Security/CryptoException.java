package Trabalho_de_Graduacao.Mesa_do_Campo_Back.Service.Security;

/** Erro de protocolo que pode ser exposto ao cliente sem revelar detalhes criptográficos. */
public class CryptoException extends RuntimeException {
    public CryptoException(String message) {
        super(message);
    }

    public CryptoException(String message, Throwable cause) {
        super(message, cause);
    }
}
