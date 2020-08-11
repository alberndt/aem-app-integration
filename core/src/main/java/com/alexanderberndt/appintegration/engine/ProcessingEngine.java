package com.alexanderberndt.appintegration.engine;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;

@Deprecated
public class ProcessingEngine<I extends ApplicationInstance> {

    public void prefetch(List<I> instancesList) {

    }

    public void getApplicationProperties(I instance) {
        // get base url
        // get get application properties (cached)
        // parse application properties
    }

    public void getHtmlSnippet(I instance) throws IOException {
        // get from cache or load


        // getApplicationProperties
        // determine required context variables
        // resolve context variables for instance
        // calculate entry-point
        // get from cache or load html-snippet
    }

    public InputStream getStaticResource(String applicationId, String path) {
        // ToDo: Support caching headers
        return null;
    }

}
