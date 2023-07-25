package ee.tenman.mmse.service.question;

import ee.tenman.mmse.domain.UserAnswer;
import ee.tenman.mmse.domain.enumeration.QuestionId;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Component;

import java.time.Clock;
import java.time.Month;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

@Component
public class Question3 implements Question {

    private static final String QUESTION_TEXT = "What is the current month?";
    private final QuestionId questionId = QuestionId.QUESTION_3;

    @Resource
    private Clock clock;

    @Override
    public String getQuestionText() {
        return QUESTION_TEXT;
    }

    @Override
    public String getImage() {
        return null;
    }

    @Override
    public boolean isAnswerCorrect(UserAnswer userAnswer) {
        ZonedDateTime zonedDateTime = userAnswer.getCreatedAt().atZone(ZoneId.systemDefault());
        String month = Month.from(zonedDateTime).name();
        return month.equalsIgnoreCase(userAnswer.getAnswerText());
    }

    @Override
    public QuestionId getQuestionId() {
        return this.questionId;
    }

    @Override
    public List<String> getAnswerOptions() {
        Month currentMonth = Month.from(ZonedDateTime.now(clock));
        List<String> answerOptions = IntStream.rangeClosed(0, 3)
            .mapToObj(currentMonth::plus)
            .map(Month::name)
            .collect(Collectors.toList());

        Collections.shuffle(answerOptions);

        return answerOptions;
    }

}
