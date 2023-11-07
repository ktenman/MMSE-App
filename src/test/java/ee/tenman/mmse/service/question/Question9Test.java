package ee.tenman.mmse.service.question;

import ee.tenman.mmse.domain.UserAnswer;
import ee.tenman.mmse.domain.enumeration.QuestionId;
import ee.tenman.mmse.domain.enumeration.QuestionType;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.List;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.params.provider.Arguments.arguments;

class Question9Test {

    private Question9 question9;
    private UserAnswer userAnswer;

    @BeforeEach
    void setUp() {
        question9 = new Question9();
        userAnswer = new UserAnswer();
        userAnswer.setQuestionId(QuestionId.QUESTION_9);
    }

    @Test
    void testIsAnswerCorrect_whenAnswerIsCorrect() {
        userAnswer.setAnswerText("93,86,79,72,65");

        assertThat(question9.getScore(userAnswer)).isEqualTo(5);
    }

    @Test
    void testIsAnswerCorrect_whenAnswerIsPartiallyCorrect() {
        userAnswer.setAnswerText("93,86,78,72,65");

        assertThat(question9.getScore(userAnswer)).isEqualTo(4);
    }

    static Stream<Arguments> provideAnswersAndExpectedScores() {
        return Stream.of(
            arguments("93,86,78,72,65", 4),
            arguments("93,86,79,72,66", 4),
            arguments("93,85,79,72,65", 4),
            arguments("93,86,79,71,65", 4),
            arguments("92,86,79,72,65", 4),
            arguments("93,86,79,72,65,58", 5),
            arguments("100,93,86,79,72", 4),
            arguments("100,93,86,79,79", 3),
            arguments("90,83,76,69,62", 0),
            arguments("90,83,76,,62", 0),
            arguments("93,83,76,,62", 1)
        );
    }

    @ParameterizedTest
    @MethodSource("provideAnswersAndExpectedScores")
    void testGetScore_whenAnswerIsPartiallyCorrect(String answerText, int expectedScore) {
        userAnswer.setAnswerText(answerText);

        assertThat(question9.getScore(userAnswer)).isEqualTo(expectedScore);
    }

    @Test
    void testIsAnswerCorrect_whenAnswerIsIncorrect() {
        userAnswer.setAnswerText("90,83,76,69,62");

        assertThat(question9.getScore(userAnswer)).isZero();
    }

    @Test
    void testGetQuestionText() {
        assertThat(question9.getQuestionText()).isEqualTo("9. Start with 100 and subtract 7, continue subtracting 7 from each new number for five steps.");
    }

    @Test
    void testGetQuestionId() {
        assertThat(question9.getQuestionId()).isEqualTo(QuestionId.QUESTION_9);
    }

    @Test
    void testGetQuestionType() {
        assertThat(question9.getQuestionType()).isEqualTo(QuestionType.SUBTRACTION_TASK);
    }

    @Test
    void testGetAnswerOptions() {
        List<InputField> answerOptions = question9.getAnswerOptions();

        assertThat(answerOptions).hasSize(5);
        assertThat(answerOptions)
            .allSatisfy(inputField -> {
                assertThat(inputField.getType()).isEqualTo(InputFieldType.NUMBER);
                assertThat(inputField.getMin()).isEqualTo(0);
                assertThat(inputField.getMax()).isEqualTo(99);
            });
    }
}
