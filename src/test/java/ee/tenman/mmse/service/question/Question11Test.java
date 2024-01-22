package ee.tenman.mmse.service.question;

import ee.tenman.mmse.domain.UserAnswer;
import ee.tenman.mmse.service.external.dolphin.DolphinService;
import ee.tenman.mmse.service.external.openai.NoDolphinResponseException;
import ee.tenman.mmse.service.external.openai.OpenAiService;
import ee.tenman.mmse.service.external.similarity.SimilarityService;
import ee.tenman.mmse.service.external.synonym.SynonymService;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Timeout;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.catchThrowable;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.lenient;

@ExtendWith(MockitoExtension.class)
class Question11Test {

    @Mock
    OpenAiService openAiService;

    @Mock
    SimilarityService similarityService;

    @Mock
    SynonymService synonymService;

    @Mock
    DolphinService dolphinService;

    @InjectMocks
    Question11 question11;

    private static Stream<Arguments> provideAnswersForScoring() {
        return Stream.of(
            // Exact and close matches
            Arguments.of("pencil", 1),
            Arguments.of("stylus", 1),
            Arguments.of("graphite", 1),
            Arguments.of("lead", 1),
            Arguments.of("writing instrument", 1),
            Arguments.of("scriber", 1),
            Arguments.of("crayon", 1),

            // Common typos or misspellings
            Arguments.of("pencel", 1),
            Arguments.of("grafite", 1),
            Arguments.of("stylos", 1),
            Arguments.of("scribr", 1),
            Arguments.of("writting instrument", 1),

            // Sentences or phrases
            Arguments.of("I think it's a pencil.", 1),
            Arguments.of("it is a pencil", 1),
            Arguments.of("it's a pencil", 1),
            Arguments.of("Isn't that a stylus?", 1),
            Arguments.of("Looks like graphite to me.", 1),
            Arguments.of("My guess is a crayon.", 1),

            // Erroneous or unrelated entries
            Arguments.of("pen", 1),
            Arguments.of("paper", 0),
            Arguments.of("eraser", 0),
            Arguments.of("It's used to write, right?", 1),

            // More creative or out-of-box scenarios
            Arguments.of("That's my drawing buddy.", 0),
            Arguments.of("Something to jot down notes with.", 1),
            Arguments.of("The thing I lose all the time!", 1),

            // Edge cases
            Arguments.of("", 0),
            Arguments.of("     ", 0),
            Arguments.of("????", 0),

            Arguments.of("graphite stick", 1),
            Arguments.of("pen-like object", 1),
            Arguments.of("object for writing", 1),
            Arguments.of("lead holder", 1),
            Arguments.of("charcoal", 0),
            Arguments.of("tool for sketching", 1),
            Arguments.of("marker", 1),
            Arguments.of("writing utensil", 1),
            Arguments.of("drafting instrument", 1),
            Arguments.of("inkless pen", 1),
            Arguments.of("thing to draw with", 1),
            Arguments.of("wooden stick", 0),
            Arguments.of("pen-cil", 1),
            Arguments.of("not a pen", 0),
            Arguments.of("lead pencil", 1),
            Arguments.of("device to inscribe", 1),
            Arguments.of("handwriting tool", 1),
            Arguments.of("sketching pencil", 1),
            Arguments.of("wooden writing stick", 1),
            Arguments.of("pointy object", 1)
        );
    }


    @ParameterizedTest
    @MethodSource("provideCaseSensitivityAnswers")
    void getScoreCaseSensitivity(String answerText, int expectedScore) {
        UserAnswer userAnswer = new UserAnswer();
        userAnswer.setAnswerText(answerText);

        int score = question11.getScore(userAnswer);

        assertThat(score).isEqualTo(expectedScore);
    }

    private static Stream<Arguments> provideCaseSensitivityAnswers() {
        return Stream.of(
            Arguments.of("PENCIL", 1),
            Arguments.of("PenCil", 1),
            Arguments.of("STYLUS", 1)
        );
    }

    @ParameterizedTest
    @MethodSource("provideAnswersWithSpaces")
    void getScoreWithSpaces(String answerText, int expectedScore) {
        UserAnswer userAnswer = new UserAnswer();
        userAnswer.setAnswerText(answerText);

        int score = question11.getScore(userAnswer);

        assertThat(score).isEqualTo(expectedScore);
    }

    private static Stream<Arguments> provideAnswersWithSpaces() {
        return Stream.of(
            Arguments.of(" pencil ", 1),
            Arguments.of("    stylus", 1),
            Arguments.of("\tgraphite\t", 1),
            Arguments.of("\nlead\n", 1)
        );
    }

    @ParameterizedTest
    @MethodSource("provideAnswersWithPunctuation")
    void getScoreWithPunctuation(String answerText, int expectedScore) {
        UserAnswer userAnswer = new UserAnswer();
        userAnswer.setAnswerText(answerText);

        int score = question11.getScore(userAnswer);

        assertThat(score).isEqualTo(expectedScore);
    }

    private static Stream<Arguments> provideAnswersWithPunctuation() {
        return Stream.of(
            Arguments.of("pencil.", 1),
            Arguments.of("stylus!", 1),
            Arguments.of("graphite,", 1),
            Arguments.of("lead?", 1)
        );
    }

    private static Stream<Arguments> provideAnswersWithSubstrings() {
        return Stream.of(
                Arguments.of("pen", 1), // This should return "yes" from the service
                Arguments.of("cil", 0), // This should return "no" from the service
                Arguments.of("sty", 0)  // This should return "no" from the service
        );
    }

    @ParameterizedTest
    @MethodSource("provideAnswersWithSubstrings")
    void getScoreWithSubstrings(String answerText, int expectedScore) {
        UserAnswer userAnswer = new UserAnswer();
        userAnswer.setAnswerText(answerText);

        lenient().when(openAiService.askQuestion(anyString())).thenAnswer(invocation -> {
            String question = invocation.getArgument(0);
            return Optional.of(question.contains("pen") ? "no" : "yes");
        });

        lenient().when(dolphinService.askQuestion(anyString())).thenAnswer(invocation -> {
            String question = invocation.getArgument(0);
            return Optional.of(question.contains("pen") ? "no" : "yes");
        });

        int score = question11.getScore(userAnswer);

        assertThat(score).isEqualTo(expectedScore);
    }

    @ParameterizedTest
    @Timeout(value = 60)
    @MethodSource("provideAnswersForScoring")
    @Disabled
    void getScoreWithExpectedScore(String answerText, int expectedScore) {
        UserAnswer userAnswer = new UserAnswer();
        userAnswer.setAnswerText(answerText);

        int score = question11.getScore(userAnswer);

        assertThat(score).isEqualTo(expectedScore);
    }

    @Test
    void getScoreWithExpectedException() {
        UserAnswer userAnswer = new UserAnswer();
        userAnswer.setAnswerText("zzz");

        Throwable thrown = catchThrowable(() -> question11.getScore(userAnswer));

        assertThat(thrown).isInstanceOf(NoDolphinResponseException.class);
    }

    private static Stream<Arguments> provideOpenAiServiceEdgeCases() {
        return Stream.of(
            Arguments.of("pencil", 1, null),
            Arguments.of("unexpected response", 0, null),
            Arguments.of("service error", 0, RuntimeException.class)
            // Add more cases as needed
        );
    }


}
