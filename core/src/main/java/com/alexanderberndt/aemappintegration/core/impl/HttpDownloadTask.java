package com.alexanderberndt.aemappintegration.core.impl;


import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URI;
import java.net.URL;

public class HttpDownloadTask {

    // Logger
    private static final Logger LOGGER = LoggerFactory.getLogger(HttpDownloadTask.class);

    // Timeout, if no connection is established after milliseconds
    private static final int CONNECTION_TIMEOUT = 10000;

    // Timeout, if connection hangs
    private static final int READ_TIMEOUT = 20000;

    public void execute() throws Exception {
  //      try {
            final URL url = new URI("http://localhost:8080").toURL();
            final HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("GET");
            connection.setConnectTimeout(CONNECTION_TIMEOUT);
            connection.setReadTimeout(READ_TIMEOUT);
            connection.connect();

            if (connection.getResponseCode() == 200) {
                final byte[] content = getResponseAsCompleteByteArray(connection);
                final String mimeType = StringUtils.defaultIfBlank(connection.getHeaderField("Content-Type"), "text/html");

                // write raw content to repository

                String md5Hex = DigestUtils.md5Hex(content);
                LOGGER.info("fetched from content with {} characters and md5-hash={}",  content.length, md5Hex);

            } else {
                throw new RuntimeException("kein 200");
//                job.addError("Failed to retrieve content with return code %d %s",
//                        connection.getResponseCode(), connection.getResponseMessage());
            }
//        } catch (Exception e) {
//            LOGGER.error("Error establish connection", e);
//            job.addError("Failed to retrieve content from " + job.getUri().toString());
//        }
    }


    private byte[] getResponseAsCompleteByteArray(HttpURLConnection connection) throws IOException {

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