package de.otto.esidialect;

import com.sun.net.httpserver.HttpContext;
import com.sun.net.httpserver.HttpServer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

import javax.annotation.PostConstruct;
import java.io.IOException;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.net.URI;
import java.net.URISyntaxException;

/**
 * <p>Defines an HTTP proxy that is used to retrieve secondary includes if desired: Esi-included pages might
 * include resources like CSS and JavaScript files causing CORS errors. Therefore, this proxy sets the HTTP
 * header <code>Access-Control-Allow-Origin: *</code> on all responses.</p>
 */
@Configuration
@EnableConfigurationProperties(EsiDialectProperties.class)
@Profile({"prod", "local"})
@ConditionalOnProperty(prefix = "esiinclude-thymeleaf-dialect",
        value = "proxy-enabled", havingValue = "true")
public class LocalhostProxyConfiguration {

    private EsiDialectProperties esiDialectProperties;

    private Fetch fetch;

    public LocalhostProxyConfiguration(EsiDialectProperties esiDialectProperties, Fetch fetch) {
        this.esiDialectProperties = esiDialectProperties;
        this.fetch = fetch;
    }

    @PostConstruct
    public void startLocalhostDevelopProxy() throws IOException {

        HttpServer server = HttpServer.create(new InetSocketAddress(esiDialectProperties.getProxyPort()), 0);
        HttpContext context = server.createContext("/");
        context.setHandler(httpExchange -> {

            try {
                URI redirectUri = changeHostToDevelopOttoDe(httpExchange.getRequestURI());

                Response response = fetch.apply(redirectUri.toString());

                httpExchange.getResponseHeaders().add("Access-Control-Allow-Origin", "*");
                httpExchange.getResponseHeaders().add("Content-Type", response.getContentType());

                httpExchange.sendResponseHeaders(response.getStatusCode(), response.getResponseBodyAsBytes().length);

                OutputStream outputStream = httpExchange.getResponseBody();
                outputStream.write(response.getResponseBodyAsBytes());
                outputStream.flush();
                httpExchange.close();

            } catch (URISyntaxException e) {
                e.printStackTrace();
            }


        });
        server.start();
    }

    private URI changeHostToDevelopOttoDe(URI uri) throws URISyntaxException {
        return new URI("https", "develop.otto.de", uri.getPath(), uri.getQuery(), uri.getFragment());
    }

}