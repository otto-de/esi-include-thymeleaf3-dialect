package de.otto.esidialect;

import com.sun.net.httpserver.HttpContext;
import com.sun.net.httpserver.HttpServer;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

import javax.annotation.PostConstruct;
import java.io.IOException;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.net.URI;
import java.net.URISyntaxException;

@Configuration
@EnableConfigurationProperties(EsiDialectProperties.class)
@Profile({"prod", "local"})
public class LocalhostProxyConfiguration {

    private EsiDialectProperties esiDialectProperties;

    private Fetch fetch;

    public LocalhostProxyConfiguration(EsiDialectProperties esiDialectProperties, Fetch fetch) {
        this.esiDialectProperties = esiDialectProperties;
        this.fetch = fetch;
    }

    @PostConstruct
    public void startLocalhostDevelopProxy() throws IOException {

        HttpServer server = HttpServer.create(new InetSocketAddress(8085), 0);
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
