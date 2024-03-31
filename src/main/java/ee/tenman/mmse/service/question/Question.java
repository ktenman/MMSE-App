package ee.tenman.mmse.service.question;

import ee.tenman.mmse.domain.UserAnswer;
import ee.tenman.mmse.domain.enumeration.QuestionId;
import ee.tenman.mmse.domain.enumeration.QuestionType;
import org.apache.commons.compress.utils.IOUtils;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.Base64;
import java.util.Collection;
import java.util.List;

public interface Question {
    String getQuestionText();

    default String getImage() {
        return null;
    }

    QuestionId getQuestionId();

    QuestionType getQuestionType();

    default Collection<?> getAnswerOptions() {
        return List.of();
    }

    default String convertImageToBase64(String imagePath) {
        try {
            ClassLoader classLoader = getClass().getClassLoader();
            InputStream inputStream = classLoader.getResourceAsStream(imagePath);
            if (inputStream == null) {
                throw new FileNotFoundException("File not found " + imagePath);
            }
            byte[] imageBytes = IOUtils.toByteArray(inputStream);
            return Base64.getEncoder().encodeToString(imageBytes);
        } catch (IOException ignored) {
        }
        return null;
    }

    int getScore(UserAnswer userAnswer);

    default int getMaximumScore() {
        return 1;
    };

}
