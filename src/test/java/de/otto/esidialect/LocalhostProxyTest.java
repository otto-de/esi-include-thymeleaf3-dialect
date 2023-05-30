package de.otto.esidialect;

import org.awaitility.Awaitility;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.stream.Collectors;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class LocalhostProxyTest {

    private LocalhostProxy localhostProxy;

    @BeforeEach
    public void setUp() {
        EsiDialectProperties esiDialectProperties = configureProxy();

        Fetch fetch = mock(Fetch.class);
        when(fetch.apply("http://somehost/test")).thenReturn(new Response(200, "OK", "test response".getBytes(), "text/html"));
        localhostProxy = new LocalhostProxy(esiDialectProperties, fetch);
    }

    @AfterEach
    public void tearDown() {
        localhostProxy.stopProxy();
    }

    @Test
    public void shouldProxyToRedirectUrl() throws IOException {
        //given
        localhostProxy.startProxy();

        //when
        URL url = new URL("http://localhost:8888/test");
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();

        //then
        String result = toString(connection.getInputStream());
        assertThat(result, is("test response"));

        //when
        localhostProxy.stopProxy();

        //then
        assertProxyIsStopped(url);
    }


    @Test
    public void shouldIgnoreWhenPortIsAlreadyInUse() throws IOException {
        //given
        localhostProxy.startProxy();

        //when
        localhostProxy.startProxy(); //should not fail
    }

    private void assertProxyIsStopped(URL url) {
        Awaitility.await().until(() -> {
            try {
                url.openConnection().connect();
                return false;
            } catch (IOException e) {
                return true;
            }
        });
    }


    private EsiDialectProperties configureProxy() {
        EsiDialectProperties esiDialectProperties = new EsiDialectProperties();
        esiDialectProperties.setProxyRedirectProtocol("http");
        esiDialectProperties.setProxyRedirectHost("somehost");
        esiDialectProperties.setProxyPort(8888);
        return esiDialectProperties;
    }


    private String toString(InputStream inputStream) throws IOException {
        try (BufferedReader br = new BufferedReader(new InputStreamReader(inputStream, StandardCharsets.UTF_8))) {
            return br.lines().collect(Collectors.joining(System.lineSeparator()));
        }
    }
}
