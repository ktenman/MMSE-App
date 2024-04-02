package ee.tenman.mmse.web.rest;

import ee.tenman.mmse.domain.MediaRecording;
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
import ee.tenman.mmse.service.dto.TestEntityDTO;
import ee.tenman.mmse.service.external.minio.StorageService;
import ee.tenman.mmse.service.mapper.PatientProfileMapper;
import ee.tenman.mmse.service.question.Question;
import ee.tenman.mmse.service.question.QuizResult;
import ee.tenman.mmse.service.question.QuizService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ContentDisposition;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

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

    @Autowired
    public QuizController(QuizService quizService, UserAnswerService userAnswerService, TestEntityService testEntityService, StorageService storageService, MediaRecordingRepository mediaRecordingRepository, PatientProfileService patientProfileService, PatientProfileMapper patientProfileMapper) {
        this.quizService = quizService;
        this.userAnswerService = userAnswerService;
        this.testEntityService = testEntityService;
        this.storageService = storageService;
        this.mediaRecordingRepository = mediaRecordingRepository;
        this.patientProfileService = patientProfileService;
        this.patientProfileMapper = patientProfileMapper;
    }

    @GetMapping("/question/{testEntityId}")
    public ResponseEntity<?> getNextQuestion(@PathVariable Long testEntityId) {
        Optional<UserAnswer> latestUserAnswer = userAnswerService.getLatestByTestEntityId(testEntityId);
        if (latestUserAnswer.isEmpty()) {
            Question firstQuestion = quizService.getQuestion();
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
        Question nextQuestion = quizService.getQuestion(nextQuestionId.get());
        return ResponseEntity.ok(nextQuestion);
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

    @GetMapping("/last-recorded-audio/{testEntityId}")
    public ResponseEntity<byte[]> getLastRecordedAudio(
        @RequestParam("questionId") QuestionId questionId,
        @PathVariable Long testEntityId
    ) {
        TestEntity testEntity = testEntityService.getById(testEntityId);
        Optional<MediaRecording> recording = mediaRecordingRepository
            .findFirstByTestEntityIdAndQuestionIdOrderByCreatedAtDesc(testEntity.getId(), questionId);

        if (recording.isEmpty()) {
            return ResponseEntity.notFound().build();
        }

        String fileName = recording.get().getFileName();
        byte[] audioData = storageService.downloadFile(fileName);

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_OCTET_STREAM);
        headers.setContentDisposition(ContentDisposition.attachment().filename(fileName).build());

        return new ResponseEntity<>(audioData, headers, HttpStatus.OK);
    }

    @PostMapping("/upload-audio/{testEntityId}")
    public ResponseEntity<?> uploadAudio(
        @RequestParam("audio") MultipartFile audioFile,
        @RequestParam("questionId") QuestionId questionId,
        @PathVariable Long testEntityId
    ) {
        TestEntity testEntity = testEntityService.getById(testEntityId);

        UUID fileUuid = UUID.randomUUID();
        String fileName = fileUuid + "." + getFileExtension(audioFile.getOriginalFilename());

        storageService.uploadFile(audioFile, fileName);

        MediaRecording mediaRecording = new MediaRecording();
        mediaRecording.setFileName(fileName);
        mediaRecording.setTestEntity(testEntity);
        mediaRecording.setQuestionId(questionId);
        mediaRecordingRepository.save(mediaRecording);

        return ResponseEntity.ok(Map.of("fileName", fileName));
    }

    @PostMapping("/save-orientation-to-place-answers/{patientProfileId}")
    @ResponseStatus(HttpStatus.CREATED)
    public TestEntityDTO saveOrientationToPlaceAnswers(
        @PathVariable Long patientProfileId,
        @RequestBody List<OrientationToPlaceQuestionDTO> answers
    ) {
        return quizService.saveOrientationToPlaceAnswers(patientProfileId, answers);
    }

    private String getFileExtension(String fileName) {
        if (fileName == null || fileName.isEmpty()) {
            return "";
        }
        int dotIndex = fileName.lastIndexOf('.');
        if (dotIndex > 0 && dotIndex < fileName.length() - 1) {
            return fileName.substring(dotIndex + 1);
        }
        return "";
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
