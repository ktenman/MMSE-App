package ee.tenman.mmse.service.question;

import ee.tenman.mmse.domain.UserAnswer;
import ee.tenman.mmse.domain.enumeration.QuestionId;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.Clock;
import java.time.DateTimeException;
import java.time.LocalDateTime;
import java.time.Month;
import java.time.ZoneOffset;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static java.time.ZoneOffset.UTC;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.catchThrowable;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class Question5Test {

    @Mock
    private Clock clock;

    @InjectMocks
    private Question5 question5;

    private UserAnswer userAnswer;

    @BeforeEach
    void setUp() {
        userAnswer = new UserAnswer();
        userAnswer.setQuestionId(QuestionId.QUESTION_5);
    }

    @ParameterizedTest
    @ValueSource(ints = {1, 2, 12})
    void testIsAnswerCorrect_whenWinter(int month) {
        when(clock.instant()).thenReturn(LocalDateTime.of(2023, month, 1, 0, 0)
            .toInstant(UTC));
        when(clock.getZone()).thenReturn(UTC);
        question5.getAnswerOptions(null);
        LocalDateTime localDateTime = LocalDateTime.of(2023, month, 1, 0, 0);
        userAnswer.setCreatedAt(localDateTime.toInstant(ZoneOffset.UTC));
        userAnswer.setAnswerText("WINTER");
        assertThat(question5.getScore(userAnswer)).isOne();
    }

    @ParameterizedTest
    @ValueSource(ints = {3, 4, 5})
    void testIsAnswerCorrect_whenSpring(int month) {
        when(clock.instant()).thenReturn(LocalDateTime.of(2023, month, 1, 0, 0)
            .toInstant(UTC));
        when(clock.getZone()).thenReturn(UTC);
        question5.getAnswerOptions(null);
        LocalDateTime localDateTime = LocalDateTime.of(2023, month, 1, 0, 0);
        userAnswer.setCreatedAt(localDateTime.toInstant(ZoneOffset.UTC));
        userAnswer.setAnswerText("SPRING");
        assertThat(question5.getScore(userAnswer)).isOne();
    }

    @ParameterizedTest
    @ValueSource(ints = {6, 7, 8})
    void testIsAnswerCorrect_whenSummer(int month) {
        when(clock.instant()).thenReturn(LocalDateTime.of(2023, month, 1, 0, 0)
            .toInstant(UTC));
        when(clock.getZone()).thenReturn(UTC);
        question5.getAnswerOptions(null);
        LocalDateTime localDateTime = LocalDateTime.of(2023, month, 1, 0, 0);
        userAnswer.setCreatedAt(localDateTime.toInstant(ZoneOffset.UTC));
        userAnswer.setAnswerText("SUMMER");
        assertThat(question5.getScore(userAnswer)).isOne();
    }

    @ParameterizedTest
    @ValueSource(ints = {9, 10, 11})
    void testIsAnswerCorrect_whenAutumn(int month) {
        when(clock.instant()).thenReturn(LocalDateTime.of(2023, month, 1, 0, 0)
            .toInstant(UTC));
        when(clock.getZone()).thenReturn(UTC);
        question5.getAnswerOptions(null);
        LocalDateTime localDateTime = LocalDateTime.of(2023, month, 1, 0, 0);
        userAnswer.setCreatedAt(localDateTime.toInstant(ZoneOffset.UTC));
        userAnswer.setAnswerText("AUTUMN");
        assertThat(question5.getScore(userAnswer)).isOne();
    }

    @Test
    void testIsAnswerCorrect_whenIncorrect() {
        when(clock.instant()).thenReturn(LocalDateTime.of(2023, 6, 1, 0, 0)
            .toInstant(UTC));
        when(clock.getZone()).thenReturn(UTC);
        question5.getAnswerOptions(null);
        LocalDateTime localDateTime = LocalDateTime.of(2023, 10, 1, 0, 0);
        userAnswer.setCreatedAt(localDateTime.toInstant(ZoneOffset.UTC));
        userAnswer.setAnswerText("WINTER");
        assertThat(question5.getScore(userAnswer)).isZero();
    }

    @Test
    void testGetQuestionText() {
        assertThat(question5.getQuestionText()).isEqualTo("5. What is the current season?");
    }

    @Test
    void testGetQuestionId() {
        assertThat(question5.getQuestionId()).isEqualTo(QuestionId.QUESTION_5);
    }

    @Test
    void testGetAnswerOptions() {
        when(clock.instant()).thenReturn(LocalDateTime.of(2023, 6, 1, 0, 0)
            .toInstant(UTC));
        when(clock.getZone()).thenReturn(UTC);
        question5.getAnswerOptions(null);
        Set<String> expectedSeasons = new HashSet<>(Arrays.asList("SPRING", "SUMMER", "AUTUMN", "WINTER"));
        List<String> answerOptions = question5.getAnswerOptions(null);
        Set<String> actualSeasons = new HashSet<>(answerOptions);
        assertThat(actualSeasons).isEqualTo(expectedSeasons);
    }

    @Test
    void testGetSeasonFromMonth_shouldThrowException() {
        Throwable thrown = catchThrowable(() -> question5.getSeasonFromMonth(Month.of(13)));

        assertThat(thrown).isInstanceOf(DateTimeException.class)
            .hasMessage("Invalid value for MonthOfYear: 13");
    }

    @Test
    void testGetImage() {
        assertThat(question5.getImage()).isNull();
    }
}
