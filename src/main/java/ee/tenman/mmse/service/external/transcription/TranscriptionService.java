package ee.tenman.mmse.service.external.transcription;

import jakarta.annotation.Resource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

@Service
public class TranscriptionService {
    private static final Logger log = LoggerFactory.getLogger(TranscriptionService.class);

    @Resource
    private TranscriptionClient transcriptionClient;

    public TranscriptionResponse transcribe(TranscriptionRequest request) {
        log.info("Transcribing audio file: {}", request.getFile().getOriginalFilename());
        return transcriptionClient.transcribe(request.getFile(), request.getModelName().getValue(), request.isConvertMp3());
    }
}
