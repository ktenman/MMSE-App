package ee.tenman.mmse.service.question;

import ee.tenman.mmse.domain.UserAnswer;
import ee.tenman.mmse.domain.enumeration.AnswerOption;
import ee.tenman.mmse.domain.enumeration.QuestionId;
import ee.tenman.mmse.domain.enumeration.QuestionType;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@Component
public class Question8 implements Question {

    private static final String QUESTION_TEXT =  "What is the name of the third object?";
    private final QuestionId questionId = QuestionId.QUESTION_8;

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
        return this.questionId;
    }

    @Override
    public QuestionType getQuestionType() {
        return QuestionType.MULTIPLE_CHOICE;
    }

    @Override
    public List<String> getAnswerOptions() {
        List<AnswerOption> answerOptions = Arrays.asList(AnswerOption.BALL, AnswerOption.CAR, AnswerOption.MAN, AnswerOption.TREE);
        Collections.shuffle(answerOptions);
        return answerOptions.stream().map(Enum::name).collect(Collectors.toList());
    }

    @Override
    public int getScore(UserAnswer userAnswer) {
        return AnswerOption.valueOf(userAnswer.getAnswerText()) == AnswerOption.MAN ? 1 : 0;
    }
}
