package ee.tenman.mmse.service.question;

import ee.tenman.mmse.domain.UserAnswer;
import ee.tenman.mmse.domain.enumeration.QuestionId;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullAndEmptySource;

import java.time.LocalDateTime;
import java.time.ZoneOffset;

import static org.assertj.core.api.Assertions.assertThat;

class Question4Test {

    private Question4 question4;
    private UserAnswer userAnswer;

    @BeforeEach
    void setUp() {
        question4 = new Question4();
        userAnswer = new UserAnswer();
        userAnswer.setQuestionId(QuestionId.QUESTION_4);
        LocalDateTime localDateTime = LocalDateTime.of(2023, 1, 1, 0, 0);
        userAnswer.setCreatedAt(localDateTime.toInstant(ZoneOffset.UTC));
    }

    @Test
    void testIsAnswerCorrect_whenTrue() {
        userAnswer.setAnswerText("2023");
        assertThat(question4.isAnswerCorrect(userAnswer)).isTrue();
    }

    @ParameterizedTest
    @NullAndEmptySource
    void testIsAnswerCorrect_whenFalse(String answerText) {
        userAnswer.setAnswerText(answerText);
        assertThat(question4.isAnswerCorrect(userAnswer)).isFalse();
    }

    @Test
    void testIsAnswerCorrect_whenFalse() {
        userAnswer.setAnswerText("2024");
        assertThat(question4.isAnswerCorrect(userAnswer)).isFalse();
    }

    @Test
    void testGetScore() {
        assertThat(question4.getScore()).isEqualTo(1);
    }

    @Test
    void testGetQuestionText() {
        assertThat(question4.getQuestionText()).isEqualTo("What is the current year?");
    }

    @Test
    void testGetQuestionId() {
        assertThat(question4.getQuestionId()).isEqualTo(QuestionId.QUESTION_4);
    }
}
