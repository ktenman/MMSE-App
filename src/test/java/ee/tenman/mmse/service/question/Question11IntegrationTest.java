package ee.tenman.mmse.service.question;

import ee.tenman.mmse.IntegrationTest;
import ee.tenman.mmse.domain.UserAnswer;
import jakarta.annotation.Resource;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Timeout;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;

@IntegrationTest
class Question11IntegrationTest {

    @Resource
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

    private static Stream<Arguments> provideCaseSensitivityAnswers() {
        return Stream.of(
            Arguments.of("PENCIL", 1),
            Arguments.of("PenCil", 1),
            Arguments.of("STYLUS", 1)
        );
    }

    private static Stream<Arguments> provideAnswersWithSpaces() {
        return Stream.of(
            Arguments.of(" pencil ", 1),
            Arguments.of("    stylus", 1),
            Arguments.of("\tgraphite\t", 1),
            Arguments.of("\nlead\n", 1)
        );
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
            Arguments.of("pen", 1),
            Arguments.of("cil", 0),
            Arguments.of("sty", 1)
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

    @ParameterizedTest
    @MethodSource("provideAnswersWithSpaces")
    void getScoreWithSpaces(String answerText, int expectedScore) {
        UserAnswer userAnswer = new UserAnswer();
        userAnswer.setAnswerText(answerText);

        int score = question11.getScore(userAnswer);

        assertThat(score).isEqualTo(expectedScore);
    }

    @ParameterizedTest
    @MethodSource("provideAnswersWithPunctuation")
    void getScoreWithPunctuation(String answerText, int expectedScore) {
        UserAnswer userAnswer = new UserAnswer();
        userAnswer.setAnswerText(answerText);

        int score = question11.getScore(userAnswer);

        assertThat(score).isEqualTo(expectedScore);
    }

    @ParameterizedTest
    @MethodSource("provideAnswersWithSubstrings")
    @Disabled("This test is disabled because it makes a call to OpenAI API and costs money.")
    void getScoreWithSubstrings(String answerText, int expectedScore) {
        UserAnswer userAnswer = new UserAnswer();
        userAnswer.setAnswerText(answerText);

        int score = question11.getScore(userAnswer);

        assertThat(score).isEqualTo(expectedScore);
    }

    @ParameterizedTest
    @Timeout(value = 60)
    @MethodSource("provideAnswersForScoring")
    @Disabled("This test is disabled because it makes a call to OpenAI API and costs money.")
    void getScoreWithExpectedScore(String answerText, int expectedScore) {
        UserAnswer userAnswer = new UserAnswer();
        userAnswer.setAnswerText(answerText);

        int score = question11.getScore(userAnswer);

        assertThat(score).isEqualTo(expectedScore);
    }

}
