package ee.tenman.mmse.service.external.minio;

import ee.tenman.mmse.IntegrationTest;
import jakarta.annotation.Resource;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.web.multipart.MultipartFile;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

@IntegrationTest
class StorageServiceTest {

    @Resource
    private StorageService storageService;

    @Test
    @Disabled
    void uploadFile() {
        MultipartFile multipartFile = new MockMultipartFile(
                "file",
                UUID.randomUUID() + "-test.txt",
                "text/plain",
                "Test file content".getBytes()
        );

        boolean result = storageService.uploadFile(multipartFile);

        assertThat(result).isTrue();
    }
}
