package ee.tenman.mmse.service.question;

import ee.tenman.mmse.domain.UserAnswer;
import ee.tenman.mmse.domain.enumeration.AnswerOption;
import ee.tenman.mmse.domain.enumeration.QuestionId;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

public class Question7 implements Question {
    private final QuestionId questionId = QuestionId.QUESTION_7;
    private final int score = 1;
    private final String questionText = "What is the name of the second object?";

    @Override
    public String getQuestionText() {
        return this.questionText;
    }

    @Override
    public String getImage() {
        return convertImageToBase64("images/ball-car-man.png");
    }

    @Override
    public int getScore() {
        return this.score;
    }

    @Override
    public boolean isAnswerCorrect(UserAnswer userAnswer) {
        return AnswerOption.valueOf(userAnswer.getAnswerText()) == AnswerOption.CAR;
    }

    @Override
    public QuestionId getQuestionId() {
        return this.questionId;
    }

    @Override
    public List<String> getAnswerOptions() {
        List<AnswerOption> answerOptions = Arrays.asList(AnswerOption.BALL, AnswerOption.CAR, AnswerOption.MAN);
        Collections.shuffle(answerOptions);
        return answerOptions.stream()
            .map(Enum::name)
            .collect(Collectors.toList());
    }

}
