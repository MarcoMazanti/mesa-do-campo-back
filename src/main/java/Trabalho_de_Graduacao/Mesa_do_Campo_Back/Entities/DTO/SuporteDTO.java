package Trabalho_de_Graduacao.Mesa_do_Campo_Back.Entities.DTO;

import Trabalho_de_Graduacao.Mesa_do_Campo_Back.Entities.Enum.CategoriaSuporte;
import org.springframework.web.multipart.MultipartFile;

public record SuporteDTO(int id_cliente, String assunto, CategoriaSuporte categoria, String mensagem, MultipartFile anexo) {
}
