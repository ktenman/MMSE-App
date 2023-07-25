package ee.tenman.mmse.service.question;

import ee.tenman.mmse.domain.TestEntity;
import ee.tenman.mmse.domain.User;
import ee.tenman.mmse.domain.UserAnswer;
import ee.tenman.mmse.domain.enumeration.QuestionId;
import ee.tenman.mmse.repository.TestEntityRepository;
import ee.tenman.mmse.repository.UserAnswerRepository;
import ee.tenman.mmse.service.UserService;
import ee.tenman.mmse.service.dto.AnswerDTO;
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

    @Autowired
    public QuizService(QuestionsConfig questionsConfig,
                       UserAnswerRepository userAnswerRepository,
                       UserService userService,
                       TestEntityRepository testEntityRepository) {
        this.questions = questionsConfig.getQuestions();
        this.userAnswerRepository = userAnswerRepository;
        this.userService = userService;
        this.testEntityRepository = testEntityRepository;
    }

    public Question getQuestion(QuestionId questionId) {
        return questions.get(questionId);
    }

    public int calculateScore(Long testEntityId) {
        List<UserAnswer> answers = userAnswerRepository.findByTestEntityIdOrderByCreatedAtDesc(testEntityId);
        int totalScore = 0;
        Set<QuestionId> answeredQuestions = new HashSet<>();

        for (UserAnswer userAnswer : answers) {
            Question question = questions.get(userAnswer.getQuestionId());

            if (answeredQuestions.contains(question.getQuestionId())) {
                continue;
            }

            if (question.isAnswerCorrect(userAnswer)) {
                totalScore += question.getScore();
                answeredQuestions.add(question.getQuestionId()); // mark the question as answered
            }
        }
        return totalScore;
    }


    public Question getFirstQuestion() {
        return new Question1();
    }

    public void saveAnswer(AnswerDTO answerDTO) {
        Optional<User> user = userService.findUserWithAuthorities();
        if (user.isEmpty()) {
            throw new RuntimeException("User not found");
        }
        TestEntity testEntity = testEntityRepository.findFirstByUserIdOrderByCreatedAtDesc(user.get().getId()).orElseGet(() -> {
            TestEntity t = new TestEntity();
            t.setUser(user.get());
            return testEntityRepository.save(t);
        });
        UserAnswer userAnswer = new UserAnswer();
        userAnswer.setAnswerText(answerDTO.getAnswerText());
        userAnswer.setQuestionId(answerDTO.getQuestionId());
        userAnswer.setTestEntity(testEntity);
        userAnswerRepository.save(userAnswer);
    }
}
