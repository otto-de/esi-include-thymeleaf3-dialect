package de.otto.esidialect;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.function.Function;

import static java.lang.String.format;

public class EsiContentResolver {

    private static final Logger LOG = LoggerFactory.getLogger(EsiContentResolver.class);
    private Fetch fetch;
    private final String prefixForRelativePath;

    /**
     *
     * @param fetch a {@link Function} that takes the url to be fetched and returns a response object with its content and status
     * @param prefixForRelativePath optional, may be null. Protocol and hostname to prefix a relative url in the {@code src}-attribute. E.g. {@code "http://www.otto.de"}
     */
    public EsiContentResolver(Fetch fetch, String prefixForRelativePath) {
        this.fetch = fetch;
        this.prefixForRelativePath = checkPrefix(prefixForRelativePath);
    }

    public String fetch(String src, String templateName, boolean continueOnError) {
        final StringBuilder html = new StringBuilder("<!-- <esi:include src=\"" + src + "\"> -->");
        try {
            String url = calculateUrl(src);
            final Response response = fetch.apply(url);
            int statusCode = response.getStatusCode();
            if (statusCode < 300) {
                html.append(response.getResponseBody());
            } else {
                html.append(handleError(src, templateName, statusCode + ": " + response.getStatusText(), null, continueOnError));
            }
        } catch (Exception e) {
            html.append(handleError(src, templateName, e.getMessage(), e, continueOnError));
        }
        return html.append("<!-- </esi:include> -->").toString();
    }

    private String calculateUrl(String srcAttribute) {
        if ((prefixForRelativePath != null) && srcAttribute.startsWith("/")) {
            return prefixForRelativePath + srcAttribute;
        } else {
            return srcAttribute;
        }
    }

    private String handleError(String srcAttribute, String templateName, String errorMessage, Exception e, boolean continueOnError) {
        LOG.error(format("Unable to resolve esi:include src=%s in %s. Request failed with %s", srcAttribute, templateName, errorMessage), e);
        if (continueOnError) {
            return "";
        } else {
            return errorMessage;
        }
    }

    private String checkPrefix(String prefixForRelativePath) {
        if (!isNullOrEmpty(prefixForRelativePath) && !prefixStartsWithProtocol(prefixForRelativePath)) {
            throw new IllegalArgumentException("invalid prefix");
        }
        return prefixForRelativePath;
    }

    private boolean isNullOrEmpty(String prefixForRelativePath) {
        return (prefixForRelativePath == null) || prefixForRelativePath.equals("");
    }

    private boolean prefixStartsWithProtocol(String prefixForRelativePath) {
        return prefixForRelativePath.startsWith("http://") || prefixForRelativePath.startsWith("https://");
    }



}
