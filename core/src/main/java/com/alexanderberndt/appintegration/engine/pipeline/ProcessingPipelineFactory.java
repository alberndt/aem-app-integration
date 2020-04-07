package com.alexanderberndt.appintegration.engine.pipeline;

import com.alexanderberndt.appintegration.engine.pipeline.api.PipelineDefinition;
import com.alexanderberndt.appintegration.engine.pipeline.api.PipelineFilter;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ProcessingPipelineFactory {

    private Map<String, PipelineFilter<?, ?>> filterMap = new HashMap<>();

    public ProcessingPipeline createPipeline(PipelineDefinition definition) {

        List<PipelineFilter<Object, Object>> filterList = new ArrayList<>();
        for (PipelineDefinition.Step step : definition.getSteps()) {
            PipelineFilter<?, ?> filter = filterMap.get(step.getFilter());
            if (filter == null) {
                throw new RuntimeException("Filter " + step.getFilter() + " is unavailable!");
            }
            filterList.add((PipelineFilter<Object, Object>) filter);
        }

        return new ProcessingPipeline(filterList);
    }

    public void register(PipelineFilter<?, ?> filter) {
        filterMap.put(filter.getName(), filter);
    }

    public void unregister(PipelineFilter<?, ?> filter) {
        filterMap.remove(filter.getName(), filter);
    }
}
