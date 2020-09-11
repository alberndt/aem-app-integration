package com.alexanderberndt.appintegration.engine.resources;

import com.alexanderberndt.appintegration.engine.resources.conversion.ConvertibleValue;
import com.alexanderberndt.appintegration.engine.resources.conversion.TextParserSupplier;
import com.alexanderberndt.appintegration.utils.DataMap;
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
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

public class ExternalResource {

    private static final Logger LOG = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());

    public enum LoadStatus {OK, CACHED}

    @Nonnull
    private final URI uri;

    @Nonnull
    private final DataMap metadataMap = new DataMap();

    @Nonnull
    private ExternalResourceType type;

    @Nonnull
    private ConvertibleValue<?> content;

    @Nonnull
    private final List<ExternalResourceRef> referencedResources = new ArrayList<>();

    private LoadStatus loadStatus;

    private Map<String, Object> loadStatusDetails;

    public ExternalResource(
            @Nonnull URI uri,
            @Nullable ExternalResourceType type,
            @Nullable InputStream content,
            @Nullable Map<String, Object> metadataMap,
            @Nullable TextParserSupplier textParserSupplier) {
        this.type = (type != null) ? type : ExternalResourceType.ANY;
        this.uri = uri;
        this.content = new ConvertibleValue<>(content, this.type.getDefaultCharset(), textParserSupplier);
        if (metadataMap != null) this.metadataMap.putAll(metadataMap);
    }

    public ExternalResource(
            @Nonnull ExternalResourceRef resourceRef,
            @Nonnull TextParserSupplier textParserSupplier) {
        this(resourceRef.getUri(), resourceRef.getExpectedType(), null, null, textParserSupplier);
    }

    public ExternalResource(
            @Nullable InputStream content,
            @Nonnull ExternalResourceRef resourceRef,
            @Nullable TextParserSupplier textParserSupplier) {
        this(resourceRef.getUri(), resourceRef.getExpectedType(), content, null, textParserSupplier);
    }

    public void setMetadata(@Nonnull String name, @Nullable Object value) {
        LOG.debug("setMetadata({}, {})", name, value);
        metadataMap.setData(name, value);
    }

    public <T> T getMetadata(@Nonnull String name, @Nonnull Class<T> tClass) {
        return metadataMap.getData(name, tClass);
    }

    @Nonnull
    public DataMap getMetadataMap() {
        return metadataMap;
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
        this.addReference(relativeUrl, ExternalResourceType.ANY);
    }

    public void addReference(String relativeUrl, ExternalResourceType expectedType) throws URISyntaxException {
        LOG.debug("addReference({},{})", relativeUrl, expectedType);
        // ToDo: Use ExternalResourceSet to identify duplicate resource references
        final URI referenceUri = this.getUri().resolve(relativeUrl);
        referencedResources.add(new ExternalResourceRef(referenceUri, expectedType));
    }

    public List<ExternalResourceRef> getReferencedResources() {
        return Collections.unmodifiableList(referencedResources);
    }

    @Nonnull
    public URI getUri() {
        return uri;
    }

    @Nonnull
    public ExternalResourceType getType() {
        return type;
    }

    public void setType(@Nonnull ExternalResourceType type) {
        LOG.debug("setType({})", type);
        // don't overwrite a more qualified type
        if (!this.type.isMoreQualifiedThan(type)) {
            this.type = type;
        }
    }

    public void setLoadStatus(LoadStatus loadStatus, Map<String, Object> loadStatusDetails) {
        this.loadStatus = loadStatus;
        this.loadStatusDetails = loadStatusDetails;
    }

    public LoadStatus getLoadStatus() {
        return loadStatus;
    }

    public Map<String, Object> getLoadStatusDetails() {
        return loadStatusDetails;
    }

}
