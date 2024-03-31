package ee.tenman.mmse.service;

import ee.tenman.mmse.config.RedisConfiguration;
import ee.tenman.mmse.domain.DolphinQuestion;
import ee.tenman.mmse.repository.DolphinQuestionRepository;
import jakarta.annotation.Resource;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class DolphinQuestionService {

    @Resource
    private DolphinQuestionRepository dolphinQuestionRepository;

    @Cacheable(value = RedisConfiguration.ONE_MONTH_CACHE, key = "#question")
    public Optional<String> findAnswer(String question) {
        return dolphinQuestionRepository.findByQuestion(question)
            .map(DolphinQuestion::getAnswer);
    }

    @Async("taskExecutor")
    public void save(DolphinQuestion dolphinQuestion) {
        dolphinQuestionRepository.save(dolphinQuestion);
    }

}
