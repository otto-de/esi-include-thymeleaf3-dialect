package de.otto.esidialect.thymeleaf3;

import de.otto.esidialect.EsiContentResolver;
import org.thymeleaf.dialect.AbstractProcessorDialect;
import org.thymeleaf.processor.IProcessor;

import java.util.HashSet;
import java.util.Set;

/**
 * <p>A thymeleaf 3 dialect that processes {@code <esi:include>} tags.</p>
 * <p>
 * The content of the url of the {@code src}-attribute is fetched and inserted into the page flow,
 * just as a esi-include processor like e.g.<strong>varnish</strong> would do.</p>
 * <p>
 * If the {@code onerror}-attribute is set to {@code continue}, any errors that occur while fetching the document are ignored.
 * <br>Otherwise an error message is output into the pageflow.
 * </p>
 */
public class EsiDialect extends AbstractProcessorDialect {

    private EsiContentResolver esiContentResolver;

    /**
     * Creates the thymeleaf dialect for esi includes.
     */
    public EsiDialect(EsiContentResolver esiContentResolver) {
        super("esiIncludeDialect", "esi", 10000);
        this.esiContentResolver = esiContentResolver;
    }

    @Override
    public Set<IProcessor> getProcessors(String dialectPrefix) {
        final Set<IProcessor> processors = new HashSet<>();
        processors.add(new EsiIncludeElementProcessor(esiContentResolver));
        return processors;
    }
}
