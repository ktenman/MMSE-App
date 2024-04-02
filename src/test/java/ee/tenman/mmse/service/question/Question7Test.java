package ee.tenman.mmse.service.question;

import ee.tenman.mmse.domain.UserAnswer;
import ee.tenman.mmse.domain.enumeration.AnswerOption;
import ee.tenman.mmse.domain.enumeration.QuestionId;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class Question7Test {

    private Question7 question7;
    private UserAnswer userAnswer;

    @BeforeEach
    void setUp() {
        question7 = new Question7();
        userAnswer = new UserAnswer();
        userAnswer.setQuestionId(QuestionId.QUESTION_7);
    }

    @Test
    void testIsAnswerCorrect_whenCorrectAnswer() {
        userAnswer.setAnswerText(AnswerOption.CAR.name());

        assertThat(question7.getScore(userAnswer)).isOne();
    }

    @Test
    void testIsAnswerCorrect_whenIncorrectAnswer() {
        userAnswer.setAnswerText(AnswerOption.BALL.name());

        assertThat(question7.getScore(userAnswer)).isZero();
    }

    @Test
    void testGetQuestionText() {
        assertThat(question7.getQuestionText()).isEqualTo("7. What is the name of the second object?");
    }

    @Test
    void testGetQuestionId() {
        assertThat(question7.getQuestionId()).isEqualTo(QuestionId.QUESTION_7);
    }

    @Test
    void testGetAnswerOptions() {
        List<AnswerOption> expectedAnswerOptions = List.of(
            AnswerOption.CAR,
            AnswerOption.BALL,
            AnswerOption.MAN,
            AnswerOption.TREE);

        assertThat(question7.getAnswerOptions(null)).containsExactlyInAnyOrderElementsOf(expectedAnswerOptions);
    }
}
