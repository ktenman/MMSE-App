package ee.tenman.mmse.service.question;

import ee.tenman.mmse.domain.enumeration.QuestionId;
import jakarta.annotation.PostConstruct;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

@Component
public class QuestionsConfig {

    private Map<QuestionId, Question> questions;

    private final List<Question> questionBeans;

    public QuestionsConfig(List<Question> questionBeans) {
        this.questionBeans = questionBeans;
    }

    @PostConstruct
    public void init() {
        questions = questionBeans.stream()
            .collect(Collectors.toMap(Question::getQuestionId, Function.identity()));
    }

    public Map<QuestionId, Question> getQuestions() {
        return questions;
    }
}
