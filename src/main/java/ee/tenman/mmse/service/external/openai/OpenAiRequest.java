package ee.tenman.mmse.service.external.openai;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.ArrayList;
import java.util.Base64;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class OpenAiRequest {
    private static final String DEFAULT_MODEL = "gpt-4";

    private static final String ROLE = "role";
    private static final String USER = "user";
    private static final String CONTENT = "content";

    private final String model;
    private final List<Map<String, Object>> messages;
    @JsonProperty("max_tokens")
    private int maxTokens = 300;

    private OpenAiRequest(String model, List<Map<String, Object>> messages) {
        this.model = model;
        this.messages = messages;
    }

    public String getModel() {
        return model;
    }

    public static OpenAiRequest createWithUserMessage(String messageContent) {
        Map<String, Object> userMessage = new HashMap<>();
        userMessage.put(ROLE, USER);
        userMessage.put(CONTENT, messageContent);
        return new OpenAiRequest(Model.GPT_4.getValue(), Collections.singletonList(userMessage));
    }

    public static OpenAiRequest createWithMessageAndImage(String messageContent, byte[] imageBytes) {
        List<Map<String, Object>> messages = new ArrayList<>();

        Map<String, String> text = Map.of(
            "type", "text",
            "text", messageContent
        );

        Map<String, Object> image = Map.of(
            "type", "image_url",
            "image_url", Map.of(
                "url", "data:image/jpeg;base64," + Base64.getEncoder().encodeToString(imageBytes))
        );

        messages.add(
            Map.of(
                ROLE, USER,
                CONTENT, List.of(text, image)
            )
        );

        return new OpenAiRequest(Model.GPT_4_VISION_PREVIEW.getValue(), messages);
    }

    public List<Map<String, Object>> getMessages() {
        return messages;
    }

    @Override
    public String toString() {
        return "OpenAiRequest{" +
            "model=" + model +
            ", messages=" + messages +
            ", maxTokens=" + maxTokens +
            '}';
    }

    public enum Model {
        GPT_4(DEFAULT_MODEL),
        GPT_4_VISION_PREVIEW("gpt-4-vision-preview");

        private final String value;

        Model(String value) {
            this.value = value;
        }

        public String getValue() {
            return value;
        }
    }
}
