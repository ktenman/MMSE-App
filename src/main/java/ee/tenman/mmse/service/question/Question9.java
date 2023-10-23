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
import java.util.stream.Stream;

@Component
public class Question9 implements Question {

    private static final String QUESTION_TEXT = "Start with 100 and subtract 7, continue subtracting 7 from each new number for five steps.";
    private static final QuestionId QUESTION_ID = QuestionId.QUESTION_9;
    private static final List<Integer> CORRECT_ANSWERS = List.of(93, 86, 79, 72, 65);
    private List<Integer> userAnswers;

    private void populateUserAnswers(UserAnswer userAnswer) {
        userAnswers = Stream.of(userAnswer.getAnswerText().split(","))
            .filter(s -> !s.isEmpty())
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
        return this.QUESTION_ID;
    }

    @Override
    public QuestionType getQuestionType() {
        return QuestionType.SUBTRACTION_TASK;
    }

    public List<InputField> getAnswerOptions() {
        InputField[] fields = new InputField[5];
        Arrays.fill(fields, new InputField(InputFieldType.NUMBER, 0, 99));
        return List.of(fields);
    }

    @Override
    public int getScore(UserAnswer userAnswer) {
        populateUserAnswers(userAnswer);

        int result = 0;
        Set<Integer> userAnswerSet = new HashSet<>(userAnswers);
        for (Integer correctAnswer : CORRECT_ANSWERS) {
            if (userAnswerSet.contains(correctAnswer)) {
                result += 1;
            }
        }

        return result;
    }

}

