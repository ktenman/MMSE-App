package ee.tenman.mmse.service.question;
import ee.tenman.mmse.domain.UserAnswer;
import ee.tenman.mmse.domain.enumeration.QuestionId;

import java.time.Month;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.List;

public class Question5 implements Question {
    private final QuestionId questionId = QuestionId.QUESTION_5;
    private final int score = 1;
    private final String questionText = "What is the current season?";

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
        Month month = Month.from(zonedDateTime);

        String season;
        switch (month) {
            case DECEMBER:
            case JANUARY:
            case FEBRUARY:
                season = "WINTER";
                break;
            case MARCH:
            case APRIL:
            case MAY:
                season = "SPRING";
                break;
            case JUNE:
            case JULY:
            case AUGUST:
                season = "SUMMER";
                break;
            case SEPTEMBER:
            case OCTOBER:
            case NOVEMBER:
                season = "AUTUMN";
                break;
            default:
                throw new IllegalStateException("Unexpected value: " + month);
        }

        return season.equalsIgnoreCase(userAnswer.getAnswerText());
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

