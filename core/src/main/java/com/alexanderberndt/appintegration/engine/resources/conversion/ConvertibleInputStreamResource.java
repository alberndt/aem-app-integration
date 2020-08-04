package com.alexanderberndt.appintegration.engine.resources.conversion;

import org.apache.commons.io.IOUtils;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.nio.charset.Charset;

public class ConvertibleInputStreamResource extends AbstractConvertibleResource<InputStream> {

    public ConvertibleInputStreamResource(InputStream value, Charset charset) {
        super(value, charset);
    }

    @Override
    public ConvertibleResource<InputStream> convertToInputStreamResource() {
        return this;
    }

    @Override
    public ConvertibleResource<Reader> convertToReaderResource() {
        return new ConvertibleReaderResource(new InputStreamReader(this.get(), this.getCharset()), this.getCharset());
    }

    @Override
    public ConvertibleResource<byte[]> convertToByteArrayResource() throws IOException {
        return new ConvertibleByteArrayResource(IOUtils.toByteArray(this.get()), this.getCharset());
    }

    @Override
    public ConvertibleResource<String> convertToStringResource() throws IOException {
        return this.convertToReaderResource().convertToStringResource();
    }

}
