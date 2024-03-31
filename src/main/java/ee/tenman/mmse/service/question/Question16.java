package ee.tenman.mmse.service.question;

import ee.tenman.mmse.domain.UserAnswer;
import ee.tenman.mmse.domain.enumeration.QuestionId;
import ee.tenman.mmse.domain.enumeration.QuestionType;
import org.springframework.stereotype.Component;

@Component
public class Question16 implements Question {

    private static final String QUESTION_TEXT = "16. Select the 'Paper' element, then tap 'Fold Paper in Half' to fold it. After that, drag and drop the folded paper onto the 'Floor' area.";
    private static final QuestionId QUESTION_ID = QuestionId.QUESTION_16;

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
        int score = 0;
        if (userAnswer.getAnswerText() != null) {
            String[] actions = userAnswer.getAnswerText().split(",");
            if (actions.length == 3) {
                if (Boolean.parseBoolean(actions[0])) {
                    score += 1; // Unlocked the slider
                }
                if (Boolean.parseBoolean(actions[1])) {
                    score += 1; // Folded the paper
                }
                if (Boolean.parseBoolean(actions[2])) {
                    score += 1; // Put the paper on the floor
                }
            }
        }
        return score;
    }

    @Override
    public int getMaximumScore() {
        return 3;
    }
}
