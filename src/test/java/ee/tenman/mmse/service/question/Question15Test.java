package ee.tenman.mmse.service.question;

import ee.tenman.mmse.domain.UserAnswer;
import ee.tenman.mmse.domain.enumeration.QuestionId;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.ZoneId;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class Question15Test {

    private Question15 question15;
    private UserAnswer userAnswer;

    @BeforeEach
    void setUp() {
        question15 = new Question15();
        userAnswer = new UserAnswer();
        userAnswer.setQuestionId(QuestionId.QUESTION_1);
        LocalDateTime localDateTime = LocalDateTime.of(LocalDate.of(2023, 1, 1), LocalTime.MIDNIGHT);
        userAnswer.setCreatedAt(localDateTime.toInstant(ZoneOffset.UTC));
    }

    @ParameterizedTest
    @EnumSource(value = DayOfWeek.class, mode = EnumSource.Mode.INCLUDE, names = {"SUNDAY"})
    void testGetScore_whenCorrect(DayOfWeek dayOfWeek) {
        userAnswer.setAnswerText(dayOfWeek.name());

        assertThat(question15.getScore(userAnswer)).isOne();
    }

    @ParameterizedTest
    @EnumSource(value = DayOfWeek.class, mode = EnumSource.Mode.EXCLUDE, names = {"SUNDAY"})
    void testGetScore_whenIncorrect(DayOfWeek dayOfWeek) {
        userAnswer.setAnswerText(dayOfWeek.name());

        assertThat(question15.getScore(userAnswer)).isZero();
    }

    @Test
    void testGetQuestionText() {
        assertThat(question15.getQuestionText()).isEqualTo("15. What is the current day of the week?");
    }

    @Test
    void testGetQuestionId() {
        assertThat(question15.getQuestionId()).isEqualTo(QuestionId.QUESTION_15);
    }

    @Test
    void testGetAnswerOptionsSize() {
        List<String> answerOptions = question15.getAnswerOptions();
        assertThat(answerOptions).hasSize(4);
    }

    @Test
    void testGetAnswerOptionsContainsCorrectDay() {
        String currentDayOfWeek = ZonedDateTime.now(ZoneId.systemDefault()).getDayOfWeek().name();

        List<String> answerOptions = question15.getAnswerOptions();

        assertThat(answerOptions).contains(currentDayOfWeek);
    }

    @Test
    void testGetAnswerOptionsNoDuplicates() {
        List<String> answerOptions = question15.getAnswerOptions();
        assertThat(answerOptions.stream().distinct().count()).isEqualTo(answerOptions.size());
    }
}
