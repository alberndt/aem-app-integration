package com.alexanderberndt.appintegration.engine.pipeline.api;

import com.alexanderberndt.appintegration.engine.pipeline.ProcessingPipeline;

/**
 * <p>Filter for a {@link ProcessingPipeline}, which is applied for all applicable resources of an
 * {@link com.alexanderberndt.appintegration.ExternalApplication}.</p>
 * <p>The following types are supported as input and output:</p>
 * <ul>
 *     <li>{@link java.io.InputStream}</li>
 *     <li>{@link java.io.Reader} (text file-types only)</li>
 *     <li>{@link org.jsoup.nodes.Document} (html-snippets only)</li>
 * </ul>
 *
 * @param <I> input type
 * @param <O> output type
 */
public interface PipelineFilter<I, O> {

    String getName();

    Class<I> getInputType();

    Class<O> getOutputType();

    O filter(ProcessingContext context, I input);

    //    /**
//     * Only validates a potential configuration. It shall NOT store the config, as it might me changed later.
//     * Returns null or an empty OperationResult.
//     *
//     * @param configuration configuration
//     */
//    IntegrationStepResult<?> validateConfiguration(Map<String, Object> configuration);
//
//
//    void setApplicableResourceTypes(List<IntegrationResourceType> applicableResourceTypes);
//
//    List<IntegrationResourceType> getApplicableResourceTypes();
//



}
