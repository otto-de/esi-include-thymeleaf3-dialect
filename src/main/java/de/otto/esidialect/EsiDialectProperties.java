package de.otto.esidialect;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "esiinclude-thymeleaf-dialect")
public class EsiDialectProperties {

    private String prefixForRelativePath;

    private String proxyRedirectProtocol = "https";
    private String proxyRedirectHost;
    private int proxyPort = 8085;
    private boolean proxyEnabled;
    private boolean dialectEnabled;

    /**
     * Optional prefix for relative esi:include paths
     *
     * @return prefix for relative esi:include paths or null if not set
     */
    public String getPrefixForRelativePath() {
        return prefixForRelativePath;
    }

    public void setPrefixForRelativePath(String prefixForRelativePath) {
        this.prefixForRelativePath = prefixForRelativePath;
    }

    /**
     * The protocol that is used for redirects. default: "https"
     * @return the proxy redirect protocol
     */
    public String getProxyRedirectProtocol() {
        return proxyRedirectProtocol;
    }

    public void setProxyRedirectProtocol(String proxyRedirectProtocol) {
        this.proxyRedirectProtocol = proxyRedirectProtocol;
    }

    /**
     * The host where the request is redirected to
     * @return HTTP proxy redirect host
     */
    public String getProxyRedirectHost() {
        return proxyRedirectHost;
    }

    public void setProxyRedirectHost(String proxyRedirectHost) {
        this.proxyRedirectHost = proxyRedirectHost;
    }

    /**
     * @return HTTP proxy port, default: 8085
     */
    public int getProxyPort() {
        return proxyPort;
    }

    public void setProxyPort(int proxyPort) {
        this.proxyPort = proxyPort;
    }

    /**
     * @return whether the HTTP proxy is enabled or not
     */
    public boolean isProxyEnabled() {
        return proxyEnabled;
    }

    public void setProxyEnabled(boolean proxyEnabled) {
        this.proxyEnabled = proxyEnabled;
    }

    public boolean isDialectEnabled() {
        return dialectEnabled;
    }

    public void setDialectEnabled(boolean dialectEnabled) {
        this.dialectEnabled = dialectEnabled;
    }
}
