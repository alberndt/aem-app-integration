package com.alexanderberndt.appintegration.engine.resources.loader.impl;

import com.alexanderberndt.appintegration.exceptions.AppIntegrationException;
import com.alexanderberndt.appintegration.engine.resources.ExternalResource;
import com.alexanderberndt.appintegration.engine.resources.ExternalResourceRef;
import com.alexanderberndt.appintegration.engine.resources.loader.ResourceLoader;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.lang.invoke.MethodHandles;
import java.net.*;

public class HttpResourceLoader implements ResourceLoader {

    private static final Logger LOG = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());

    // Timeout, if no connection is established after milliseconds
    private static final int CONNECTION_TIMEOUT = 10000;

    // Timeout, if connection hangs
    private static final int READ_TIMEOUT = 20000;

    @Override
    public ExternalResource load(String baseUrl, ExternalResourceRef resourceRef) throws IOException {

        try {
            final URL url;
            if (StringUtils.isNotBlank(baseUrl)) {
                final URI baseUri = new URI(baseUrl);
                url = baseUri.resolve(resourceRef.getRelativeUrl()).toURL();
            } else {
                url = new URL(resourceRef.getRelativeUrl());
            }

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

                ExternalResource resource = new ExternalResource(resourceRef);
                resource.setInputStream(connection.getInputStream());

                // ToDo: verify mime type, get charset
                final String mimeType = connection.getHeaderField("Content-Type");
                System.out.println(mimeType);
                LOG.info("Fetching content for {}", resource.getRelativeUrl());

                return resource;

            } else {
                throw new AppIntegrationException("Failed to load resource " + resourceRef.getRelativeUrl()
                        + " - status: " + connection.getResponseCode()
                        + " - message: " + connection.getResponseMessage());
            }
        } catch (Exception e) {
            throw new AppIntegrationException("Failed to load resource " + resourceRef.getRelativeUrl(), e);
        }
    }

//
//    private static byte[] getResponseAsCompleteByteArray(HttpURLConnection connection) throws IOException {
//
//        final ByteArrayOutputStream store = new ByteArrayOutputStream();
//        final InputStream inputStream = connection.getInputStream();
//
//        int readNoOfBytes;
//        final byte[] buffer = new byte[1024];
//        while ((readNoOfBytes = inputStream.read(buffer)) >= 0) {
//            store.write(buffer, 0, readNoOfBytes);
//        }
//        return store.toByteArray();
//    }
//
}
