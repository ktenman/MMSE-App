package ee.tenman.mmse.service.question;

import ee.tenman.mmse.domain.UserAnswer;
import ee.tenman.mmse.domain.enumeration.QuestionId;
import ee.tenman.mmse.domain.enumeration.QuestionType;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Component;

import java.time.Clock;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.time.format.TextStyle;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Locale;

@Component
public class Question2 implements Question {

    private static final String QUESTION_TEXT = "What is the current date?";
    private static final QuestionId QUESTION_ID = QuestionId.QUESTION_2;

    @Resource
    private Clock clock;

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
        return this.QUESTION_ID;
    }

    @Override
    public QuestionType getQuestionType() {
        return QuestionType.MULTIPLE_CHOICE;
    }

    @Override
    public List<String> getAnswerOptions() {
        // Get today's date
        LocalDate today = LocalDate.now(clock);

        // Format it as a string in the format '25th May 2023'
        String formattedToday = formatToOrdinalDate(today);

        // Generate three incorrect dates
        List<String> incorrectDates = Arrays.asList(
            formatToOrdinalDate(today.minus(1, ChronoUnit.MONTHS)),
            formatToOrdinalDate(today.plus(1, ChronoUnit.MONTHS)),
            formatToOrdinalDate(today.minus(1, ChronoUnit.MONTHS).plus(1, ChronoUnit.DAYS))
        );

        // Combine correct and incorrect dates into one list
        List<String> options = new ArrayList<>(incorrectDates);
        options.add(formattedToday);

        // Randomly shuffle the options
        Collections.shuffle(options);

        return options;
    }

    @Override
    public int getScore(UserAnswer userAnswer) {
        LocalDate localDate = userAnswer.getCreatedAt().atZone(ZoneId.systemDefault()).toLocalDate();
        LocalDate userAnswerDate;
        try {
            String userAnswerText = userAnswer.getAnswerText().replaceAll("(st|nd|rd|th)", "");
            userAnswerDate = LocalDate.parse(userAnswerText, DateTimeFormatter.ofPattern("d MMMM yyyy", Locale.ENGLISH));
        } catch (DateTimeParseException e) {
            return 0;
        }
        return localDate.equals(userAnswerDate) ? 1 : 0;
    }

    private String formatToOrdinalDate(LocalDate date) {
        int day = date.getDayOfMonth();
        String dayWithSuffix = day + getDayOfMonthSuffix(day);
        return dayWithSuffix + " " + date.getMonth().getDisplayName(TextStyle.FULL, Locale.ENGLISH) + " " + date.getYear();
    }

    String getDayOfMonthSuffix(final int n) {
        if (n < 1 || n > 31) throw new IllegalArgumentException("Illegal day of month");
        if (n >= 11 && n <= 13) return "th";
        return switch (n % 10) {
            case 1 -> "st";
            case 2 -> "nd";
            case 3 -> "rd";
            default -> "th";
        };
    }
}
