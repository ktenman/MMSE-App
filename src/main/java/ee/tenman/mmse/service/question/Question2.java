package ee.tenman.mmse.service.question;

import ee.tenman.mmse.domain.UserAnswer;
import ee.tenman.mmse.domain.enumeration.QuestionId;

import java.time.ZoneId;
import java.time.ZonedDateTime;

public class Question2 implements Question {
    private final QuestionId questionId = QuestionId.QUESTION_2;
    private final int score = 1;
    private final String questionText = "What is the current date (day of the month)?";

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
        int dayOfMonth = zonedDateTime.getDayOfMonth();
        return String.valueOf(dayOfMonth).equals(userAnswer.getAnswerText());
    }

    @Override
    public QuestionId getQuestionId() {
        return this.questionId;
    }
}
