package de.otto.esidialect.thymeleaf3;

import de.otto.esidialect.Fetch;
import org.thymeleaf.dialect.AbstractProcessorDialect;
import org.thymeleaf.processor.IProcessor;

import java.util.HashSet;
import java.util.Set;
import java.util.function.Function;

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

    private final Fetch fetch;
    private String prefixForRelativePath;

    /**
     * Creates the thymeleaf dialect for esi includes.
     * @param fetch a {@link Function} that takes the url to be fetched and returns a response object with its content and status
     * @param prefixForRelativePath optional, may be null. Protocol and hostname to prefix a relative url in the {@code src}-attribute. E.g. {@code "http://www.otto.de"}
     */
    public EsiDialect(Fetch fetch, String prefixForRelativePath) {
        super("esiIncludeDialect", "esi", 10000);
        this.fetch = fetch;
        this.prefixForRelativePath = prefixForRelativePath;
    }

    @Override
    public Set<IProcessor> getProcessors(String dialectPrefix) {
        final Set<IProcessor> processors = new HashSet<>();
        processors.add(new EsiIncludeElementProcessor(fetch, prefixForRelativePath));
        return processors;
    }
}
