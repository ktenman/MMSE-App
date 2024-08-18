package ee.tenman.mmse.service.question;

import ee.tenman.mmse.domain.UserAnswer;
import ee.tenman.mmse.domain.enumeration.QuestionId;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import static org.assertj.core.api.Assertions.assertThat;

class Question16Test {

    private Question16 question16;
    private UserAnswer userAnswer;

    @BeforeEach
    void setUp() {
        question16 = new Question16();
        userAnswer = new UserAnswer();
        userAnswer.setQuestionId(QuestionId.QUESTION_16);
    }

    @ParameterizedTest
    @CsvSource({
            "'true,true,true', 3",
            "'true,true,false', 2",
            "'true,false,false', 1",
            "'false,false,false', 0",
            "null, 0"
    })
    void testGetScore(String answerText, int expectedScore) {
        userAnswer.setAnswerText(answerText);

        assertThat(question16.getScore(userAnswer)).isEqualTo(expectedScore);
    }

    @Test
    void testGetQuestionText() {
        assertThat(question16.getQuestionText()).isEqualTo("16. Fold and Drop Paper Task");
    }

    @Test
    void testGetQuestionId() {
        assertThat(question16.getQuestionId()).isEqualTo(QuestionId.QUESTION_16);
    }

    @Test
    void testGetMaximumScore() {
        assertThat(question16.getMaximumScore()).isEqualTo(3);
    }

    @Test
    void testGetInstructions() {
        assertThat(question16.getInstructions()).containsExactly(
                "Select the 'Paper'",
                "Tap/Click the 'Fold Paper in Half' button to fold it",
                "With your mouse/finger, drag and drop the folded paper into the 'Floor' area",
                "When finished, use the 'Next Task' button to proceed"
        );
    }

    @Test
    void testGetCorrectAnswer() {
        assertThat(question16.getCorrectAnswer(null)).isEqualTo("true,true,true");
    }
}
