package ee.tenman.mmse.service.question;

import ee.tenman.mmse.domain.UserAnswer;
import ee.tenman.mmse.domain.enumeration.QuestionId;
import ee.tenman.mmse.domain.enumeration.QuestionType;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

@Component
public class Question9 implements Question {

    private static final String QUESTION_TEXT =  "Start with 100 and subtract 7, continue subtracting 7 from each new number for five steps.";
    private final QuestionId questionId = QuestionId.QUESTION_9;
    private List<Integer> correctAnswers = Arrays.asList(93, 86, 79, 72, 65);
    private List<Integer> userAnswers;

    private void populateUserAnswers(UserAnswer userAnswer) {
        userAnswers = Arrays.stream(userAnswer.getAnswerText().split(","))
            .map(Integer::parseInt)
            .collect(Collectors.toList());
    }
    @Override
    public String getQuestionText() {
        return QUESTION_TEXT;
    }

    @Override
    public String getImage() {
        return null;
    }

    @Override
    public QuestionId getQuestionId() {
        return this.questionId;
    }

    @Override
    public QuestionType getQuestionType() {
        return QuestionType.SUBTRACTION_TASK;
    }

    @Override
    public List<InputField> getAnswerOptions() {
        return IntStream.range(0, 5)
            .mapToObj(i -> new InputField(InputFieldType.NUMBER, 0, 99))
            .collect(Collectors.toList());
    }

    @Override
    public int getScore(UserAnswer userAnswer) {
        populateUserAnswers(userAnswer);

        int result = 0;
        Set<Integer> userAnswerSet = new HashSet<>(userAnswers);
        for (Integer correctAnswer : correctAnswers) {
            if (userAnswerSet.contains(correctAnswer)) {
                result += 1;
            }
        }

        return result;
    }

}

