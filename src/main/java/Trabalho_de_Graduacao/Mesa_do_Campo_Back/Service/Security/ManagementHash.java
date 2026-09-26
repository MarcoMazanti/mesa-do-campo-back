package Trabalho_de_Graduacao.Mesa_do_Campo_Back.Service.Security;

import org.springframework.security.crypto.bcrypt.BCrypt;

public class ManagementHash {
    public static String encriptarSenha(String senha) {
        return BCrypt.hashpw(senha, BCrypt.gensalt());
    }

    public static boolean validarSenha(String senhaFront, String senhaBancoDeDados) {
        return BCrypt.checkpw(senhaFront, senhaBancoDeDados);
    }
}
