package ee.tenman.mmse.service.question;

import ee.tenman.mmse.domain.OrientationToPlaceAnswer;
import ee.tenman.mmse.domain.PatientProfile;
import ee.tenman.mmse.domain.TestEntity;
import ee.tenman.mmse.domain.User;
import ee.tenman.mmse.domain.UserAnswer;
import ee.tenman.mmse.domain.enumeration.QuestionId;
import ee.tenman.mmse.repository.UserAnswerRepository;
import ee.tenman.mmse.service.OrientationToPlaceAnswerService;
import ee.tenman.mmse.service.PatientProfileService;
import ee.tenman.mmse.service.TestEntityService;
import ee.tenman.mmse.service.UserService;
import ee.tenman.mmse.service.dto.AnswerDTO;
import ee.tenman.mmse.service.dto.OrientationToPlaceQuestionDTO;
import ee.tenman.mmse.service.lock.Lock;
import jakarta.persistence.EntityNotFoundException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;

import static java.util.stream.Collectors.toSet;

@Service
public class QuizService {

    private final Logger log = LoggerFactory.getLogger(QuizService.class);

    private final Map<QuestionId, Question> questions;
    private final UserAnswerRepository userAnswerRepository;
    private final UserService userService;
    private final TestEntityService testEntityService;
    private final PatientProfileService patientProfileService;
    private final OrientationToPlaceAnswerService orientationToPlaceAnswerService;

    @Autowired
    public QuizService(
        QuestionsConfig questionsConfig,
        UserAnswerRepository userAnswerRepository,
        UserService userService, TestEntityService testEntityService,
        PatientProfileService patientProfileService,
        OrientationToPlaceAnswerService orientationToPlaceAnswerService
    ) {
        this.questions = questionsConfig.getQuestions();
        this.userAnswerRepository = userAnswerRepository;
        this.userService = userService;
        this.testEntityService = testEntityService;
        this.patientProfileService = patientProfileService;
        this.orientationToPlaceAnswerService = orientationToPlaceAnswerService;
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
        TestEntity testEntity = testEntityService.getLast();
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

    public List<OrientationToPlaceQuestionDTO> saveOrientationToPlaceAnswers(Long patientProfileId, List<OrientationToPlaceQuestionDTO> answers) {
        PatientProfile patientProfile = patientProfileService.findById(patientProfileId)
            .orElseThrow(() -> new RuntimeException("Patient profile not found"));
        Set<OrientationToPlaceAnswer> orientationToPlaceAnswers = answers.stream()
            .map(answer -> {
                OrientationToPlaceAnswer orientationToPlaceAnswer = orientationToPlaceAnswerService
                    .findByPatientProfileAndQuestionId(patientProfile, answer.getQuestionId())
                    .orElse(new OrientationToPlaceAnswer());
                orientationToPlaceAnswer.setQuestionId(answer.getQuestionId());
                orientationToPlaceAnswer.setCorrectAnswer(answer.getCorrectAnswer());
                orientationToPlaceAnswer.setAnswerOptions(answerOptions(answer.getAnswerOptions()));
                orientationToPlaceAnswer.setPatientProfile(patientProfile);
                return orientationToPlaceAnswer;
            })
            .collect(toSet());
        testEntityService.createTestEntity(patientProfile);
        return orientationToPlaceAnswerService.saveAll(orientationToPlaceAnswers)
            .stream()
            .map(this::toOrientationToPlaceQuestionDTO)
            .toList();
    }

    private OrientationToPlaceQuestionDTO toOrientationToPlaceQuestionDTO(OrientationToPlaceAnswer answer) {
        return new OrientationToPlaceQuestionDTO(
            answer.getQuestionId(),
            questions.get(answer.getQuestionId()).getQuestionText(),
            answer.getCorrectAnswer(),
            String.join(",", answer.getAnswerOptions())
        );
    }

    private List<String> answerOptions(String answerOptions) {
        if (answerOptions == null || answerOptions.isBlank()) return List.of();
        return List.of(answerOptions.split(","));
    }

    @Transactional(readOnly = true)
    public List<OrientationToPlaceQuestionDTO> getOrientationToPlaceQuestions(Long patientProfileId) {
        PatientProfile patientProfile = patientProfileService.findById(patientProfileId)
            .orElseThrow(() -> new EntityNotFoundException("Patient profile not found with ID: " + patientProfileId));

        List<OrientationToPlaceQuestionDTO> allQuestionsDTO = getOrientationToPlaceQuestions();

        Map<QuestionId, OrientationToPlaceQuestionDTO> answeredQuestionsMap = patientProfile.getOrientationToPlaceAnswers().stream()
            .map(this::toOrientationToPlaceQuestionDTO)
            .collect(Collectors.toMap(OrientationToPlaceQuestionDTO::getQuestionId, Function.identity()));

        allQuestionsDTO.forEach(questionDTO -> {
            OrientationToPlaceQuestionDTO answeredQuestion = answeredQuestionsMap.get(questionDTO.getQuestionId());
            if (answeredQuestion != null) {
                questionDTO.setCorrectAnswer(answeredQuestion.getCorrectAnswer());
                questionDTO.setAnswerOptions(answeredQuestion.getAnswerOptions());
            }
        });

        return allQuestionsDTO;
    }
}
