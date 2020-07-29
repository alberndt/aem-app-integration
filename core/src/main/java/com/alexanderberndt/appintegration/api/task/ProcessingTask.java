package com.alexanderberndt.appintegration.api.task;

import com.alexanderberndt.appintegration.engine.resources.ExternalResource;
import com.alexanderberndt.appintegration.pipeline.ProcessingPipeline;
import com.alexanderberndt.appintegration.pipeline.TaskContext;

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
 */
public interface ProcessingTask extends GenericTask {

    void process(TaskContext context, ExternalResource resource);



    // ToDo: Input-Combinations or Alternatives



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



    // ToDo: getAcceptableCachingStrategy() -> FORCE_RELOAD, USE_CACHE

}
