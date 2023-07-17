package ee.tenman.mmse.service.question;

import ee.tenman.mmse.domain.UserAnswer;
import ee.tenman.mmse.domain.enumeration.QuestionId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

@Service
public class QuizService {

    private Map<QuestionId, Question> questions;

    @Autowired
    public QuizService(QuestionsConfig questionsConfig) {
        this.questions = questionsConfig.getQuestions();
    }

    public int calculateScore(List<UserAnswer> userAnswers) {
        int totalScore = 0;
        for (UserAnswer userAnswer : userAnswers) {
            Question question = questions.get(userAnswer.getQuestionId());
            if (question.isAnswerCorrect(userAnswer)) {
                totalScore += question.getScore();
            }
        }
        return totalScore;
    }
}
