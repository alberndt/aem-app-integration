package com.alexanderberndt.appintegration.engine.resources;

import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;

public enum ExternalResourceType {

    ANY(null, false, null),
    BINARY(null, false, ANY),
    TEXT(StandardCharsets.UTF_8, false, ANY),
    PLAIN_TEXT(StandardCharsets.UTF_8, false, TEXT),
    APPLICATION_PROPERTIES(StandardCharsets.UTF_8, false, TEXT),
    CACHE_MANIFEST(StandardCharsets.UTF_8, false, TEXT),
    JAVASCRIPT(StandardCharsets.UTF_8, false, TEXT),
    CSS(StandardCharsets.UTF_8, false, TEXT),
    HTML(StandardCharsets.UTF_8, true, TEXT),
    HTML_SNIPPET(StandardCharsets.UTF_8, true, TEXT);

    private final Charset defaultCharset;

    private final boolean isHtmlDocument;

    private final ExternalResourceType lessQualifiedType;

    ExternalResourceType(Charset defaultCharset, boolean isHtmlDocument, ExternalResourceType lessQualifiedType) {
        this.defaultCharset = defaultCharset;
        this.isHtmlDocument = isHtmlDocument;
        this.lessQualifiedType = lessQualifiedType;
    }

    public Charset getDefaultCharset() {
        return defaultCharset;
    }

    public boolean isHtmlDocument() {
        return isHtmlDocument;
    }

    public ExternalResourceType getLessQualifiedType() {
        return lessQualifiedType;
    }

    public boolean isSameOrSpecializationOf(final ExternalResourceType otherType) {
        ExternalResourceType myType = this;
        while (myType != null) {
            if (otherType == myType) return true;
            myType = myType.getLessQualifiedType();
        }
        return false;
    }

    public boolean isMoreQualifiedThan(ExternalResourceType otherType) {

        // search, if other-type is the same as we
        // or a less-qualified version of other-type is the same as we
        // then we are not more qualified
        while (otherType != null) {
            if (otherType == this) return false;
            otherType = otherType.getLessQualifiedType();
        }

        //  we are more qualified
        return true;
    }

    public static ExternalResourceType parse(String str) {
        try {
            return ExternalResourceType.valueOf(str.toUpperCase().replace('-', '_'));
        } catch (IllegalArgumentException e) {
            return null;
        }
    }
}
