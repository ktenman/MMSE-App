package ee.tenman.mmse.service.external.dolphin;

import org.apache.commons.lang3.StringUtils;

public class DolphinRequest {

    public static final String DEFAULT_MODEL = Model.LLAMA_31_70B.value();
    private String model = DEFAULT_MODEL;
    private final boolean stream = false;
    private String prompt;

    public DolphinRequest(String prompt, Model model) {
        this.prompt = prompt;
        if (model != null && StringUtils.isNotBlank(model.value())) {
            this.model = model.value();
        }
    }

    public DolphinRequest(String prompt) {
        this.prompt = prompt;
    }

    public enum Model {
        GEMMA_2_9B("gemma2:9b"),
        DOLPHIN_MIXTRAL("dolphin-mixtral"),
        LLAMA_31_70B("llama3.1:70b");

        private final String value;

        Model(String value) {
            this.value = value;
        }

        public String value() {
            return this.value;
        }
    }

    public String getModel() {
        return model;
    }

    public String getPrompt() {
        return prompt;
    }

    public void setPrompt(String prompt) {
        this.prompt = prompt;
    }

    public boolean isStream() {
        return stream;
    }

    @Override
    public String toString() {
        return "DolphinRequest{" +
            "model='" + model + '\'' +
            ", prompt='" + prompt + '\'' +
            ", stream=" + stream +
            '}';
    }
}
