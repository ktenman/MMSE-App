package ee.tenman.mmse.service.external.synonym;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

public class SynonymResponse {

    private double similarityScore;

    @JsonCreator
    public SynonymResponse(@JsonProperty("similarity_score") double similarityScore) {
        this.similarityScore = similarityScore;
    }

    public double getSimilarityScore() {
        return similarityScore;
    }

    public void setSimilarityScore(double similarityScore) {
        this.similarityScore = similarityScore;
    }

    @Override
    public String toString() {
        return "SynonymResponse{" +
            "similarityScore=" + similarityScore +
            '}';
    }
}
