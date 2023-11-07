package ee.tenman.mmse.service.external.openai;

import jakarta.annotation.Resource;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class OpenAiService {

    private static final Logger log = LoggerFactory.getLogger(OpenAiService.class);

    @Resource
    private OpenAiClient openAiClient;

    public Optional<String> askQuestion(String question) {
        if (StringUtils.isBlank(question)) {
            log.warn("Received blank question, returning empty response");
            return Optional.empty();
        }

        try {
            OpenAiRequest request = OpenAiRequest.createWithUserMessage(question);
            OpenAiResponse openAiResponse = openAiClient.askQuestion(request);
            return openAiResponse.getAnswer();
        } catch (Exception e) {
            log.error("Failed to ask question: {}", question, e);
            return Optional.empty();
        }
    }
}
