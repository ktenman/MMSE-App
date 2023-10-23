package ee.tenman.mmse.web.rest;

import ee.tenman.mmse.domain.TestEntity;
import ee.tenman.mmse.domain.User;
import ee.tenman.mmse.domain.UserAnswer;
import ee.tenman.mmse.domain.enumeration.QuestionId;
import ee.tenman.mmse.repository.TestEntityRepository;
import ee.tenman.mmse.service.UserAnswerService;
import ee.tenman.mmse.service.UserService;
import ee.tenman.mmse.service.dto.AnswerDTO;
import ee.tenman.mmse.service.question.Question;
import ee.tenman.mmse.service.question.QuizService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.NoSuchElementException;
import java.util.Optional;

@RestController
@RequestMapping("/api")
public class QuizController {

    private final QuizService quizService;
    private final UserAnswerService userAnswerService;
    private final TestEntityRepository testEntityRepository;
    private final UserService userService;

    @Autowired
    public QuizController(QuizService quizService, UserAnswerService userAnswerService, TestEntityRepository testEntityRepository, UserService userService) {
        this.quizService = quizService;
        this.userAnswerService = userAnswerService;
        this.testEntityRepository = testEntityRepository;
        this.userService = userService;
    }

    @GetMapping("/question")
    public ResponseEntity<?> getNextQuestion() {
        Optional<UserAnswer> latestUserAnswer = userAnswerService.getLatest();

        if (latestUserAnswer.isPresent()) {
            Optional<QuestionId> nextQuestionId = getNextQuestionId(latestUserAnswer.get().getQuestionId());
            return handleNextQuestion(nextQuestionId);
        } else {
            // If there's no latest user answer, this means the quiz has just started. Return the first question.
            Question firstQuestion = quizService.getFirstQuestion();
            return ResponseEntity.ok(firstQuestion);
        }
    }

    @PostMapping("/answer")
    public ResponseEntity<?> saveAnswerAndGetNextQuestion(@RequestBody AnswerDTO answerDTO) {
        quizService.saveAnswer(answerDTO);
        Optional<QuestionId> nextQuestionId = getNextQuestionId(answerDTO.getQuestionId());
        return handleNextQuestion(nextQuestionId);
    }

    @PostMapping("/retake")
    public Question retakeTest() {
        return quizService.retakeTest();
    }

    private Optional<QuestionId> getNextQuestionId(QuestionId currentQuestionId) {
        QuestionId[] questionIds = QuestionId.values();
        int nextQuestionIndex = currentQuestionId.ordinal() + 1;

        if (nextQuestionIndex < questionIds.length) {
            return Optional.of(questionIds[nextQuestionIndex]);
        }

        return Optional.empty();
    }

    private ResponseEntity<?> handleNextQuestion(Optional<QuestionId> nextQuestionId) {
        if (nextQuestionId.isEmpty()) {
            User user = userService.getUserWithAuthorities();
            TestEntity testEntity = testEntityRepository.findFirstByUserIdOrderByCreatedAtDesc(user.getId()).orElseThrow(() -> new NoSuchElementException("Test not found"));
            int calculateScore = quizService.calculateScore(testEntity.getId());
            testEntity.setScore(calculateScore);
            testEntityRepository.save(testEntity);

            // Return a response indicating that the quiz has ended.
            return ResponseEntity.ok().body("Quiz has ended. Your score is " + calculateScore);
        }

        // Continue the quiz with the next question
        Question nextQuestion = quizService.getQuestion(nextQuestionId.get());
        return ResponseEntity.ok(nextQuestion);
    }

}
