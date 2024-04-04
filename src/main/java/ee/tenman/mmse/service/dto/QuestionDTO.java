package ee.tenman.mmse.service.dto;

import ee.tenman.mmse.domain.enumeration.QuestionId;
import ee.tenman.mmse.domain.enumeration.QuestionType;

import java.util.Collection;
import java.util.List;

public class QuestionDTO {
    private String questionText;
    private String image;
    private QuestionId questionId;
    private QuestionType questionType;
    private Collection<?> answerOptions;
    private int maximumScore;
    private boolean orientationToPlace;
    private List<String> instructions;

    // Default constructor
    public QuestionDTO() {
    }

    // Constructor with parameters
    public QuestionDTO(
        String questionText,
        String image,
        QuestionId questionId,
        QuestionType questionType,
        Collection<?> answerOptions,
        int maximumScore,
        boolean orientationToPlace,
        List<String> instructions
    ) {
        this.questionText = questionText;
        this.image = image;
        this.questionId = questionId;
        this.questionType = questionType;
        this.answerOptions = answerOptions;
        this.maximumScore = maximumScore;
        this.orientationToPlace = orientationToPlace;
        this.instructions = instructions;
    }


    public String getQuestionText() {
        return questionText;
    }

    public void setQuestionText(String questionText) {
        this.questionText = questionText;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public QuestionId getQuestionId() {
        return questionId;
    }

    public void setQuestionId(QuestionId questionId) {
        this.questionId = questionId;
    }

    public QuestionType getQuestionType() {
        return questionType;
    }

    public void setQuestionType(QuestionType questionType) {
        this.questionType = questionType;
    }

    public Collection<?> getAnswerOptions() {
        return answerOptions;
    }

    public void setAnswerOptions(Collection<?> answerOptions) {
        this.answerOptions = answerOptions;
    }

    public int getMaximumScore() {
        return maximumScore;
    }

    public void setMaximumScore(int maximumScore) {
        this.maximumScore = maximumScore;
    }

    public boolean isOrientationToPlace() {
        return orientationToPlace;
    }

    public void setOrientationToPlace(boolean orientationToPlace) {
        this.orientationToPlace = orientationToPlace;
    }

    public List<String> getInstructions() {
        return instructions;
    }

    public void setInstructions(List<String> instructions) {
        this.instructions = instructions;
    }
}
