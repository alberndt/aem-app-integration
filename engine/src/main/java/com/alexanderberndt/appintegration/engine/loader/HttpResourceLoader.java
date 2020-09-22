package com.alexanderberndt.appintegration.engine.loader;

import com.alexanderberndt.appintegration.engine.ResourceLoader;
import com.alexanderberndt.appintegration.engine.ResourceLoaderException;
import com.alexanderberndt.appintegration.engine.ResourceLoaderException.FailedReason;
import com.alexanderberndt.appintegration.engine.resources.ExternalResource;
import com.alexanderberndt.appintegration.engine.resources.ExternalResource.LoadStatus;
import com.alexanderberndt.appintegration.engine.resources.ExternalResourceFactory;
import com.alexanderberndt.appintegration.engine.resources.ExternalResourceRef;
import com.alexanderberndt.appintegration.engine.resources.ExternalResourceType;
import com.alexanderberndt.appintegration.exceptions.AppIntegrationException;
import org.apache.commons.lang3.StringUtils;
import org.osgi.service.component.annotations.Component;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.io.Serializable;
import java.lang.invoke.MethodHandles;
import java.net.*;
import java.nio.charset.Charset;
import java.nio.charset.UnsupportedCharsetException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import static java.net.HttpURLConnection.*;

@Component
public class HttpResourceLoader implements ResourceLoader {

    public static final String HTTP_HEADER_PREFIX = "HttpHeader.";

    // Timeout, if no connection is established after milliseconds
    private static final int CONNECTION_TIMEOUT = 10000;

    // Timeout, if connection hangs
    private static final int READ_TIMEOUT = 20000;

