package ee.tenman.mmse.service.question;

import ee.tenman.mmse.domain.enumeration.QuestionId;

import java.util.Map;
import java.util.TreeMap;

public class QuizResult {
    private int score;
    private int maxScore;
    private Map<QuestionId, QuestionResult> questionResults;

    public QuizResult(int score, int maxScore, Map<QuestionId, QuestionResult> questionResults) {
        this.score = score;
        this.maxScore = maxScore;
        this.questionResults = new TreeMap<>(questionResults);
    }

    public int getScore() {
        return score;
    }

    public int getMaxScore() {
        return maxScore;
    }

    public Map<QuestionId, QuestionResult> getQuestionResults() {
        return questionResults;
    }

    public static class QuestionResult {
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
