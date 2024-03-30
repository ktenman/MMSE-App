package ee.tenman.mmse;

import ee.tenman.mmse.config.AsyncSyncConfiguration;
import ee.tenman.mmse.config.EmbeddedSQL;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.util.TestPropertyValues;
import org.springframework.context.ApplicationContextInitializer;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ContextConfiguration;
import org.testcontainers.containers.GenericContainer;
import org.testcontainers.utility.DockerImageName;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Base composite annotation for integration tests.
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@SpringBootTest(classes = {MmseApp.class, AsyncSyncConfiguration.class})
@EmbeddedSQL
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_CLASS)
@ContextConfiguration(initializers = IntegrationTest.Initializer.class)
public @interface IntegrationTest {
    class Initializer implements ApplicationContextInitializer<ConfigurableApplicationContext> {
        private static final String CUSTOM_PASSWORD = "something";

        private static final GenericContainer<?> REDIS_CONTAINER =
            new GenericContainer<>(DockerImageName.parse("redis:7.2-alpine"))
                .withExposedPorts(6379)
                .withCommand("redis-server", "--requirepass", CUSTOM_PASSWORD);

        static {
            REDIS_CONTAINER.start();
        }

        @Override
        public void initialize(ConfigurableApplicationContext applicationContext) {
            TestPropertyValues.of(
                "spring.data.redis.host=" + REDIS_CONTAINER.getHost(),
                "spring.data.redis.port=" + REDIS_CONTAINER.getFirstMappedPort(),
                "spring.data.redis.password=" + CUSTOM_PASSWORD
            ).applyTo(applicationContext.getEnvironment());
        }
    }
}
