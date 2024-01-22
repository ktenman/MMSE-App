package ee.tenman.mmse.service.external.dolphin;

import ee.tenman.mmse.IntegrationTest;
import jakarta.annotation.Resource;
import org.junit.jupiter.api.Test;

import java.util.Optional;

@IntegrationTest
class DolphinServiceTest {

    @Resource
    private DolphinService dolphinService;

    @Test
    void generate() {
        Optional<String> answer = dolphinService.askQuestion("2+2");

        System.out.println();
    }
}
