package ee.tenman.mmse.service.question;

import ee.tenman.mmse.domain.UserAnswer;
import ee.tenman.mmse.domain.enumeration.QuestionId;

import java.time.DayOfWeek;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.List;

public class Question1 implements Question {
    private final QuestionId questionId = QuestionId.QUESTION_1;
    private final String questionText = "What is the current day of the week?";

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
        String dayOfWeek = DayOfWeek.from(zonedDateTime).name();
        return dayOfWeek.equalsIgnoreCase(userAnswer.getAnswerText());
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
