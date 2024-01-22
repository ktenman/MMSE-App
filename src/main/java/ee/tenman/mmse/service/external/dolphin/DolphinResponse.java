package ee.tenman.mmse.service.external.dolphin;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.time.Instant;
import java.util.List;

public class DolphinResponse {
    private String model;
    @JsonProperty("created_at")
    private Instant createdAt;
    private String response;
    private boolean done;
    private List<Integer> context;
    @JsonProperty("total_duration")
    private long totalDuration;
    @JsonProperty("load_duration")
    private long loadDuration;
    @JsonProperty("prompt_eval_count")
    private int promptEvalCount;
    @JsonProperty("prompt_eval_duration")
    private long promptEvalDuration;
    @JsonProperty("eval_count")
    private int evalCount;
    @JsonProperty("eval_duration")
    private long evalDuration;

    public String getModel() {
        return model;
    }

    public void setModel(String model) {
        this.model = model;
    }

    public Instant getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Instant createdAt) {
        this.createdAt = createdAt;
    }

    public String getResponse() {
        return response;
    }

    public void setResponse(String response) {
        this.response = response;
    }

    public boolean isDone() {
        return done;
    }

    public void setDone(boolean done) {
        this.done = done;
    }

    public List<Integer> getContext() {
        return context;
    }

    public void setContext(List<Integer> context) {
        this.context = context;
    }

    public long getTotalDuration() {
        return totalDuration;
    }

    public void setTotalDuration(long totalDuration) {
        this.totalDuration = totalDuration;
    }

    public long getLoadDuration() {
        return loadDuration;
    }

    public void setLoadDuration(long loadDuration) {
        this.loadDuration = loadDuration;
    }

    public int getPromptEvalCount() {
        return promptEvalCount;
    }

    public void setPromptEvalCount(int promptEvalCount) {
        this.promptEvalCount = promptEvalCount;
    }

    public long getPromptEvalDuration() {
        return promptEvalDuration;
    }

    public void setPromptEvalDuration(long promptEvalDuration) {
        this.promptEvalDuration = promptEvalDuration;
    }

    public int getEvalCount() {
        return evalCount;
    }

    public void setEvalCount(int evalCount) {
        this.evalCount = evalCount;
    }

    public long getEvalDuration() {
        return evalDuration;
    }

    public void setEvalDuration(long evalDuration) {
        this.evalDuration = evalDuration;
    }

    @Override
    public String toString() {
        return "DolphinResponse{" +
            "model='" + model + '\'' +
            ", createdAt='" + createdAt + '\'' +
            ", response='" + response + '\'' +
            ", done=" + done +
            ", totalDuration=" + totalDuration +
            ", loadDuration=" + loadDuration +
            ", promptEvalCount=" + promptEvalCount +
            ", promptEvalDuration=" + promptEvalDuration +
            ", evalCount=" + evalCount +
            ", evalDuration=" + evalDuration +
            '}';
    }
}

