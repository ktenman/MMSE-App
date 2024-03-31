package ee.tenman.mmse.service.question;

import ee.tenman.mmse.domain.UserAnswer;
import ee.tenman.mmse.domain.enumeration.QuestionId;
import ee.tenman.mmse.domain.enumeration.QuestionType;
import ee.tenman.mmse.service.external.prediction.PredictionService;
import jakarta.annotation.Resource;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Component
public class Question14 implements Question {

    private static final Logger log = LoggerFactory.getLogger(Question14.class);

    private static final QuestionId QUESTION_ID = QuestionId.QUESTION_14;
    private static final String QUESTION_TEXT = "14. Write a complete sentence about something you do every day.";

    @Resource
    private PredictionService predictionService;

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
    public int getScore(UserAnswer userAnswer) {
        if (StringUtils.isBlank(userAnswer.getAnswerText())) {
            log.debug("Received blank answer.");
            return 0;
        }

        return predictionService.isGrammaticallyCorrect(userAnswer.getAnswerText()) ? 1 : 0;
    }


}
