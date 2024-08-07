package ee.tenman.mmse.service.dto;

import ee.tenman.mmse.domain.enumeration.QuestionId;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;

import java.io.Serializable;
import java.util.Objects;

public class AnswerDTO implements Serializable {

    private Long id;

    @NotEmpty
    private String idempotencyKey;

    @NotEmpty
    private String answerText;

    @NotNull
    private QuestionId questionId;

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

    public QuestionId getQuestionId() {
        return questionId;
    }

    public void setQuestionId(QuestionId questionId) {
        this.questionId = questionId;
    }

    public String getIdempotencyKey() {
        return idempotencyKey;
    }

    public void setIdempotencyKey(String idempotencyKey) {
        this.idempotencyKey = idempotencyKey;
    }

    @Override
    public String toString() {
        return "AnswerDTO{" +
            "id=" + id +
            ", answerText='" + answerText + '\'' +
            ", questionId=" + questionId +
            ", idempotencyKey=" + idempotencyKey +
            '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        AnswerDTO answerDTO = (AnswerDTO) o;
        return Objects.equals(id, answerDTO.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}
