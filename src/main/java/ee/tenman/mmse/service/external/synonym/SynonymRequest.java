package ee.tenman.mmse.service.external.synonym;

public class SynonymRequest {
    private String text1;
    private String text2;

    public SynonymRequest() {
    }

    public SynonymRequest(String text1, String sentence2) {
        this.text1 = text1;
        this.text2 = sentence2;
    }

    public String getText1() {
        return text1;
    }

    public void setText1(String text1) {
        this.text1 = text1;
    }

    public String getText2() {
        return text2;
    }

    public void setText2(String text2) {
        this.text2 = text2;
    }

    @Override
    public String toString() {
        return "SynonymRequest{" +
            "text1='" + text1 + '\'' +
            ", text2='" + text2 + '\'' +
            '}';
    }
}
