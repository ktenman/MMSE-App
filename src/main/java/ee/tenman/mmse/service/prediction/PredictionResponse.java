package ee.tenman.mmse.service.prediction;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

public class PredictionResponse {

    private boolean grammaticallyCorrect;

    @JsonCreator
    public PredictionResponse(@JsonProperty("grammatically_correct") boolean grammaticallyCorrect) {
        this.grammaticallyCorrect = grammaticallyCorrect;
    }

    public boolean isGrammaticallyCorrect() {
        return grammaticallyCorrect;
    }

    public void setGrammaticallyCorrect(boolean grammaticallyCorrect) {
        this.grammaticallyCorrect = grammaticallyCorrect;
    }

    @Override
    public String toString() {
        return "PredictionResponse{" +
                "grammaticallyCorrect=" + grammaticallyCorrect +
                '}';
    }
}
