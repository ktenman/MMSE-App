package ee.tenman.mmse.service.question;

import ee.tenman.mmse.domain.UserAnswer;
import ee.tenman.mmse.domain.enumeration.QuestionId;

public interface Question {
    String getQuestionText();
    int getScore();
    boolean isAnswerCorrect(UserAnswer userAnswer);
    QuestionId getQuestionId();
}
