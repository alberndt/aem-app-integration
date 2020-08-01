package com.alexanderberndt.appintegration.engine.resources;

import javax.annotation.Nonnull;
import java.io.*;
import java.nio.charset.Charset;
import java.util.*;
import java.util.function.Function;

public class ExternalResource {

    private String url;

    private Function<String, String> relativeUrlResolver;

    private ExternalResourceType type;

    private InputStream inputStream;

    private Charset charset;

    private final Map<String, String> propertiesMap = new HashMap<>();

    private final List<ExternalResourceRef> referencedResources = new ArrayList<>();

    public ExternalResource(ExternalResourceRef resourceRef) {
        this.url = resourceRef.getRelativeUrl();
        this.type = resourceRef.getExpectedType();
    }

    public void setProperty(String name, String value) {
        propertiesMap.put(name, value);
    }

    public String getProperty(String name) {
        return propertiesMap.get(name);
    }

    public InputStream getInputStream() {
        return inputStream;
    }

    public byte[] getBytes() throws IOException {
        return readFully(inputStream);
    }

    public Charset getCharset() {
        if (charset != null) return charset;
        return (type != null) ? type.getDefaultCharset() : null;
    }

    public Reader getReader() throws IOException {
        final Charset cs = this.getCharset();
        if (cs != null) {
            return new InputStreamReader(inputStream, cs);
        } else {
            throw new UnsupportedEncodingException("Unknown character encoding for external resource " + url);
        }
    }

    public String getString() throws IOException {
        final Charset cs = this.getCharset();
        if (cs != null) {
            final byte[] byteArray = readFully(inputStream);
            return new String(byteArray, cs);
        } else {
            throw new UnsupportedEncodingException("Unknown character encoding for external resource " + url);
        }
    }

    public void addReference(String relativeUrl, ExternalResourceType expectedType) {
        referencedResources.add(new ExternalResourceRef(relativeUrl, expectedType));
    }

    public List<ExternalResourceRef> getReferencedResources() {
        return Collections.unmodifiableList(referencedResources);
    }

    public static byte[] readFully(InputStream inputStream) throws IOException {
        final List<byte[]> bufferList = new ArrayList<>();
        while (true) {
            final byte[] buffer = new byte[2048];
            final int size = inputStream.read(buffer);
            if (size <= 0) {
                break;
            } else {
                if (size < buffer.length) {
                    final byte[] smallerBuffer = new byte[size];
                    System.arraycopy(buffer, 0, smallerBuffer, 0, size);
                    bufferList.add(smallerBuffer);
                } else {
                    bufferList.add(buffer);
                }
            }
        }

        final int totalSize = bufferList.stream().map(buffer -> buffer.length).reduce(0, Integer::sum);
        final byte[] byteArray = new byte[totalSize];
        int pos = 0;
        for (byte[] buffer : bufferList) {
            System.arraycopy(buffer, 0, byteArray, pos, buffer.length);
            pos += buffer.length;
        }
        return byteArray;
    }

    public String getUrl() {
        return url;
    }

    public void setRelativeUrlResolver(Function<String, String> relativeUrlResolver) {
        this.relativeUrlResolver = relativeUrlResolver;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public ExternalResourceType getType() {
        return type;
    }

    public void setType(@Nonnull ExternalResourceType type) {
        this.type = type;
    }

    public void setCharset(Charset charset) {
        this.charset = charset;
    }

    public void setInputStream(InputStream inputStream) {
        this.inputStream = inputStream;
    }

    public void setReader(Reader reader) {
        // ToDo: Implement
        throw new UnsupportedOperationException("Not implemented yet");
    }

}
