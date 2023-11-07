package ee.tenman.mmse.service.openai;

public class NoOpenAiResponseException extends RuntimeException {
    public NoOpenAiResponseException(String message) {
        super(message);
    }
}
