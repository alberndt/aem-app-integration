package com.alexanderberndt.appintegration.api;

import org.jsoup.nodes.Document;

import java.nio.charset.Charset;

public interface IntegrationResource {

    boolean isText();

    boolean isHtmlDocument();

    IntegrationResourceType getType();

    byte[] getData();

    Charset getCharset();

    String getDataAsString();

    Document getDataAsHtmlDocument();

    String getMd5Hex();
}
