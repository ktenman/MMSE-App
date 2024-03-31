package ee.tenman.mmse.domain;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import ee.tenman.mmse.domain.enumeration.QuestionId;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotNull;

@Entity
@Table(name = "user_answer")
public class UserAnswer extends BaseEntity {

    @Column(name = "answer_text")
    private String answerText;

    @Column(name = "score")
    private Integer score;

    @Column(name = "maximum_score")
    private Integer maximumScore;

    @NotNull
    @Enumerated(EnumType.STRING)
    @Column(name = "question_id", nullable = false)
    private QuestionId questionId;

    @ManyToOne(optional = false)
    @NotNull
    @JsonIgnoreProperties(value = {"user"}, allowSetters = true)
    private TestEntity testEntity;

    public String getAnswerText() {
        return answerText;
    }

    public void setAnswerText(String answerText) {
        this.answerText = answerText;
    }

    public Integer getScore() {
        return score;
    }

    public void setScore(Integer score) {
        this.score = score;
    }

    public Integer getMaximumScore() {
        return maximumScore;
    }

    public void setMaximumScore(Integer maximumScore) {
        this.maximumScore = maximumScore;
    }

    public QuestionId getQuestionId() {
        return questionId;
    }

    public void setQuestionId(QuestionId questionId) {
        this.questionId = questionId;
    }

    public TestEntity getTestEntity() {
        return testEntity;
    }

    public void setTestEntity(TestEntity testEntity) {
        this.testEntity = testEntity;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof UserAnswer)) {
            return false;
        }
        return getId() != null && getId().equals(((UserAnswer) o).getId());
    }

    @Override
    public int hashCode() {
        // see https://vladmihalcea.com/how-to-implement-equals-and-hashcode-using-the-jpa-entity-identifier/
        return getClass().hashCode();
    }

    // prettier-ignore

    @Override
    public String toString() {
        return "UserAnswer{" +
            "id=" + getId() +
            ", answerText='" + answerText + '\'' +
            ", createdAt=" + getCreatedAt() +
            ", updatedAt=" + getUpdatedAt() +
            ", score=" + score +
            ", maximumScore=" + maximumScore +
            ", questionId=" + questionId +
            '}';
    }
}
