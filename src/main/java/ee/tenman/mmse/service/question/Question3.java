package ee.tenman.mmse.service.question;

import ee.tenman.mmse.domain.UserAnswer;
import ee.tenman.mmse.domain.enumeration.QuestionId;

import java.time.Month;
import java.time.ZoneId;
import java.time.ZonedDateTime;

public class Question3 implements Question {
    private final QuestionId questionId = QuestionId.QUESTION_3;
    private final int score = 1;
    private final String questionText = "What is the current month?";

    @Override
    public String getQuestionText() {
        return this.questionText;
    }

    @Override
    public int getScore() {
        return this.score;
    }

    @Override
    public boolean isAnswerCorrect(UserAnswer userAnswer) {
        ZonedDateTime zonedDateTime = userAnswer.getCreatedAt().atZone(ZoneId.systemDefault());
        String month = Month.from(zonedDateTime).name();
        return month.equalsIgnoreCase(userAnswer.getAnswerText());
    }

    @Override
    public QuestionId getQuestionId() {
        return this.questionId;
    }
}
