package ee.tenman.mmse.service.question;

import ee.tenman.mmse.domain.TestEntity;
import ee.tenman.mmse.domain.User;
import ee.tenman.mmse.domain.UserAnswer;
import ee.tenman.mmse.domain.enumeration.QuestionId;
import ee.tenman.mmse.repository.TestEntityRepository;
import ee.tenman.mmse.repository.UserAnswerRepository;
import ee.tenman.mmse.service.UserService;
import ee.tenman.mmse.service.dto.AnswerDTO;
import ee.tenman.mmse.service.dto.OrientationToPlaceQuestionDTO;
import ee.tenman.mmse.service.lock.Lock;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

@Service
public class QuizService {

    private final Map<QuestionId, Question> questions;
    private final UserAnswerRepository userAnswerRepository;
    private final UserService userService;
    private final TestEntityRepository testEntityRepository;
    private final Logger log = LoggerFactory.getLogger(QuizService.class);

    @Autowired
    public QuizService(
        QuestionsConfig questionsConfig,
        UserAnswerRepository userAnswerRepository,
        UserService userService,
        TestEntityRepository testEntityRepository
    ) {
        this.questions = questionsConfig.getQuestions();
        this.userAnswerRepository = userAnswerRepository;
        this.userService = userService;
        this.testEntityRepository = testEntityRepository;
    }

    public Question getQuestion(QuestionId... questionIds) {
        QuestionId questionId = questionIds.length == 0 ? QuestionId.QUESTION_1 : questionIds[0];
        return questions.get(questionId);
    }

    public QuizResult calculateScore(Long testEntityId) {
        List<UserAnswer> answers = userAnswerRepository.findByTestEntityIdOrderByCreatedAtDesc(testEntityId);
        int totalScore = 0;
        Set<QuestionId> answeredQuestions = new HashSet<>();

        int maxScore = 0;

        for (UserAnswer userAnswer : answers) {
            Question question = questions.get(userAnswer.getQuestionId());
            maxScore += question.getMaximumScore();

            if (question == null) {
                log.warn("No Question found for ID: {}", userAnswer.getQuestionId());
                continue;
            }

            if (answeredQuestions.contains(question.getQuestionId())) {
                continue;
            }

            int score = userAnswer.getScore() == null ? question.getScore(userAnswer) : userAnswer.getScore();
            userAnswer.setScore(score);
            userAnswer.setMaximumScore(question.getMaximumScore());
            totalScore += score;

            log.info("Total score: {}, Question: {}, Score: {}", totalScore, question.getQuestionId(), score);
            answeredQuestions.add(question.getQuestionId());
        }

        userAnswerRepository.saveAll(answers);
        return new QuizResult(totalScore, maxScore);
    }

    @Lock(key = "#answerDTO.idempotencyKey")
    public UserAnswer saveAnswer(AnswerDTO answerDTO) {
        Optional<User> user = userService.findUserWithAuthorities();
        if (user.isEmpty()) {
            throw new RuntimeException("User not found");
        }
        TestEntity testEntity = testEntityRepository.findFirstByUserIdOrderByCreatedAtDesc(user.get().getId()).orElseGet(() -> {
            TestEntity t = new TestEntity();
            t.setUser(user.get());
            return testEntityRepository.save(t);
        });
        UserAnswer userAnswer = userAnswerRepository
            .findFirstByTestEntityIdAndQuestionIdOrderByCreatedAtDesc(testEntity.getId(), answerDTO.getQuestionId())
            .orElse(new UserAnswer());
        userAnswer.setAnswerText(answerDTO.getAnswerText());
        userAnswer.setQuestionId(answerDTO.getQuestionId());
        userAnswer.setTestEntity(testEntity);
        return userAnswerRepository.save(userAnswer);
    }

    public List<OrientationToPlaceQuestionDTO> getOrientationToPlaceQuestions() {
        return questions.entrySet().stream()
            .filter(entry -> entry.getValue().isOrientationToPlace())
            .map(entry -> new OrientationToPlaceQuestionDTO(entry.getKey(), entry.getValue().getQuestionText()))
            .toList();
    }
}
