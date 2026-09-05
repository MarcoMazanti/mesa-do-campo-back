package Trabalho_de_Graduacao.Mesa_do_Campo_Back;

import com.github.alexdlaird.ngrok.NgrokClient;
import com.github.alexdlaird.ngrok.conf.JavaNgrokConfig;
import com.github.alexdlaird.ngrok.protocol.CreateTunnel;
import io.github.cdimascio.dotenv.Dotenv;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class MesaDoCampoBackApplication {

	public static void main(String[] args) {
		Dotenv dotenv = carregarDotenv();

		SpringApplication app = new SpringApplication(MesaDoCampoBackApplication.class);
		app.run(args);

		//iniciarNgrok(dotenv);
	}

	private static Dotenv carregarDotenv() {
		try {
			Dotenv dotenv = Dotenv.load();
			dotenv.entries().forEach(entry -> System.setProperty(entry.getKey(), entry.getValue()));
			System.out.println("Variáveis do .env carregadas com sucesso na JVM.");
			return dotenv;
		} catch (Exception e) {
			System.out.println("Arquivo .env não encontrado. Utilizando variáveis de ambiente do sistema ou application.properties.");
			return null;
		}
	}

	public static void iniciarNgrok(Dotenv dotenv) {
		try {
			String authToken = dotenv.get("NGROK_AUTHTOKEN");
			String domain = dotenv.get("NGROK_DOMAIN");
			int porta = 8080;
			String portEnv = dotenv.get("SERVER_PORT");

			if (portEnv != null && !portEnv.trim().isEmpty()) {
				try {
					porta = Integer.parseInt(portEnv);
				} catch (NumberFormatException e) {
					System.out.println("Porta inválida no .env. Usando a porta padrão 8080.");
				}
			}

			if (authToken == null || authToken.isEmpty()) {
				System.out.println("Erro: NGROK_AUTHTOKEN não foi encontrado no arquivo .env");
				return;
			}

			JavaNgrokConfig config = new JavaNgrokConfig.Builder().withAuthToken(authToken).build();
			NgrokClient ngrokClient = new NgrokClient.Builder().withJavaNgrokConfig(config).build();

			var tunnel = ngrokClient.connect(new CreateTunnel.Builder()
					.withAddr(porta)
					.withDomain(domain)
					.build());

			System.out.println("\n✅ API iniciada com sucesso!");
			System.out.println("Requisição externa: " + tunnel.getPublicUrl() + "\n");
		} catch (Exception e) {
			System.out.println("Não foi possível iniciar o ngrok: " + e.getMessage());
			e.printStackTrace();
		}
	}
}