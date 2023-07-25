package ee.tenman.mmse.service.question;

import ee.tenman.mmse.domain.UserAnswer;
import ee.tenman.mmse.domain.enumeration.AnswerOption;
import ee.tenman.mmse.domain.enumeration.QuestionId;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@Component
public class Question6 implements Question {

    private static final String QUESTION_TEXT = "Please look at these three images. What is the name of the first object?";
    private final QuestionId questionId = QuestionId.QUESTION_6;

    @Override
    public String getQuestionText() {
        return QUESTION_TEXT;
    }

    @Override
    public String getImage() {
        return convertImageToBase64("images/ball-car-man.png");
    }

    @Override
    public boolean isAnswerCorrect(UserAnswer userAnswer) {
        return AnswerOption.valueOf(userAnswer.getAnswerText()) == AnswerOption.BALL;
    }

    @Override
    public QuestionId getQuestionId() {
        return this.questionId;
    }

    @Override
    public List<String> getAnswerOptions() {
        List<AnswerOption> answerOptions = Arrays.asList(AnswerOption.BALL, AnswerOption.CAR, AnswerOption.MAN);
        Collections.shuffle(answerOptions);
        return answerOptions.stream().map(Enum::name).collect(Collectors.toList());
    }
}
