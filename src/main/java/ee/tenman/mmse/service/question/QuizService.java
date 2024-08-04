package ee.tenman.mmse.service.question;

import ee.tenman.mmse.config.RedisConfiguration;
import ee.tenman.mmse.domain.OrientationToPlaceAnswer;
import ee.tenman.mmse.domain.PatientProfile;
import ee.tenman.mmse.domain.TestEntity;
import ee.tenman.mmse.domain.UserAnswer;
import ee.tenman.mmse.domain.enumeration.QuestionId;
import ee.tenman.mmse.repository.UserAnswerRepository;
import ee.tenman.mmse.service.OrientationToPlaceAnswerService;
import ee.tenman.mmse.service.PatientProfileService;
import ee.tenman.mmse.service.TestEntityService;
import ee.tenman.mmse.service.dto.AnswerDTO;
import ee.tenman.mmse.service.dto.OrientationToPlaceQuestionDTO;
import ee.tenman.mmse.service.dto.QuestionDTO;
import ee.tenman.mmse.service.external.dolphin.DolphinRequest;
import ee.tenman.mmse.service.external.dolphin.DolphinService;
import ee.tenman.mmse.service.external.dolphin.PromptWrapper;
import ee.tenman.mmse.service.lock.Lock;
import ee.tenman.mmse.service.question.QuizResult.QuestionResult;
import jakarta.persistence.EntityNotFoundException;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Duration;
import java.time.Instant;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import java.util.function.Function;
import java.util.stream.Collectors;

import static java.util.stream.Collectors.toSet;

@Service
public class QuizService {

    private final Logger log = LoggerFactory.getLogger(QuizService.class);

    private final Map<QuestionId, Question> questions;
    private final UserAnswerRepository userAnswerRepository;
    private final TestEntityService testEntityService;
    private final PatientProfileService patientProfileService;
    private final OrientationToPlaceAnswerService orientationToPlaceAnswerService;
    private final DolphinService dolphinService;

    @Autowired
    public QuizService(
        QuestionsConfig questionsConfig,
        UserAnswerRepository userAnswerRepository,
        TestEntityService testEntityService,
        PatientProfileService patientProfileService,
        OrientationToPlaceAnswerService orientationToPlaceAnswerService,
        DolphinService dolphinService
    ) {
        this.questions = questionsConfig.getQuestions();
        this.userAnswerRepository = userAnswerRepository;
        this.testEntityService = testEntityService;
        this.patientProfileService = patientProfileService;
        this.orientationToPlaceAnswerService = orientationToPlaceAnswerService;
        this.dolphinService = dolphinService;
    }

    public QuestionDTO getQuestion(QuestionId questionId, Long testEntityId) {
        Question question = questions.get(questionId);
        return mapToQuestionDTO(question, testEntityId);
    }

    public QuestionDTO mapToQuestionDTO(Question question, Long testEntityId) {
        QuestionDTO questionDTO = new QuestionDTO();
        questionDTO.setQuestionText(question.getQuestionText());
        questionDTO.setImage(question.getImage());
        questionDTO.setQuestionId(question.getQuestionId());
        questionDTO.setQuestionType(question.getQuestionType());
        questionDTO.setAnswerOptions(question.getAnswerOptions(testEntityId));
        questionDTO.setMaximumScore(question.getMaximumScore());
        questionDTO.setOrientationToPlace(question.isOrientationToPlace());
        questionDTO.setInstructions(question.getInstructions());
        return questionDTO;
    }

