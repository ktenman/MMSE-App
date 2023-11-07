package ee.tenman.mmse.service.question;

import ee.tenman.mmse.domain.UserAnswer;
import ee.tenman.mmse.domain.enumeration.QuestionId;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.Clock;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.Month;
import java.time.ZoneId;
import java.time.ZoneOffset;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@ExtendWith(MockitoExtension.class)
class Question3Test {

    @InjectMocks
    private Question3 question3;
    private UserAnswer userAnswer;
    @Mock
    private Clock clock;

    @BeforeEach
    void setUp() {
        userAnswer = new UserAnswer();
        userAnswer.setQuestionId(QuestionId.QUESTION_3);
        LocalDateTime localDateTime = LocalDateTime.of(2023, 1, 1, 0, 0);
        userAnswer.setCreatedAt(localDateTime.toInstant(ZoneOffset.UTC));
    }

    @ParameterizedTest
    @EnumSource(value = Month.class, mode = EnumSource.Mode.INCLUDE, names = {"JANUARY"})
    void testGetScore_whenCorrect(Month month) {
        userAnswer.setAnswerText(month.name());
        assertThat(question3.getScore(userAnswer)).isOne();
    }

    @ParameterizedTest
    @EnumSource(value = Month.class, mode = EnumSource.Mode.EXCLUDE, names = {"JANUARY"})
    void testGetScore_whenWrong(Month month) {
        userAnswer.setAnswerText(month.name());
        assertThat(question3.getScore(userAnswer)).isZero();
    }

    @Test
    void testGetQuestionText() {
        assertThat(question3.getQuestionText()).isEqualTo("3. What is the current month?");
    }

    @Test
    void testGetQuestionId() {
        assertThat(question3.getQuestionId()).isEqualTo(QuestionId.QUESTION_3);
    }

    @Test
    void testGetAnswerOptionsContainsFourMonths() {
        Mockito.when(clock.instant()).thenReturn(Instant.parse("2023-07-25T00:00:00Z"));
        Mockito.when(clock.getZone()).thenReturn(ZoneId.systemDefault());

        List<String> answerOptions = question3.getAnswerOptions();

        assertThat(answerOptions).hasSize(4);
    }

    @Test
    void testGetAnswerOptionsContainsCorrectMonths() {
        Mockito.when(clock.instant()).thenReturn(Instant.parse("2023-07-25T00:00:00Z"));
        Mockito.when(clock.getZone()).thenReturn(ZoneId.systemDefault());

        List<String> answerOptions = question3.getAnswerOptions();

        assertThat(answerOptions).containsExactlyInAnyOrder("JULY", "AUGUST", "SEPTEMBER", "OCTOBER");
    }

    @Test
    void testGetAnswerOptionsReturnsShuffledMonths() {
        Mockito.when(clock.instant()).thenReturn(Instant.parse("2023-07-25T00:00:00Z"));
        Mockito.when(clock.getZone()).thenReturn(ZoneId.systemDefault());

        List<String> previousOrder = question3.getAnswerOptions();
        boolean isDifferent = false;

        for (int i = 0; i < 100; i++) {
            List<String> currentOrder = question3.getAnswerOptions();
            if (!currentOrder.equals(previousOrder)) {
                isDifferent = true;
                break;
            }
            previousOrder = currentOrder;
        }

        assertThat(isDifferent).isTrue();
    }

}
