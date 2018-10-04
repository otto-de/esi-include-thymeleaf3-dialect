package de.otto.esidialect;

import de.otto.esidialect.thymeleaf3.EsiDialect;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.thymeleaf.processor.element.AbstractElementTagProcessor;

/**
 * Configures the thymeleaf {@code <esi:include>} dialect.
 * <p>
 * You need to provide a fetch function that takes the source url as parameter and returns a response object with the result and status.
 * </p>
 * <p>
 * This library has a compile-only dependency on thymeleaf3.
 * The user of this library needs to provide the thymeleaf3 dependency at runtime, so that the EsiDialect bean is created
 * and automatically registered as another esi-dialect by spring.
 * </p>
 * <p>
 * Furthermore this library has a compile-only dependency on com.ning:async-http-client.
 * When this library is provided at runtime, a fetch-function bean is created that uses AsyncHttpClient internally.
 * A user of this library is free to override this bean definition or provide a different implementation for the fetch-function.
 * </p>
 */
@Configuration
@EnableConfigurationProperties(EsiDialectProperties.class)
@ConditionalOnProperty(prefix = "esiinclude-thymeleaf-dialect",
        value = "dialect-enabled", havingValue = "true")
public class EsiDialectConfiguration {

    @Bean
    @ConditionalOnClass(AbstractElementTagProcessor.class)
    @ConditionalOnMissingBean(EsiDialect.class)
    public EsiDialect conditionalEsiDialect(EsiContentResolver esiContentResolver) {
        return new EsiDialect(esiContentResolver);
    }

    @Bean
    @ConditionalOnClass(AbstractElementTagProcessor.class)
    @ConditionalOnMissingBean(EsiContentResolver.class)
    public EsiContentResolver esiContentResolver(Fetch fetch, EsiDialectProperties properties) {
        return new EsiContentResolver(fetch, properties.getPrefixForRelativePath());
    }


}
