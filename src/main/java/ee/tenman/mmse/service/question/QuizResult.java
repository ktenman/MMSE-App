package ee.tenman.mmse.service.question;

public class QuizResult {
    private int score;
    private int maxScore;

    public QuizResult(int score, int maxScore) {
        this.score = score;
        this.maxScore = maxScore;
    }

    public int getScore() {
        return score;
    }

    public int getMaxScore() {
        return maxScore;
    }

}
