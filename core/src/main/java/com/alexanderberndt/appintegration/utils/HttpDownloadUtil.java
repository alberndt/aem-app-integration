package com.alexanderberndt.appintegration.utils;


import com.alexanderberndt.appintegration.api.AppIntegrationException;
import com.alexanderberndt.appintegration.engine.processors.html.api.IntegrationResource;
import com.alexanderberndt.appintegration.engine.processors.html.api.IntegrationResourceType;
import com.alexanderberndt.appintegration.IntegrationResourceImpl;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URI;
import java.net.URL;

public class HttpDownloadUtil {

    // Logger
    private static final Logger LOGGER = LoggerFactory.getLogger(HttpDownloadUtil.class);

    // Timeout, if no connection is established after milliseconds
    private static final int CONNECTION_TIMEOUT = 10000;

    // Timeout, if connection hangs
    private static final int READ_TIMEOUT = 20000;

    private HttpDownloadUtil() {
    }

    public static IntegrationResource download(URI baseUri, String ref, IntegrationResourceType expectedType) {

        try {
            final URL url = baseUri.resolve(ref).toURL();
            final HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("GET");
            connection.setConnectTimeout(CONNECTION_TIMEOUT);
            connection.setReadTimeout(READ_TIMEOUT);
            connection.connect();

            if (connection.getResponseCode() == 200) {
                final byte[] data = getResponseAsCompleteByteArray(connection);

                // ToDo: verify mime type, get charset
                //final String mimeType = StringUtils.defaultIfBlank(connection.getHeaderField("Content-Type"));
                final IntegrationResource resource = IntegrationResourceImpl.create(expectedType, data, expectedType.getDefaultCharset(), expectedType.isHtmlDocument());
                LOGGER.info("fetched from content with {} characters and md5-hash={}", resource.getData().length, resource.getMd5Hex());
                return resource;

            } else {
                throw new AppIntegrationException("Failed to load resource " + ref
                        + " - status: " + connection.getResponseCode()
                        + " - message: " + connection.getResponseMessage());
            }
        } catch (Exception e) {
            LOGGER.error("Error establish connection due to {}", e.getMessage());
            throw new AppIntegrationException("Failed to load resource " + ref, e);
        }
    }


    private static byte[] getResponseAsCompleteByteArray(HttpURLConnection connection) throws IOException {

        final ByteArrayOutputStream store = new ByteArrayOutputStream();
        final InputStream inputStream = connection.getInputStream();

        int readNoOfBytes;
        final byte[] buffer = new byte[1024];
        while ((readNoOfBytes = inputStream.read(buffer)) >= 0) {
            store.write(buffer, 0, readNoOfBytes);
        }
        return store.toByteArray();
    }


}