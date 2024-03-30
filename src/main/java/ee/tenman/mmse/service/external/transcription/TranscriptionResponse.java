package ee.tenman.mmse.service.external.transcription;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

public class TranscriptionResponse {
    private String fileName;
    private String transcription;
    private double durationSeconds;

    @JsonCreator
    public TranscriptionResponse(
        @JsonProperty("file_name") String fileName,
        @JsonProperty("transcription") String transcription,
        @JsonProperty("duration_seconds") double durationSeconds
    ) {
        this.fileName = fileName;
        this.transcription = transcription;
        this.durationSeconds = durationSeconds;
    }

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public String getTranscription() {
        return transcription;
    }

    public void setTranscription(String transcription) {
        this.transcription = transcription;
    }

    public double getDurationSeconds() {
        return durationSeconds;
    }

    public void setDurationSeconds(double durationSeconds) {
        this.durationSeconds = durationSeconds;
    }

    @Override
    public String toString() {
        return "TranscriptionResponse{" +
            "fileName='" + fileName + '\'' +
            ", transcription='" + transcription + '\'' +
            ", durationSeconds=" + durationSeconds +
            '}';
    }
}
