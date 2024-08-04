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
import jakarta.annotation.Resource;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;

import java.util.Collections;
import java.util.List;

@Component
public class Question18 implements Question {

    private static final String QUESTION = "What floor of the building are you on?";
    private static final String QUESTION_TEXT = "18. " + QUESTION;
    private static final QuestionId QUESTION_ID = QuestionId.QUESTION_18;

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
        return QuestionType.MULTIPLE_CHOICE;
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
    public int getScore(UserAnswer userAnswer) {
        return StringUtils.equalsIgnoreCase(userAnswer.getAnswerText().strip(), userAnswer.getCorrectAnswer()) ? 1 : 0;
    }

    @Override
    public boolean isOrientationToPlace() {
        return true;
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
}
