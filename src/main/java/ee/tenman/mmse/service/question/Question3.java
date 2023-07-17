package ee.tenman.mmse.service.question;

import ee.tenman.mmse.domain.UserAnswer;
import ee.tenman.mmse.domain.enumeration.QuestionId;

import java.time.Month;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.List;

public class Question3 implements Question {
    private final QuestionId questionId = QuestionId.QUESTION_3;
    private final String questionText = "What is the current month?";

    @Override
    public String getQuestionText() {
        return this.questionText;
    }

    @Override
    public String getImage() {
        return null;
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

    @Override
    public List<String> getAnswerOptions() {
        return null;
    }
}
