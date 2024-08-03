package ee.tenman.mmse.service.question;

import ee.tenman.mmse.domain.UserAnswer;
import ee.tenman.mmse.domain.enumeration.QuestionId;
import ee.tenman.mmse.domain.enumeration.QuestionType;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class Question16 implements Question {

    private static final String QUESTION_TEXT = "16. Fold and Drop Paper Task";
    private static final QuestionId QUESTION_ID = QuestionId.QUESTION_16;
    private static final String CORRECT_ANSWER = "true,true,true"; // The correct sequence of actions

    @Override
    public String getQuestionText() {
        return QUESTION_TEXT;
    }

    @Override
    public String getImage() {
        return null;
    }

    @Override
    public QuestionId getQuestionId() {
        return QUESTION_ID;
    }

    @Override
    public QuestionType getQuestionType() {
        return QuestionType.DRAG_AND_DROP;
    }

    @Override
    public int getScore(UserAnswer userAnswer) {
        if (userAnswer.getAnswerText() == null) {
            return 0;
        }

        return userAnswer.getAnswerText().equals(CORRECT_ANSWER) ? getMaximumScore() : 0;
    }

    @Override
    public int getMaximumScore() {
        return 3;
    }

    @Override
    public List<String> getInstructions() {
        return List.of(
            "Select the 'Paper'",
            "Tap/Click the 'Fold Paper in Half' button to fold it",
            "With your mouse/finger, drag and drop the folded paper into the 'Floor' area",
            "When finished, use the 'Next Task' button to proceed"
        );
    }

    @Override
    public String getCorrectAnswer() {
        return CORRECT_ANSWER;
    }
}
