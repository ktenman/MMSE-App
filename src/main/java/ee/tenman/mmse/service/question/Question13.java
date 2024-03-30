package ee.tenman.mmse.service.question;

import ee.tenman.mmse.domain.UserAnswer;
import ee.tenman.mmse.domain.enumeration.QuestionId;
import ee.tenman.mmse.domain.enumeration.QuestionType;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class Question13 implements Question {

    private static final Logger log = LoggerFactory.getLogger(Question13.class);

    private static final QuestionId QUESTION_ID = QuestionId.QUESTION_13;
    private static final String QUESTION_TEXT = "13. Please type the word 'agree' into the text box to confirm that you have read and understood this command.";
    private static final String EXPECTED_CONFIRMATION = "agree";


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
        return QuestionType.TEXT_INPUT;
    }

    @Override
    public List<String> getAnswerOptions() {
        return List.of();
    }

    @Override
    public int getScore(UserAnswer userAnswer) {
        String answerText = userAnswer.getAnswerText();

        if (StringUtils.isBlank(answerText)) {
            log.debug("Received blank answer.");
            return 0;
        }

        answerText = StringUtils.trim(answerText).toLowerCase();

        // Check if the user typed the expected confirmation word
        if (EXPECTED_CONFIRMATION.equals(answerText)) {
            log.debug("User typed the expected confirmation word.");
            return 1;
        } else {
            log.debug("User did not type the expected confirmation word.");
            return 0;
        }
    }

}