    @Cacheable(value = RedisConfiguration.ONE_MONTH_CACHE, key = "#testEntityId")
    public QuizResult calculateScore(Long testEntityId) {
        List<UserAnswer> answers = userAnswerRepository.findByTestEntityIdOrderByCreatedAtDesc(testEntityId);
        int totalScore = 0;
        int maxScore = 0;
        Map<QuestionId, QuestionResult> questionResults = new TreeMap<>();

        for (UserAnswer userAnswer : answers) {
            Question question = questions.get(userAnswer.getQuestionId());
            if (question == null) {
                log.warn("No Question found for ID: {}", userAnswer.getQuestionId());
                continue;
            }
            question.getAnswerOptions(testEntityId); // This is a hack to set the correct answer for the question

            maxScore += question.getMaximumScore();
            int score = question.getScore(userAnswer);
            totalScore += score;
            boolean correct = (score == question.getMaximumScore());
            questionResults.put(userAnswer.getQuestionId(),
                new QuestionResult(
                    question.getQuestionText(),
                    userAnswer.getAnswerText(),
                    question.getCorrectAnswer(testEntityId),
                    correct,
                    score,
                    question.getMaximumScore()
                )
            );

            userAnswer.setScore(score);
            userAnswer.setMaximumScore(question.getMaximumScore());
            log.info("Question: {}, User Answer: {}, Correct Answer: {}, Score: {}/{}",
                question.getQuestionId(), userAnswer.getAnswerText(), question.getCorrectAnswer(testEntityId), score, question.getMaximumScore());
        }

        userAnswerRepository.saveAll(answers);

        long duration = calculateDuration(answers);

        return new QuizResult(totalScore, maxScore, questionResults, duration);
    }

    private long calculateDuration(List<UserAnswer> answers) {
        Instant minTime = answers.stream().map(UserAnswer::getCreatedAt).min(Instant::compareTo)
            .orElseThrow(() -> new RuntimeException("No answers found"));
        Instant maxTime = answers.stream().map(UserAnswer::getCreatedAt).max(Instant::compareTo)
            .orElseThrow(() -> new RuntimeException("No answers found"));
        return Duration.between(minTime, maxTime).getSeconds();
    }
    @Lock(key = "#answerDTO.idempotencyKey")
    public UserAnswer saveAnswer(AnswerDTO answerDTO, Long testEntityId) {
        TestEntity testEntity = testEntityService.getById(testEntityId);
        UserAnswer userAnswer = userAnswerRepository
            .findFirstByTestEntityIdAndQuestionIdOrderByCreatedAtDesc(testEntity.getId(), answerDTO.getQuestionId())
            .orElse(new UserAnswer());

        Question question = questions.get(answerDTO.getQuestionId());

        userAnswer.setAnswerText(answerDTO.getAnswerText());
        userAnswer.setQuestionId(answerDTO.getQuestionId());
        userAnswer.setTestEntity(testEntity);
        userAnswer.setCorrectAnswer(question.getCorrectAnswer(testEntityId));

        return userAnswerRepository.save(userAnswer);
    }

    public List<OrientationToPlaceQuestionDTO> getOrientationToPlaceQuestions() {
        return questions.entrySet().stream()
            .filter(entry -> entry.getValue().isOrientationToPlace())
            .sorted(Comparator.comparing(entry -> entry.getKey().ordinal()))
            .map(entry -> new OrientationToPlaceQuestionDTO(entry.getKey(), entry.getValue().getQuestionText()))
            .toList();
    }

    public List<OrientationToPlaceQuestionDTO> saveOrientationToPlaceCorrectAnswers(Long patientProfileId, List<OrientationToPlaceQuestionDTO> answers) {
        PatientProfile patientProfile = patientProfileService.findById(patientProfileId)
            .orElseThrow(() -> new RuntimeException("Patient profile not found"));
        Set<OrientationToPlaceAnswer> orientationToPlaceAnswers = answers.stream()
            .filter(answer -> StringUtils.isNotBlank(answer.getCorrectAnswer()))
            .map(answer -> {
                Question question = questions.get(answer.getQuestionId());
                PromptWrapper promptWrapper = new PromptWrapper(question.getLLMPrompt(answer.getCorrectAnswer()), DolphinRequest.Model.GEMMA_2_9B);
                String found = dolphinService.find(promptWrapper);
                OrientationToPlaceAnswer orientationToPlaceAnswer = orientationToPlaceAnswerService
                    .findByPatientProfileAndQuestionId(patientProfile, answer.getQuestionId())
                    .orElse(new OrientationToPlaceAnswer());
                orientationToPlaceAnswer.setQuestionId(answer.getQuestionId());
                orientationToPlaceAnswer.setCorrectAnswer(answer.getCorrectAnswer());
                orientationToPlaceAnswer.setAnswerOptions(answerOptions(found));
                orientationToPlaceAnswer.setPatientProfile(patientProfile);
                return orientationToPlaceAnswer;
            })
            .collect(toSet());
        orientationToPlaceAnswerService.saveAll(orientationToPlaceAnswers);
        return orientationToPlaceAnswerService.saveAll(orientationToPlaceAnswers)
            .stream()
            .map(this::toOrientationToPlaceQuestionDTO)
            .sorted(Comparator.comparing(OrientationToPlaceQuestionDTO::getQuestionId))
            .toList();
    }

