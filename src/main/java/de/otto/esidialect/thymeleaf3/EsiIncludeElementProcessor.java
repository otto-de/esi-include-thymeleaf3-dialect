package de.otto.esidialect.thymeleaf3;

import de.otto.esidialect.EsiContentResolver;
import org.thymeleaf.context.ITemplateContext;
import org.thymeleaf.context.WebEngineContext;
import org.thymeleaf.model.IProcessableElementTag;
import org.thymeleaf.processor.element.AbstractElementTagProcessor;
import org.thymeleaf.processor.element.IElementTagStructureHandler;
import org.thymeleaf.templatemode.TemplateMode;

import java.util.List;

/**
 * Thymeleaf 3 processor that replaces {@code <esi:include ..>} tags with the content of its {@code src}-attribute.
 */
public class EsiIncludeElementProcessor extends AbstractElementTagProcessor {

    private EsiContentResolver esiContentResolver;
    private List<String> enabledUris;

    protected EsiIncludeElementProcessor(EsiContentResolver esiContentResolver, List<String> enabledUris) {
        super(TemplateMode.HTML, "esi", "include", true, null, false, 1000);
        this.esiContentResolver = esiContentResolver;
        this.enabledUris = enabledUris;
    }

    @Override
    protected void doProcess(ITemplateContext context, IProcessableElementTag tag, IElementTagStructureHandler structureHandler) {
        if (enabledUris == null || enabledUris.isEmpty() || enabledUris.stream().anyMatch(uri -> ((WebEngineContext) context).getRequest().getRequestURI().startsWith(uri))) {
            String src = tag.getAttributeValue("src");
            String body = esiContentResolver.fetch(src, tag.getTemplateName(), continueOnError(tag.getAttributeValue("onerror")));
            structureHandler.replaceWith(body, false);
        }
    }

    private boolean continueOnError(String onerror) {
        return "continue".equals(onerror);
    }

}
