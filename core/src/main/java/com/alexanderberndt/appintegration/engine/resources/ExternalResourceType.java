package com.alexanderberndt.appintegration.engine.resources;

import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;

public enum ExternalResourceType {

    APPLICATION_PROPERTIES(StandardCharsets.UTF_8, false, 2),
    HTML(StandardCharsets.UTF_8, true, 2),
    HTML_SNIPPET(StandardCharsets.UTF_8, true, 3),
    JAVASCRIPT(StandardCharsets.UTF_8, false, 2),
    CSS(StandardCharsets.UTF_8, false, 2),
    CACHE_MANIFEST(StandardCharsets.UTF_8, false, 2),
    PLAIN_TEXT(StandardCharsets.UTF_8, false, 1),
    UNKNOWN(null, false, 0);

    private final Charset defaultCharset;

    private final boolean isHtmlDocument;

    private final int qualificationLevel;

    ExternalResourceType(Charset defaultCharset, boolean isHtmlDocument, int qualificationLevel) {
        this.defaultCharset = defaultCharset;
        this.isHtmlDocument = isHtmlDocument;
        this.qualificationLevel = qualificationLevel;
    }

    public Charset getDefaultCharset() {
        return defaultCharset;
    }

    public boolean isHtmlDocument() {
        return isHtmlDocument;
    }

    public static ExternalResourceType parse(String str) {
        return ExternalResourceType.valueOf(str.toUpperCase().replace('-', '_'));
    }

    public boolean isMoreQualified(ExternalResourceType otherType) {
        if (otherType == null) return true;
        return qualificationLevel > otherType.qualificationLevel;
    }
}
