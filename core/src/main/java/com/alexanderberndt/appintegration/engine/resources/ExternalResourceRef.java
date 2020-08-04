package com.alexanderberndt.appintegration.engine.resources;

import com.alexanderberndt.appintegration.pipeline.valuemap.RankedAndTypedValueMap;

public class ExternalResourceRef {

    private String relativeUrl;

    private ExternalResourceType expectedType;

    private final RankedAndTypedValueMap properties = new RankedAndTypedValueMap();

    public ExternalResourceRef() {
    }

    public ExternalResourceRef(String relativeUrl) {
        this.relativeUrl = relativeUrl;
    }

    public ExternalResourceRef(String relativeUrl, ExternalResourceType expectedType) {
        this.relativeUrl = relativeUrl;
        this.expectedType = expectedType;
    }

    public String getRelativeUrl() {
        return relativeUrl;
    }

    public void setRelativeUrl(String relativeUrl) {
        this.relativeUrl = relativeUrl;
    }

    public ExternalResourceType getExpectedType() {
        return expectedType;
    }

    public void setExpectedType(ExternalResourceType expectedType) {
        this.expectedType = expectedType;
    }

    public RankedAndTypedValueMap getProperties() {
        return properties;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ExternalResourceRef that = (ExternalResourceRef) o;
        return (relativeUrl != null) && relativeUrl.equals(that.relativeUrl);
    }

    @Override
    public int hashCode() {
        return relativeUrl != null ? relativeUrl.hashCode() : 0;
    }

    @Override
    public String toString() {
        return "ExternalResourceRef{" +
                "relativeUrl='" + relativeUrl + '\'' +
                ", expectedType=" + expectedType +
                '}';
    }
}
