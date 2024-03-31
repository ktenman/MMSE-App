package ee.tenman.mmse.domain;

import ee.tenman.mmse.domain.enumeration.QuestionId;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotNull;

@Entity
@Table(name = "media_recording")
public class MediaRecording extends BaseEntity {

    @Column(name = "file_name")
    private String fileName;

    @ManyToOne
    @JoinColumn(name = "test_entity_id")
    private TestEntity testEntity;

    @NotNull
    @Enumerated(EnumType.STRING)
    @Column(name = "question_id", nullable = false)
    private QuestionId questionId;

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public TestEntity getTestEntity() {
        return testEntity;
    }

    public void setTestEntity(TestEntity testEntity) {
        this.testEntity = testEntity;
    }

    public QuestionId getQuestionId() {
        return questionId;
    }

    public void setQuestionId(QuestionId questionId) {
        this.questionId = questionId;
    }

}
