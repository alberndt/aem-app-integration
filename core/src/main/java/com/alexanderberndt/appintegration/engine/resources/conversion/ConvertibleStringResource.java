package com.alexanderberndt.appintegration.engine.resources.conversion;

import java.io.InputStream;
import java.io.Reader;
import java.io.StringReader;
import java.nio.charset.Charset;

public class ConvertibleStringResource extends AbstractConvertibleResource<String> {

    public ConvertibleStringResource(String value, Charset charset) {
        super(value, charset);
    }

    @Override
    public ConvertibleResource<InputStream> convertToInputStreamResource() {
        return convertToReaderResource().convertToInputStreamResource();
    }

    @Override
    public ConvertibleResource<Reader> convertToReaderResource() {
        return new ConvertibleReaderResource(new StringReader(this.get()), this.getCharset());
    }

    @Override
    public ConvertibleResource<byte[]> convertToByteArrayResource() {
        return new ConvertibleByteArrayResource(this.get().getBytes(this.getCharset()), this.getCharset());
    }

    @Override
    public ConvertibleResource<String> convertToStringResource() {
        return this;
    }
}
