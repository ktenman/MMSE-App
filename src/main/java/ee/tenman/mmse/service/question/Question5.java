package ee.tenman.mmse.service.question;

import ee.tenman.mmse.domain.UserAnswer;
import ee.tenman.mmse.domain.enumeration.QuestionId;
import ee.tenman.mmse.domain.enumeration.QuestionType;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Component;

import java.time.Clock;
import java.time.Month;
import java.time.ZonedDateTime;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Set;

@Component
public class Question5 implements Question {

    private static final String QUESTION_TEXT = "5. What is the current season?";
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

    private static final QuestionId QUESTION_ID = QuestionId.QUESTION_5;

    @Resource
    private Clock clock;

    private String questionSeason;

    @Override
    public String getQuestionText() {
        return QUESTION_TEXT;
    }

    @Override
    public QuestionId getQuestionId() {
        return QUESTION_ID;
    }

    @Override
    public QuestionType getQuestionType() {
        return QuestionType.MULTIPLE_CHOICE;
    }

    @Override
    public List<String> getAnswerOptions(Long testEntityId) {
        // Set the question season when generating options
        ZonedDateTime now = ZonedDateTime.now(clock);
        Month currentMonth = now.getMonth();
        this.questionSeason = getSeasonFromMonth(currentMonth);

        List<String> answerOptions = Arrays.asList(SPRING, SUMMER, AUTUMN, WINTER);
        Collections.shuffle(answerOptions);
        return answerOptions;
    }

    @Override
    public int getScore(UserAnswer userAnswer) {
        return this.questionSeason.equalsIgnoreCase(userAnswer.getAnswerText()) ? 1 : 0;
    }

    @Override
    public String getCorrectAnswer(Long testEntityId) {
        if (this.questionSeason == null) {
            throw new IllegalStateException("Question season has not been set. Ensure getAnswerOptions is called before getCorrectAnswer.");
        }
        return this.questionSeason;
    }

    String getSeasonFromMonth(Month month) {
        return SEASONS.entrySet().stream()
            .filter(entry -> entry.getValue().contains(month))
            .map(Map.Entry::getKey)
            .findFirst()
            .orElseThrow(() -> new IllegalArgumentException("Month " + month + " is not mapped to a season"));
    }
}
