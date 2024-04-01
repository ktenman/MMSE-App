package ee.tenman.mmse.domain;

import ee.tenman.mmse.converter.StringListConverter;
import ee.tenman.mmse.domain.enumeration.QuestionId;
import jakarta.persistence.Column;
import jakarta.persistence.Convert;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotNull;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "orientation_to_place_answer")
public class OrientationToPlaceAnswer extends BaseEntity {

    @NotNull
    @Enumerated(EnumType.STRING)
    @Column(name = "question_id", nullable = false)
    private QuestionId questionId;

    @Column(name = "correct_answer")
    private String correctAnswer;

    @NotNull
    @Column(name = "answer_options")
    @Convert(converter = StringListConverter.class)
    private List<String> answerOptions = new ArrayList<>();

    @ManyToOne(optional = false)
    @NotNull
    private PatientProfile patientProfile;

    public QuestionId getQuestionId() {
        return questionId;
    }

    public void setQuestionId(QuestionId questionId) {
        this.questionId = questionId;
    }

    public List<String> getAnswerOptions() {
        return answerOptions;
    }

    public void setAnswerOptions(List<String> answerOptions) {
        this.answerOptions = answerOptions;
    }

    public PatientProfile getPatientProfile() {
        return patientProfile;
    }

    public void setPatientProfile(PatientProfile patientProfile) {
        this.patientProfile = patientProfile;
    }

    public String getCorrectAnswer() {
        return correctAnswer;
    }

    public void setCorrectAnswer(String correctAnswer) {
        this.correctAnswer = correctAnswer;
    }
}
