package ee.tenman.mmse.service.question;

import ee.tenman.mmse.domain.UserAnswer;
import ee.tenman.mmse.domain.enumeration.QuestionId;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.ValueSource;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.Clock;
import java.time.LocalDateTime;
import java.util.List;

import static java.time.ZoneOffset.UTC;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatExceptionOfType;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class Question2Test {

    @Mock
    private Clock clock;

    @InjectMocks
    private Question2 question2;

    private UserAnswer userAnswer;

    @BeforeEach
    void setUp() {
        userAnswer = new UserAnswer();
        userAnswer.setQuestionId(QuestionId.QUESTION_2);
        LocalDateTime localDateTime = LocalDateTime.of(2023, 1, 1, 0, 0);
        userAnswer.setCreatedAt(localDateTime.toInstant(UTC));
    }

    @Test
    void testIsAnswerCorrect_whenTrue() {
        userAnswer.setAnswerText("1st January 2023");
        assertThat(question2.isAnswerCorrect(userAnswer)).isTrue();
    }

    @Test
    void testIsAnswerCorrect_whenFalse() {
        userAnswer.setAnswerText("2nd January 2023");
        assertThat(question2.isAnswerCorrect(userAnswer)).isFalse();
    }

    @Test
    void testGetScore() {
        assertThat(question2.getScore()).isEqualTo(1);
    }

    @Test
    void testGetQuestionText() {
        assertThat(question2.getQuestionText()).isEqualTo("What is the current date?");
    }

    @Test
    void testGetQuestionId() {
        assertThat(question2.getQuestionId()).isEqualTo(QuestionId.QUESTION_2);
    }

    @Test
    void testGetAnswerOptions() {
        when(clock.instant()).thenReturn(LocalDateTime.of(2023, 1, 1, 0, 0)
            .toInstant(UTC));
        when(clock.getZone()).thenReturn(UTC);

        List<String> answerOptions = question2.getAnswerOptions();
        assertThat(answerOptions).hasSize(4).contains("1st January 2023");
    }

    @ParameterizedTest
    @CsvSource({
        "1, st",
        "2, nd",
        "3, rd",
        "4, th",
        "11, th",
        "12, th",
        "13, th",
        "21, st",
        "22, nd",
        "23, rd",
        "31, st"
    })
    void testGetDayOfMonthSuffix(int day, String expectedSuffix) {
        assertThat(question2.getDayOfMonthSuffix(day)).isEqualTo(expectedSuffix);
    }

    @ParameterizedTest
    @ValueSource(ints = {
        0,
        32
    })
    void testGetDayOfMonthSuffix_ThrowsException(int day) {
        assertThatExceptionOfType(IllegalArgumentException.class).isThrownBy(() -> question2.getDayOfMonthSuffix(day));
    }
}
