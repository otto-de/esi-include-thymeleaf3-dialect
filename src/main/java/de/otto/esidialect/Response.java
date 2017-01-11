package de.otto.esidialect;

/**
 * Response from the fetch-function.
 */
public class Response {

    private int statusCode;
    private String responseBody;
    private String statusText;

    public Response(int statusCode, String statusText, String responseBody) {
        this.statusCode = statusCode;
        this.responseBody = responseBody;
        this.statusText = statusText;
    }

    public int getStatusCode() {
        return statusCode;
    }

    public String getResponseBody() {
        return responseBody;
    }

    public String getStatusText() {
        return statusText;
    }


}
