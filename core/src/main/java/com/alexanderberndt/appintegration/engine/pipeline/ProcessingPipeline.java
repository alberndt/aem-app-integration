package com.alexanderberndt.appintegration.engine.pipeline;

import com.alexanderberndt.appintegration.engine.converter.StreamToReaderConverter;
import com.alexanderberndt.appintegration.engine.pipeline.api.PipelineFilter;
import com.alexanderberndt.appintegration.engine.pipeline.api.ProcessingContext;

import java.io.InputStream;
import java.io.Reader;
import java.util.List;

public class ProcessingPipeline implements PipelineFilter<InputStream, Reader> {

    private final List<PipelineFilter<Object, Object>> filterList;

    public ProcessingPipeline(List<PipelineFilter<Object, Object>> filterList) {
        this.filterList = filterList;
    }

    @Override
    public String getName() {
        return "pipeline";
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
        if ((filterList == null) || (filterList.isEmpty())) {
            return null;// new InputStreamReader(input);
        } else {
            ProcessingPipelineBuilder<InputStream> builder = new ProcessingPipelineBuilder<>(input);

            for (PipelineFilter<Object, Object> curFilter : filterList) {
                if (/*currentPipelineFilter.isApplicable(context)*/ true) {

                    // check for conversion
                    if (!curFilter.getInputType().isAssignableFrom(builder.getCurrentPipelineOutputType())) {
                        // ToDo Conversion
                        PipelineFilter<Object, Object> converter = getConverter((Class<Object>) builder.getCurrentPipelineOutputType(), curFilter.getInputType());
                        builder.appendFilter(null, converter);
                    }

                    builder.appendFilter(context, curFilter);
                }
            }
            return (Reader) builder.getCurrentPipelineOutput();
        }
    }

    @SuppressWarnings("unchecked")
    private <I, O> PipelineFilter<I, O> getConverter(Class<I> inputType, Class<O> outputType) {
        if (InputStream.class.isAssignableFrom(inputType)) {
            if (outputType.isAssignableFrom(Reader.class)) {
                return (PipelineFilter<I, O>) new StreamToReaderConverter();
            } else {
                return null;
            }
        }

        return null;
    }



}
