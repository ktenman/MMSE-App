package ee.tenman.mmse.service.dto;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import ee.tenman.mmse.domain.enumeration.QuestionId;

public class OrientationToPlaceQuestionDTO {
    private QuestionId questionId;
    private String questionText;
    private String correctAnswer;
    private String answerOptions;

    public QuestionId getQuestionId() {
        return questionId;
    }

    public void setQuestionId(QuestionId questionId) {
        this.questionId = questionId;
    }

    public String getQuestionText() {
        return questionText;
    }

    public void setQuestionText(String questionText) {
        this.questionText = questionText;
    }

    public OrientationToPlaceQuestionDTO(QuestionId questionId, String questionText) {
        this.questionId = questionId;
        this.questionText = questionText;
    }

    @JsonCreator
    public OrientationToPlaceQuestionDTO(
        @JsonProperty("questionId") QuestionId questionId,
        @JsonProperty("questionText") String questionText,
        @JsonProperty("correctAnswer") String correctAnswer,
        @JsonProperty("answerOptions") String answerOptions
    ) {
        this.questionId = questionId;
        this.questionText = questionText;
        this.correctAnswer = correctAnswer;
        this.answerOptions = answerOptions;
    }

    public String getCorrectAnswer() {
        return correctAnswer;
    }

    public void setCorrectAnswer(String correctAnswer) {
        this.correctAnswer = correctAnswer;
    }

    public String getAnswerOptions() {
        return answerOptions;
    }

    public void setAnswerOptions(String answerOptions) {
        this.answerOptions = answerOptions;
    }
}
