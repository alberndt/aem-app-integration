package com.alexanderberndt.appintegration.engine.loader.impl;

import com.alexanderberndt.appintegration.engine.loader.ResourceLoader;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.util.ArrayList;
import java.util.List;

public abstract class AbstractResourceLoader implements ResourceLoader {

    @Override
    @SuppressWarnings("unchecked")
    public final <T> T load(String url, Class<T> tClass) throws IOException {

        final AttributedInputStream inputStream = loadInternal(url);

        if (inputStream.getInputStream() == null) {
            return null;
        } else if (tClass.isAssignableFrom(InputStream.class)) {
            return (T) inputStream.getInputStream();
        } else if (tClass.isAssignableFrom(Reader.class)) {
            if (inputStream.getCharset() == null) {
                return (T) new InputStreamReader(inputStream.inputStream);
            } else {
                return (T) new InputStreamReader(inputStream.inputStream, inputStream.getCharset());
            }
        } else if (tClass.isAssignableFrom(byte[].class)) {
            return (T) readFully(inputStream.getInputStream());
        } else if (tClass.isAssignableFrom(String.class)) {
            final byte[] byteArray = readFully(inputStream.getInputStream());
            if (inputStream.getCharset() == null) {
                return (T) new String(byteArray);
            } else {
                return (T) new String(byteArray, inputStream.getCharset());
            }
        } else {
            throw new UnsupportedOperationException("Cannot load resource as " + tClass);
        }
    }


    protected abstract AttributedInputStream loadInternal(String url);

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

    protected static class AttributedInputStream {

        private final InputStream inputStream;

        private String charset;

        public AttributedInputStream(InputStream inputStream) {
            this.inputStream = inputStream;
        }

        public InputStream getInputStream() {
            return inputStream;
        }

        public String getCharset() {
            return charset;
        }

        public void setCharset(String charset) {
            this.charset = charset;
        }
    }
}
