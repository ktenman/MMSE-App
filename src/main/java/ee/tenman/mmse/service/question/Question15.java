package ee.tenman.mmse.service.question;

import ee.tenman.mmse.domain.UserAnswer;
import ee.tenman.mmse.domain.enumeration.QuestionId;
import ee.tenman.mmse.domain.enumeration.QuestionType;
import org.springframework.stereotype.Component;

import java.time.DayOfWeek;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Component
public class Question15 implements Question {

    private static final String QUESTION_TEXT = "15. What is the current day of the week?";
    private static final QuestionId QUESTION_ID = QuestionId.QUESTION_15;

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
    public List<String> getAnswerOptions() {
        // Get the current day of the week
        ZonedDateTime zonedDateTime = ZonedDateTime.now(ZoneId.systemDefault());
        String currentDayOfWeek = DayOfWeek.from(zonedDateTime).name();

        // Prepare a list of three random days excluding the current day
        List<String> daysOfWeek = Stream.of(DayOfWeek.values()).map(DayOfWeek::name).collect(Collectors.toList());
        daysOfWeek.remove(currentDayOfWeek);
        Collections.shuffle(daysOfWeek);
        List<String> answerOptions = daysOfWeek.subList(0, 3);

        // Add the current day to the list and shuffle again
        answerOptions.add(currentDayOfWeek);
        Collections.shuffle(answerOptions);

        return answerOptions;
    }

    @Override
    public int getScore(UserAnswer userAnswer) {
        ZonedDateTime zonedDateTime = userAnswer.getCreatedAt().atZone(ZoneId.systemDefault());
        String dayOfWeek = DayOfWeek.from(zonedDateTime).name();
        return dayOfWeek.equalsIgnoreCase(userAnswer.getAnswerText()) ? 1 : 0;
    }
}
