package com.alexanderberndt.appintegration.engine.resources;

import com.alexanderberndt.appintegration.engine.ResourceLoader;
import com.alexanderberndt.appintegration.engine.resources.conversion.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.Nonnull;
import java.io.IOException;
import java.io.InputStream;
import java.io.Reader;
import java.lang.invoke.MethodHandles;
import java.nio.charset.Charset;
import java.util.*;

public class ExternalResource {

    private static final Logger LOG = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());

    private final String url;

    private final ResourceLoader loader;

    private final Map<String, String> metadataMap = new HashMap<>();

    private ExternalResourceType type;

    @Nonnull
    private ConvertibleValue<?> content;

    private final List<ExternalResourceRef> referencedResources = new ArrayList<>();

    public ExternalResource(@Nonnull ResourceLoader loader, @Nonnull ExternalResourceRef resourceRef) {
        this.loader = loader;
        this.url = resourceRef.getUrl();
        this.type = resourceRef.getExpectedType();
        this.content = new ConvertibleNullValue(Charset.defaultCharset());
    }

    public void setMetadata(String name, String value) {
        LOG.debug("setMetadata({}, {})", name, value);
        metadataMap.put(name, value);
    }

    public String getMetadata(String name) {
        return metadataMap.get(name);
    }

    public InputStream getContentAsInputStream() {
        return content.convertToInputStreamValue().get();
    }

    public Reader getContentAsReader() {
        return content.convertToReaderValue().get();
    }

    public byte[] getContentAsByteArray() throws IOException {
        return content.convertToByteArrayValue().get();
    }

    public String getContentAsString() throws IOException {
        return content.convertToStringValue().get();
    }

    public <C> C getContentAsObject(@Nonnull Class<C> expectedType) {
        return content.get(expectedType);
    }

    public void setContent(InputStream inputStream) {
        LOG.debug("setContent(InputStream = {})", inputStream);
        this.content = new ConvertibleInputStreamValue(inputStream, this.getCharset());
    }

    public void setContent(Reader reader) {
        LOG.debug("setContent(Reader = {})", reader);
        this.content = new ConvertibleReaderValue(reader, this.getCharset());
    }

    public void setContent(byte[] bytes) {
        LOG.debug("setContent(byte[] = {})", System.identityHashCode(bytes));
        this.content = new ConvertibleByteArrayValue(bytes, this.getCharset());
    }

    public void setContent(String content) {
        LOG.debug("setContent(String = {})", System.identityHashCode(content));
        this.content = new ConvertibleStringValue(content, this.getCharset());
    }


    public Charset getCharset() {
        Charset charset = content.getCharset();
        // ToDo: Rethink charset handling
        if (charset != null) return charset;
        return (type != null) ? type.getDefaultCharset() : Charset.defaultCharset();
    }

    public void setCharset(Charset charset) {
        LOG.debug("setCharset({})", charset);
        this.content = this.content.changeCharset(charset);
    }


    public void addReference(String relativeUrl) {
        LOG.debug("addReference({})", relativeUrl);
        referencedResources.add(loader.resolveRelativeUrl(this, relativeUrl));
    }

    public void addReference(String relativeUrl, ExternalResourceType expectedType) {
        LOG.debug("addReference({},{})", relativeUrl, expectedType);
        referencedResources.add(loader.resolveRelativeUrl(this, relativeUrl, expectedType));
    }

    public List<ExternalResourceRef> getReferencedResources() {
        return Collections.unmodifiableList(referencedResources);
    }

    public String getUrl() {
        return url;
    }

    public ExternalResourceType getType() {
        return type;
    }

    public void setType(@Nonnull ExternalResourceType type) {
        LOG.debug("setType({})", type);
        this.type = type;
    }

}
