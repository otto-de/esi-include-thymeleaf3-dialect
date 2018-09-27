package de.otto.esidialect.thymeleaf3;

import de.otto.esidialect.EsiContentResolver;
import de.otto.esidialect.Fetch;
import de.otto.esidialect.Response;
import org.junit.Test;

import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class EsiIncludeElementProcessorTest {

    private static final boolean CONTINUE_ON_ERROR = true;
    private static final boolean NO_CONTINUE_ON_ERROR = false;

    @Test
    public void shouldResolveEsiInclude() {
        Fetch fetch = s -> withResponse(200, "test");

        //when
        final String html = resolver(fetch).fetch("someSrc", "template", CONTINUE_ON_ERROR);

        //then
        assertThat(html, is("<!-- <esi:include src=\"someSrc\"> -->test<!-- </esi:include> -->"));
    }

    @Test
    public void shouldHandleHttp404() {
        Fetch fetch = fetchWith404();

        //when
        final String html = resolver(fetch).fetch("someSrc", "template", CONTINUE_ON_ERROR);

        //then
        assertThat(html, is("<!-- <esi:include src=\"someSrc\"> --><!-- </esi:include> -->"));
    }

    @Test
    public void shouldHttp404WithError() {
        Fetch fetch = fetchWith404();

        //when
        final String html = resolver(fetch).fetch("someSrc", "template", NO_CONTINUE_ON_ERROR);

        //then
        assertThat(html, is("<!-- <esi:include src=\"someSrc\"> -->404: some status<!-- </esi:include> -->"));
    }

    @Test
    public void shouldFailOnUnknownStatusCode() {
        Fetch fetch = s -> withResponse(333, "kaputt");

        //when
        final String html = resolver(fetch).fetch("redirect", "template", CONTINUE_ON_ERROR);

        //then
        assertThat(html, is("<!-- <esi:include src=\"redirect\"> --><!-- </esi:include> -->"));
    }

    @Test
    public void shouldFailOnUnknownStatusCodeWithError() {
        Fetch fetch = s -> withResponse(333, "kaputt");

        //when
        final String html = resolver(fetch).fetch("redirect", "template", NO_CONTINUE_ON_ERROR);

        //then
        assertThat(html, is("<!-- <esi:include src=\"redirect\"> -->333: some status<!-- </esi:include> -->"));
    }


    @Test
    public void shouldHandleExceptionInFetchFunction() {
        Fetch fetch = s -> {
            throw new IllegalStateException("error");
        };

        //when
        final String html = resolver(fetch).fetch("redirect", "template", CONTINUE_ON_ERROR);

        //then
        assertThat(html, is("<!-- <esi:include src=\"redirect\"> --><!-- </esi:include> -->"));
    }

    @Test
    public void shouldHandleExceptionInFetchFunctionWithError() {
        Fetch fetch = s -> {
            throw new IllegalStateException("error");
        };

        //when
        final String html = resolver(fetch).fetch("redirect", "template", NO_CONTINUE_ON_ERROR);

        //then
        assertThat(html, is("<!-- <esi:include src=\"redirect\"> -->error<!-- </esi:include> -->"));
    }

    @Test
    public void shouldReplaceRelativePathWithAbsolutePath() {
        Fetch fetch = src -> withResponse(200, src);

        //when
        final String html = resolver(fetch).fetch("/relative", "template", NO_CONTINUE_ON_ERROR);

        //then
        assertThat(html, is("<!-- <esi:include src=\"/relative\"> -->https://www.otto.de/relative<!-- </esi:include> -->"));
    }

    @Test
    public void shouldNotReplaceRelativePathWhenHostnameIsEmpty() {
        Fetch fetch = src -> withResponse(200, src);

        //when
        final String html = resolver(fetch, null).fetch("/relative", "template", NO_CONTINUE_ON_ERROR);

        //then
        assertThat(html, is("<!-- <esi:include src=\"/relative\"> -->/relative<!-- </esi:include> -->"));
    }

    private Fetch fetchWith404() {
        return s -> withResponse(404, "irgendwas");
    }

    private EsiContentResolver resolver(Fetch fetch) {
        return resolver(fetch, "https://www.otto.de");
    }

    private EsiContentResolver resolver(Fetch fetch, String hostname) {
        return new EsiContentResolver(fetch, hostname);
    }

    private Response withResponse(int statusCode, String responseBody) {
        Response response = mock(Response.class);
        when(response.getResponseBody()).thenReturn(responseBody);
        when(response.getStatusCode()).thenReturn(statusCode);
        when(response.getStatusText()).thenReturn("some status");
        return response;
    }


}