    public TestEntity saveOrientationToPlaceAnswerOptions(Long patientProfileId, List<OrientationToPlaceQuestionDTO> answers) {
        validateAnswers(answers);
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
        orientationToPlaceAnswerService.saveAll(orientationToPlaceAnswers);
        return testEntityService.createTestEntity(patientProfile);
    }

    public TestEntity saveOrientationToPlaceAnswerOptionsV2(Long patientProfileId, List<OrientationToPlaceQuestionDTO> answers) {
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
        orientationToPlaceAnswerService.saveAll(orientationToPlaceAnswers);
        return testEntityService.createTestEntity(patientProfile);
    }

    private void validateAnswers(List<OrientationToPlaceQuestionDTO> answers) {
        for (OrientationToPlaceQuestionDTO answer : answers) {
            if (StringUtils.isBlank(answer.getCorrectAnswer())) {
                throw new IllegalArgumentException("Correct answer is required for question: '" + answer.getQuestionText() + "'");
            }
            if (StringUtils.isBlank(answer.getAnswerOptions())) {
                throw new IllegalArgumentException("Answer options are required for question: '" + answer.getQuestionText() + "'");
            }
            List<String> answerOptions = answerOptions(answer.getAnswerOptions());
            if (answerOptions.size() < 3) {
                throw new IllegalArgumentException("At least 3 answer options are required for question: '" + answer.getQuestionText() + "'");
            }
            if (answerOptions.stream().anyMatch(StringUtils::isBlank)) {
                throw new IllegalArgumentException("Answer options should not be empty for question: '" + answer.getQuestionText() + "'");
            }
            if (answerOptions.stream().anyMatch(option -> option.length() > 50)) {
                throw new IllegalArgumentException("Answer options should not be longer than 50 characters for question: '" + answer.getQuestionText() + "'");
            }
            if (!StringUtils.containsIgnoreCase(answer.getAnswerOptions(), answer.getCorrectAnswer())) {
                throw new IllegalArgumentException("Correct answer should be one of the answer options for question: " + answer.getQuestionText());
            }
            for (OrientationToPlaceQuestionDTO orientationToPlaceQuestion : getOrientationToPlaceQuestions()) {
                boolean found = answers.stream().anyMatch(placeQuestionDTO -> orientationToPlaceQuestion.getQuestionId().equals(placeQuestionDTO.getQuestionId()));
                if (!found) {
                    throw new IllegalArgumentException("Question not found: '" + orientationToPlaceQuestion.getQuestionText() + "'. Please try again.");
                }
            }
        }
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
        answerOptions = answerOptions.replace(".", "");
        return List.of(answerOptions.split(","));
    }

    @Transactional(readOnly = true)
    public List<OrientationToPlaceQuestionDTO> getOrientationToPlaceQuestions(Long patientProfileId) {
        PatientProfile patientProfile = patientProfileService.findById(patientProfileId)
            .orElseThrow(() -> new EntityNotFoundException("Patient profile not found with ID: " + patientProfileId));

        List<OrientationToPlaceQuestionDTO> allQuestionsDTO = getOrientationToPlaceQuestions()
            .stream()
            .sorted(Comparator.comparing(OrientationToPlaceQuestionDTO::getQuestionId))
            .toList();

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
