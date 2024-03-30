package ee.tenman.mmse.service.external.transcription;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.multipart.MultipartFile;

@FeignClient(name = "transcriptionClient", url = "http://127.0.0.1:61235")
public interface TranscriptionClient {
    @PostMapping(value = "/transcribe", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    TranscriptionResponse transcribe(@RequestPart("file") MultipartFile file,
                                     @RequestPart("model_name") String modelName,
                                     @RequestPart("convert_mp3") boolean convertMp3);
}
