package Trabalho_de_Graduacao.Mesa_do_Campo_Back.Service;

import Trabalho_de_Graduacao.Mesa_do_Campo_Back.Entities.DTO.ClienteDTO;
import Trabalho_de_Graduacao.Mesa_do_Campo_Back.Entities.DTO.SuporteDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
public class SuporteService {
    @Autowired
    private EmailService emailService;
    @Autowired
    private ClienteService clienteService;
    @Value("${email.username}")
    private String emailUsername;

    public void solicitarSuporte(SuporteDTO suporteDTO) {
        ClienteDTO clienteDTO = clienteService.getById(suporteDTO.id_cliente());

        emailService.sendEmail(
                emailUsername,
                clienteDTO.email(),
                "Suporte Solicitado - " + suporteDTO.assunto(),
                "O cliente " + clienteDTO.nome() + " com o email " + clienteDTO.email() + ", solicitou suporte.\n" + suporteDTO.mensagem(),
                suporteDTO.anexo());
    }
}
