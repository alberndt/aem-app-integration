package com.alexanderberndt.appintegration.engine.pipeline.api;

import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;

public enum IntegrationResourceType {

    HTML(StandardCharsets.UTF_8, true),
    HTML_SNIPPET(StandardCharsets.UTF_8, true),
    JAVASCRIPT(StandardCharsets.UTF_8, false),
    CSS(StandardCharsets.UTF_8, false),
    CACHE_MANIFEST(StandardCharsets.UTF_8, false),
    PLAIN_TEXT(StandardCharsets.UTF_8, false),
    UNKNOWN(null, false);

    private final Charset defaultCharset;

    private final boolean isHtmlDocument;

    IntegrationResourceType(Charset defaultCharset, boolean isHtmlDocument) {
        this.defaultCharset = defaultCharset;
        this.isHtmlDocument = isHtmlDocument;
    }

    public Charset getDefaultCharset() {
        return defaultCharset;
    }

    public boolean isHtmlDocument() {
        return isHtmlDocument;
    }

    public static IntegrationResourceType parse(String str) {
        return IntegrationResourceType.valueOf(str.toUpperCase().replaceAll("-", "_"));
    }
}
