package ee.tenman.mmse.service.question;

import ee.tenman.mmse.IntegrationTest;
import ee.tenman.mmse.domain.UserAnswer;
import jakarta.annotation.Resource;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

@IntegrationTest
class Question14IntegrationTest {

    @Resource
    Question14 question14;

    @Test
    @Disabled
    void whenAnswerIsCorrectSentence_thenScoreShouldBeOne() {
        UserAnswer userAnswer = new UserAnswer();
        userAnswer.setAnswerText("I walk my dog every morning.");

        int score = question14.getScore(userAnswer);

        assertThat(score).isOne();
    }

    @Test
    @Disabled
    void whenAnswerIsIncorrectSentence_thenScoreShouldBeZero() {
        UserAnswer userAnswer = new UserAnswer();
        userAnswer.setAnswerText("Dog morning I walk.");

        int score = question14.getScore(userAnswer);

        assertThat(score).isZero();
    }

    @Test
    @Disabled
    void whenAnswerIsEmpty_thenScoreShouldBeZero() {
        UserAnswer userAnswer = new UserAnswer();
        userAnswer.setAnswerText("");

        int score = question14.getScore(userAnswer);

        assertThat(score).isZero();
    }

    @Test
    @Disabled
    void whenAnswerIsNull_thenScoreShouldBeZero() {
        UserAnswer userAnswer = new UserAnswer();
        userAnswer.setAnswerText(null);

        int score = question14.getScore(userAnswer);

        assertThat(score).isZero();
    }

}

