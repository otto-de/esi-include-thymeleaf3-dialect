package de.otto.esidialect.thymeleaf3;

import de.otto.esidialect.EsiContentResolver;
import org.thymeleaf.dialect.AbstractProcessorDialect;
import org.thymeleaf.processor.IProcessor;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * <p>A thymeleaf 3 dialect that processes {@code <esi:include>} tags.</p>
 * @see EsiContentResolver
 */
public class EsiDialect extends AbstractProcessorDialect {

    private EsiContentResolver esiContentResolver;
    private List<String> enabledUris;

    /**
     * Creates the thymeleaf dialect for esi includes.
     * @param esiContentResolver actual resolver that fetches the target URL's content
     * @param enabledUris request uris where included esis should be resolved
     */
    public EsiDialect(EsiContentResolver esiContentResolver, List<String> enabledUris) {
        super("esiIncludeDialect", "esi", 10000);
        this.esiContentResolver = esiContentResolver;
        this.enabledUris = enabledUris;
    }

    @Override
    public Set<IProcessor> getProcessors(String dialectPrefix) {
        final Set<IProcessor> processors = new HashSet<>();
        processors.add(new EsiIncludeElementProcessor(esiContentResolver, enabledUris));
        return processors;
    }
}
