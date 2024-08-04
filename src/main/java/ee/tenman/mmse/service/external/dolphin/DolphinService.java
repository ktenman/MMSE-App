package ee.tenman.mmse.service.external.dolphin;

import ee.tenman.mmse.config.RedisConfiguration;
import ee.tenman.mmse.domain.DolphinQuestion;
import ee.tenman.mmse.service.DolphinQuestionService;
import ee.tenman.mmse.service.external.openai.NoDolphinResponseException;
import jakarta.annotation.Resource;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class DolphinService {

    private static final Logger log = LoggerFactory.getLogger(DolphinService.class);

    @Resource
    private DolphinClient dolphinClient;

    @Resource
    private DolphinQuestionService dolphinQuestionService;

    @Cacheable(value = RedisConfiguration.DOLPHIN_CACHE, key = "#promptWrapper.getPromptAsSha256()")
    public String find(PromptWrapper promptWrapper) {
        String prompt = promptWrapper.prompt();
        Optional<String> answer = dolphinQuestionService.findAnswer(prompt);
        if (answer.isPresent()) {
            log.info("Dolphin Question Service Answer: {}", answer.get());
            return answer.get();
        }

        Optional<String> response = askQuestion(promptWrapper);
        if (response.isEmpty()) {
            log.debug("Answer '{}' was not recognized as correct by Dolphin Service.", prompt);
            throw new NoDolphinResponseException("Dolphin Service returned no response");
        }

        response.ifPresent(r -> {
            log.info("Dolphin Response: {}", r.toLowerCase());
            DolphinQuestion dolphinQuestion = dolphinQuestionService.findByQuestion(prompt)
                .orElse(new DolphinQuestion());
            dolphinQuestion.setQuestion(prompt);
            dolphinQuestion.setAnswer(r);
            DolphinQuestion savedDolphinQuestion = dolphinQuestionService.save(dolphinQuestion);
            log.info("Saved Dolphin Question: {}", savedDolphinQuestion);
        });
        return response.get();
    }

    private Optional<String> askQuestion(PromptWrapper promptWrapper) {
        String question = promptWrapper.prompt();
        if (StringUtils.isBlank(question)) {
            log.warn("Received blank question, returning empty response");
            return Optional.empty();
        }

        try {
            DolphinRequest request = new DolphinRequest(question, promptWrapper.model());
            log.info("Generating: {}", request);
            DolphinResponse response = dolphinClient.generate(request);
            log.info("Dolphin response: {}", response);
            return Optional.ofNullable(response.getResponse())
                .filter(StringUtils::isNotBlank)
                .map(s -> s.replaceFirst("^\\s+", ""));
        } catch (Exception e) {
            log.error("Failed to ask question: {}", question, e);
            return Optional.empty();
        }
    }

}
