package ee.tenman.mmse.service.question;

import ee.tenman.mmse.domain.UserAnswer;
import ee.tenman.mmse.domain.enumeration.QuestionId;
import ee.tenman.mmse.domain.enumeration.QuestionType;
import org.springframework.stereotype.Component;

import java.time.Month;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Set;

@Component
public class Question5 implements Question {

    private static final String QUESTION_TEXT = "What is the current season?";
    private static final String SPRING = "SPRING";
    private static final String SUMMER = "SUMMER";
    private static final String AUTUMN = "AUTUMN";
    private static final String WINTER = "WINTER";
    private static final Map<String, Set<Month>> SEASONS = Map.of(
        WINTER, Set.of(Month.DECEMBER, Month.JANUARY, Month.FEBRUARY),
        SPRING, Set.of(Month.MARCH, Month.APRIL, Month.MAY),
        SUMMER, Set.of(Month.JUNE, Month.JULY, Month.AUGUST),
        AUTUMN, Set.of(Month.SEPTEMBER, Month.OCTOBER, Month.NOVEMBER)
    );

    private final QuestionId questionId = QuestionId.QUESTION_5;

    @Override
    public String getQuestionText() {
        return QUESTION_TEXT;
    }

    @Override
    public String getImage() {
        return null;
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
        List<String> answerOptions = Arrays.asList(SPRING, SUMMER, AUTUMN, WINTER);
        Collections.shuffle(answerOptions);
        return answerOptions;
    }

    @Override
    public int getScore(UserAnswer userAnswer) {
        ZonedDateTime zonedDateTime = userAnswer.getCreatedAt().atZone(ZoneId.systemDefault());
        Month month = Month.from(zonedDateTime);
        String season = getSeasonFromMonth(month);

        return season.equalsIgnoreCase(userAnswer.getAnswerText()) ? 1 : 0;
    }

    String getSeasonFromMonth(Month month) {
        return SEASONS.entrySet().stream()
            .filter(entry -> entry.getValue().contains(month))
            .map(Map.Entry::getKey)
            .findFirst()
            .orElseThrow(() -> new IllegalArgumentException("Month " + month + " is not mapped to a season"));
    }
}
