package com.alexanderberndt.appintegration.engine.loader;

import com.alexanderberndt.appintegration.engine.ResourceLoader;
import com.alexanderberndt.appintegration.engine.resources.ExternalResource;
import com.alexanderberndt.appintegration.engine.resources.ExternalResourceFactory;
import com.alexanderberndt.appintegration.engine.resources.ExternalResourceRef;
import com.alexanderberndt.appintegration.engine.resources.ExternalResourceType;
import com.alexanderberndt.appintegration.exceptions.AppIntegrationException;
import org.osgi.service.component.annotations.Component;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.Nonnull;
import java.lang.invoke.MethodHandles;
import java.net.*;

@Component
public class HttpResourceLoader implements ResourceLoader {

    private static final Logger LOG = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());

    // Timeout, if no connection is established after milliseconds
    private static final int CONNECTION_TIMEOUT = 10000;

    // Timeout, if connection hangs
    private static final int READ_TIMEOUT = 20000;

    @Override
    public ExternalResource load(@Nonnull ExternalResourceRef resourceRef, @Nonnull ExternalResourceFactory factory) {

        try {
            final URL url = resourceRef.getUri().toURL();

            // ToDo: Use global proxy settings

//            final Proxy proxy = new Proxy(Proxy.Type.HTTP, new InetSocketAddress("127.0.0.1", 8888));
//            final HttpURLConnection connection = (HttpURLConnection) url.openConnection(proxy);

            final HttpURLConnection connection = (HttpURLConnection) url.openConnection();

            connection.setRequestMethod("GET");

            //connection.setRequestProperty("If-Modified-Since", "Tue, 21 Jul 2020 14:31:16 GMT");
            connection.setRequestProperty("If-None-Match", "\"1b0c-56dbc06964990xx\"");
            connection.setRequestProperty("User-Agent", "aem appintegration-client");
            connection.setConnectTimeout(CONNECTION_TIMEOUT);
            connection.setReadTimeout(READ_TIMEOUT);
            connection.connect();

            if (connection.getResponseCode() == 200) {

                ExternalResource resource = factory.createExternalResource(connection.getInputStream(), resourceRef, this);

                // ToDo: verify mime type, get charset
                final String mimeType = connection.getHeaderField("Content-Type");
                System.out.println(mimeType);
                LOG.info("Fetching content for {}", resource.getUri());

                return resource;

            } else {
                throw new AppIntegrationException("Failed to load resource " + resourceRef.getUri()
                        + " - status: " + connection.getResponseCode()
                        + " - message: " + connection.getResponseMessage());
            }
        } catch (Exception e) {
            throw new AppIntegrationException("Failed to load resource " + resourceRef.getUri(), e);
        }
    }

    @Override
    public ExternalResourceRef resolveRelativeUrl(@Nonnull URI baseUri, @Nonnull String relativeUrl, @Nonnull ExternalResourceType expectedType) {
        try {
            final URL url = baseUri.resolve(relativeUrl).toURL();
            return resolveAbsoluteUrl(url.toString(), expectedType);
        } catch (URISyntaxException | MalformedURLException e) {
            LOG.error(String.format("Cannot resolve relative-url %s from base-url %s", relativeUrl, baseUri), e);
            // ToDo: Add error message to any context object
            return null;
        }
    }

}
