package de.otto.esidialect;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "esiinclude-thymeleaf-dialect")
public class EsiDialectProperties {

    private String prefixForRelativePath;

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
}
