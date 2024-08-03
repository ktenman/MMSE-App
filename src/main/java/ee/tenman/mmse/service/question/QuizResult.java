package ee.tenman.mmse.service.question;

import ee.tenman.mmse.domain.enumeration.QuestionId;

import java.io.Serializable;
import java.util.Map;
import java.util.TreeMap;

public class QuizResult implements Serializable {
    private int score;
    private int maxScore;
    private Map<QuestionId, QuestionResult> questionResults;
    private long duration; // Duration in seconds

    public QuizResult(int score, int maxScore, Map<QuestionId, QuestionResult> questionResults, long duration) {
        this.score = score;
        this.maxScore = maxScore;
        this.questionResults = new TreeMap<>(questionResults);
        this.duration = duration;
    }

    public int getScore() {
        return score;
    }

    public int getMaxScore() {
        return maxScore;
    }

    public long getDuration() {
        return duration;
    }

    public Map<QuestionId, QuestionResult> getQuestionResults() {
        return questionResults;
    }

    public static class QuestionResult implements Serializable {
        private String questionText;
        private String userAnswer;
        private String correctAnswer;
        private boolean correct;
        private int score;
        private int maxScore;

        public QuestionResult(String questionText, String userAnswer, String correctAnswer, boolean correct, int score, int maxScore) {
            this.questionText = questionText;
            this.userAnswer = userAnswer;
            this.correctAnswer = correctAnswer;
            this.correct = correct;
            this.score = score;
            this.maxScore = maxScore;
        }

        public String getQuestionText() {
            return questionText;
        }

        public String getUserAnswer() {
            return userAnswer;
        }

        public String getCorrectAnswer() {
            return correctAnswer;
        }

        public boolean isCorrect() {
            return correct;
        }

        public int getScore() {
            return score;
        }

        public int getMaxScore() {
            return maxScore;
        }
    }
}
