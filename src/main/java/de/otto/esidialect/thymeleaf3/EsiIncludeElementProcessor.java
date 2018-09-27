package de.otto.esidialect.thymeleaf3;

import de.otto.esidialect.EsiContentResolver;
import org.thymeleaf.context.ITemplateContext;
import org.thymeleaf.model.IProcessableElementTag;
import org.thymeleaf.processor.element.AbstractElementTagProcessor;
import org.thymeleaf.processor.element.IElementTagStructureHandler;
import org.thymeleaf.templatemode.TemplateMode;

/**
 * Thymeleaf 3 processor that replaces {@code <esi:include ..>} tags with the content of its {@code src}-attribute.
 */
public class EsiIncludeElementProcessor extends AbstractElementTagProcessor {

    private EsiContentResolver esiContentResolver;

    protected EsiIncludeElementProcessor(EsiContentResolver esiContentResolver) {
        super(TemplateMode.HTML, "esi", "include", true, null, false, 1000);
        this.esiContentResolver = esiContentResolver;
    }

    @Override
    protected void doProcess(ITemplateContext context, IProcessableElementTag tag, IElementTagStructureHandler structureHandler) {
        String src = tag.getAttributeValue("src");
        String body = esiContentResolver.fetch(src, tag.getTemplateName(), continueOnError(tag.getAttributeValue("onerror")));
        structureHandler.replaceWith(body, false);
    }

    private boolean continueOnError(String onerror) {
        return "continue".equals(onerror);
    }

}
