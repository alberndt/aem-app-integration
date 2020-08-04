package com.alexanderberndt.appintegration.engine.resources.conversion;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.io.Reader;
import java.nio.charset.Charset;

public class ConvertibleByteArrayResource extends AbstractConvertibleResource<byte[]> {

    public ConvertibleByteArrayResource(byte[] value, Charset charset) {
        super(value, charset);
    }

    @Override
    public ConvertibleResource<InputStream> convertToInputStreamResource() {
        return new ConvertibleInputStreamResource(new ByteArrayInputStream(this.get()), this.getCharset());
    }

    @Override
    public ConvertibleResource<Reader> convertToReaderResource() {
        return convertToInputStreamResource().convertToReaderResource();
    }

    @Override
    public ConvertibleResource<byte[]> convertToByteArrayResource() {
        return this;
    }

    @Override
    public ConvertibleResource<String> convertToStringResource() {
        return new ConvertibleStringResource(new String(this.get(), this.getCharset()), this.getCharset());
    }
}
