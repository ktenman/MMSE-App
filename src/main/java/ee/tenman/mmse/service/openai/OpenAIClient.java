package ee.tenman.mmse.service.openai;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@FeignClient(name = "openai", url = "https://api.openai.com/v1/engines/davinci", configuration = OpenAIConfig.class)
public interface OpenAIClient {

    @PostMapping("/completions")
    OpenAIResponse callGPT(@RequestBody OpenAIRequest request);

}

