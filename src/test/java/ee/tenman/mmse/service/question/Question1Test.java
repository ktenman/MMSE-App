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
import java.time.ZoneOffset;

import static org.assertj.core.api.Assertions.assertThat;

class Question1Test {

    private Question1 question1;
    private UserAnswer userAnswer;

    @BeforeEach
    void setUp() {
        question1 = new Question1();
        userAnswer = new UserAnswer();
        userAnswer.setQuestionId(QuestionId.QUESTION_1);
        LocalDateTime localDateTime = LocalDateTime.of(LocalDate.of(2023, 1, 1), LocalTime.MIDNIGHT);
        userAnswer.setCreatedAt(localDateTime.toInstant(ZoneOffset.UTC));
    }

    @ParameterizedTest
    @EnumSource(value = DayOfWeek.class, mode = EnumSource.Mode.INCLUDE, names = {"SUNDAY"})
    void testIsAnswerCorrect_whenTrue(DayOfWeek dayOfWeek) {
        userAnswer.setAnswerText(dayOfWeek.name());

        assertThat(question1.isAnswerCorrect(userAnswer)).isTrue();
    }

    @ParameterizedTest
    @EnumSource(value = DayOfWeek.class, mode = EnumSource.Mode.EXCLUDE, names = {"SUNDAY"})
    void testIsAnswerCorrect_whenFalse(DayOfWeek dayOfWeek) {
        userAnswer.setAnswerText(dayOfWeek.name());

        assertThat(question1.isAnswerCorrect(userAnswer)).isFalse();
    }

    @Test
    void testGetScore() {
        assertThat(question1.getScore()).isEqualTo(1);
    }

    @Test
    void testGetQuestionText() {
        assertThat(question1.getQuestionText()).isEqualTo("What is the current day of the week?");
    }

    @Test
    void testGetQuestionId() {
        assertThat(question1.getQuestionId()).isEqualTo(QuestionId.QUESTION_1);
    }
}
