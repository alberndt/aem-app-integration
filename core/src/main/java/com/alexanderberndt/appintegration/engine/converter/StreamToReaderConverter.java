package com.alexanderberndt.appintegration.engine.converter;

import com.alexanderberndt.appintegration.engine.pipeline.api.PipelineFilter;
import com.alexanderberndt.appintegration.engine.pipeline.api.ProcessingContext;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;

public class StreamToReaderConverter implements PipelineFilter<InputStream, Reader> {

    @Override
    public String getName() {
        return "stream-to-reader-converter";
    }

    @Override
    public Class<InputStream> getInputType() {
        return InputStream.class;
    }

    @Override
    public Class<Reader> getOutputType() {
        return Reader.class;
    }

    @Override
    public Reader filter(ProcessingContext context, InputStream input) {
        return new InputStreamReader(input);
    }

}
