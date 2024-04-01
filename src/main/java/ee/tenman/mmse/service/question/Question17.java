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
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;

@Component
public class Question17 implements Question {

    private static final String QUESTION_TEXT = "17. What city are you in?";
    private static final QuestionId QUESTION_ID = QuestionId.QUESTION_17;
    private static final String SAMPLE_CORRECT_ANSWER = "Tartu";
    private static final List<String> SAMPLE_ANSWERS = List.of(SAMPLE_CORRECT_ANSWER, "Tallinn", "Pärnu", "Narva");

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
    public List<String> getAnswerOptions() {
        TestEntity testEntity = testEntityService.getLast();
        PatientProfile patientProfile = patientProfileService.getByTestEntity(testEntity);
        return orientationToPlaceAnswerService
            .findByPatientProfileAndQuestionId(patientProfile, QUESTION_ID)
            .or(() -> {
                OrientationToPlaceAnswer newAnswer = new OrientationToPlaceAnswer();
                newAnswer.setQuestionId(QUESTION_ID);
                newAnswer.setCorrectAnswer(SAMPLE_CORRECT_ANSWER);
                newAnswer.setAnswerOptions(SAMPLE_ANSWERS);
                newAnswer.setPatientProfile(patientProfile);
                orientationToPlaceAnswerService.save(newAnswer);
                return Optional.of(newAnswer);
            })
            .map(OrientationToPlaceAnswer::getAnswerOptions)
            .orElseThrow(() -> new IllegalStateException("No answer options found"));
    }

    @Override
    public int getScore(UserAnswer userAnswer) {
        TestEntity testEntity = testEntityService.getByUserAnswer(userAnswer);
        PatientProfile patientProfile = patientProfileService.getByTestEntity(testEntity);
        Optional<OrientationToPlaceAnswer> correctAnswer = orientationToPlaceAnswerService
            .findByPatientProfileAndQuestionId(patientProfile, QUESTION_ID)
            .stream()
            .filter(answer -> answer.getQuestionId().equals(QUESTION_ID))
            .findFirst()
            .filter(answer -> answer.getCorrectAnswer().equalsIgnoreCase(userAnswer.getAnswerText()));
        return correctAnswer.isPresent() ? 1 : 0;
    }

    @Override
    public boolean isOrientationToPlace() {
        return true;
    }
}