    private static final Logger LOG = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());

    @Nonnull
    @Override
    public ExternalResource load(@Nonnull ExternalResourceRef resourceRef, @Nonnull ExternalResourceFactory factory) {

        try {
            final URI uri = resourceRef.getUri();
            final URL url = uri.toURL();

            final Proxy proxy = getSystemProxy(uri); // use system proxy
            final HttpURLConnection connection = (HttpURLConnection) ((proxy == null) ? url.openConnection() : url.openConnection(proxy));

            connection.setRequestMethod("GET");
            connection.setRequestProperty("User-Agent", "app-integration-client");
            connection.setConnectTimeout(CONNECTION_TIMEOUT);
            connection.setReadTimeout(READ_TIMEOUT);

            if (resourceRef.getCachedExternalRes() != null) {
                final String lastModified = resourceRef.getMetadata(HTTP_HEADER_PREFIX + "Last-Modified", String.class);
                if (StringUtils.isNotBlank(lastModified)) {
                    connection.setRequestProperty("If-Modified-Since", lastModified);
                }
                final String eTag = resourceRef.getMetadata(HTTP_HEADER_PREFIX + "ETag", String.class);
                if (StringUtils.isNotBlank(eTag)) {
                    connection.setRequestProperty("If-None-Match", eTag);
                }
            }

            LOG.info("Fetching content for {}", url);
            connection.connect();
            final Map<String, Serializable> loadStatusDetails = new HashMap<>();
            loadStatusDetails.put("status", connection.getResponseCode());
            loadStatusDetails.put("message", connection.getResponseMessage());

            switch (connection.getResponseCode()) {

                case HTTP_OK:
                    ExternalResource resource = factory.createExternalResource(resourceRef, connection.getInputStream());
                    // add https headers as metadata
                    setResourceMetadataByHeader(resource, connection, "Date");
                    setResourceMetadataByHeader(resource, connection, "Last-Modified");
                    setResourceMetadataByHeader(resource, connection, "ETag");
                    setResourceMetadataByHeader(resource, connection, "Content-Type");
                    setResourceMetadataByHeader(resource, connection, "Cache-Control");
                    setResourceMetadataByHeader(resource, connection, "Age");
                    setResourceMetadataByHeader(resource, connection, "Expires");
                    setResourceMetadataByHeader(resource, connection, "Set-Cookie");
                    setResourceMetadataByHeader(resource, connection, "Content-Encoding");
                    setResourceMetadataByHeader(resource, connection, "Content-Language");
                    setResourceMetadataByHeader(resource, connection, "Server");
                    setResourceMetadataByHeader(resource, connection, "Content-Length", Integer.class);

                    setResourceContentType(resource, connection);
                    setResourceContentEncoding(resource, connection);
                    resource.setLoadStatus(LoadStatus.OK, loadStatusDetails);

                    return resource;

                case HTTP_NOT_MODIFIED:
                    LOG.info("Not modified - take the cached version");
                    final ExternalResource cachedResource = Optional.of(resourceRef)
                            .map(ExternalResourceRef::getCachedExternalRes)
                            .orElse(null);

                    if (cachedResource != null) {
                        cachedResource.setLoadStatus(LoadStatus.CACHED, loadStatusDetails);
                        return cachedResource;
                    } else {
                        throw new ResourceLoaderException(FailedReason.ERROR, "Cached resource should be used, but failed to retrieve", loadStatusDetails);
                    }

                case HTTP_NOT_FOUND:
                    LOG.error("Not found resource {} with {}", url, loadStatusDetails);
                    throw new ResourceLoaderException(FailedReason.NOT_FOUND, connection.getResponseMessage(), loadStatusDetails);

                default:
                    LOG.error("Failed to load resource {} with {}", url, loadStatusDetails);
                    throw new ResourceLoaderException(FailedReason.ERROR, connection.getResponseMessage(), loadStatusDetails);
            }

        } catch (Exception e) {
            throw new AppIntegrationException("Failed to load resource " + resourceRef.getUri(), e);
        }

    }

    protected void setResourceContentType(ExternalResource resource, HttpURLConnection connection) {
        final String mimeType = connection.getHeaderField("Content-Type");
        if (StringUtils.isNotBlank(mimeType)) {
            switch (mimeType) {
                case "text/css":
                    resource.setType(ExternalResourceType.CSS);
                    break;
                case "text/javascript":
                    resource.setType(ExternalResourceType.JAVASCRIPT);
                    break;
                default:
                    // do nothing
                    break;
            }
        }
    }

    protected void setResourceContentEncoding(ExternalResource resource, HttpURLConnection connection) {
        final String encoding = connection.getHeaderField("Content-Encoding");
        if (StringUtils.isNotBlank(encoding)) {
            try {
                resource.setCharset(Charset.forName(encoding));
            } catch (UnsupportedCharsetException e) {
                // just ignore it otherwise
                LOG.error("Unexpected content-encoding {}", encoding, e);
            }
        }
    }

    protected void setResourceMetadataByHeader(@Nonnull ExternalResource resource, @Nonnull HttpURLConnection connection, @Nonnull String headerName) {
        setResourceMetadataByHeader(resource, connection, headerName, String.class);
    }

    protected void setResourceMetadataByHeader(@Nonnull ExternalResource resource, @Nonnull HttpURLConnection connection, @Nonnull String headerName, @Nonnull Class<?> typeHint) {
        if (typeHint == String.class) {
            final String value = connection.getHeaderField(headerName);
            if (StringUtils.isNotBlank(value)) resource.setMetadata(HTTP_HEADER_PREFIX + headerName, value);
        } else if (typeHint == Integer.class) {
            final int value = connection.getHeaderFieldInt(headerName, -1);
            if (value >= 0) resource.setMetadata(HTTP_HEADER_PREFIX + headerName, value);
        } else {
            LOG.warn("Unexpected type-hint {} - cannot read header value", typeHint);
        }
    }


    @Nullable
    protected Proxy getSystemProxy(@Nonnull URI uri) {
        final List<Proxy> proxyList = ProxySelector.getDefault().select(uri);
        return proxyList.stream()
                .filter(proxy -> proxy.type() == Proxy.Type.HTTP)
                .findFirst()
                .orElse(null);
    }

}
