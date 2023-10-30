package ee.tenman.mmse.service.openai;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class OpenAIApiService {

    private static final int MAX_TOKENS = 150;

    private final OpenAIClient openAIClient;

    @Autowired
    public OpenAIApiService(OpenAIClient openAIClient) {
        this.openAIClient = openAIClient;
    }

    public String callGPT(String prompt) {
        OpenAIRequest request = new OpenAIRequest(prompt, MAX_TOKENS);
        return openAIClient.callGPT(request).getText();
    }


}
