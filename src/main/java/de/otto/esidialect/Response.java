package de.otto.esidialect;

import org.springframework.util.MultiValueMap;

/**
 * Response from the fetch-function. It uses byte[] instead of String so that binary content can be passed as well.
 */
public class Response {

    private int statusCode;
    private String statusText;
    private byte[] responseBody;
    private String contentType;

    public Response(int statusCode, String statusText, byte[] responseBody, String contentType) {
        this.statusCode = statusCode;
        this.statusText = statusText;
        this.responseBody = responseBody;
        this.contentType = contentType;
    }

    public int getStatusCode() {
        return statusCode;
    }

    public String getResponseBody() {
        return new String(responseBody);
    }

    public byte[] getResponseBodyAsBytes() {
        return responseBody;
    }

    public String getStatusText() {
        return statusText;
    }

    public String getContentType() {
        return contentType;
    }
}
