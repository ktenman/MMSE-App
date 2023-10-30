package ee.tenman.mmse.service.question;

import ee.tenman.mmse.domain.UserAnswer;
import ee.tenman.mmse.domain.enumeration.QuestionId;
import ee.tenman.mmse.domain.enumeration.QuestionType;
import ee.tenman.mmse.service.openai.OpenAIApiService;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class Question11 implements Question {

    private static final String QUESTION_TEXT = "Please name the object shown.";
    private static final QuestionId QUESTION_ID = QuestionId.QUESTION_11;
    private static final String CORRECT_ANSWER = "pencil";
    @Resource
    OpenAIApiService openAIApiService;

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
        return this.QUESTION_ID;
    }

    @Override
    public QuestionType getQuestionType() {
        return QuestionType.TEXT_INPUT;
    }

    @Override
    public List<String> getAnswerOptions() {
        return List.of(); // No predefined options since it's a text input.
    }

    @Override
    public int getScore(UserAnswer userAnswer) {
        String prompt = "Is the phrase \"" + userAnswer.getAnswerText() + "\" similar in meaning to \"" + CORRECT_ANSWER + "\"?";

        String modelResponse;
        try {
            modelResponse = openAIApiService.callGPT(prompt);
            assert modelResponse != null;
            return modelResponse.contains("yes") ? 1 : 0;
        } catch (RuntimeException e) {
            return 0;
        }
    }


}
