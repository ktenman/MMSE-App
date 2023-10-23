package ee.tenman.mmse.config;

import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class FilterConfig {
    @Bean
    public FilterRegistrationBean<DebugFilter> loggingFilter() {
        FilterRegistrationBean<DebugFilter> registrationBean
            = new FilterRegistrationBean<>();

        registrationBean.setFilter(new DebugFilter());
        registrationBean.addUrlPatterns("/*");

        return registrationBean;
    }
}
