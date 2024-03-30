package ee.tenman.mmse.service.external.transcription;

import org.springframework.web.multipart.MultipartFile;

public class TranscriptionRequest {
    private final MultipartFile file;
    private final ModelName modelName;
    private boolean convertMp3;

    public TranscriptionRequest(MultipartFile file, ModelName modelName, boolean convertMp3) {
        this.file = file;
        this.modelName = modelName;
        this.convertMp3 = convertMp3;
    }

    public TranscriptionRequest(MultipartFile file, ModelName modelName) {
        this.file = file;
        this.modelName = modelName;
    }

    public MultipartFile getFile() {
        return file;
    }

    public boolean isConvertMp3() {
        return convertMp3;
    }

    public ModelName getModelName() {
        return modelName;
    }

    @Override
    public String toString() {
        return "TranscriptionRequest{" +
            "file=" + file +
            ", modelName='" + modelName + '\'' +
            ", convertMp3=" + convertMp3 +
            '}';
    }

    public enum ModelName {
        FACEBOOK_WAV_2_VEC_2_LARGE_960_H("facebook/wav2vec2-large-960h"),
        FACEBOOK_WAV_2_VEC_2_LARGE_ROBUST_FT_LIBRI_960_H("facebook/wav2vec2-large-robust-ft-libri-960h");

        private final String value;

        ModelName(String value) {
            this.value = value;
        }

        public String getValue() {
            return value;
        }
    }
}
