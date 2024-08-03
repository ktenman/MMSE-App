package ee.tenman.mmse.web.rest;

import ee.tenman.mmse.domain.PatientProfile;
import ee.tenman.mmse.domain.TestEntity;
import ee.tenman.mmse.domain.UserAnswer;
import ee.tenman.mmse.domain.enumeration.QuestionId;
import ee.tenman.mmse.repository.MediaRecordingRepository;
import ee.tenman.mmse.service.PatientProfileService;
import ee.tenman.mmse.service.TestEntityService;
import ee.tenman.mmse.service.UserAnswerService;
import ee.tenman.mmse.service.dto.AnswerDTO;
import ee.tenman.mmse.service.dto.OrientationToPlaceQuestionDTO;
import ee.tenman.mmse.service.dto.PatientProfileDTO;
import ee.tenman.mmse.service.dto.PatientProfileRequest;
import ee.tenman.mmse.service.dto.QuestionDTO;
import ee.tenman.mmse.service.dto.TestEntityDTO;
import ee.tenman.mmse.service.external.minio.StorageService;
import ee.tenman.mmse.service.mapper.PatientProfileMapper;
import ee.tenman.mmse.service.mapper.TestEntityMapper;
import ee.tenman.mmse.service.question.QuizResult;
import ee.tenman.mmse.service.question.QuizService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api")
public class QuizController {

    private final QuizService quizService;
    private final UserAnswerService userAnswerService;
    private final TestEntityService testEntityService;
    private final StorageService storageService;
    private final MediaRecordingRepository mediaRecordingRepository;
    private final PatientProfileService patientProfileService;
    private final PatientProfileMapper patientProfileMapper;
    private final TestEntityMapper testEntityMapper;

    @Autowired
    public QuizController(QuizService quizService, UserAnswerService userAnswerService, TestEntityService testEntityService, StorageService storageService, MediaRecordingRepository mediaRecordingRepository, PatientProfileService patientProfileService, PatientProfileMapper patientProfileMapper, TestEntityMapper testEntityMapper) {
        this.quizService = quizService;
        this.userAnswerService = userAnswerService;
        this.testEntityService = testEntityService;
        this.storageService = storageService;
        this.mediaRecordingRepository = mediaRecordingRepository;
        this.patientProfileService = patientProfileService;
        this.patientProfileMapper = patientProfileMapper;
        this.testEntityMapper = testEntityMapper;
    }

    @GetMapping("/question/{testEntityId}")
    public ResponseEntity<?> getNextQuestion(@PathVariable Long testEntityId) {
        Optional<UserAnswer> latestUserAnswer = userAnswerService.getLatestByTestEntityId(testEntityId);
        if (latestUserAnswer.isEmpty()) {
            QuestionDTO firstQuestion = quizService.getQuestion(QuestionId.QUESTION_1, testEntityId);
            return ResponseEntity.ok(firstQuestion);
        }
        Optional<QuestionId> nextQuestionId = getNextQuestionId(latestUserAnswer.get().getQuestionId());
        if (nextQuestionId.isEmpty()) {
            TestEntity testEntity = testEntityService.getById(testEntityId);
            QuizResult quizResult = quizService.calculateScore(testEntity.getId());
            testEntity.setScore(quizResult.getScore());
            testEntityService.save(testEntity);
            String result = String.format("Quiz has ended. Your score is %d/%d", quizResult.getScore(), quizResult.getMaxScore());
            return ResponseEntity.ok().body(result);
        }
        QuestionDTO nextQuestion = quizService.getQuestion(nextQuestionId.get(), testEntityId);
        return ResponseEntity.ok(nextQuestion);
    }

    @GetMapping("/results/{testEntityId}")
    public ResponseEntity<?> getResults(@PathVariable Long testEntityId) {
        TestEntity testEntity = testEntityService.getById(testEntityId);
        QuizResult quizResult = quizService.calculateScore(testEntity.getId());
        testEntity.setScore(quizResult.getScore());
        testEntityService.save(testEntity);
        String result = String.format("Quiz has ended. Your score is %d/%d", quizResult.getScore(), quizResult.getMaxScore());
        return ResponseEntity.ok().body(result);
    }

    @PostMapping("/answer/{testEntityId}")
    @ResponseStatus(HttpStatus.CREATED)
    public void saveAnswer(@RequestBody @Valid AnswerDTO answerDTO, @PathVariable Long testEntityId) {
        quizService.saveAnswer(answerDTO, testEntityId);
    }

    @GetMapping("/orientation-to-place-questions")
    public List<OrientationToPlaceQuestionDTO> getOrientationToPlaceQuestions() {
        return quizService.getOrientationToPlaceQuestions();
    }

    @GetMapping("/orientation-to-place-questions/{patientProfileId}")
    public List<OrientationToPlaceQuestionDTO> getOrientationToPlaceQuestionsByPatientProfileId(@PathVariable Long patientProfileId) {
        return quizService.getOrientationToPlaceQuestions(patientProfileId);
    }

    @PostMapping("/start")
    public PatientProfileDTO startQuiz(@RequestBody PatientProfileRequest patientProfileRequest) {
        Optional<TestEntity> uncompletedTest = testEntityService.findByPatientIdUncompleted(patientProfileRequest.getPatientId());
        if (uncompletedTest.isPresent()) {
            PatientProfile existingPatientProfile = uncompletedTest.get().getPatientProfile();
            return patientProfileMapper.toDto(existingPatientProfile);
        }
        PatientProfile patientProfile = patientProfileService.createPatientProfile(patientProfileRequest);
        return patientProfileMapper.toDto(patientProfile);
    }

    @PostMapping("/orientation-to-place/correct-answers/{patientProfileId}")
    @ResponseStatus(HttpStatus.OK)
    public List<OrientationToPlaceQuestionDTO> saveOrientationToPlaceCorrectAnswers(
        @PathVariable Long patientProfileId,
        @RequestBody List<OrientationToPlaceQuestionDTO> answers
    ) {
        return quizService.saveOrientationToPlaceCorrectAnswers(patientProfileId, answers);
    }

    @PostMapping("/orientation-to-place/answer-options/{patientProfileId}")
    @ResponseStatus(HttpStatus.OK)
    public TestEntityDTO saveOrientationToPlaceAnswerOptions(
        @PathVariable Long patientProfileId,
        @RequestBody List<OrientationToPlaceQuestionDTO> answers
    ) {
        TestEntity testEntity = quizService.saveOrientationToPlaceAnswerOptions(patientProfileId, answers);
        return testEntityMapper.toDto(testEntity);
    }

    private Optional<QuestionId> getNextQuestionId(QuestionId currentQuestionId) {
        QuestionId[] questionIds = QuestionId.values();
        int nextQuestionIndex = currentQuestionId.ordinal() + 1;

        if (nextQuestionIndex < questionIds.length) {
            return Optional.of(questionIds[nextQuestionIndex]);
        }

        return Optional.empty();
    }

}
