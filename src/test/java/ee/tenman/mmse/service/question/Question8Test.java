package ee.tenman.mmse.service.question;

import ee.tenman.mmse.domain.UserAnswer;
import ee.tenman.mmse.domain.enumeration.AnswerOption;
import ee.tenman.mmse.domain.enumeration.QuestionId;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class Question8Test {

    private Question8 question8;
    private UserAnswer userAnswer;

    @BeforeEach
    void setUp() {
        question8 = new Question8();
        userAnswer = new UserAnswer();
        userAnswer.setQuestionId(QuestionId.QUESTION_8);
    }

    @Test
    void testIsAnswerCorrect_whenAnswerIsCorrect() {
        userAnswer.setAnswerText(AnswerOption.MAN.name());

        assertThat(question8.isAnswerCorrect(userAnswer)).isTrue();
    }

    @Test
    void testIsAnswerCorrect_whenAnswerIsIncorrect() {
        userAnswer.setAnswerText(AnswerOption.BALL.name());

        assertThat(question8.isAnswerCorrect(userAnswer)).isFalse();
    }

    @Test
    void testGetScore() {
        assertThat(question8.getScore()).isEqualTo(1);
    }

    @Test
    void testGetQuestionText() {
        assertThat(question8.getQuestionText()).isEqualTo("What is the name of the third object?");
    }

    @Test
    void testGetQuestionId() {
        assertThat(question8.getQuestionId()).isEqualTo(QuestionId.QUESTION_8);
    }

    @Test
    void testGetAnswerOptions() {
        List<String> expectedAnswerOptions = List.of(
            "Ball",
            "Car",
            "Man"
        );

        assertThat(question8.getAnswerOptions()).containsExactlyInAnyOrderElementsOf(expectedAnswerOptions);
    }
}
