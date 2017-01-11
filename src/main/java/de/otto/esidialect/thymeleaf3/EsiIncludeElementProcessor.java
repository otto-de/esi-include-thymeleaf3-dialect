package de.otto.esidialect.thymeleaf3;

import de.otto.esidialect.Fetch;
import de.otto.esidialect.Response;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.thymeleaf.context.ITemplateContext;
import org.thymeleaf.model.IProcessableElementTag;
import org.thymeleaf.processor.element.AbstractElementTagProcessor;
import org.thymeleaf.processor.element.IElementTagStructureHandler;
import org.thymeleaf.templatemode.TemplateMode;

import static java.lang.String.format;

/**
 * Thymeleaf 3 processor that replaces {@code <esi:include ..>} tags with the content of its {@code src}-attribute.
 */
public class EsiIncludeElementProcessor extends AbstractElementTagProcessor {

    private static final Logger LOG = LoggerFactory.getLogger(EsiIncludeElementProcessor.class);

    private Fetch fetch;
    private String prefixForRelativePath;

    protected EsiIncludeElementProcessor(Fetch fetch, String prefixForRelativePath) {
        super(TemplateMode.HTML, "esi", "include", true, null, false, 1000);
        this.fetch = fetch;
        this.prefixForRelativePath = checkPrefix(prefixForRelativePath);
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

    @Override
    protected void doProcess(ITemplateContext context, IProcessableElementTag tag, IElementTagStructureHandler structureHandler) {
        String src = tag.getAttributeValue("src");
        String body = fetch(src, tag.getTemplateName(), continueOnError(tag.getAttributeValue("onerror")));
        structureHandler.replaceWith(body, false);
    }

    String fetch(String srcAttribute, String templateName, boolean continueOnError) {
        final StringBuilder html = new StringBuilder("<!-- <esi:include src=\"" + srcAttribute + "\"> -->");
        try {
            String url = calculateUrl(srcAttribute);
            final Response response = fetch.apply(url);
            int statusCode = response.getStatusCode();
            if (statusCode < 300) {
                html.append(response.getResponseBody());
            } else {
                html.append(handleError(srcAttribute, templateName, statusCode + ": " + response.getStatusText(), null, continueOnError));
            }
        } catch (Exception e) {
            html.append(handleError(srcAttribute, templateName, e.getMessage(), e, continueOnError));
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

    private boolean continueOnError(String onerror) {
        return "continue".equals(onerror);
    }
}
