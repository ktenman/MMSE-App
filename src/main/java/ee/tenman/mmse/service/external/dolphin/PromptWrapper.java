package ee.tenman.mmse.service.external.dolphin;

import org.apache.commons.codec.digest.DigestUtils;

public record PromptWrapper(String prompt) {

    public String getPromptAsSha256() {
        return DigestUtils.sha256Hex(prompt);
    }
}
