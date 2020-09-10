package com.alexanderberndt.appintegration.engine.resources;

import com.alexanderberndt.appintegration.engine.ResourceLoader;
import com.alexanderberndt.appintegration.engine.resources.conversion.ConvertibleValue;
import com.alexanderberndt.appintegration.engine.resources.conversion.TextParserSupplier;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.io.IOException;
import java.io.InputStream;
import java.io.Reader;
import java.lang.invoke.MethodHandles;
import java.net.URI;
import java.net.URISyntaxException;
import java.nio.charset.Charset;
import java.util.*;

public class ExternalResource {

    private static final Logger LOG = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());

    @Nonnull
    private final URI uri;

    @Nullable
    private final ResourceLoader loader;

    @Nonnull
    private final Map<String, Object> metadataMap = new HashMap<>();

    @Nonnull
    private ExternalResourceType type;

    @Nonnull
    private ConvertibleValue<?> content;

    @Nonnull
    private final List<ExternalResourceRef> referencedResources = new ArrayList<>();

    public ExternalResource(
            @Nonnull URI uri,
            @Nullable ExternalResourceType type,
            @Nullable InputStream content,
            @Nullable Map<String, Object> metadataMap,
            @Nullable ResourceLoader loader,
            @Nullable TextParserSupplier textParserSupplier) {
        this.type = (type != null) ? type : ExternalResourceType.ANY;
        this.uri = uri;
        this.loader = loader;
        this.content = new ConvertibleValue<>(content, this.type.getDefaultCharset(), textParserSupplier);
        if (metadataMap != null) this.metadataMap.putAll(metadataMap);
    }


    public ExternalResource(
            @Nullable ResourceLoader loader,
            @Nonnull ExternalResourceRef resourceRef,
            @Nonnull TextParserSupplier textParserSupplier) {
        this(resourceRef.getUri(), resourceRef.getExpectedType(), null, null, loader, textParserSupplier);
    }

    public ExternalResource(
            @Nullable InputStream content,
            @Nullable ResourceLoader loader,
            @Nonnull ExternalResourceRef resourceRef,
            @Nullable TextParserSupplier textParserSupplier) {
        this(resourceRef.getUri(), resourceRef.getExpectedType(), content, null, loader, textParserSupplier);
    }

    public void setMetadata(@Nonnull String name, @Nullable Object value) {
        LOG.debug("setMetadata({}, {})", name, value);
        if (value != null) {
            metadataMap.put(name, value);
        } else {
            metadataMap.remove(name);
        }
    }

    @SuppressWarnings("unchecked")
    public <T> T getMetadata(@Nonnull String name, @Nonnull Class<T> tClass) {
        final Object value = metadataMap.get(name);
        if (tClass.isInstance(value)) {
            return (T) value;
        } else {
            return null;
        }
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


    public void addReference(String relativeUrl) throws URISyntaxException {
        LOG.debug("addReference({})", relativeUrl);
        referencedResources.add(loader.resolveRelativeUrl(this, relativeUrl));
    }

    public void addReference(String relativeUrl, ExternalResourceType expectedType) throws URISyntaxException {
        LOG.debug("addReference({},{})", relativeUrl, expectedType);
        referencedResources.add(loader.resolveRelativeUrl(this, relativeUrl, expectedType));
    }

    public List<ExternalResourceRef> getReferencedResources() {
        return Collections.unmodifiableList(referencedResources);
    }

    public URI getUri() {
        return uri;
    }

    public ExternalResourceType getType() {
        return type;
    }

    public void setType(@Nonnull ExternalResourceType type) {
        LOG.debug("setType({})", type);
        this.type = type;
    }

}
