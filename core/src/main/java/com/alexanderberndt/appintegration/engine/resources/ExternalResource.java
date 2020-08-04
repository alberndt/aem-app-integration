package com.alexanderberndt.appintegration.engine.resources;

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
import java.util.function.Function;

public class ExternalResource {

    private static final Logger LOG = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());

    private String url;

    private Function<String, String> relativeUrlResolver;

    private ExternalResourceType type;

    private ConvertibleValue<?> convertibleValue = new ConvertibleNullValue(Charset.defaultCharset());

    private final Map<String, String> metadataMap = new HashMap<>();

    private final List<ExternalResourceRef> referencedResources = new ArrayList<>();

    public ExternalResource(ExternalResourceRef resourceRef) {
        this.url = resourceRef.getRelativeUrl();
        this.type = resourceRef.getExpectedType();
    }

    public void setMetadata(String name, String value) {
        LOG.debug("setMetadata({}, {})", name, value);
        metadataMap.put(name, value);
    }

    public String getMetadata(String name) {
        return metadataMap.get(name);
    }

    public InputStream getContentAsInputStream() {
        return convertibleValue.convertToInputStreamValue().get();
    }

    public Reader getContentAsReader() {
        return convertibleValue.convertToReaderValue().get();
    }

    public byte[] getContentAsByteArray() throws IOException {
        return convertibleValue.convertToByteArrayValue().get();
    }

    public String getContentAsString() throws IOException {
        return convertibleValue.convertToStringValue().get();
    }

    public <C> C getContentAsObject(@Nonnull Class<C> expectedType) {
        return convertibleValue.get(expectedType);
    }

    public void setContent(InputStream inputStream) {
        LOG.debug("setContent(InputStream = {})", inputStream);
        this.convertibleValue = new ConvertibleInputStreamValue(inputStream, this.getCharset());
    }

    public void setContent(Reader reader) {
        LOG.debug("setContent(Reader = {})", reader);
        this.convertibleValue = new ConvertibleReaderValue(reader, this.getCharset());
    }

    public void setContent(byte[] bytes) {
        LOG.debug("setContent(byte[] = {})", System.identityHashCode(bytes));
        this.convertibleValue = new ConvertibleByteArrayValue(bytes, this.getCharset());
    }

    public void setContent(String content) {
        LOG.debug("setContent(String = {})", System.identityHashCode(content));
        this.convertibleValue = new ConvertibleStringValue(content, this.getCharset());
    }


    public Charset getCharset() {
        Charset charset = convertibleValue.getCharset();
        if (charset != null) return charset;
        return (type != null) ? type.getDefaultCharset() : Charset.defaultCharset();
    }

    public void setCharset(Charset charset) {
        LOG.debug("setCharset({})", charset);
        this.convertibleValue = this.convertibleValue.changeCharset(charset);
    }


    public void addReference(String relativeUrl, ExternalResourceType expectedType) {
        LOG.debug("addReference({},{})", relativeUrl, expectedType);
        referencedResources.add(new ExternalResourceRef(relativeUrl, expectedType));
    }

    public List<ExternalResourceRef> getReferencedResources() {
        return Collections.unmodifiableList(referencedResources);
    }

    public String getUrl() {
        return url;
    }

    public void setRelativeUrlResolver(Function<String, String> relativeUrlResolver) {
        LOG.debug("setRelativeUrlResolver({})", relativeUrlResolver);
        this.relativeUrlResolver = relativeUrlResolver;
    }

    public void setUrl(String url) {
        LOG.debug("setUrl({})", url);
        this.url = url;
    }

    public ExternalResourceType getType() {
        return type;
    }

    public void setType(@Nonnull ExternalResourceType type) {
        LOG.debug("setType({})", type);
        this.type = type;
    }

}
