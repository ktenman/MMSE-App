package ee.tenman.mmse.config;

import feign.Logger;
import org.springframework.context.annotation.Bean;

public class OpenAiFeignConfig {

    @Bean
    public Logger.Level feignLoggerLevel() {
        return Logger.Level.FULL;
    }

}
