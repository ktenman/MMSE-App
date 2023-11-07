package ee.tenman.mmse.service.external.openai;

public class NoOpenAiResponseException extends RuntimeException {
    public NoOpenAiResponseException(String message) {
        super(message);
    }
}
