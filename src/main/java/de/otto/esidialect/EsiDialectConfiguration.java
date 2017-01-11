package de.otto.esidialect;

import com.ning.http.client.AsyncHttpClient;
import com.ning.http.client.AsyncHttpClientConfig;
import de.otto.esidialect.thymeleaf3.EsiDialect;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
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
@Profile({"local", "prod"})
public class EsiDialectConfiguration {

    @Bean
    @ConditionalOnBean(AsyncHttpClient.class)
    @ConditionalOnMissingBean(Fetch.class)
    public Fetch fetch() {
        final Integer timeout = 2 * 1000;
        final int maxRedirects = 20;
        final AsyncHttpClientConfig cfg = new AsyncHttpClientConfig.Builder()
                .setConnectTimeout(timeout)
                .setRequestTimeout(timeout)
                .setFollowRedirect(true)
                .setMaxRedirects(maxRedirects)
                .build();

        final AsyncHttpClient httpClient = new AsyncHttpClient(cfg);

        return src -> {
            try {
                com.ning.http.client.Response response = httpClient.prepareGet(src).execute().get();
                return new Response(response.getStatusCode(), response.getStatusText(), response.getResponseBody());
            } catch (InterruptedException | ExecutionException | IOException e) {
                throw new RuntimeException(e);
            }
        };
    }

    @Bean
    @ConditionalOnClass(AbstractElementTagProcessor.class)
    @ConditionalOnMissingBean(EsiDialect.class)
    public EsiDialect conditionalEsiDialect(Fetch fetch, @Value("${esiinclude-thymeleaf-dialect.prefixForRelativePath:}") String prefixForRelativePath) {
        return new EsiDialect(fetch, prefixForRelativePath);
    }

}
