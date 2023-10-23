package ee.tenman.mmse.domain;

import ee.tenman.mmse.web.rest.TestUtil;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class UserAnswerTest {

    @Test
    void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(UserAnswer.class);
        UserAnswer userAnswer1 = new UserAnswer();
        userAnswer1.setId(1L);
        UserAnswer userAnswer2 = new UserAnswer();
        userAnswer2.setId(userAnswer1.getId());
        assertThat(userAnswer1).isEqualTo(userAnswer2);
        userAnswer2.setId(2L);
        assertThat(userAnswer1).isNotEqualTo(userAnswer2);
        userAnswer1.setId(null);
        assertThat(userAnswer1).isNotEqualTo(userAnswer2);
    }
}
