package ee.tenman.mmse.service.external.openai;

public class NoDolphinResponseException extends RuntimeException {
    public NoDolphinResponseException(String message) {
        super(message);
    }
}
