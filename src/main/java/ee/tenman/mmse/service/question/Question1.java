package ee.tenman.mmse.service.question;

import ee.tenman.mmse.domain.UserAnswer;
import ee.tenman.mmse.domain.enumeration.QuestionId;
import ee.tenman.mmse.domain.enumeration.QuestionType;
import ee.tenman.mmse.service.external.dolphin.DolphinRequest;
import ee.tenman.mmse.service.external.dolphin.DolphinService;
import ee.tenman.mmse.service.external.dolphin.PromptWrapper;
import ee.tenman.mmse.service.external.minio.StorageService;
import ee.tenman.mmse.service.external.similarity.SimilarityRequest;
import ee.tenman.mmse.service.external.similarity.SimilarityService;
import ee.tenman.mmse.service.external.synonym.SynonymRequest;
import ee.tenman.mmse.service.external.synonym.SynonymService;
import ee.tenman.mmse.service.external.transcription.ByteArrayMultipartFile;
import ee.tenman.mmse.service.external.transcription.TranscriptionRequest;
import ee.tenman.mmse.service.external.transcription.TranscriptionResponse;
import ee.tenman.mmse.service.external.transcription.TranscriptionService;
import jakarta.annotation.Resource;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import java.util.Set;

import static ee.tenman.mmse.service.external.transcription.TranscriptionRequest.ModelName.FACEBOOK_WAV_2_VEC_2_LARGE_960_H;

@Component
public class Question1 implements Question {

    private static final Logger log = LoggerFactory.getLogger(Question1.class);

    private static final QuestionId QUESTION_ID = QuestionId.QUESTION_1;

    private static final String SENTENCE = "No ifs, ands, or buts";
    private static final String QUESTION_TEXT = String.format("1. Press the 'Start Recording' button and repeat the " +
        "following phrase: \"%s\". Tap 'Stop Recording' when done.", SENTENCE);

    private static final Set<String> CORRECT_INDICATORS = Set.of(
        "yes",
        "is similar",
        "which directly relates",
        "could be considered somewhat similar",
        "It might",
        "seems to be a misspelling",
        "is close",
        "is a type",
        "closely refers"
    );
    @Resource
    SimilarityService similarityService;
    @Resource
    SynonymService synonymService;
    @Resource
    DolphinService dolphinService;
    @Resource
    private StorageService storageService;
    @Resource
    private TranscriptionService transcriptionService;

    @Override
    public String getQuestionText() {
        return QUESTION_TEXT;
    }

    @Override
    public String getImage() {
        return null;
    }

    @Override
    public QuestionId getQuestionId() {
        return QUESTION_ID;
    }

    @Override
    public QuestionType getQuestionType() {
        return QuestionType.VOICE_INPUT;
    }

    @Override
    public int getScore(UserAnswer userAnswer) {
        String answerText = processVoiceInput(userAnswer.getAnswerText());
        if (StringUtils.isBlank(answerText)) {
            log.debug("Received blank answer.");
            return 0;
        }

        if (!containsWordWithTwoLetters(answerText)) {
            log.debug("Answer '{}' does not contain any word with at least two letters.", answerText);
            return 0;
        }

        if (isSynonym(answerText)) {
            log.debug("Answer '{}' is a synonym to one of the accepted answers.", answerText);
            return 1;
        }
        if (isSimilar(answerText)) {
            log.debug("Answer '{}' is similar to one of the accepted answers.", answerText);
            return 1;
        }
        if (isDolphinSimilar(answerText)) {
            log.debug("Answer '{}' is similar to one of the accepted answers.", answerText);
            return 1;
        } else {
            log.debug("User did not repeat the phrase correctly.");
            return 0;
        }
    }

    @Override
    public String getCorrectAnswer(Long testEntityId) {
        return SENTENCE;
    }

    private String processVoiceInput(String fileName) {
        byte[] bytes = storageService.downloadFile(fileName);
        MultipartFile multipartFile = new ByteArrayMultipartFile(fileName, bytes);
        TranscriptionRequest transcriptionRequest = new TranscriptionRequest(multipartFile, FACEBOOK_WAV_2_VEC_2_LARGE_960_H);
        TranscriptionResponse transcriptionResponse = transcriptionService.transcribe(transcriptionRequest);
        return transcriptionResponse.getTranscription();
    }

    private boolean isSynonym(String answerText) {
        return synonymService.isSynonym(new SynonymRequest(answerText, SENTENCE));
    }

    private boolean isSimilar(String answerText) {
        return similarityService.isSimilar(new SimilarityRequest(answerText, SENTENCE));
    }

    private boolean isDolphinSimilar(String answerText) {
        String prompt = prepareAiPrompt(answerText);
        String response = dolphinService.find(new PromptWrapper(prompt, DolphinRequest.Model.LLAMA_31_70B)).toLowerCase();
        log.debug("DolphinAI Service Response: '{}'", response);
        if (CORRECT_INDICATORS.stream().anyMatch(response::contains)) {
            log.debug("DolphinAI Service deemed answer '{}' as correct. Response: '{}'", answerText, response);
            return true;
        }
        log.debug("DolphinAI Service deemed answer '{}' as incorrect. Response: '{}'", answerText, response);
        return false;
    }

    private String prepareAiPrompt(String answerText) {
        return String.format("Is the phrase \"%s\" same as: %s? Answer only yes/no",
            answerText, SENTENCE);
    }

    private boolean containsWordWithTwoLetters(String answerText) {
        answerText = StringUtils.trim(answerText).replaceAll("[^a-zA-Z0-9\\s]", "").toLowerCase();
        String[] words = answerText.split("\\s+");
        for (String word : words) {
            if (word.length() >= 2) {
                return true;
            }
        }
        return false;
    }

}
