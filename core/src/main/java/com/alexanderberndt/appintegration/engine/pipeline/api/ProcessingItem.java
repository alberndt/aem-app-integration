package com.alexanderberndt.appintegration.engine.pipeline.api;

import org.jsoup.nodes.Document;

import java.io.InputStream;
import java.io.Reader;
import java.nio.charset.Charset;

public class ProcessingItem {

    private enum InputType { NONE, INPUT_STREAM, READER, BYTE_ARRAY, STRING, DOM_DOCUMENT}

    private InputType inputType = InputType.NONE;

    private InputStream contentAsInputStream;

    private Reader contentAsReader;

    private byte[] contentAsByteArray;

    private String contentAsString;

    private Document contentAsDocument;


    public InputStream getContentAsInputStream() {
        return contentAsInputStream;
    }

    public Reader getContentAsReader() {
        return contentAsReader;
    }

    public byte[] getContentAsByteArray() {
        return contentAsByteArray;
    }

    public String getContentAsString() {
        return contentAsString;
    }

    public Document getContentAsDocument() {
        return contentAsDocument;
    }

    private void clearContent() {
        this.contentAsInputStream = null;
        this.contentAsReader = null;
        this.contentAsByteArray = null;
        this.contentAsString = null;
        this.contentAsDocument = null;
        this.inputType = InputType.NONE;
    }

    public void setContent(InputStream content) {
        clearContent();
        this.contentAsInputStream = content;
        this.inputType = InputType.INPUT_STREAM;
    }


    public void setContent(Reader content) {
        clearContent();
        this.contentAsReader = content;
        this.inputType = InputType.READER;
    }


    public void setContent(byte[] content) {
        clearContent();
        this.contentAsByteArray = content;
        this.inputType = InputType.BYTE_ARRAY;
    }


    public void setContentAsString(String content) {
        clearContent();
        this.contentAsString = content;
        this.inputType = InputType.STRING;
    }


    public void setContentAsDocument(Document document) {
        clearContent();
        this.contentAsDocument = document;
        this.inputType = InputType.DOM_DOCUMENT;
    }

    public boolean isText() {
        return false;
    }

    public boolean isHtmlDocument() {
        return false;
    }

    public IntegrationResourceType getType() {
        return null;
    }

    public Charset getCharset() {
        return Charset.defaultCharset();
    }


    public String getMd5Hex() {
        return null;
    }
}
