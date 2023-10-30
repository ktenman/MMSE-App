package ee.tenman.mmse.service.openai;

public class OpenAIResponse {
    private String text;

    public OpenAIResponse(String text) {
        this.text = text;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    @Override
    public String toString() {
        return "OpenAIResponse{" +
            "text='" + text + '\'' +
            '}';
    }
}
