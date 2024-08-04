package ee.tenman.mmse.service.question;

import ee.tenman.mmse.domain.OrientationToPlaceAnswer;
import ee.tenman.mmse.domain.PatientProfile;
import ee.tenman.mmse.domain.TestEntity;
import ee.tenman.mmse.domain.UserAnswer;
import ee.tenman.mmse.domain.enumeration.QuestionId;
import ee.tenman.mmse.domain.enumeration.QuestionType;
import ee.tenman.mmse.service.OrientationToPlaceAnswerService;
import ee.tenman.mmse.service.PatientProfileService;
import ee.tenman.mmse.service.TestEntityService;
import ee.tenman.mmse.service.external.dolphin.DolphinRequest;
import ee.tenman.mmse.service.external.dolphin.DolphinService;
import ee.tenman.mmse.service.external.dolphin.PromptWrapper;
import ee.tenman.mmse.service.external.openai.NoOpenAiResponseException;
import ee.tenman.mmse.service.external.openai.OpenAiService;
import ee.tenman.mmse.service.external.similarity.SimilarityRequest;
import ee.tenman.mmse.service.external.similarity.SimilarityService;
import ee.tenman.mmse.service.external.synonym.SynonymRequest;
import ee.tenman.mmse.service.external.synonym.SynonymService;
import jakarta.annotation.Resource;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.Set;

@Component
public class Question20 implements Question {

    private static final Logger log = LoggerFactory.getLogger(Question20.class);

    private static final String QUESTION = "What county/region are you in?";
    private static final String QUESTION_TEXT = "20. " + QUESTION;
    private static final QuestionId QUESTION_ID = QuestionId.QUESTION_20;

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

    private static final Set<String> INCORRECT_INDICATORS = Set.of(
        "no",
        "is not similar",
        "incorrect",
        "which does not directly relate",
        "could not be considered somewhat similar",
        "It might not",
        "does not seem to be a misspelling",
        "is not close",
        "is not a type",
        "does not closely refer",
        "unable",
        "impossible"
    );

    private static final String POSITIVE = "positive";
    private static final String NEGATIVE = "negative";
    private static final String NO_RESPONSE = "No response";

    @Resource
    OpenAiService openAiService;

    @Resource
    SimilarityService similarityService;

    @Resource
    SynonymService synonymService;

    @Resource
    DolphinService dolphinService;

    @Resource
    private TestEntityService testEntityService;

    @Resource
    private PatientProfileService patientProfileService;

    @Resource
    private OrientationToPlaceAnswerService orientationToPlaceAnswerService;

    @Override
    public String getQuestionText() {
        return QUESTION_TEXT;
    }

    @Override
    public QuestionId getQuestionId() {
        return QUESTION_ID;
    }

    @Override
    public QuestionType getQuestionType() {
        return QuestionType.TEXT_INPUT;
    }

    @Override
    public List<String> getAnswerOptions(Long testEntityId) {
        TestEntity testEntity = testEntityService.getById(testEntityId);
        PatientProfile patientProfile = patientProfileService.getByTestEntity(testEntity);
        List<String> answerOptions = orientationToPlaceAnswerService
            .findByPatientProfileAndQuestionId(patientProfile, QUESTION_ID)
            .map(OrientationToPlaceAnswer::getAnswerOptions)
            .orElseThrow(() -> new IllegalStateException("No answer options found"));
        Collections.shuffle(answerOptions);
        return answerOptions;
    }

    @Override
    public boolean isOrientationToPlace() {
        return true;
    }


    @Override
    public int getScore(UserAnswer userAnswer) {
        String answerText = userAnswer.getAnswerText().strip();
        String correctAnswer = userAnswer.getCorrectAnswer();

        if (StringUtils.isBlank(answerText)) {
            log.debug("Received blank answer.");
            return 0;
        }

        answerText = StringUtils.trim(answerText).replaceAll("[^a-zA-Z0-9\\s]", "").toLowerCase();

        if (!containsWordWithThreeLetters(answerText)) {
            log.debug("Answer '{}' does not contain any word with at least three letters.", answerText);
            return 0;
        }

        if (isSynonym(answerText, correctAnswer)) {
            log.debug("Answer '{}' is a synonym to one of the accepted answers.", answerText);
            return 1;
        }

        if (isSimilar(answerText, correctAnswer)) {
            log.debug("Answer '{}' is similar to one of the accepted answers.", answerText);
            return 1;
        }

        if (isDolphinSimilar(answerText, correctAnswer)) {
            log.debug("Answer '{}' is similar to one of the accepted answers.", answerText);
            return 1;
        }
        Optional<String> openAiResponse = checkWithOpenAiService(answerText, correctAnswer);
        return evaluateOpenAiResponse(answerText, openAiResponse);
    }

