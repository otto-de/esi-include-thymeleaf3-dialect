package de.otto.esidialect;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "esiinclude-thymeleaf-dialect")
public class EsiDialectProperties {

    private String prefixForRelativePath;

    private int proxyPort;

    private boolean proxyEnabled;

    /**
     * Optional prefix for relative esi:include paths
     * @return prefix for relative esi:include paths or null if not set
     */
    public String getPrefixForRelativePath() {
        return prefixForRelativePath;
    }

    public void setPrefixForRelativePath(String prefixForRelativePath) {
        this.prefixForRelativePath = prefixForRelativePath;
    }

    public int getProxyPort() {
        return proxyPort;
    }

    public void setProxyPort(int proxyPort) {
        this.proxyPort = proxyPort;
    }

    public boolean isProxyEnabled() {
        return proxyEnabled;
    }

    public void setProxyEnabled(boolean proxyEnabled) {
        this.proxyEnabled = proxyEnabled;
    }
}
