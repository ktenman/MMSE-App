package ee.tenman.mmse.service.question;

import ee.tenman.mmse.IntegrationTest;
import ee.tenman.mmse.domain.UserAnswer;
import ee.tenman.mmse.service.prediction.PredictionService;
import jakarta.annotation.Resource;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.boot.test.mock.mockito.MockBean;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

@IntegrationTest
class Question14Test {

    @MockBean
    PredictionService predictionService;

    @Resource
    Question14 question14;

    @Test
    void whenAnswerIsCorrectSentence_thenScoreShouldBeOne() {
        when(predictionService.isGrammaticallyCorrect(Mockito.anyString())).thenReturn(true);
        UserAnswer userAnswer = new UserAnswer();
        userAnswer.setAnswerText("I walk my dog every morning.");

        int score = question14.getScore(userAnswer);

        assertThat(score).isOne();
    }

    @Test
    void whenAnswerIsIncorrectSentence_thenScoreShouldBeZero() {
        UserAnswer userAnswer = new UserAnswer();
        userAnswer.setAnswerText("Dog morning I walk.");

        int score = question14.getScore(userAnswer);

        assertThat(score).isZero();
    }

    @Test
    void whenAnswerIsEmpty_thenScoreShouldBeZero() {
        UserAnswer userAnswer = new UserAnswer();
        userAnswer.setAnswerText("");

        int score = question14.getScore(userAnswer);

        assertThat(score).isZero();
    }

    @Test
    void whenAnswerIsNull_thenScoreShouldBeZero() {
        UserAnswer userAnswer = new UserAnswer();
        userAnswer.setAnswerText(null);

        int score = question14.getScore(userAnswer);

        assertThat(score).isZero();
    }

}

