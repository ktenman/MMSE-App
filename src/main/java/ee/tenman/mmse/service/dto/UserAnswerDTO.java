package ee.tenman.mmse.service.dto;

import ee.tenman.mmse.domain.enumeration.QuestionId;
import jakarta.validation.constraints.NotNull;

import java.io.Serializable;
import java.time.Instant;
import java.util.Objects;

/**
 * A DTO for the {@link ee.tenman.mmse.domain.UserAnswer} entity.
 */
@SuppressWarnings("common-java:DuplicatedBlocks")
public class UserAnswerDTO implements Serializable {

    private Long id;

    private String answerText;

    @NotNull
    private Instant createdAt;

    private Instant updatedAt;

    @NotNull
    private QuestionId questionId;

    private TestEntityDTO testEntity;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getAnswerText() {
        return answerText;
    }

    public void setAnswerText(String answerText) {
        this.answerText = answerText;
    }

    public Instant getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Instant createdAt) {
        this.createdAt = createdAt;
    }

    public Instant getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(Instant updatedAt) {
        this.updatedAt = updatedAt;
    }

    public QuestionId getQuestionId() {
        return questionId;
    }

    public void setQuestionId(QuestionId questionId) {
        this.questionId = questionId;
    }

    public TestEntityDTO getTestEntity() {
        return testEntity;
    }

    public void setTestEntity(TestEntityDTO testEntity) {
        this.testEntity = testEntity;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof UserAnswerDTO)) {
            return false;
        }

        UserAnswerDTO userAnswerDTO = (UserAnswerDTO) o;
        if (this.id == null) {
            return false;
        }
        return Objects.equals(this.id, userAnswerDTO.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.id);
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "UserAnswerDTO{" +
            "id=" + getId() +
            ", answerText='" + getAnswerText() + "'" +
            ", createdAt='" + getCreatedAt() + "'" +
            ", updatedAt='" + getUpdatedAt() + "'" +
            ", questionId='" + getQuestionId() + "'" +
            ", testEntity=" + getTestEntity() +
            "}";
    }
}
