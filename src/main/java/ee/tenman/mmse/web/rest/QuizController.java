package ee.tenman.mmse.web.rest;

import ee.tenman.mmse.domain.MediaRecording;
import ee.tenman.mmse.domain.TestEntity;
import ee.tenman.mmse.domain.UserAnswer;
import ee.tenman.mmse.domain.enumeration.QuestionId;
import ee.tenman.mmse.repository.MediaRecordingRepository;
import ee.tenman.mmse.service.TestEntityService;
import ee.tenman.mmse.service.UserAnswerService;
import ee.tenman.mmse.service.dto.AnswerDTO;
import ee.tenman.mmse.service.external.minio.StorageService;
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
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.util.Map;
import java.util.NoSuchElementException;
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

    @Autowired
    public QuizController(QuizService quizService, UserAnswerService userAnswerService, TestEntityService testEntityService, StorageService storageService, MediaRecordingRepository mediaRecordingRepository) {
        this.quizService = quizService;
        this.userAnswerService = userAnswerService;
        this.testEntityService = testEntityService;
        this.storageService = storageService;
        this.mediaRecordingRepository = mediaRecordingRepository;
    }

    @GetMapping("/question")
    public ResponseEntity<?> getNextQuestion() {
        Optional<UserAnswer> latestUserAnswer = userAnswerService.getLatest();
        if (latestUserAnswer.isPresent()) {
            Optional<QuestionId> nextQuestionId = getNextQuestionId(latestUserAnswer.get().getQuestionId());
            return handleNextQuestion(nextQuestionId);
        } else {
            Question firstQuestion = quizService.getFirstQuestion();
            return ResponseEntity.ok(firstQuestion);
        }
    }

    @PostMapping("/answer")
    @ResponseStatus(HttpStatus.CREATED)
    public void saveAnswer(@RequestBody @Valid AnswerDTO answerDTO) {
        quizService.saveAnswer(answerDTO);
    }

    @PostMapping("/retake")
    public Question retakeTest() {
        return quizService.retakeTest();
    }

    @GetMapping("/last-recorded-audio")
    public ResponseEntity<byte[]> getLastRecordedAudio(@RequestParam("questionId") QuestionId questionId) {
        TestEntity testEntity = testEntityService.getLast();
        MediaRecording mediaRecording = mediaRecordingRepository
            .findFirstByTestEntityIdAndQuestionIdOrderByCreatedAtDesc(testEntity.getId(), questionId)
            .orElseThrow(() -> new NoSuchElementException("No media recording found"));

        String fileName = mediaRecording.getFileName();
        byte[] audioData = storageService.downloadFile(fileName);

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_OCTET_STREAM);
        headers.setContentDisposition(ContentDisposition.attachment().filename(fileName).build());

        return new ResponseEntity<>(audioData, headers, HttpStatus.OK);
    }

    @PostMapping("/upload-audio")
    public ResponseEntity<?> uploadAudio(
        @RequestParam("audio") MultipartFile audioFile,
        @RequestParam("questionId") QuestionId questionId
    ) {
        TestEntity testEntity = testEntityService.getLast();

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

    private ResponseEntity<?> handleNextQuestion(Optional<QuestionId> nextQuestionId) {
        if (nextQuestionId.isEmpty()) {
            TestEntity testEntity = testEntityService.getLast();
            QuizResult quizResult = quizService.calculateScore(testEntity.getId());
            testEntity.setScore(quizResult.getScore());
            testEntityService.save(testEntity);
            String result = String.format("Quiz has ended. Your score is %d/%d", quizResult.getScore(), quizResult.getMaxScore());
            return ResponseEntity.ok().body(result);
        }
        Question nextQuestion = quizService.getQuestion(nextQuestionId.get());
        return ResponseEntity.ok(nextQuestion);
    }

}
