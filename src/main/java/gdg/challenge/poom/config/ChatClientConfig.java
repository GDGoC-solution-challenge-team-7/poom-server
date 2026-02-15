package gdg.challenge.poom.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.ai.chat.client.ChatClient;

@Configuration
public class ChatClientConfig {

    /** 시스템 프롬프트는 요청별 style(empathy/solution/balanced)에 따라 ChatService에서 주입합니다. */
    @Bean
    public ChatClient poomChatClient(ChatClient.Builder builder) {
        return builder.build();
    }
}
