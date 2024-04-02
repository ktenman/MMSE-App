package ee.tenman.mmse.service.dto;

import ee.tenman.mmse.domain.enumeration.QuestionId;

public class OrientationToPlaceQuestionDTO {
    private QuestionId questionId;
    private String questionText;

    public OrientationToPlaceQuestionDTO(QuestionId questionId, String questionText) {
        this.questionId = questionId;
        this.questionText = questionText;
    }

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
}
