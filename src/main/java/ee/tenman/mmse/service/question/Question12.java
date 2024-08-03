package ee.tenman.mmse.service.question;

import ee.tenman.mmse.domain.UserAnswer;
import ee.tenman.mmse.domain.enumeration.QuestionId;
import ee.tenman.mmse.domain.enumeration.QuestionType;
import ee.tenman.mmse.service.external.openai.NoOpenAiResponseException;
import ee.tenman.mmse.service.external.openai.OpenAiService;
import jakarta.annotation.Resource;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.util.Optional;
import java.util.Set;

@Component
public class Question12 implements Question {

    private static final Logger log = LoggerFactory.getLogger(Question12.class);

    private static final String QUESTION_TEXT = "12. Please name the object shown.";
    private static final QuestionId QUESTION_ID = QuestionId.QUESTION_12;
    private static final Set<String> ACCEPTED_ANSWERS = Set.of(
        "watch",
        "timepiece",
        "wristwatch",
        "chronometer",
        "clock",
        "digital watch",
        "analog watch",
        "quartz watch",
        "smartwatch",
        "pocket watch",
        "stopwatch",
        "diver's watch",
        "chronograph",
        "sports watch",
        "tactical watch",
        "fitness watch",
        "hand watch",
        "automatic watch",
        "mechanical watch",
        "luxury watch",
        "dress watch",
        "pilot watch",
        "field watch",
        "solar watch",
        "atomic watch",
        "LED watch",
        "LCD watch"
    );

    private static final Set<String> INCORRECT_ANSWERS = Set.of(
        "bracelet",
        "bangle",
        "cufflink",
        "ring",
        "necklace",
        "pendant",
        "chain",
        "smart band",
        "fitness tracker",
        "anklet",
        "charm",
        "brooch",
        "earring",
        "sundial",
        "timer",
        "hourglass",
        "metronome",
        "compass",
        "thermometer",
        "barometer",
        "altimeter",
        "speedometer",
        "odometer",
        "calculator",
        "mobile phone"
    );
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

    @Override
    public String getQuestionText() {
        return QUESTION_TEXT;
    }

    @Override
    public String getImage() {
        return convertImageToBase64("images/watch.png");
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
    public int getScore(UserAnswer userAnswer) {
        String answerText = userAnswer.getAnswerText();

        if (StringUtils.isBlank(answerText)) {
            log.debug("Received blank answer.");
            return 0;
        }

        answerText = StringUtils.trim(answerText).replaceAll("[^a-zA-Z0-9\\s]", "");

        if (!containsWordWithThreeLetters(answerText)) {
            log.debug("Answer '{}' does not contain any word with at least three letters.", answerText);
            return 0;
        }

        if (isAcceptedAnswer(answerText)) {
            log.debug("Answer '{}' is in the list of accepted answers.", answerText);
            return 1;
        }

        return evaluateOpenAiResponse(answerText);
    }

    @Override
    public String getCorrectAnswer(Long testEntityId) {
        return "watch";
    }

    private int evaluateOpenAiResponse(String answerText) {
        Optional<String> openAiResponse = checkWithOpenAiService(answerText);
        if (openAiResponse.isEmpty()) {
            log.debug("Answer '{}' was not recognized as correct by OpenAI Service. Response: '{}'", answerText, Question12.NO_RESPONSE);
            return 0;
        }

        String response = openAiResponse.get().toLowerCase();
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

    private boolean isAcceptedAnswer(String answerText) {
        return ACCEPTED_ANSWERS.contains(StringUtils.lowerCase(answerText));
    }

    private Optional<String> checkWithOpenAiService(String answerText) {
        String prompt = prepareOpenAiPrompt(answerText);

        Optional<String> response = openAiService.askQuestion(prompt);
        if (response.isEmpty()) {
            log.debug("Answer '{}' was not recognized as correct by OpenAI Service. Response: '{}'", answerText, NO_RESPONSE);
            throw new NoOpenAiResponseException("OpenAI Service returned no response");
        }

        String openAiResponse = response.get().toLowerCase();
        log.debug("OpenAI Response: {}", openAiResponse);

        return response;
    }

    private String prepareOpenAiPrompt(String answerText) {
        String allAnswers = String.join(", ", ACCEPTED_ANSWERS);
        String allIncorrectAnswers = String.join(", ", INCORRECT_ANSWERS);
        return String.format("Is the phrase \"%s\" close to or misspelled in meaning to any of these: %s? And it is not close to or misspelled in meaning to any of these: %s?. Please answer as 'yes' or 'no'", answerText,
            allAnswers, allIncorrectAnswers);
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
            if (word.length() >= 5) {
                return true;
            }
        }
        return false;
    }

}
