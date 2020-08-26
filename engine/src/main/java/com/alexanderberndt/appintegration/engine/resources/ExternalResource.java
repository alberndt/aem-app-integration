package com.alexanderberndt.appintegration.engine.resources;

import com.alexanderberndt.appintegration.engine.ResourceLoader;
import com.alexanderberndt.appintegration.engine.resources.conversion.ConversionSupplier;
import com.alexanderberndt.appintegration.engine.resources.conversion.ConvertibleValue;
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

    public ExternalResource(
            @Nonnull ResourceLoader loader,
            @Nonnull ExternalResourceRef resourceRef,
            @Nonnull ConversionSupplier conversionSupplier) {
        this.loader = loader;
        this.url = resourceRef.getUrl();
        this.type = resourceRef.getExpectedType();
        // ToDo: Add TextParsers
        this.content = new ConvertibleValue<>(null, Charset.defaultCharset(), conversionSupplier);
    }

    public void setMetadata(String name, String value) {
        LOG.debug("setMetadata({}, {})", name, value);
        metadataMap.put(name, value);
    }

    public String getMetadata(String name) {
        return metadataMap.get(name);
    }

    public InputStream getContentAsInputStream() throws IOException {
        return content.convertToInputStreamValue().get();
    }

    public Reader getContentAsReader() throws IOException {
        return content.convertToReaderValue().get();
    }

    public void setContent(InputStream inputStream) {
        LOG.debug("setContent(InputStream = {})", inputStream);
        this.content = this.content.recreateWithNewContent(inputStream);
    }

    public void setContent(Reader reader) {
        LOG.debug("setContent(Reader = {})", reader);
        this.content = this.content.recreateWithNewContent(reader);
    }

    public <C> C getContentAsParsedObject(@Nonnull Class<C> expectedType) throws IOException {
        return content.convertTo(expectedType).get();
    }

    public void setContentAsParsedObject(Object parsedContent) {
        LOG.debug("setContent(parsedContent = {})", parsedContent);
        this.content = this.content.recreateWithNewContent(parsedContent);
    }

    public Charset getCharset() {
        Charset charset = content.getCharset();
        // ToDo: Rethink charset handling
        if (charset != null) return charset;
        return (type != null) ? type.getDefaultCharset() : Charset.defaultCharset();
    }

    public void setCharset(Charset charset) {
        LOG.debug("setCharset({})", charset);
        this.content = this.content.recreateWithNewCharset(charset);
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
