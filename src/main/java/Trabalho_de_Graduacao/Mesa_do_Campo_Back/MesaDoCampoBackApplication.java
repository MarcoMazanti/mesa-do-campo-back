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

		iniciarNgrok(dotenv);
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
			// 1. Pega o token diretamente do .env
			String authToken = dotenv.get("NGROK_AUTHTOKEN");

			if (authToken == null || authToken.isEmpty()) {
				System.out.println("Erro: NGROK_AUTHTOKEN não foi encontrado no arquivo .env");
				return;
			}

			// 2. Configura o ngrok com o token
			JavaNgrokConfig config = new JavaNgrokConfig.Builder().withAuthToken(authToken).build();

			// 3. Constrói o cliente passando as configurações
			NgrokClient ngrokClient = new NgrokClient.Builder().withJavaNgrokConfig(config).build();

			var tunnel = ngrokClient.connect(new CreateTunnel.Builder()
					.withAddr(Integer.parseInt(dotenv.get("SERVER_PORT")))
					.build());

			System.out.println("\n✅ API iniciada com sucesso!");
			System.out.println("Requisição externa: " + tunnel.getPublicUrl() + "\n");
		} catch (Exception e) {
			System.out.println("Não foi possível iniciar o ngrok: " + e.getMessage());
			e.printStackTrace();
		}
	}
}