package ee.tenman.mmse.service.openai;

public class OpenAIRequest {
    private String prompt;
    private int max_tokens;

    public OpenAIRequest(String prompt, int max_tokens) {
        this.prompt = prompt;
        this.max_tokens = max_tokens;
    }

    public String getPrompt() {
        return prompt;
    }

    public int getMax_tokens() {
        return max_tokens;
    }

    public void setPrompt(String prompt) {
        this.prompt = prompt;
    }

    public void setMax_tokens(int max_tokens) {
        this.max_tokens = max_tokens;
    }

    @Override
    public String toString() {
        return "OpenAIRequest{" +
            "prompt='" + prompt + '\'' +
            ", max_tokens=" + max_tokens +
            '}';
    }
}
