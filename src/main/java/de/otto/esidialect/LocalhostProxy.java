package de.otto.esidialect;

import com.sun.net.httpserver.HttpContext;
import com.sun.net.httpserver.HttpServer;
import jakarta.annotation.PostConstruct;
import jakarta.annotation.PreDestroy;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.OutputStream;
import java.net.BindException;
import java.net.InetSocketAddress;
import java.net.URI;
import java.net.URISyntaxException;

public class LocalhostProxy {
    private static final Logger LOGGER = LoggerFactory.getLogger(LocalhostProxyConfiguration.class);

    private HttpServer server;

    private EsiDialectProperties esiDialectProperties;

    private Fetch fetch;

    public LocalhostProxy(EsiDialectProperties esiDialectProperties, Fetch fetch) {
        this.esiDialectProperties = esiDialectProperties;
        this.fetch = fetch;
    }

    @PostConstruct
    public void startProxy() throws IOException {
        if (server != null) {
            return;
        }

        try {
            server = HttpServer.create(new InetSocketAddress(esiDialectProperties.getProxyPort()), 0);
        } catch (BindException e) {
            // Spring caches up to 32 test contexts, so that the proxy could have been bound in an already cached context
            LOGGER.warn("Port already in use. Assuming that the proxy is already running");
            return;
        }

        HttpContext context = server.createContext("/");
        context.setHandler(httpExchange -> {

            URI redirectUri = changeHostToProxyHost(httpExchange.getRequestURI());

            Response response = fetch.apply(redirectUri.toString());

            httpExchange.getResponseHeaders().add("Access-Control-Allow-Origin", "*");
            httpExchange.getResponseHeaders().add("Content-Type", response.getContentType());

            httpExchange.sendResponseHeaders(response.getStatusCode(), response.getResponseBodyAsBytes().length);

            OutputStream outputStream = httpExchange.getResponseBody();
            outputStream.write(response.getResponseBodyAsBytes());
            outputStream.flush();
            httpExchange.close();

        });
        server.start();
    }

    @PreDestroy
    public void stopProxy() {
        if (server != null) {
            LOGGER.info("stopping proxy");
            server.stop(0);
        }
    }

    private URI changeHostToProxyHost(URI uri) {
        try {
            return new URI(esiDialectProperties.getProxyRedirectProtocol(), esiDialectProperties.getProxyRedirectHost(), uri.getPath(), uri.getQuery(), uri.getFragment());
        } catch (URISyntaxException e) {
            throw new RuntimeException(e);
        }
    }

}
