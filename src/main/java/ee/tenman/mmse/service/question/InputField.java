package ee.tenman.mmse.service.question;

public class InputField {
    private String type;
    private int min;
    private int max;
    private String placeholder;

    public InputField(String type, int min, int max, String placeholder) {
        this.type = type;
        this.min = min;
        this.max = max;
        this.placeholder = placeholder;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public int getMin() {
        return min;
    }

    public void setMin(int min) {
        this.min = min;
    }

    public int getMax() {
        return max;
    }

    public void setMax(int max) {
        this.max = max;
    }

    public String getPlaceholder() {
        return placeholder;
    }

    public void setPlaceholder(String placeholder) {
        this.placeholder = placeholder;
    }
}

