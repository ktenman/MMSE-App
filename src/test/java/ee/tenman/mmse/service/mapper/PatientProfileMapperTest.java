package ee.tenman.mmse.service.mapper;

import org.junit.jupiter.api.BeforeEach;

class PatientProfileMapperTest {

    private PatientProfileMapper patientProfileMapper;

    @BeforeEach
    public void setUp() {
        patientProfileMapper = new PatientProfileMapperImpl();
    }
}
