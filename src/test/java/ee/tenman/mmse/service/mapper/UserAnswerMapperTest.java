package ee.tenman.mmse.service.mapper;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class UserAnswerMapperTest {

    private UserAnswerMapper userAnswerMapper;

    @BeforeEach
    public void setUp() {
        userAnswerMapper = new UserAnswerMapperImpl();
    }
}
