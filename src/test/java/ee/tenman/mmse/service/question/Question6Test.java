package ee.tenman.mmse.service.question;

import ee.tenman.mmse.domain.UserAnswer;
import ee.tenman.mmse.domain.enumeration.AnswerOption;
import ee.tenman.mmse.domain.enumeration.QuestionId;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class Question6Test {

    private Question6 question6;
    private UserAnswer userAnswer;

    @BeforeEach
    void setUp() {
        question6 = new Question6();
        userAnswer = new UserAnswer();
        userAnswer.setQuestionId(QuestionId.QUESTION_6);
        userAnswer.setCreatedAt(LocalDateTime.now().toInstant(ZoneOffset.UTC));
    }

    @Test
    void testIsAnswerCorrect_whenAnswerIsCorrect() {
        userAnswer.setAnswerText(AnswerOption.BALL.name());

        assertThat(question6.getScore(userAnswer)).isOne();
    }

    @Test
    void testIsAnswerCorrect_whenAnswerIsIncorrect() {
        userAnswer.setAnswerText(AnswerOption.CAR.name());

        assertThat(question6.getScore(userAnswer)).isZero();
    }

    @Test
    void testGetQuestionText() {
        assertThat(question6.getQuestionText()).isEqualTo("6. Please look at these three images. What is the name of the first object?");
    }

    @Test
    void testGetQuestionId() {
        assertThat(question6.getQuestionId()).isEqualTo(QuestionId.QUESTION_6);
    }

    @Test
    void testGetAnswerOptions() {
        List<AnswerOption> expectedAnswerOptions = List.of(
            AnswerOption.CAR,
            AnswerOption.BALL,
            AnswerOption.MAN,
            AnswerOption.TREE);

        assertThat(question6.getAnswerOptions(null)).containsExactlyInAnyOrderElementsOf(expectedAnswerOptions);
    }
}
