package ee.tenman.mmse.service.question;

import ee.tenman.mmse.domain.UserAnswer;
import ee.tenman.mmse.domain.enumeration.QuestionId;
import ee.tenman.mmse.domain.enumeration.QuestionType;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import java.util.Collections;

import static org.assertj.core.api.Assertions.assertThat;

class Question10Test {

    private Question10 question10;
    private UserAnswer userAnswer;

    @BeforeEach
    void setUp() {
        question10 = new Question10();
        userAnswer = new UserAnswer();
        userAnswer.setQuestionId(QuestionId.QUESTION_10);
    }

    @ParameterizedTest
    @CsvSource({
        "man ball car, 3",
        "car ball man, 3",
        "man dog ball, 2",
        "dog cat bird, 0",
        "MaN cAr bALL, 3",
        "MaNcArbALL, 3",
        "MaN;cAr;bALL, 3",
    })
    void testGetScore(String answerText, int expectedScore) {
        userAnswer.setAnswerText(answerText);

        assertThat(question10.getScore(userAnswer)).isEqualTo(expectedScore);
    }

    @Test
    void testGetQuestionText() {
        assertThat(question10.getQuestionText()).isEqualTo("10. Please recall the three objects that were previously mentioned.");
    }

    @Test
    void testGetQuestionId() {
        assertThat(question10.getQuestionId()).isEqualTo(QuestionId.QUESTION_10);
    }

    @Test
    void testGetQuestionType() {
        assertThat(question10.getQuestionType()).isEqualTo(QuestionType.TEXT_INPUT);
    }

    @Test
    void testGetAnswerOptions() {
        assertThat(question10.getAnswerOptions(null)).isEqualTo(Collections.emptyList());
    }
}
