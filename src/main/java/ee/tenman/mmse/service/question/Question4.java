package ee.tenman.mmse.service.question;

import ee.tenman.mmse.domain.UserAnswer;
import ee.tenman.mmse.domain.enumeration.QuestionId;
import ee.tenman.mmse.domain.enumeration.QuestionType;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Component;

import java.time.Clock;
import java.time.ZonedDateTime;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

@Component
public class Question4 implements Question {

    private static final String QUESTION_TEXT = "4. What is the current year?";
    private static final QuestionId QUESTION_ID = QuestionId.QUESTION_4;

    @Resource
    private Clock clock;

    private Integer questionYear;

    @Override
    public String getQuestionText() {
        return QUESTION_TEXT;
    }

    @Override
    public QuestionId getQuestionId() {
        return QUESTION_ID;
    }

    @Override
    public QuestionType getQuestionType() {
        return QuestionType.MULTIPLE_CHOICE;
    }

    @Override
    public List<String> getAnswerOptions(Long testEntityId) {
        // Set the question year when generating options
        this.questionYear = ZonedDateTime.now(clock).getYear();

        List<String> answerOptions = IntStream.rangeClosed(this.questionYear - 1, this.questionYear + 2)
            .mapToObj(String::valueOf)
            .collect(Collectors.toList());

        Collections.shuffle(answerOptions);

        return answerOptions;
    }

    @Override
    public int getScore(UserAnswer userAnswer) {
        return String.valueOf(this.questionYear).equals(userAnswer.getAnswerText()) ? 1 : 0;
    }

    @Override
    public String getCorrectAnswer() {
        if (this.questionYear == null) {
            throw new IllegalStateException("Question year has not been set. Ensure getAnswerOptions is called before getCorrectAnswer.");
        }
        return String.valueOf(this.questionYear);
    }
}
