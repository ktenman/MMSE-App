package ee.tenman.mmse.web.rest;

import ee.tenman.mmse.domain.MediaRecording;
import ee.tenman.mmse.domain.TestEntity;
import ee.tenman.mmse.domain.UserAnswer;
import ee.tenman.mmse.domain.enumeration.QuestionId;
import ee.tenman.mmse.repository.MediaRecordingRepository;
import ee.tenman.mmse.service.TestEntityService;
import ee.tenman.mmse.service.UserAnswerService;
import ee.tenman.mmse.service.dto.AnswerDTO;
import ee.tenman.mmse.service.dto.FileDTO;
import ee.tenman.mmse.service.dto.QuestionDTO;
import ee.tenman.mmse.service.dto.TestEntityDTO;
import ee.tenman.mmse.service.external.minio.StorageService;
import ee.tenman.mmse.service.mapper.TestEntityMapper;
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

import java.util.Optional;
import java.util.UUID;

@RestController
@RequestMapping("/api/test")
public class TestController {

    private final QuizService quizService;
    private final UserAnswerService userAnswerService;
    private final TestEntityService testEntityService;
    private final StorageService storageService;
    private final MediaRecordingRepository mediaRecordingRepository;
    private final TestEntityMapper testEntityMapper;

    @Autowired
    public TestController(
        QuizService quizService,
        UserAnswerService userAnswerService,
        TestEntityService testEntityService,
        StorageService storageService,
        MediaRecordingRepository mediaRecordingRepository,
        TestEntityMapper testEntityMapper) {
        this.quizService = quizService;
        this.userAnswerService = userAnswerService;
        this.testEntityService = testEntityService;
        this.storageService = storageService;
        this.mediaRecordingRepository = mediaRecordingRepository;
        this.testEntityMapper = testEntityMapper;
    }

    @GetMapping("/question/{testEntityHash}")
    public ResponseEntity<?> getNextQuestionByTestEntityHash(@PathVariable String testEntityHash) {
        TestEntity testEntity = testEntityService.findByHash(testEntityHash)
            .orElseThrow(() -> new IllegalArgumentException(String.format("Test entity not found for hash %s", testEntityHash)));
        Optional<UserAnswer> latestUserAnswer = userAnswerService.getLatestByTestEntityId(testEntity.getId());
        if (latestUserAnswer.isEmpty()) {
            QuestionDTO firstQuestion = quizService.getQuestion(QuestionId.QUESTION_1, testEntity.getId());
            return ResponseEntity.ok(firstQuestion);
        }
        Optional<QuestionId> nextQuestionId = getNextQuestionId(latestUserAnswer.get().getQuestionId());
        if (nextQuestionId.isEmpty()) {
            QuizResult quizResult = quizService.calculateScore(testEntity.getId());
            testEntity.setScore(quizResult.getScore());
            testEntityService.save(testEntity);
            String result = String.format("Quiz has ended. Your score is %d/%d", quizResult.getScore(), quizResult.getMaxScore());
            return ResponseEntity.ok().body(result);
        }
        QuestionDTO nextQuestion = quizService.getQuestion(nextQuestionId.get(), testEntity.getId());
        return ResponseEntity.ok(nextQuestion);
    }

    @GetMapping("/test-entity/{testEntityHash}")
    public TestEntityDTO getTestEntityByHash(@PathVariable String testEntityHash) {
        TestEntity testEntity = testEntityService.findByHash(testEntityHash)
            .orElseThrow(() -> new IllegalArgumentException(String.format("Test entity not found for hash %s", testEntityHash)));
        return testEntityMapper.toDto(testEntity);
    }

    @PostMapping("/answer/{testEntityId}")
    @ResponseStatus(HttpStatus.CREATED)
    public void saveAnswer(@RequestBody @Valid AnswerDTO answerDTO, @PathVariable Long testEntityId) {
        quizService.saveAnswer(answerDTO, testEntityId);
    }

    @GetMapping("/file/{testEntityId}")
    public ResponseEntity<byte[]> getFile(
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

    @PostMapping("/file/{testEntityId}")
    public FileDTO uploadFile(
        @RequestParam("file") MultipartFile multipartFile,
        @RequestParam("questionId") QuestionId questionId,
        @PathVariable Long testEntityId
    ) {
        TestEntity testEntity = testEntityService.getById(testEntityId);

        UUID fileUuid = UUID.randomUUID();
        String fileName = fileUuid + "." + getFileExtension(multipartFile.getOriginalFilename());

        storageService.uploadFile(multipartFile, fileName);

        MediaRecording mediaRecording = new MediaRecording();
        mediaRecording.setFileName(fileName);
        mediaRecording.setTestEntity(testEntity);
        mediaRecording.setQuestionId(questionId);
        mediaRecordingRepository.save(mediaRecording);

        return new FileDTO(fileName);
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