    private int evaluateOpenAiResponse(String answerText, Optional<String> responseText) {
        if (responseText.isEmpty()) {
            log.debug("Answer '{}' was not recognized as correct by OpenAI Service", answerText);
            return 0;
        }

        String response = responseText.get().toLowerCase();
        log.debug("OpenAI Service Response: '{}'", response);

        if (CORRECT_INDICATORS.stream().anyMatch(response::contains)) {
            log.debug("OpenAI Service deemed answer '{}' as correct. Response: '{}'", answerText, response);
            return 1;
        }

        if (INCORRECT_INDICATORS.stream().anyMatch(response::contains)) {
            log.debug("OpenAI Service deemed answer '{}' as incorrect. Response: '{}'", answerText, response);
            return 0;
        }

        log.debug("OpenAI Service deemed answer '{}' as incorrect. Response: '{}'", answerText, response);
        return performSecondaryOpenAiCheck(response);
    }

    private boolean isDolphinSimilar(String answerText, String correctAnswer) {
        String prompt = prepareAiPrompt(answerText, correctAnswer);
        String response = dolphinService.find(new PromptWrapper(prompt, DolphinRequest.Model.GEMMA_2_9B)).toLowerCase();
        log.debug("DolphinAI Service Response: '{}'", response);
        if (CORRECT_INDICATORS.stream().anyMatch(response::contains)) {
            log.debug("DolphinAI Service deemed answer '{}' as correct. Response: '{}'", answerText, response);
            return true;
        }
        log.debug("DolphinAI Service deemed answer '{}' as incorrect. Response: '{}'", answerText, response);
        return false;
    }

    private boolean isSynonym(String answerText, String correctAnswer) {
        return synonymService.isSynonym(new SynonymRequest(answerText, correctAnswer));
    }

    private boolean isSimilar(String answerText, String correctAnswer) {
        return similarityService.isSimilar(new SimilarityRequest(answerText, correctAnswer));
    }


    @Override
    public String getLLMPrompt(String input) {
        return String.format("%s is the correct answer to the question: '" + QUESTION + "'. Please give me four different answer " +
            "options to this question. Separate these by commas, and only give me answers, nothing else. These should " +
            "be one to two-word options, and one of the answers should be '%s'. However, these options shouldn't be " +
            "similar; they should be quite different because it's part of a mini-mental examination. Don't give me any " +
            "examples or an explanation.", input, input);
    }

    @Override
    public String getCorrectAnswer(Long testEntityId) {
        TestEntity testEntity = testEntityService.getById(testEntityId);
        PatientProfile patientProfile = patientProfileService.getByTestEntity(testEntity);
        return orientationToPlaceAnswerService
            .findByPatientProfileAndQuestionId(patientProfile, QUESTION_ID)
            .map(OrientationToPlaceAnswer::getCorrectAnswer)
            .orElseThrow(() -> new IllegalStateException("No correct answer found"));
    }

    private Optional<String> checkWithOpenAiService(String answerText, String correctAnswer) {
        String prompt = prepareAiPrompt(answerText, correctAnswer);

        Optional<String> response = openAiService.askQuestion(prompt);
        if (response.isEmpty()) {
            log.debug("Answer '{}' was not recognized as correct by OpenAI Service. Response: '{}'", answerText, NO_RESPONSE);
            throw new NoOpenAiResponseException("OpenAI Service returned no response");
        }

        String openAiResponse = response.get().toLowerCase();
        log.debug("OpenAI Response: {}", openAiResponse);

        return response;
    }

    private String prepareAiPrompt(String answerText, String correctAnswer) {
        return String.format("Is the phrase \"%s\" close to or misspelled in meaning to any of these: %s? Answer only yes/no", answerText,
            correctAnswer);
    }

    private int performSecondaryOpenAiCheck(String openAiResponse) {
        Optional<String> response = openAiService.askQuestion(
            String.format("Is this phrase %s %s or %s?", openAiResponse, POSITIVE, NEGATIVE)
        );

        if (response.isPresent() && response.get().toLowerCase().contains(POSITIVE)) {
            return 1;
        }
        return 0;
    }

    private boolean containsWordWithThreeLetters(String answerText) {
        String[] words = answerText.split("\\s+");
        for (String word : words) {
            if (word.length() >= 3) {
                return true;
            }
        }
        return false;
    }
}
