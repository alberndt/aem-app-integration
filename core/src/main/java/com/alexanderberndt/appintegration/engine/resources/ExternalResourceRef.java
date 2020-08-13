package com.alexanderberndt.appintegration.engine.resources;

import com.alexanderberndt.appintegration.pipeline.configuration.PipelineConfiguration;

import javax.annotation.Nonnull;

public class ExternalResourceRef {

    @Nonnull
    private final String url;

    private ExternalResourceType expectedType;

    private final PipelineConfiguration properties = new PipelineConfiguration();

    public ExternalResourceRef(@Nonnull String url) {
        this.url = url;
        this.expectedType = ExternalResourceType.ANY;
    }

    public ExternalResourceRef(@Nonnull String url, @Nonnull ExternalResourceType expectedType) {
        this.url = url;
        this.expectedType = expectedType;
    }

    public String getUrl() {
        return url;
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
        return (url != null) && url.equals(that.url);
    }

    @Override
    public int hashCode() {
        return url != null ? url.hashCode() : 0;
    }

    @Override
    public String toString() {
        return "ExternalResourceRef{" +
                "url='" + url + '\'' +
                ", expectedType=" + expectedType +
                '}';
    }
}
