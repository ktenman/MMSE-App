package ee.tenman.mmse.service.question;

import ee.tenman.mmse.domain.UserAnswer;
import ee.tenman.mmse.domain.enumeration.QuestionId;
import ee.tenman.mmse.domain.enumeration.QuestionType;
import ee.tenman.mmse.service.external.minio.StorageService;
import ee.tenman.mmse.service.external.openai.OpenAiRequest;
import ee.tenman.mmse.service.external.openai.OpenAiService;
import jakarta.annotation.Resource;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;

@Component
public class Question22 implements Question {

    private static final String QUESTION_TEXT = "22. Copy the drawing in the gray area below";
    private static final QuestionId QUESTION_ID = QuestionId.QUESTION_22;

    @Resource
    private StorageService storageService;
    @Resource
    private OpenAiService openAiService;

    @Override
    public String getQuestionText() {
        return QUESTION_TEXT;
    }

    @Override
    public String getImage() {
        return convertImageToBase64("images/intersecting_pentagons.png");
    }

    @Override
    public QuestionId getQuestionId() {
        return QUESTION_ID;
    }

    @Override
    public QuestionType getQuestionType() {
        return QuestionType.DRAWING;
    }

    @Override
    public int getScore(UserAnswer userAnswer) {
        byte[] bytes = storageService.downloadFile(userAnswer.getAnswerText());
        OpenAiRequest openAiRequest = OpenAiRequest.createWithMessageAndImage(getLLMPrompt(null), bytes);
        Optional<String> completion = openAiService.createCompletion(openAiRequest);
        if (completion.isPresent()) {
            return StringUtils.containsIgnoreCase(completion.get(), "yes") ? 1 : 0;
        }
        throw new IllegalStateException("OpenAI completion failed");
    }

    @Override
    public String getLLMPrompt(String input) {
        return "Answer only yes/no if this image has: All 10 angles are present. Two 5-sided figures intersect. The " +
            "intersection forms a 4-sided figure. The 4-sided figure at the intersection is roughly a rhombus or " +
            "diamond shape (it does not have to be perfect).";
    }

    @Override
    public List<String> getInstructions() {
        return List.of(
            "Use your finger or mouse to draw lines. Take your time and copy carefully.",
            "To fix mistakes, use the \"Eraser\" button. To erase, use the \"Clear\" button.",
            "Use the \"Save Drawing\" button when finished, then proceed to the next task."
        );
    }

    @Override
    public String getCorrectAnswer(Long testEntityId) {
        return "The drawing should have all 10 angles present, two 5-sided figures intersecting, " +
            "and the intersection forming a 4-sided figure that is roughly a rhombus or diamond shape.";
    }
}
