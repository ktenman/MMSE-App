package ee.tenman.mmse.service.openai;

import ee.tenman.mmse.config.OpenAiFeignConfig;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

//@FeignClient(name = "openai", url = "https://api.openai.com", configuration = OpenAiFeignConfig.class)
@FeignClient(name = "openai", url = "http://localhost:1337", configuration = OpenAiFeignConfig.class)
public interface OpenAiClient {

//    @PostMapping("/v1/chat/completions")
//    OpenAiResponse askQuestion(@RequestBody OpenAiRequest request);

    @PostMapping("/gpt4free")
    OpenAiResponse askQuestion(@RequestBody OpenAiRequest request);
}
