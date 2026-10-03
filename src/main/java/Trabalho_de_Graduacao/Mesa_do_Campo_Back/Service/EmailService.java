package Trabalho_de_Graduacao.Mesa_do_Campo_Back.Service;

import java.io.IOException;
import java.util.Properties;

import javax.activation.DataHandler;
import javax.mail.Authenticator;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Multipart;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMultipart;
import javax.mail.util.ByteArrayDataSource;

import Trabalho_de_Graduacao.Mesa_do_Campo_Back.Exception.EmailException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

@Service
public class EmailService {
    @Value("${email.username}")
    private String username;
    @Value("${email.password}")
    private String password;

    public void sendEmail(String emailAlvo, String emailCopia, String titulo, String mensagem, MultipartFile anexo) {
        Properties props = new Properties();
        props.put("mail.smtp.host", "smtp.gmail.com");
        props.put("mail.smtp.port", "465");
        props.put("mail.smtp.auth", "true");
        props.put("mail.smtp.socketFactory.port", "465");
        props.put("mail.smtp.socketFactory.class", "javax.net.ssl.SSLSocketFactory");

        // Autenticação e criação da Sessão
        Session session = Session.getInstance(props, new Authenticator() {
            @Override
            protected PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication(username, password);
            }
        });

        // session.setDebug(true); // ver os logs do servidor no console

        try {
            // Criação da Mensagem
            Message message = new MimeMessage(session);
            message.setFrom(new InternetAddress(username));

            // Destinatário principal
            message.setRecipients(
                    Message.RecipientType.TO,
                    InternetAddress.parse(emailAlvo)
            );

            // Adicionando o e-mail de Cópia (CC), caso tenha sido informado
            if (emailCopia != null && !emailCopia.trim().isEmpty()) {
                message.setRecipients(
                        Message.RecipientType.CC,
                        InternetAddress.parse(emailCopia)
                );
            }

            message.setSubject(titulo);

            // Criando o "pacote" que vai conter o texto e o anexo
            Multipart multipart = new MimeMultipart();

            // Adição do texto
            MimeBodyPart textPart = new MimeBodyPart();
            textPart.setText(mensagem);
            multipart.addBodyPart(textPart);

            // Adição do anexo
            if (anexo != null && !anexo.isEmpty()) {
                MimeBodyPart attachmentPart = new MimeBodyPart();

                // Converte os bytes do MultipartFile para um formato que o JavaMail entende
                ByteArrayDataSource dataSource = new ByteArrayDataSource(anexo.getBytes(), anexo.getContentType());

                attachmentPart.setDataHandler(new DataHandler(dataSource));
                attachmentPart.setFileName(anexo.getOriginalFilename()); // Mantém o nome original do arquivo

                multipart.addBodyPart(attachmentPart);
            }

            // Junta tudo na mensagem
            message.setContent(multipart);

            // Envio do E-mail
            Transport.send(message);
        } catch (MessagingException | IOException e) {
            throw new EmailException("Erro ao enviar o e-mail: \"" + e.getMessage() + "\"");
        }
    }
}
