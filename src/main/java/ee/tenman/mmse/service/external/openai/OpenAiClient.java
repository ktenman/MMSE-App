package ee.tenman.mmse.service.external.openai;

import feign.RequestInterceptor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.context.annotation.Bean;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@FeignClient(name = OpenAiClient.CLIENT_NAME, url = OpenAiClient.CLIENT_URL, configuration = OpenAiClient.Configuration.class)
public interface OpenAiClient {

    String CLIENT_NAME = "openAiClient";
    String CLIENT_URL = "https://api.openai.com";

    @PostMapping("/v1/chat/completions")
    OpenAiResponse createCompletion(@RequestBody OpenAiRequest request);

    class Configuration {
        @Value("${openai.token}")
        private String openAiToken;

        @Bean
        public RequestInterceptor requestInterceptor() {
            return template -> template.header("Authorization", "Bearer " + openAiToken);
        }
    }

}
