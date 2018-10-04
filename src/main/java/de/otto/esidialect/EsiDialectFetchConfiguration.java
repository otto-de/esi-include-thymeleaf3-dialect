package de.otto.esidialect;

import org.asynchttpclient.AsyncHttpClient;
import org.asynchttpclient.AsyncHttpClientConfig;
import org.asynchttpclient.DefaultAsyncHttpClient;
import org.asynchttpclient.DefaultAsyncHttpClientConfig;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.io.IOException;
import java.util.concurrent.ExecutionException;

/**
 *
 * Provides a default Fetch implementation that resolves esi includes through the internal HTTP proxy.
 * The HTTP proxy sets CORS headers to "*" for all responses.
 *
 * When a Bean "userAgent" is provided, this user agent is set on all requests.
 *
 */
@Configuration
@ConditionalOnClass(AsyncHttpClient.class)
public class EsiDialectFetchConfiguration {

    @Bean
    @ConditionalOnMissingBean
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


}
