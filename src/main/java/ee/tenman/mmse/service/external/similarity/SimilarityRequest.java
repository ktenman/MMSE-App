package ee.tenman.mmse.service.external.similarity;

public class SimilarityRequest {
    private String sentence1;
    private String sentence2;

    public SimilarityRequest() {
    }

    public SimilarityRequest(String sentence1, String sentence2) {
        this.sentence1 = sentence1;
        this.sentence2 = sentence2;
    }

    public String getSentence1() {
        return sentence1;
    }

    public void setSentence1(String sentence1) {
        this.sentence1 = sentence1;
    }

    public String getSentence2() {
        return sentence2;
    }

    public void setSentence2(String sentence2) {
        this.sentence2 = sentence2;
    }

    @Override
    public String toString() {
        return "SimilarityRequest{" +
            "sentence1='" + sentence1 + '\'' +
            ", sentence2='" + sentence2 + '\'' +
            '}';
    }
}
