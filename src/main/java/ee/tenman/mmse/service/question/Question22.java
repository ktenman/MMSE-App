package ee.tenman.mmse.service.question;

import ee.tenman.mmse.domain.UserAnswer;
import ee.tenman.mmse.domain.enumeration.QuestionId;
import ee.tenman.mmse.domain.enumeration.QuestionType;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class Question22 implements Question {

    private static final String QUESTION_TEXT = "22. Copy the drawing in the gray area below";
    private static final QuestionId QUESTION_ID = QuestionId.QUESTION_22;

    @Override
    public String getQuestionText() {
        return QUESTION_TEXT;
    }

    @Override
    public String getImage() {
        return convertImageToBase64("images/intersecting_pentagons.png");
    }

    @Override
    public QuestionId getQuestionId() {
        return QUESTION_ID;
    }

    @Override
    public QuestionType getQuestionType() {
        return QuestionType.DRAWING;
    }

    @Override
    public int getScore(UserAnswer userAnswer) {
        return 0;
    }

    @Override
    public String getDolphinPrompt(String input) {
        return "Answer only yes/no if this image has: All 10 angles are present. Two 5-sided figures intersect. The " +
            "intersection forms a 4-sided figure. The 4-sided figure at the intersection is roughly a rhombus or " +
            "diamond shape (it does not have to be perfect).";
    }

    @Override
    public List<String> getInstructions() {
        return List.of(
            "Use your finger or mouse to draw lines. Take your time and copy carefully.",
            "To fix mistakes, use the \"Eraser\" button. To erase, use the \"Clear\" button.",
            "Use the \"Save Drawing\" button when finished, then proceed to the next task."
        );
    }

}
