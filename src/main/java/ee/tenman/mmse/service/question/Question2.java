package ee.tenman.mmse.service.question;

import ee.tenman.mmse.domain.UserAnswer;
import ee.tenman.mmse.domain.enumeration.QuestionId;

import java.time.LocalDate;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.List;

public class Question2 implements Question {

    private static final String QUESTION_TEXT = "What is the current date?";
    private final QuestionId questionId = QuestionId.QUESTION_2;

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
        LocalDate localDate = userAnswer.getCreatedAt().atZone(ZoneId.systemDefault()).toLocalDate();
        LocalDate userAnswerDate;
        try {
            userAnswerDate = LocalDate.parse(userAnswer.getAnswerText(), DateTimeFormatter.ofPattern("yyyy-MM-dd"));
        } catch (DateTimeParseException e) {
            return false;
        }
        return localDate.equals(userAnswerDate);
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
