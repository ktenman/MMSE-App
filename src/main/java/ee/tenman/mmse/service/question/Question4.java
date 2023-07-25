package ee.tenman.mmse.service.question;

import ee.tenman.mmse.domain.UserAnswer;
import ee.tenman.mmse.domain.enumeration.QuestionId;

import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.List;

public class Question4 implements Question {

    private static final String QUESTION_TEXT = "What is the current year?";
    private final QuestionId questionId = QuestionId.QUESTION_4;

    @Override
    public String getQuestionText() {
        return QUESTION_TEXT;
    }

    @Override
    public String getImage() {
        return null;
    }

    @Override
    public boolean isAnswerCorrect(UserAnswer userAnswer) {
        ZonedDateTime zonedDateTime = userAnswer.getCreatedAt().atZone(ZoneId.systemDefault());
        int year = zonedDateTime.getYear();
        return String.valueOf(year).equals(userAnswer.getAnswerText());
    }

    @Override
    public QuestionId getQuestionId() {
        return this.questionId;
    }

    @Override
    public List<String> getAnswerOptions() {
        return null;
    }
}
