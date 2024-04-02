package ee.tenman.mmse.domain;

import jakarta.persistence.Entity;
import jakarta.persistence.Table;

@Entity
@Table(name = "dolphin_question")
public class DolphinQuestion extends BaseEntity {

    private String question;

    private String answer;

    public String getQuestion() {
        return question;
    }

    public void setQuestion(String question) {
        this.question = question;
    }

    public String getAnswer() {
        return answer;
    }

    public void setAnswer(String answer) {
        this.answer = answer;
    }

    @Override
    public String toString() {
        return "DolphinQuestion{" +
            "question='" + question + '\'' +
            ", answer='" + answer + '\'' +
            '}';
    }
}
