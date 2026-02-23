package gdg.challenge.poom;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.ai.model.google.genai.autoconfigure.chat.GoogleGenAiChatAutoConfiguration;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

@EnableJpaAuditing
@SpringBootApplication(exclude = GoogleGenAiChatAutoConfiguration.class)
public class PoomApplication {

	public static void main(String[] args) {
		SpringApplication.run(PoomApplication.class, args);
	}

}
