package ee.tenman.mmse.service.question;

import static org.assertj.core.api.Assertions.assertThat;

import ee.tenman.mmse.domain.UserAnswer;
import ee.tenman.mmse.domain.enumeration.QuestionId;
import java.time.LocalDateTime;
import java.time.Month;
import java.time.ZoneOffset;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;

class Question3Test {

    private Question3 question3;
    private UserAnswer userAnswer;

    @BeforeEach
    void setUp() {
        question3 = new Question3();
        userAnswer = new UserAnswer();
        userAnswer.setQuestionId(QuestionId.QUESTION_3);
        LocalDateTime localDateTime = LocalDateTime.of(2023, 1, 1, 0, 0);
        userAnswer.setCreatedAt(localDateTime.toInstant(ZoneOffset.UTC));
    }

    @ParameterizedTest
    @EnumSource(value = Month.class, mode = EnumSource.Mode.INCLUDE, names = { "JANUARY" })
    void testIsAnswerCorrect_whenTrue(Month month) {
        userAnswer.setAnswerText(month.name());
        assertThat(question3.isAnswerCorrect(userAnswer)).isTrue();
    }

    @ParameterizedTest
    @EnumSource(value = Month.class, mode = EnumSource.Mode.EXCLUDE, names = { "JANUARY" })
    void testIsAnswerCorrect_whenFalse(Month month) {
        userAnswer.setAnswerText(month.name());
        assertThat(question3.isAnswerCorrect(userAnswer)).isFalse();
    }

    @Test
    void testGetScore() {
        assertThat(question3.getScore()).isEqualTo(1);
    }

    @Test
    void testGetQuestionText() {
        assertThat(question3.getQuestionText()).isEqualTo("What is the current month?");
    }

    @Test
    void testGetQuestionId() {
        assertThat(question3.getQuestionId()).isEqualTo(QuestionId.QUESTION_3);
    }
}
