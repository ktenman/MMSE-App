package ee.tenman.mmse.service.question;

import ee.tenman.mmse.domain.UserAnswer;
import ee.tenman.mmse.domain.enumeration.QuestionId;
import ee.tenman.mmse.domain.enumeration.QuestionType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.util.Collection;
import java.util.UUID;

@Component
public class Question15 implements Question {

    private static final Logger log = LoggerFactory.getLogger(Question15.class);

    private static final QuestionId QUESTION_ID = QuestionId.QUESTION_15;
    private static final String QUESTION_TEXT = "Repeat the following phrase: 'No ifs, ands, or buts'";

    @Override
    public String getQuestionText() {
        return QUESTION_TEXT;
    }

    @Override
    public String getImage() {
        // This question does not use an image
        return null;
    }

    @Override
    public QuestionId getQuestionId() {
        return QUESTION_ID;
    }

    @Override
    public QuestionType getQuestionType() {
        // Assuming you have a VOICE_INPUT type for this kind of question
        return QuestionType.VOICE_INPUT;
    }

    @Override
    public Collection<?> getAnswerOptions() {
        // This question does not have predefined answer options
        return null;
    }

    @Override
    public int getScore(UserAnswer userAnswer) {
        // This method needs to handle the voice input.
        // Assuming you have a service to process and validate the voice input
        String transcribedText = processVoiceInput(userAnswer.getVoiceInputFileUuid());
        if ("no ifs, ands, or buts".equalsIgnoreCase(transcribedText)) {
            log.debug("User repeated the phrase correctly.");
            return 1;
        } else {
            log.debug("User did not repeat the phrase correctly.");
            return 0;
        }
    }

    private String processVoiceInput(UUID voiceInputFileUuid) {
        // Implement the logic to process the voice input.
        // This would involve sending the audio to a speech-to-text service and getting the transcribed text.
        return ""; // Return the transcribed text
    }
}
