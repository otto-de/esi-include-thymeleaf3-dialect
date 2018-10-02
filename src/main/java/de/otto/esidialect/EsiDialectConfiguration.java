package de.otto.esidialect;

import de.otto.esidialect.thymeleaf3.EsiDialect;
import org.asynchttpclient.AsyncHttpClient;
import org.asynchttpclient.AsyncHttpClientConfig;
import org.asynchttpclient.DefaultAsyncHttpClient;
import org.asynchttpclient.DefaultAsyncHttpClientConfig;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.thymeleaf.processor.element.AbstractElementTagProcessor;

import java.io.IOException;
import java.util.concurrent.ExecutionException;

/**
 * Configures the thymeleaf {@code <esi:include>} dialect.
 * <p>
 * You need to provide a fetch function that takes the source url as parameter and returns a response object with the result and status.
 * </p>
 * <p>
 * This library has a compile-only dependency on thymeleaf3.
 * The user of this library needs to provide the thymleaf3 dependency at runtime, so that the EsiDialect bean is created
 * and automatically registered as another esi-dialect by spring.
 * </p>
 * <p>
 * Furthermore this library has a compile-only dependency on com.ning:async-http-client.
 * When this library is provided at runtime, a fetch-function bean is created that uses AsyncHttpClient internally.
 * A user of this library is free to override this bean definition or provide a different implementation for the fetch-function
 * </p>
 */
@Configuration
@EnableConfigurationProperties(EsiDialectProperties.class)
@ConditionalOnProperty(prefix = "esiinclude-thymeleaf-dialect",
        value = "dialect-enabled", havingValue = "true")
public class EsiDialectConfiguration {

    @Bean
    public Fetch fetch(String userAgent, EsiDialectProperties properties) {
        final Integer timeout = 2 * 1000;
        final int maxRedirects = 20;
        final AsyncHttpClientConfig cfg = new DefaultAsyncHttpClientConfig.Builder()
                .setConnectTimeout(timeout)
                .setRequestTimeout(timeout)
                .setFollowRedirect(true)
                .setMaxRedirects(maxRedirects)
                .setUserAgent(userAgent)
                .build();

        final String urlPrefix;
        if (properties.isProxyEnabled()) {
            urlPrefix = "http://localhost:" + properties.getProxyPort();
        } else {
            urlPrefix = properties.getPrefixForRelativePath();
        }

        return src -> {
            try (AsyncHttpClient httpClient = new DefaultAsyncHttpClient(cfg)) {
                org.asynchttpclient.Response response = httpClient.prepareGet(src).execute().get();

                byte[] responseBodyAsBytes = response.getResponseBodyAsBytes();

                if (requiresSanitizing(response.getContentType())) {
                    String responseBody = response.getResponseBody();
                    String sanitizedEsiIncludesBody = responseBody.replaceAll("href=\"/", "href=\"" + urlPrefix + "/");
                    sanitizedEsiIncludesBody = sanitizedEsiIncludesBody.replaceAll("src=\"/", "src=\"" + urlPrefix + "/");
                    responseBodyAsBytes = sanitizedEsiIncludesBody.getBytes();
                }

                return new Response(response.getStatusCode(), response.getStatusText(), responseBodyAsBytes, response.getContentType());
            } catch (InterruptedException | ExecutionException | IOException e) {
                throw new RuntimeException(e);
            }
        };

    }

    private boolean requiresSanitizing(String contentType) {
        return contentType != null && contentType.contains("text/html");
    }

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
