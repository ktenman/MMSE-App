package ee.tenman.mmse.service.question;

import ee.tenman.mmse.domain.enumeration.QuestionId;

import java.util.Map;

public class QuizResult {
    private int score;
    private int maxScore;
    private Map<QuestionId, QuestionResult> questionResults;

    public QuizResult(int score, int maxScore, Map<QuestionId, QuestionResult> questionResults) {
        this.score = score;
        this.maxScore = maxScore;
        this.questionResults = questionResults;
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
        private String userAnswer;
        private String correctAnswer;
        private boolean correct;

        public QuestionResult(String userAnswer, String correctAnswer, boolean correct) {
            this.userAnswer = userAnswer;
            this.correctAnswer = correctAnswer;
            this.correct = correct;
        }

        public String getUserAnswer() {
            return userAnswer;
        }

        public String getCorrectAnswer(Long testEntityId) {
            return correctAnswer;
        }

        public boolean isCorrect() {
            return correct;
        }
    }
}
