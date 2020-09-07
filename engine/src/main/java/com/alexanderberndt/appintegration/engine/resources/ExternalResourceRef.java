package com.alexanderberndt.appintegration.engine.resources;

import com.alexanderberndt.appintegration.exceptions.AppIntegrationException;
import com.alexanderberndt.appintegration.pipeline.configuration.PipelineConfiguration;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

import javax.annotation.Nonnull;
import java.net.URI;
import java.net.URISyntaxException;

public class ExternalResourceRef {

    @Nonnull
    private final URI uri;

    private ExternalResourceType expectedType;

    private final PipelineConfiguration properties = new PipelineConfiguration();

    public ExternalResourceRef(@Nonnull URI uri) {
        this.uri = uri;
        this.expectedType = ExternalResourceType.ANY;
    }

    public ExternalResourceRef(@Nonnull URI uri, @Nonnull ExternalResourceType expectedType) {
        this.uri = uri;
        this.expectedType = expectedType;
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

    public ExternalResourceType getExpectedType() {
        return expectedType;
    }

    public void setExpectedType(ExternalResourceType expectedType) {
        this.expectedType = expectedType;
    }

    public PipelineConfiguration getProperties() {
        return properties;
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
