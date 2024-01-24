package ee.tenman.mmse.service.external.dolphin;

public class DolphinRequest {

    //    private final String model = "dolphin-mixtral";
    private final String model = "tinydolphin";
    private final boolean stream = false;
    private String prompt;

    public DolphinRequest(String prompt) {
        this.prompt = prompt;
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
