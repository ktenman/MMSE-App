package ee.tenman.mmse.service.question;

import static org.assertj.core.api.Assertions.assertThat;

import ee.tenman.mmse.domain.UserAnswer;
import ee.tenman.mmse.domain.enumeration.QuestionId;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

class Question5Test {

    private Question5 question5;
    private UserAnswer userAnswer;

    @BeforeEach
    void setUp() {
        question5 = new Question5();
        userAnswer = new UserAnswer();
        userAnswer.setQuestionId(QuestionId.QUESTION_5);
    }

    @ParameterizedTest
    @ValueSource(ints = { 1, 2, 12 })
    void testIsAnswerCorrect_whenWinter(int month) {
        LocalDateTime localDateTime = LocalDateTime.of(2023, month, 1, 0, 0);
        userAnswer.setCreatedAt(localDateTime.toInstant(ZoneOffset.UTC));
        userAnswer.setAnswerText("WINTER");
        assertThat(question5.isAnswerCorrect(userAnswer)).isTrue();
    }

    @ParameterizedTest
    @ValueSource(ints = { 3, 4, 5 })
    void testIsAnswerCorrect_whenSpring(int month) {
        LocalDateTime localDateTime = LocalDateTime.of(2023, month, 1, 0, 0);
        userAnswer.setCreatedAt(localDateTime.toInstant(ZoneOffset.UTC));
        userAnswer.setAnswerText("SPRING");
        assertThat(question5.isAnswerCorrect(userAnswer)).isTrue();
    }

    @ParameterizedTest
    @ValueSource(ints = { 6, 7, 8 })
    void testIsAnswerCorrect_whenSummer(int month) {
        LocalDateTime localDateTime = LocalDateTime.of(2023, month, 1, 0, 0);
        userAnswer.setCreatedAt(localDateTime.toInstant(ZoneOffset.UTC));
        userAnswer.setAnswerText("SUMMER");
        assertThat(question5.isAnswerCorrect(userAnswer)).isTrue();
    }

    @ParameterizedTest
    @ValueSource(ints = { 9, 10, 11 })
    void testIsAnswerCorrect_whenAutumn(int month) {
        LocalDateTime localDateTime = LocalDateTime.of(2023, month, 1, 0, 0);
        userAnswer.setCreatedAt(localDateTime.toInstant(ZoneOffset.UTC));
        userAnswer.setAnswerText("AUTUMN");
        assertThat(question5.isAnswerCorrect(userAnswer)).isTrue();
    }

    @Test
    void testIsAnswerCorrect_whenIncorrect() {
        LocalDateTime localDateTime = LocalDateTime.of(2023, 10, 1, 0, 0);
        userAnswer.setCreatedAt(localDateTime.toInstant(ZoneOffset.UTC));
        userAnswer.setAnswerText("WINTER");
        assertThat(question5.isAnswerCorrect(userAnswer)).isFalse();
    }

    @Test
    void testGetScore() {
        assertThat(question5.getScore()).isEqualTo(1);
    }

    @Test
    void testGetQuestionText() {
        assertThat(question5.getQuestionText()).isEqualTo("What is the current season?");
    }

    @Test
    void testGetQuestionId() {
        assertThat(question5.getQuestionId()).isEqualTo(QuestionId.QUESTION_5);
    }
}
