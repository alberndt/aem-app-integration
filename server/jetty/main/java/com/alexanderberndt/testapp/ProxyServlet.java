package com.alexanderberndt.testapp;

import org.apache.commons.codec.net.URLCodec;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URI;
import java.net.URL;
import java.net.URLEncoder;

public class ProxyServlet extends HttpServlet {

    // Logger
    private static final Logger LOGGER = LoggerFactory.getLogger(ProxyServlet.class);

    // Timeout, if no connection is established after milliseconds
    private static final int CONNECTION_TIMEOUT = 10000;

    // Timeout, if connection hangs
    private static final int READ_TIMEOUT = 20000;

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {

        LOGGER.error("proxy {}", req.getRequestURI());

        URLCodec urlCodec = new URLCodec();

        try {
            final URL url;
            url = new URI("http://www.spiegel.de" + encodePathToUrl(StringUtils.defaultString(req.getPathInfo()))).toURL();
            LOGGER.info("request url {}", url);
            final HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("GET");
            connection.setConnectTimeout(CONNECTION_TIMEOUT);
            connection.setReadTimeout(READ_TIMEOUT);
            connection.connect();


            resp.setStatus(connection.getResponseCode());
            resp.setContentType(connection.getContentType());

            IOUtils.copy(connection.getInputStream(), resp.getOutputStream());

            LOGGER.info("done with url {}", url);


        } catch (Exception e) {
            LOGGER.error("Failed to proxy {}", req.getRequestURI());
            LOGGER.error(e.getMessage(), e);
        }
    }

    private static String encodePathToUrl(String path) throws UnsupportedEncodingException {
        final StringBuilder sb = new StringBuilder();
        for (String part : StringUtils.split(path, '/')) {
            sb.append("/");
            sb.append(URLEncoder.encode(part, "utf-8").replaceAll("\\+", "%20"));
        }
        return sb.toString();
    }

}
