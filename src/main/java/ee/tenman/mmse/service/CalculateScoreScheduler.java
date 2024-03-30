package ee.tenman.mmse.service;

import ee.tenman.mmse.domain.UserAnswer;
import ee.tenman.mmse.domain.enumeration.QuestionId;
import ee.tenman.mmse.repository.UserAnswerRepository;
import ee.tenman.mmse.service.question.Question;
import ee.tenman.mmse.service.question.QuestionsConfig;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;

@Component
public class CalculateScoreScheduler {

    private final Logger log = LoggerFactory.getLogger(CalculateScoreScheduler.class);

    private final Map<QuestionId, Question> questions;
    private final UserAnswerRepository userAnswerRepository;

    @Autowired
    public CalculateScoreScheduler(QuestionsConfig questionsConfig, UserAnswerRepository userAnswerRepository) {
        this.questions = questionsConfig.getQuestions();
        this.userAnswerRepository = userAnswerRepository;
    }

    @Scheduled(fixedDelay = 5000)
    public void calculateUnscored() {
        List<UserAnswer> answers;
        do {
            answers = userAnswerRepository.findUnscored();
            for (UserAnswer userAnswer : answers) {
                Question question = questions.get(userAnswer.getQuestionId());
                if (question == null) {
                    log.warn("No Question found for ID: {}", userAnswer.getQuestionId());
                    continue;
                }
                int score = userAnswer.getScore() == null ? question.getScore(userAnswer) : userAnswer.getScore();
                userAnswer.setScore(score);
                userAnswer.setMaximumScore(question.getMaximumScore());
            }
            userAnswerRepository.saveAll(answers);
        } while (!answers.isEmpty());
    }
}
