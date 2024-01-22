package ee.tenman.mmse.service.external.similarity;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

public class SimilarityResponse {

    private double similarity;

    @JsonCreator
    public SimilarityResponse(@JsonProperty("similarity") double similarity) {
        this.similarity = similarity;
    }

    public double getSimilarity() {
        return similarity;
    }

    public void setSimilarity(double similarity) {
        this.similarity = similarity;
    }

    @Override
    public String toString() {
        return "SimilarityResponse{" +
            "similarity=" + similarity +
            '}';
    }
}
