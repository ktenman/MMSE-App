package ee.tenman.mmse.service.question;

import ee.tenman.mmse.domain.UserAnswer;
import ee.tenman.mmse.domain.enumeration.QuestionId;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Base64;
import java.util.List;

public interface Question {
    String getQuestionText();

    String getImage();

    boolean isAnswerCorrect(UserAnswer userAnswer);

    QuestionId getQuestionId();

    List<String> getAnswerOptions();

    default String convertImageToBase64(String imagePath) {
        try {
            Path path = Paths.get(imagePath);
            byte[] imageBytes = Files.readAllBytes(path);
            return Base64.getEncoder().encodeToString(imageBytes);
        } catch (IOException ignored) {
            // handle exception
        }
        return null;
    }

    default int getScore() {
        return 1;
    }
}
