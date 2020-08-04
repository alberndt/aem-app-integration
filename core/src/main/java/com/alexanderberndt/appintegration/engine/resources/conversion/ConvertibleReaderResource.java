package com.alexanderberndt.appintegration.engine.resources.conversion;

import org.apache.commons.io.IOUtils;
import org.apache.commons.io.input.ReaderInputStream;

import java.io.IOException;
import java.io.InputStream;
import java.io.Reader;
import java.io.StringWriter;
import java.nio.charset.Charset;

public class ConvertibleReaderResource extends AbstractConvertibleResource<Reader> {

    protected ConvertibleReaderResource(Reader value, Charset charset) {
        super(value, charset);
    }

    @Override
    public ConvertibleResource<InputStream> convertToInputStreamResource() {
        return new ConvertibleInputStreamResource(new ReaderInputStream(this.get(), this.getCharset()), this.getCharset());
    }

    @Override
    public ConvertibleResource<Reader> convertToReaderResource() {
        return this;
    }

    @Override
    public ConvertibleResource<byte[]> convertToByteArrayResource() throws IOException {
        return convertToInputStreamResource().convertToByteArrayResource();
    }

    @Override
    public ConvertibleResource<String> convertToStringResource() throws IOException {
        StringWriter temp = new StringWriter();
        IOUtils.copy(this.get(), temp);
        return new ConvertibleStringResource(temp.toString(), this.getCharset());
    }
}
