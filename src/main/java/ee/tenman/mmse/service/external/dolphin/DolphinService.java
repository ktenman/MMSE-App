package ee.tenman.mmse.service.external.dolphin;

import jakarta.annotation.Resource;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class DolphinService {

    private static final Logger log = LoggerFactory.getLogger(DolphinService.class);

    @Resource
    private DolphinClient dolphinClient;

    public Optional<String> askQuestion(String question) {
        if (StringUtils.isBlank(question)) {
            log.warn("Received blank question, returning empty response");
            return Optional.empty();
        }

        try {
            DolphinRequest request = new DolphinRequest(question);
            log.info("Generating: {}", request);
            DolphinResponse response = dolphinClient.generate(request);
            log.info("Dolphin response: {}", response);
            return Optional.ofNullable(response.getResponse());
        } catch (Exception e) {
            log.error("Failed to ask question: {}", question, e);
            return Optional.empty();
        }
    }

}
