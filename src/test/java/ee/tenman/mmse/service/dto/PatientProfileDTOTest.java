package ee.tenman.mmse.service.dto;

import ee.tenman.mmse.web.rest.TestUtil;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class PatientProfileDTOTest {

    @Test
    void dtoEqualsVerifier() throws Exception {
        TestUtil.equalsVerifier(PatientProfileDTO.class);
        PatientProfileDTO patientProfileDTO1 = new PatientProfileDTO();
        patientProfileDTO1.setId(1L);
        PatientProfileDTO patientProfileDTO2 = new PatientProfileDTO();
        assertThat(patientProfileDTO1).isNotEqualTo(patientProfileDTO2);
        patientProfileDTO2.setId(patientProfileDTO1.getId());
        assertThat(patientProfileDTO1).isEqualTo(patientProfileDTO2);
        patientProfileDTO2.setId(2L);
        assertThat(patientProfileDTO1).isNotEqualTo(patientProfileDTO2);
        patientProfileDTO1.setId(null);
        assertThat(patientProfileDTO1).isNotEqualTo(patientProfileDTO2);
    }
}
