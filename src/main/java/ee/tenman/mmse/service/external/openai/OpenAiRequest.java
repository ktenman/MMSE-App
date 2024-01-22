package ee.tenman.mmse.service.external.openai;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class OpenAiRequest {
    private static final String DEFAULT_MODEL = "gpt-4";
//    private static final String DEFAULT_MODEL = "gpt-4-1106-preview";


    private static final String ROLE = "role";
    private static final String USER = "user";
    private static final String CONTENT = "content";

    private String model;
    private List<Map<String, String>> messages;

    public OpenAiRequest() {
        this.model = DEFAULT_MODEL;
    }

    public OpenAiRequest(String model, List<Map<String, String>> messages) {
        this.model = model;
        this.messages = messages;
    }

    public OpenAiRequest(List<Map<String, String>> messages) {
        this.model = DEFAULT_MODEL;
        this.messages = messages;
    }

    public String getModel() {
        return model;
    }

    public void setModel(String model) {
        this.model = model;
    }

    public List<Map<String, String>> getMessages() {
        return messages;
    }

    public void setMessages(List<Map<String, String>> messages) {
        this.messages = messages;
    }

    public static OpenAiRequest createWithUserMessage(String messageContent) {
        Map<String, String> userMessage = new HashMap<>();
        userMessage.put(ROLE, USER);
        userMessage.put(CONTENT, messageContent);

        OpenAiRequest request = new OpenAiRequest();
        request.setMessages(Collections.singletonList(userMessage));
        return request;
    }
}
