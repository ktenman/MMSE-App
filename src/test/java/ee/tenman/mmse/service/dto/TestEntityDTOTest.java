package ee.tenman.mmse.service.dto;

import ee.tenman.mmse.web.rest.TestUtil;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class TestEntityDTOTest {

    @Test
    void dtoEqualsVerifier() throws Exception {
        TestUtil.equalsVerifier(TestEntityDTO.class);
        TestEntityDTO testEntityDTO1 = new TestEntityDTO();
        testEntityDTO1.setId(1L);
        TestEntityDTO testEntityDTO2 = new TestEntityDTO();
        assertThat(testEntityDTO1).isNotEqualTo(testEntityDTO2);
        testEntityDTO2.setId(testEntityDTO1.getId());
        assertThat(testEntityDTO1).isEqualTo(testEntityDTO2);
        testEntityDTO2.setId(2L);
        assertThat(testEntityDTO1).isNotEqualTo(testEntityDTO2);
        testEntityDTO1.setId(null);
        assertThat(testEntityDTO1).isNotEqualTo(testEntityDTO2);
    }
}
