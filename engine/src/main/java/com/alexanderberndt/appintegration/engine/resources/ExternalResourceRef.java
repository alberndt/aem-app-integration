package com.alexanderberndt.appintegration.engine.resources;

import com.alexanderberndt.appintegration.exceptions.AppIntegrationException;
import com.alexanderberndt.appintegration.utils.DataMap;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.net.URI;
import java.net.URISyntaxException;

public class ExternalResourceRef {

    @Nonnull
    private final URI uri;

    @Nonnull
    private ExternalResourceType expectedType;

    @Nonnull
    private final DataMap metadataMap = new DataMap();

    public ExternalResourceRef(@Nonnull URI uri, @Nullable ExternalResourceType expectedType) {
        this.uri = uri;
        this.expectedType = (expectedType != null) ? expectedType : ExternalResourceType.ANY;
    }

    public ExternalResourceRef(@Nonnull URI uri) {
        this(uri, null);
    }

    public static ExternalResourceRef create(@Nonnull String url) {
        return create(url, ExternalResourceType.ANY);
    }

    public static ExternalResourceRef create(@Nonnull String url, @Nonnull ExternalResourceType expectedType) {
        try {
            final URI uri = new URI(url).normalize();
            return new ExternalResourceRef(uri, expectedType);
        } catch (URISyntaxException e) {
            throw new AppIntegrationException("Cannot create external-resource-reference!", e);
        }
    }

    @Nonnull
    public URI getUri() {
        return uri;
    }

    @Nonnull
    public ExternalResourceType getExpectedType() {
        return expectedType;
    }

    public void setExpectedType(@Nullable ExternalResourceType expectedType) {
        this.expectedType = (expectedType != null) ? expectedType : ExternalResourceType.ANY;
    }

    @Nonnull
    public DataMap getMetadataMap() {
        return metadataMap;
    }

    public void setMetadata(@Nonnull String name, @Nullable Object value) {
        metadataMap.setData(name, value);
    }

    public <T> T getMetadata(@Nonnull String name, @Nonnull Class<T> tClass) {
        return metadataMap.getData(name, tClass);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ExternalResourceRef that = (ExternalResourceRef) o;
        return new EqualsBuilder()
                .append(uri, that.uri)
                .isEquals();
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder(17, 37)
                .append(uri)
                .toHashCode();
    }

    @Override
    public String toString() {
        return "ExternalResourceRef{" +
                "url='" + uri + '\'' +
                ", expectedType=" + expectedType +
                '}';
    }

}
