package com.alexanderberndt.appintegration.pipeline.task;

import com.alexanderberndt.appintegration.engine.context.TaskContext;
import com.alexanderberndt.appintegration.engine.resources.ExternalResource;
import com.alexanderberndt.appintegration.engine.resources.ExternalResourceType;
import com.alexanderberndt.appintegration.pipeline.ProcessingPipeline;

import javax.annotation.Nonnull;

/**
 * <p>Filter for a {@link ProcessingPipeline}, which is applied for all applicable resources of an
 * {@link com.alexanderberndt.appintegration.engine.Application}.</p>
 * <p>The following types are supported as input and output:</p>
 * <ul>
 *     <li>{@link java.io.InputStream}</li>
 *     <li>{@link java.io.Reader} (text file-types only)</li>
 *     <li>{@link org.jsoup.nodes.Document} (html-snippets only)</li>
 * </ul>
 */
public interface ProcessingTask {

    enum Status {
        NOTHING_DONE,
        SKIPPED,
        OK,
        WARNING,
        ERROR;

        public Status mergeStatus(Status newStatus) {
            if ((newStatus != null) && (this.ordinal() < newStatus.ordinal())) {
                return newStatus;
            } else {
                return this;
            }
        }
    }

    default ExternalResourceType getApplicableResourceType() {
        return ExternalResourceType.ANY;
    }

    void process(@Nonnull TaskContext taskContext, @Nonnull ExternalResource resource);

    /**
     * <p>Implementing classes should define a set of task-properties. This should be done by calling
     * {@link TaskContext#setValue(String, Object)} and {@link TaskContext#setType(String, Class)}. Although these
     * defaults can be overwritten, this ensures that they have a defined type and meaningful default values.</p>
     *
     * <p>With {@link TaskContext#setKeyComplete()} can be assured, that only known properties can be overwritten.
     * A warning is created, if a new property is created. This helps, that not accidentally wrong properties are specified.</p>
     *
     * @param taskContext TaskContext
     */
    default void declareTaskPropertiesAndDefaults(TaskContext taskContext) {
    }

    // ToDo: Input-Combinations or Alternatives

    // ToDo: getAcceptableCachingStrategy() -> FORCE_RELOAD, USE_CACHE

    // ToDo: getApplicableResourceTypes
}
