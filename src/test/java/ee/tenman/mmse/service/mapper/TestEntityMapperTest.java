package ee.tenman.mmse.service.mapper;

import org.junit.jupiter.api.BeforeEach;

class TestEntityMapperTest {

    private TestEntityMapper testEntityMapper;

    @BeforeEach
    public void setUp() {
        testEntityMapper = new TestEntityMapperImpl();
    }
}
