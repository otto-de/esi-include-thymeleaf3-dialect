package de.otto.esidialect;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "esiinclude-thymeleaf-dialect")
public class EsiDialectProperties {

    private String prefixForRelativePath;

    private int proxyPort = 8085;

    private boolean proxyEnabled;

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
}
