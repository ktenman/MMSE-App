package ee.tenman.mmse.service.question;

import ee.tenman.mmse.domain.UserAnswer;
import ee.tenman.mmse.domain.enumeration.QuestionId;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.RepeatedTest;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullAndEmptySource;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.Clock;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZoneOffset;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static java.time.ZoneOffset.UTC;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class Question4Test {

    @InjectMocks
    private Question4 question4;
    @Mock
    private Clock clock;
    private UserAnswer userAnswer;

    @BeforeEach
    void setUp() {
        userAnswer = new UserAnswer();
        userAnswer.setQuestionId(QuestionId.QUESTION_4);
        LocalDateTime localDateTime = LocalDateTime.of(2023, 1, 1, 0, 0);
        userAnswer.setCreatedAt(localDateTime.toInstant(ZoneOffset.UTC));
    }

    @Test
    void testIsAnswerCorrect_whenTrue() {
        when(clock.instant()).thenReturn(LocalDateTime.of(2023, 1, 1, 0, 0)
            .toInstant(UTC));
        when(clock.getZone()).thenReturn(UTC);
        question4.getAnswerOptions(null);
        userAnswer.setAnswerText("2023");
        assertThat(question4.getScore(userAnswer)).isOne();
    }

    @ParameterizedTest
    @NullAndEmptySource
    void testIsAnswerCorrect_whenFalse(String answerText) {
        userAnswer.setAnswerText(answerText);
        assertThat(question4.getScore(userAnswer)).isZero();
    }

    @Test
    void testIsAnswerCorrect_whenFalse() {
        userAnswer.setAnswerText("2024");
        assertThat(question4.getScore(userAnswer)).isZero();
    }

    @Test
    void testGetQuestionText() {
        assertThat(question4.getQuestionText()).isEqualTo("4. What is the current year?");
    }

    @Test
    void testGetQuestionId() {
        assertThat(question4.getQuestionId()).isEqualTo(QuestionId.QUESTION_4);
    }

    @Test
    void testGetAnswerOptionsContainsFourYears() {
        when(clock.instant()).thenReturn(Instant.parse("2023-07-25T00:00:00Z"));
        when(clock.getZone()).thenReturn(ZoneId.systemDefault());

        List<String> answerOptions = question4.getAnswerOptions(null);

        assertThat(answerOptions).hasSize(4);
    }

    @Test
    void testGetAnswerOptionsContainsCorrectYears() {
        when(clock.instant()).thenReturn(Instant.parse("2023-07-25T00:00:00Z"));
        when(clock.getZone()).thenReturn(ZoneId.systemDefault());

        List<String> answerOptions = question4.getAnswerOptions(null);

        assertThat(answerOptions).containsExactlyInAnyOrder("2022", "2023", "2024", "2025");
    }

    @RepeatedTest(50)
    void testGetAnswerOptionsReturnsShuffledYears() {
        when(clock.instant()).thenReturn(Instant.parse("2023-07-25T00:00:00Z"));
        when(clock.getZone()).thenReturn(ZoneId.systemDefault());

        Set<List<String>> distinctOrders = new HashSet<>();

        for (int i = 0; i < 50; i++) {
            distinctOrders.add(question4.getAnswerOptions(null));
        }

        assertThat(distinctOrders).hasSizeGreaterThan(1);
    }
}
