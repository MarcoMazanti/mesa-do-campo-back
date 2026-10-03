package Trabalho_de_Graduacao.Mesa_do_Campo_Back.Service.Security;

import org.springframework.stereotype.Service;

import java.security.SecureRandom;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@Service
public class PasswordGenerator {
    private static final String MAIUSCULAS = "ABCDEFGHIJKLMNOPQRSTUVWXYZ";
    private static final String MINUSCULAS = "abcdefghijklmnopqrstuvwxyz";
    private static final String NUMEROS = "0123456789";
    private static final String ESPECIAIS = "!@#$%^&*()_+-=[]{}|;:,.<>?";

    private static final String TODOS = MAIUSCULAS + MINUSCULAS + NUMEROS + ESPECIAIS;
    private static final SecureRandom random = new SecureRandom();

    public static String generatePassword() {
        int tamanho = random.nextInt(10, 20);
        if (tamanho < 4) {
            throw new IllegalArgumentException("O tamanho mínimo da senha deve ser de pelo menos 4 caracteres.");
        }

        List<Character> caracteresSenha = new ArrayList<>();

        // Garante ao menos um caractere de cada categoria exigida
        caracteresSenha.add(MAIUSCULAS.charAt(random.nextInt(MAIUSCULAS.length())));
        caracteresSenha.add(NUMEROS.charAt(random.nextInt(NUMEROS.length())));
        caracteresSenha.add(ESPECIAIS.charAt(random.nextInt(ESPECIAIS.length())));
        caracteresSenha.add(MINUSCULAS.charAt(random.nextInt(MINUSCULAS.length())));

        // Preenche o restante do tamanho solicitado com caracteres aleatórios
        for (int i = 4; i < tamanho; i++) {
            caracteresSenha.add(TODOS.charAt(random.nextInt(TODOS.length())));
        }

        // Embaralha a lista para que os caracteres obrigatórios não fiquem sempre no início
        Collections.shuffle(caracteresSenha, random);

        // Converte a lista em uma String final
        StringBuilder senhaFinal = new StringBuilder();
        for (char c : caracteresSenha) {
            senhaFinal.append(c);
        }

        return senhaFinal.toString();
    }
}
