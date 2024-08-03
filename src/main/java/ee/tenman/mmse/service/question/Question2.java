package ee.tenman.mmse.service.question;

import ee.tenman.mmse.domain.UserAnswer;
import ee.tenman.mmse.domain.enumeration.QuestionId;
import ee.tenman.mmse.domain.enumeration.QuestionType;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Component;

import java.time.Clock;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.time.format.TextStyle;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Locale;
import java.util.regex.Pattern;

@Component
public class Question2 implements Question {

    private static final String QUESTION_TEXT = "2. What is the current date?";
    private static final QuestionId QUESTION_ID = QuestionId.QUESTION_2;
    private static final Pattern ORDINAL_SUFFIX_PATTERN = Pattern.compile("(\\d+)(st|nd|rd|th)");

    @Resource
    private Clock clock;

    private LocalDate questionDate;

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
        // Set today's date
        this.questionDate = LocalDate.now(clock);

        // Format it as a string in the format '25th May 2023'
        String formattedToday = formatToOrdinalDate(this.questionDate);

        // Generate three incorrect dates
        List<String> incorrectDates = Arrays.asList(
            formatToOrdinalDate(this.questionDate.minus(1, ChronoUnit.MONTHS)),
            formatToOrdinalDate(this.questionDate.plus(1, ChronoUnit.MONTHS)),
            formatToOrdinalDate(this.questionDate.minus(1, ChronoUnit.MONTHS).plus(1, ChronoUnit.DAYS))
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
        LocalDate userAnswerDate;
        try {
            // Remove ordinal suffixes before parsing the date
            String userAnswerText = ORDINAL_SUFFIX_PATTERN.matcher(userAnswer.getAnswerText()).replaceAll("$1");
            userAnswerDate = LocalDate.parse(userAnswerText, DateTimeFormatter.ofPattern("d MMMM yyyy", Locale.ENGLISH));
        } catch (DateTimeParseException e) {
            throw new IllegalArgumentException(String.format("Invalid date format: %s", userAnswer.getAnswerText()), e);
        }
        return questionDate.equals(userAnswerDate) ? 1 : 0;
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

    @Override
    public String getCorrectAnswer(Long testEntityId) {
        if (this.questionDate == null) {
            throw new IllegalStateException("Question date has not been set. Ensure getAnswerOptions is called before getCorrectAnswer.");
        }
        return formatToOrdinalDate(this.questionDate);
    }
}
