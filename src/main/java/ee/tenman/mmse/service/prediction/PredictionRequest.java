package ee.tenman.mmse.service.prediction;

public class PredictionRequest {
    private String sentence;

    public PredictionRequest(String sentence) {
        this.sentence = sentence;
    }

    public String getSentence() {
        return sentence;
    }

    public void setSentence(String sentence) {
        this.sentence = sentence;
    }

    public String toString() {
        return "PredictionRequest{" +
                "sentence=" + sentence +
                '}';
    }
}
