package ee.tenman.mmse.service.external.similarity;

import ee.tenman.mmse.IntegrationTest;
import jakarta.annotation.Resource;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;

@IntegrationTest
class SynonymServiceTest {

    @Resource
    SimilarityService similarityService;

    private static Stream<Arguments> provideCaseSensitivityAnswers() {
        return Stream.of(
            Arguments.of(new SimilarityRequest("PENCIL", "pencil"), true),
            Arguments.of(new SimilarityRequest("PenCil", "pencil"), true),
            Arguments.of(new SimilarityRequest("STYLUS", "pencil"), false)
        );
    }

    private static Stream<Arguments> provideAnswersForScoring() {
        return Stream.of(
            // Exact and close matches
            Arguments.of(new SimilarityRequest("pencil", "pencil"), true),
            Arguments.of(new SimilarityRequest("stylus", "pencil"), false),
            Arguments.of(new SimilarityRequest("graphite", "pencil"), true),
            Arguments.of(new SimilarityRequest("lead", "pencil"), false),
            Arguments.of(new SimilarityRequest("writing instrument", "pencil"), false),
            Arguments.of(new SimilarityRequest("scriber", "pencil"), false),
            Arguments.of(new SimilarityRequest("crayon", "pencil"), true),

            // Common typos or misspellings
            Arguments.of(new SimilarityRequest("pencel", "pencil"), false),
            Arguments.of(new SimilarityRequest("grafite", "pencil"), false),
            Arguments.of(new SimilarityRequest("stylos", "pencil"), false),
            Arguments.of(new SimilarityRequest("scribr", "pencil"), false),
            Arguments.of(new SimilarityRequest("writting instrument", "pencil"), false),

            // Sentences or phrases
            Arguments.of(new SimilarityRequest("I think it's a pencil.", "pencil"), true),
            Arguments.of(new SimilarityRequest("it is a pencil", "pencil"), true),
            Arguments.of(new SimilarityRequest("it's a pencil", "pencil"), true),
            Arguments.of(new SimilarityRequest("Isn't that a stylus?", "pencil"), false),
            Arguments.of(new SimilarityRequest("Looks like graphite to me.", "pencil"), false),
            Arguments.of(new SimilarityRequest("My guess is a crayon.", "pencil"), false),

            // Erroneous or unrelated entries
            Arguments.of(new SimilarityRequest("pen", "pencil"), true),
            Arguments.of(new SimilarityRequest("paper", "pencil"), false),
            Arguments.of(new SimilarityRequest("eraser", "pencil"), false),
            Arguments.of(new SimilarityRequest("It's used to write, right?", "pencil"), false),

            // More creative or out-of-box scenarios
            Arguments.of(new SimilarityRequest("That's my drawing buddy.", "pencil"), false),
            Arguments.of(new SimilarityRequest("Something to jot down notes with.", "pencil"), false),
            Arguments.of(new SimilarityRequest("The thing I lose all the time!", "pencil"), false),

            // Edge cases
            Arguments.of(new SimilarityRequest("", "pencil"), false),
            Arguments.of(new SimilarityRequest("     ", "pencil"), false),
            Arguments.of(new SimilarityRequest("????", "pencil"), false),

            Arguments.of(new SimilarityRequest("graphite stick", "pencil"), true),
            Arguments.of(new SimilarityRequest("pen-like object", "pencil"), false),
            Arguments.of(new SimilarityRequest("object for writing", "pencil"), false),
            Arguments.of(new SimilarityRequest("lead holder", "pencil"), false),
            Arguments.of(new SimilarityRequest("charcoal", "pencil"), false),
            Arguments.of(new SimilarityRequest("tool for sketching", "pencil"), false),
            Arguments.of(new SimilarityRequest("marker", "pencil"), false),
            Arguments.of(new SimilarityRequest("writing utensil", "pencil"), false),
            Arguments.of(new SimilarityRequest("drafting instrument", "pencil"), false),
            Arguments.of(new SimilarityRequest("inkless pen", "pencil"), true),
            Arguments.of(new SimilarityRequest("thing to draw with", "pencil"), false),
            Arguments.of(new SimilarityRequest("wooden stick", "pencil"), false),
            Arguments.of(new SimilarityRequest("pen-cil", "pencil"), true),
            Arguments.of(new SimilarityRequest("not a pen", "pencil"), false),
            Arguments.of(new SimilarityRequest("lead pencil", "pencil"), true),
            Arguments.of(new SimilarityRequest("device to inscribe", "pencil"), false),
            Arguments.of(new SimilarityRequest("handwriting tool", "pencil"), false),
            Arguments.of(new SimilarityRequest("sketching pencil", "pencil"), true),
            Arguments.of(new SimilarityRequest("wooden writing stick", "pencil"), false),
            Arguments.of(new SimilarityRequest("pointy object", "pencil"), false)
        );
    }

    @ParameterizedTest
    @MethodSource("provideCaseSensitivityAnswers")
    @Disabled
    void isSimilarCaseSensitivity(SimilarityRequest request, boolean expectedOutcome) {
        boolean isSimilar = similarityService.isSimilar(request);
        assertThat(isSimilar).isEqualTo(expectedOutcome);
    }

    @ParameterizedTest
    @MethodSource("provideAnswersForScoring")
    @Disabled
    void isSimilarForVariousInputs(SimilarityRequest request, boolean expectedOutcome) {
        boolean isSimilar = similarityService.isSimilar(request);

        assertThat(isSimilar).as("Expected similarity for %s to be %s", request, expectedOutcome).isEqualTo(expectedOutcome);
    }

}
