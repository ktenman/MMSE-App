package ee.tenman.mmse.service.dto;

import ee.tenman.mmse.domain.enumeration.QuestionId;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import static org.assertj.core.api.Assertions.assertThat;

class OrientationToPlaceQuestionDTOTest {

    @ParameterizedTest
    @CsvSource(
        {
            "18. What county are you in?,What county are you in?",
            "19. What is the name of this place?,What is the name of this place?",
        }
    )
    void testConstructorAndGetters(String questionText, String expectedQuestionText) {
        OrientationToPlaceQuestionDTO questionDTO = new OrientationToPlaceQuestionDTO(
            QuestionId.QUESTION_1, questionText);

        assertThat(questionDTO.getQuestionId()).isEqualTo(QuestionId.QUESTION_1);
        assertThat(questionDTO.getQuestionText()).isEqualTo(expectedQuestionText);
    }
}
