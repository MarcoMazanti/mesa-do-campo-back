package Trabalho_de_Graduacao.Mesa_do_Campo_Back.Config;

import Trabalho_de_Graduacao.Mesa_do_Campo_Back.Entities.Cliente;
import Trabalho_de_Graduacao.Mesa_do_Campo_Back.Repository.ClienteRepository;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.List;

import static Trabalho_de_Graduacao.Mesa_do_Campo_Back.Security.ManagementHash.validarSenha;

@Component
public class Interceptador implements HandlerInterceptor {
    @Autowired
    private ClienteRepository clienteRepository;

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) {
        String authHeader = request.getHeader("Authorization");

        try {
            if (authHeader == null || authHeader.isBlank()) {
                response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                response.setCharacterEncoding("UTF-8");
                response.getWriter().write("Não foi fornecido uma autenticação.");
                return false;
            }

            if (!authHeader.split(" ")[1].contains("Basic")) {
                response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                response.setCharacterEncoding("UTF-8");
                response.getWriter().write("Modelo de autenticação incorreta, utilize Basic Auth.");
                return false;
            }

            // Garante que pega apenas a parte Base64 após "Basic "
            String base64Token = authHeader.split(" ")[1].trim();

            // Decodifica os bytes e converte para String usando UTF-8
            byte[] decodedBytes = Base64.getDecoder().decode(base64Token);
            String autenticacao = new String(decodedBytes, StandardCharsets.UTF_8);

            String nome = autenticacao.split(":")[0];
            String senha = autenticacao.split(":")[1];
            System.out.println(nome);

            List<Cliente> clienteList = clienteRepository.findAllByNome(nome);

            for (Cliente cliente : clienteList) {
                if (validarSenha(senha, cliente.getSenha())) {
                    request.setAttribute("idUsuarioAuth", cliente.getId());

                    return true;
                }
            }

            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            response.setCharacterEncoding("UTF-8");
            response.getWriter().write("Não foi identificado a conta desejada.");
            return false;
        } catch (Exception e) {
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            return false;
        }
    }
}
