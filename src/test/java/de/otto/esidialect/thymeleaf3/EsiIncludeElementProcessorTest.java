package de.otto.esidialect.thymeleaf3;

import de.otto.esidialect.EsiContentResolver;
import org.junit.jupiter.api.Test;
import org.thymeleaf.context.WebEngineContext;
import org.thymeleaf.model.IProcessableElementTag;
import org.thymeleaf.processor.element.IElementTagStructureHandler;
import org.thymeleaf.web.IWebExchange;
import org.thymeleaf.web.IWebRequest;

import java.util.Collections;

import static org.mockito.Mockito.*;

public class EsiIncludeElementProcessorTest {

    private final EsiContentResolver esiContentResolver = mock(EsiContentResolver.class);

    @Test
    public void shouldNotReplaceUrlThatIsNotEnabled() {
        //given
        EsiIncludeElementProcessor esiIncludeElementProcessor = new EsiIncludeElementProcessor(esiContentResolver, Collections.singletonList("/some/enabled/url"));
        WebEngineContext templateContext = withContextRequest("/some/not/enabled/url/with/more/path/params");

        //when
        esiIncludeElementProcessor.doProcess(templateContext, withTag("someSrc", false), mock(IElementTagStructureHandler.class));

        //then
        verifyNoInteractions(esiContentResolver);
    }

    @Test
    public void shouldReplaceUrlThatIsEnabled() {
        //given
        EsiIncludeElementProcessor esiIncludeElementProcessor = new EsiIncludeElementProcessor(esiContentResolver, Collections.singletonList("/some/enabled/url"));
        WebEngineContext templateContext = withContextRequest("/some/enabled/url/with/more/path/params");
        IProcessableElementTag tag = withTag("someSrc", false);

        //when
        esiIncludeElementProcessor.doProcess(templateContext, tag, mock(IElementTagStructureHandler.class));

        //then
        verify(esiContentResolver).fetch("someSrc", null, false);
    }

    @Test
    public void shouldReplaceUrlWhenEnabledUriListIsEmpty() {
        //given
        EsiIncludeElementProcessor esiIncludeElementProcessor = new EsiIncludeElementProcessor(esiContentResolver, Collections.emptyList());
        WebEngineContext templateContext = withContextRequest("/some/enabled/url/with/more/path/params");
        IProcessableElementTag tag = withTag("someSrc", false);

        //when
        esiIncludeElementProcessor.doProcess(templateContext, tag, mock(IElementTagStructureHandler.class));

        //then
        verify(esiContentResolver).fetch("someSrc", null, false);
    }

    private WebEngineContext withContextRequest(String requestUri) {
        IWebRequest mockRequest = mock(IWebRequest.class);
        when(mockRequest.getRequestURL()).thenReturn(requestUri);
        IWebExchange mockExchange = mock(IWebExchange.class);
        when(mockExchange.getRequest()).thenReturn(mockRequest);
        WebEngineContext templateContext = mock(WebEngineContext.class);
        when(templateContext.getExchange()).thenReturn(mockExchange);
        return templateContext;
    }

    private IProcessableElementTag withTag(String src, boolean onerror) {
        IProcessableElementTag tag = mock(IProcessableElementTag.class);
        when(tag.getAttributeValue("src")).thenReturn(src);
        when(tag.getAttributeValue("onerror")).thenReturn(String.valueOf(onerror));
        return tag;
    }

}