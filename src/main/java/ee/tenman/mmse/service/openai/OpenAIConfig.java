package ee.tenman.mmse.service.openai;

import feign.RequestInterceptor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OpenAIConfig {

//    @Value("${openai.api.key}") // Assume you store the API key in application properties or environment variable
//    private String apiKey;

    private static final String API_KEY = "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";

    @Bean
    public RequestInterceptor requestInterceptor() {
        return requestTemplate -> requestTemplate.header("Authorization", "Bearer " + API_KEY);
    }
}
