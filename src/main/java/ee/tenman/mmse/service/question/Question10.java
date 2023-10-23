package ee.tenman.mmse.service.question;

import ee.tenman.mmse.domain.UserAnswer;
import ee.tenman.mmse.domain.enumeration.QuestionId;
import ee.tenman.mmse.domain.enumeration.QuestionType;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class Question10 implements Question {

    private static final String QUESTION_TEXT = "Please recall the three objects that were previously mentioned.";
    private final QuestionId questionId = QuestionId.QUESTION_10;
    private static final List<String> CORRECT_ANSWERS = List.of("man", "car", "ball");

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
        return this.questionId;
    }

    @Override
    public QuestionType getQuestionType() {
        return QuestionType.TEXT_INPUT;
    }

    @Override
    public List<InputField> getAnswerOptions() {
        return List.of();
    }

    @Override
    public int getScore(UserAnswer userAnswer) {
        int result = 0;
        for (String correctAnswer : CORRECT_ANSWERS) {
            if (StringUtils.containsIgnoreCase(userAnswer.getAnswerText(), correctAnswer)) {
                result += 1;
            }
        }

        return result;
    }
}
