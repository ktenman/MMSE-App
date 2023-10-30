package ee.tenman.mmse.service.question;

import ee.tenman.mmse.domain.UserAnswer;
import ee.tenman.mmse.domain.enumeration.AnswerOption;
import ee.tenman.mmse.domain.enumeration.QuestionId;
import ee.tenman.mmse.domain.enumeration.QuestionType;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

@Component
public class Question8 implements Question {

    private static final String QUESTION_TEXT = "What is the name of the third object?";
    private static final QuestionId QUESTION_ID = QuestionId.QUESTION_8;

    @Override
    public String getQuestionText() {
        return QUESTION_TEXT;
    }

    @Override
    public String getImage() {
        return convertImageToBase64("images/ball-car-man.png");
    }

    @Override
    public QuestionId getQuestionId() {
        return this.QUESTION_ID;
    }

    @Override
    public QuestionType getQuestionType() {
        return QuestionType.MULTIPLE_CHOICE;
    }

    @Override
    public List<AnswerOption> getAnswerOptions() {
        List<AnswerOption> answerOptions = Arrays.asList(AnswerOption.BALL, AnswerOption.CAR, AnswerOption.MAN, AnswerOption.TREE);
        Collections.shuffle(answerOptions);
        return answerOptions;
    }

    @Override
    public int getScore(UserAnswer userAnswer) {
        return AnswerOption.valueOf(userAnswer.getAnswerText()) == AnswerOption.MAN ? 1 : 0;
    }
}
