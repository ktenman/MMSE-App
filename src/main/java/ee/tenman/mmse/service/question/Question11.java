package ee.tenman.mmse.service.question;

import ee.tenman.mmse.domain.UserAnswer;
import ee.tenman.mmse.domain.enumeration.QuestionId;
import ee.tenman.mmse.domain.enumeration.QuestionType;
import ee.tenman.mmse.service.openai.OpenAiService;
import jakarta.annotation.Resource;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;
import java.util.Set;

@Component
public class Question11 implements Question {

    private static final Logger log = LoggerFactory.getLogger(Question11.class);

    private static final String QUESTION_TEXT = "Please name the object shown.";
    private static final QuestionId QUESTION_ID = QuestionId.QUESTION_11;
    private static final Set<String> ACCEPTED_ANSWERS = Set.of(
        "pencil",
        "stylus",
        "graphite",
        "lead",
        "writing instrument",
        "scriber",
        "crayon",
        "pen"
    );
    private static final Set<String> INCORRECT_ANSWERS = Set.of(
        "paper",
        "eraser"
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
        return convertImageToBase64("images/pencil.png");
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
    public List<String> getAnswerOptions() {
        return List.of();
    }

    @Override
    public int getScore(UserAnswer userAnswer) {
        String answerText = userAnswer.getAnswerText();

        if (StringUtils.isBlank(answerText)) {
            log.debug("Received blank answer.");
            return 0;
        }

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

    private int evaluateOpenAiResponse(String answerText) {
        Optional<String> openAiResponse = checkWithOpenAiService(answerText);
        if (openAiResponse.isEmpty()) {
            log.debug("Answer '{}' was not recognized as correct by OpenAI Service. Response: '{}'", answerText, Question11.NO_RESPONSE);
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
        try {
            Optional<String> response = openAiService.askQuestion(prompt);
            if (response.isEmpty()) {
                log.warn("OpenAI Service returned no response for prompt: {}", prompt);
                return Optional.empty();
            }

            String openAiResponse = response.get().toLowerCase();
            log.debug("OpenAI Response: {}", openAiResponse);

            return response;
        } catch (Exception e) {
            log.error("Error during OpenAI Service call", e);
            return Optional.empty();
        }
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
            if (word.length() >= 3) {
                return true;
            }
        }
        return false;
    }

}