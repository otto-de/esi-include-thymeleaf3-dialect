package de.otto.esidialect;

import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * <p>Defines an HTTP proxy that is used to retrieve secondary includes if desired: Esi-included pages might
 * include resources like CSS and JavaScript files causing CORS errors. Therefore, this proxy sets the HTTP
 * header <code>Access-Control-Allow-Origin: *</code> on all responses.</p>
 */
@Configuration
@EnableConfigurationProperties(EsiDialectProperties.class)
@ConditionalOnProperty(prefix = "esiinclude-thymeleaf-dialect", value = "proxy-enabled", havingValue = "true")
public class LocalhostProxyConfiguration {

    @Bean
    public LocalhostProxy localhostProxy(EsiDialectProperties esiDialectProperties, Fetch fetch) {
        return new LocalhostProxy(esiDialectProperties, fetch);
    }

}
