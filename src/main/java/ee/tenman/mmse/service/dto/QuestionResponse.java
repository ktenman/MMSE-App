package ee.tenman.mmse.service.dto;

public class QuestionResponse<T> {
    private T question;
    private Long testEntityId;

    public QuestionResponse(T question, Long testEntityId) {
        this.question = question;
        this.testEntityId = testEntityId;
    }

    public T getQuestion() {
        return question;
    }

    public void setQuestion(T question) {
        this.question = question;
    }

    public Long getTestEntityId() {
        return testEntityId;
    }

    public void setTestEntityId(Long testEntityId) {
        this.testEntityId = testEntityId;
    }
}
