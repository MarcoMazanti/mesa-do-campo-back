package Trabalho_de_Graduacao.Mesa_do_Campo_Back;

import io.github.cdimascio.dotenv.Dotenv;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.core.env.Environment;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.HashMap;
import java.util.Map;

@SpringBootApplication
public class MesaDoCampoBackApplication {

	public static void main(String[] args) {
		Dotenv dotenv = carregarDotenv();

		SpringApplication app = new SpringApplication(MesaDoCampoBackApplication.class);
		Map<String, Object> propriedadesDinamicas = new HashMap<>();
		String hostAddress = "127.0.0.1";

		try {
			InetAddress localMachine = InetAddress.getLocalHost();
			hostAddress = localMachine.getHostAddress();

			// Força a API a rodar neste IP específico da rede
			propriedadesDinamicas.put("server.address", hostAddress);
			System.out.println("IP da rede detectado. Preparando para iniciar em: " + hostAddress);

		} catch (UnknownHostException e) {
			System.out.println("Não foi possível obter o IP da rede. Fazendo fallback para localhost (127.0.0.1).");
			propriedadesDinamicas.put("server.address", "127.0.0.1");
		}

		// Aplica as propriedades antes de inicializar o contexto do Spring
		app.setDefaultProperties(propriedadesDinamicas);

		// Iniciar a API e captura o contexto
		ConfigurableApplicationContext context = app.run(args);

		Environment env = context.getEnvironment();
		String port = env.getProperty("server.port", dotenv != null ? dotenv.get("SERVER_PORT", "8080") : "8080");

		System.out.println("\n✅ API iniciada com sucesso!");
		System.out.println("Endereço para Rede: http://" + hostAddress + ":" + port + "\n");
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
}