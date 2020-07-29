package com.alexanderberndt.appintegration;

import com.alexanderberndt.appintegration.pipeline.old.ProcessingItem;
import com.alexanderberndt.appintegration.pipeline.old.IntegrationResourceType;
import org.apache.commons.codec.digest.DigestUtils;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

import java.nio.charset.Charset;

public class ProcessingItemImpl  {

    private final IntegrationResourceType type;

    private final byte[] data;

    private final Charset charset;

    private final String dataAsString;

    private final Document dataAsHtmlDocument;

    private final String md5Hex;


    private ProcessingItemImpl(IntegrationResourceType type, byte[] data, Charset charset, String dataAsString, Document dataAsHtmlDocument) {
        this.type = type;
        this.data = data;
        this.charset = charset;
        this.dataAsString = dataAsString;
        this.dataAsHtmlDocument = dataAsHtmlDocument;
        this.md5Hex = DigestUtils.md5Hex(data);
    }

    public static ProcessingItem create(IntegrationResourceType type, byte[] data, Charset charset, boolean isHtmlDocument) {

        // decode text resources
        final String dataAsString;
        if (charset != null) {
            dataAsString = new String(data, charset);
        } else {
            dataAsString = null;
        }

        // parse html
        final Document doc;
        if (isHtmlDocument) {
            doc = Jsoup.parse(dataAsString);
        } else {
            doc = null;
        }

        return null;
//        return new ProcessingItemImpl(type, data, charset, dataAsString, doc);
    }

//    public static ProcessingItem create(IntegrationResourceType type, String data) {
//        return new ProcessingItemImpl(type, data.getBytes(StandardCharsets.UTF_8), StandardCharsets.UTF_8, data, null);
//    }
//
//    @Override
//    public boolean isText() {
//        return dataAsString != null;
//    }
//
//    @Override
//    public boolean isHtmlDocument() {
//        return dataAsHtmlDocument != null;
//    }
//
//    @Override
//    public IntegrationResourceType getType() {
//        return type;
//    }
//
//    @Override
//    public byte[] getData() {
//        return data;
//    }
//
//    @Override
//    public Charset getCharset() {
//        return charset;
//    }
//
//    @Override
//    public String getDataAsString() {
//        return dataAsString;
//    }
//
//    @Override
//    public Document getDataAsHtmlDocument() {
//        return dataAsHtmlDocument;
//    }
//
//    @Override
//    public String getMd5Hex() {
//        return md5Hex;
//    }
}
