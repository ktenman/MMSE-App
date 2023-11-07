package ee.tenman.mmse.service.external.minio;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import software.amazon.awssdk.auth.credentials.AwsBasicCredentials;
import software.amazon.awssdk.auth.credentials.StaticCredentialsProvider;
import software.amazon.awssdk.core.ResponseBytes;
import software.amazon.awssdk.core.exception.SdkException;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.core.sync.ResponseTransformer;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.GetObjectRequest;
import software.amazon.awssdk.services.s3.model.GetObjectResponse;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;

import java.io.IOException;
import java.net.URI;

@Service
public class StorageService {

    private static final Logger log = LoggerFactory.getLogger(StorageService.class);

    private final String bucketName;
    private final S3Client s3Client;

    public StorageService(@Value("${minio.url}") String minioUrl,
                          @Value("${minio.access-key}") String accessKey,
                          @Value("${minio.secret-key}") String secretKey,
                          @Value("${minio.bucket-name}") String bucketName) {
        this.bucketName = bucketName;
        this.s3Client = S3Client.builder()
                .endpointOverride(URI.create(minioUrl))
                .region(Region.EU_CENTRAL_1)
                .credentialsProvider(StaticCredentialsProvider.create(AwsBasicCredentials.create(accessKey, secretKey)))
                .build();
    }

    public boolean uploadFile(MultipartFile file) {
        log.info("Uploading file: {}", file.getOriginalFilename());
        try {
            PutObjectRequest putObjectRequest = PutObjectRequest.builder()
                    .bucket(bucketName)
                    .key(file.getOriginalFilename())
                    .build();

            s3Client.putObject(putObjectRequest, RequestBody.fromBytes(file.getBytes()));
            return true;
        } catch (SdkException e) {
            log.error("Error uploading file: {}", e.getMessage());
            return false;
        } catch (IOException e) {
            log.error("IO Error: {}", e.getMessage());
            return false;
        }
    }


    public byte[] downloadFile(String fileName) {
        log.info("Downloading file: {}", fileName);
        try {
            GetObjectRequest getObjectRequest = GetObjectRequest.builder()
                    .bucket(bucketName)
                    .key(fileName)
                    .build();

            ResponseBytes<GetObjectResponse> objectBytes = s3Client.getObject(getObjectRequest,
                    ResponseTransformer.toBytes());

            log.info("File downloaded successfully: {}", fileName);
            return objectBytes.asByteArray();
        } catch (SdkException e) {
            log.error("Error downloading file: {}", e.getMessage());
            throw new RuntimeException("Error downloading file from S3", e);
        }
    }
}
