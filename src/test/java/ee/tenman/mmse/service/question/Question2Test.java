package ee.tenman.mmse.service.question;

import ee.tenman.mmse.domain.UserAnswer;
import ee.tenman.mmse.domain.enumeration.QuestionId;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.time.ZoneOffset;

import static org.assertj.core.api.Assertions.assertThat;

class Question2Test {

    private Question2 question2;
    private UserAnswer userAnswer;

    @BeforeEach
    void setUp() {
        question2 = new Question2();
        userAnswer = new UserAnswer();
        userAnswer.setQuestionId(QuestionId.QUESTION_2);
        LocalDateTime localDateTime = LocalDateTime.of(2023, 1, 1, 0, 0);
        userAnswer.setCreatedAt(localDateTime.toInstant(ZoneOffset.UTC));
    }

    @Test
    void testIsAnswerCorrect_whenTrue() {
        userAnswer.setAnswerText("2023-01-01");
        assertThat(question2.isAnswerCorrect(userAnswer)).isTrue();
    }

    @Test
    void testIsAnswerCorrect_whenFalse() {
        userAnswer.setAnswerText("2023-01-02");
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
}
