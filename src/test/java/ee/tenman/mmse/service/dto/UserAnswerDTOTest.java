package ee.tenman.mmse.service.dto;

import ee.tenman.mmse.web.rest.TestUtil;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class UserAnswerDTOTest {

    @Test
    void dtoEqualsVerifier() throws Exception {
        TestUtil.equalsVerifier(UserAnswerDTO.class);
        UserAnswerDTO userAnswerDTO1 = new UserAnswerDTO();
        userAnswerDTO1.setId(1L);
        UserAnswerDTO userAnswerDTO2 = new UserAnswerDTO();
        assertThat(userAnswerDTO1).isNotEqualTo(userAnswerDTO2);
        userAnswerDTO2.setId(userAnswerDTO1.getId());
        assertThat(userAnswerDTO1).isEqualTo(userAnswerDTO2);
        userAnswerDTO2.setId(2L);
        assertThat(userAnswerDTO1).isNotEqualTo(userAnswerDTO2);
        userAnswerDTO1.setId(null);
        assertThat(userAnswerDTO1).isNotEqualTo(userAnswerDTO2);
    }
}
