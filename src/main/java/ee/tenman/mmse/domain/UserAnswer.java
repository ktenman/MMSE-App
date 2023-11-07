package ee.tenman.mmse.domain;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import ee.tenman.mmse.domain.enumeration.QuestionId;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.PostUpdate;
import jakarta.persistence.PrePersist;
import jakarta.persistence.PreUpdate;
import jakarta.persistence.SequenceGenerator;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotNull;
import org.mapstruct.Builder;

import java.io.Serializable;
import java.time.Instant;

/**
 * A UserAnswer.
 */
@Entity
@Table(name = "user_answer")
@SuppressWarnings("common-java:DuplicatedBlocks")
public class UserAnswer implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "sequenceGenerator")
    @SequenceGenerator(name = "sequenceGenerator")
    @Column(name = "id")
    private Long id;

    @Column(name = "answer_text")
    private String answerText;

    @NotNull
    @Column(name = "created_at", nullable = false)
    private Instant createdAt = Instant.now();

    @Column(name = "updated_at")
    private Instant updatedAt;

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

    @PrePersist
    public void prePersist() {
        this.createdAt = Instant.now();
        this.updatedAt = Instant.now(); // Set updatedAt on insert as well.
    }

    @PreUpdate
    public void preUpdate() {
        this.updatedAt = Instant.now(); // Update the updatedAt field before update.
    }

    // jhipster-needle-entity-add-field - JHipster will add fields here

    public Long getId() {
        return this.id;
    }

    public UserAnswer id(Long id) {
        this.setId(id);
        return this;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getAnswerText() {
        return this.answerText;
    }

    public UserAnswer answerText(String answerText) {
        this.setAnswerText(answerText);
        return this;
    }

    public void setAnswerText(String answerText) {
        this.answerText = answerText;
    }

    public Instant getCreatedAt() {
        return this.createdAt;
    }

    public UserAnswer createdAt(Instant createdAt) {
        this.setCreatedAt(createdAt);
        return this;
    }

    public void setCreatedAt(Instant createdAt) {
        this.createdAt = createdAt;
    }

    public Instant getUpdatedAt() {
        return this.updatedAt;
    }

    public UserAnswer updatedAt(Instant updatedAt) {
        this.setUpdatedAt(updatedAt);
        return this;
    }

    public void setUpdatedAt(Instant updatedAt) {
        this.updatedAt = updatedAt;
    }

    public QuestionId getQuestionId() {
        return this.questionId;
    }

    public UserAnswer questionId(QuestionId questionId) {
        this.setQuestionId(questionId);
        return this;
    }

    public void setQuestionId(QuestionId questionId) {
        this.questionId = questionId;
    }

    public TestEntity getTestEntity() {
        return this.testEntity;
    }

    public void setTestEntity(TestEntity testEntity) {
        this.testEntity = testEntity;
    }

    public Integer getScore() {
        return this.score;
    }

    public void setScore(Integer score) {
        this.score = score;
    }

    public UserAnswer testEntity(TestEntity testEntity) {
        this.setTestEntity(testEntity);
        return this;
    }

    public Integer getMaximumScore() {
        return this.maximumScore;
    }

    public void setMaximumScore(Integer maximumScore) {
        this.maximumScore = maximumScore;
    }

    // jhipster-needle-entity-add-getters-setters - JHipster will add getters and setters here

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof UserAnswer)) {
            return false;
        }
        return id != null && id.equals(((UserAnswer) o).id);
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
            ", answerText='" + getAnswerText() + "'" +
            ", createdAt='" + getCreatedAt() + "'" +
            ", updatedAt='" + getUpdatedAt() + "'" +
            ", questionId='" + getQuestionId() + "'" +
            "}";
    }
}
