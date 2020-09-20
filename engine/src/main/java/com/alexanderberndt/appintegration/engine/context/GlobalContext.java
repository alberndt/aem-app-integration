package com.alexanderberndt.appintegration.engine.context;

import com.alexanderberndt.appintegration.engine.*;
import com.alexanderberndt.appintegration.engine.logging.IntegrationLogger;
import com.alexanderberndt.appintegration.engine.logging.LogAppender;
import com.alexanderberndt.appintegration.engine.logging.ResourceLogger;
import com.alexanderberndt.appintegration.engine.logging.TaskLogger;
import com.alexanderberndt.appintegration.engine.logging.appender.Slf4jLogAppender;
import com.alexanderberndt.appintegration.engine.resources.ExternalResourceFactory;
import com.alexanderberndt.appintegration.engine.resources.ExternalResourceRef;
import com.alexanderberndt.appintegration.engine.resources.ExternalResourceType;
import com.alexanderberndt.appintegration.exceptions.AppIntegrationException;
import com.alexanderberndt.appintegration.pipeline.ProcessingPipeline;
import com.alexanderberndt.appintegration.pipeline.configuration.PipelineConfiguration;
import com.alexanderberndt.appintegration.pipeline.configuration.Ranking;
import com.alexanderberndt.appintegration.utils.DataMap;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.function.Supplier;

public abstract class GlobalContext<I extends ApplicationInstance, C extends GlobalContext<I, C>> {

    @Nonnull
    private final String applicationId;

    @Nonnull
    private final AppIntegrationFactory<I, C> factory;

    @Nonnull
    private final IntegrationLogger logger;

    private final PipelineConfiguration processingParams = new PipelineConfiguration();

    private final LazyValue<Application> application = new LazyValue<>();

    private final LazyValue<ResourceLoader> resourceLoaderLazyValue = new LazyValue<>();

    private final LazyValue<URI> applicationInfoUriLazyValue = new LazyValue<>();

    private final LazyValue<ProcessingPipeline> pipelineLazyValue = new LazyValue<>();

    private final LazyValue<List<ContextProvider<I>>> contextProviderListLazyValue = new LazyValue<>();

    protected GlobalContext(@Nonnull String applicationId, @Nonnull AppIntegrationFactory<I, C> factory, @Nullable LogAppender logAppender) {
        this.applicationId = applicationId;
        this.factory = factory;
        this.logger = new IntegrationLogger((logAppender != null) ? logAppender : new Slf4jLogAppender());
    }

    @Nonnull
    public abstract TaskContext createTaskContext(
            @Nonnull TaskLogger taskLogger,
            @Nonnull Ranking rank,
            @Nonnull String taskId,
            @Nonnull ExternalResourceType resourceType,
            @Nullable DataMap processingData);


    @Nonnull
    public String getApplicationId() {
        return applicationId;
    }

    @Nonnull
    public Application getApplication() {
        return application.getLazy(
                () -> factory.getApplication(applicationId),
                "Application %s is undefined", applicationId);
    }

    @Nonnull
    public ResourceLoader getResourceLoader() {
        final String resourceLoaderName = getApplication().getResourceLoaderName();
        return resourceLoaderLazyValue.getLazy(
                () -> factory.getResourceLoader(resourceLoaderName),
                "ResourceLoader %s for application %s is not available!", resourceLoaderName, applicationId);
    }

    @Nonnull
    public ExternalResourceFactory getResourceFactory() {
        return factory.getExternalResourceFactory();
    }

    @Nonnull
    public URI getApplicationInfoUri() {

        return applicationInfoUriLazyValue.getLazy(
                () -> {
                    final ResourceLoader loader = this.getResourceLoader();
                    final String appInfoUrl = this.getApplication().getApplicationInfoUrl();
                    try {
                        return loader.resolveBaseUri(appInfoUrl);
                    } catch (URISyntaxException e) {
                        throw new AppIntegrationException("Cannot resolve application-information.json url " + appInfoUrl, e);
                    }
                },
                "Cannot resolve application-information.json url");
    }

    @SuppressWarnings("unchecked")
    private C me() {
        return (C) this;
    }

    public ProcessingPipeline getProcessingPipeline() {

        return pipelineLazyValue.getLazy(
                () -> {
                    final ProcessingPipeline pipeline = factory.createProcessingPipeline(me(), getApplication().getProcessingPipelineName());
                    pipeline.initContextWithTaskDefaults(this);
                    pipeline.initContextWithPipelineConfig(this);
                    injectGlobalProperties();
                    getProcessingParams().setReadOnly();
                    return pipeline;
                });
    }

    private void injectGlobalProperties() {
        final Map<String, Object> globalProperties = getApplication().getGlobalProperties();
        if (globalProperties != null) {
            ResourceLogger resourceLogger = getIntegrationLog().createResourceLogger(ExternalResourceRef.create("global", ExternalResourceType.ANY));
            // ToDo: TaskLogger shall be part of task-context
            final TaskLogger taskLogger = resourceLogger.createTaskLogger("Set Global Properties Task", "set-globals");
            final TaskContext taskContext = createTaskContext(taskLogger, Ranking.GLOBAL, "global", ExternalResourceType.ANY, null);
            for (final Map.Entry<String, Object> propEntry : globalProperties.entrySet()) {
                taskContext.setValue(propEntry.getKey(), propEntry.getValue());
            }
        }
    }


    @Nonnull
    public List<ContextProvider<I>> getContextProviderList() {
        return contextProviderListLazyValue.getLazy(
                () -> {
                    final List<String> notFoundContextProviders = new ArrayList<>();
                    final List<ContextProvider<I>> contextProviders = new ArrayList<>();

                    for (String providerName : getApplication().getContextProviderNames()) {
                        final ContextProvider<I> provider = factory.getContextProvider(providerName);
                        if (provider != null) {
                            contextProviders.add(provider);
                        } else {
                            notFoundContextProviders.add(providerName);
                        }
                    }
                    if (notFoundContextProviders.isEmpty()) {
                        return contextProviders;
                    } else {
                        throw new AppIntegrationException(String.format(
                                "The requested context providers %s for application %s are not available",
                                notFoundContextProviders, applicationId));
                    }
                });
    }


    @Nonnull
    public PipelineConfiguration getProcessingParams() {
        return processingParams;
    }

    @Nonnull
    public IntegrationLogger getIntegrationLog() {
        return logger;
    }


    private static class LazyValue<T> {

        private T value;

        public T getLazy(Supplier<T> supplier, String msg, Object... args) {
            if (value == null) {
                value = supplier.get();
                if (value == null) {
                    throw new AppIntegrationException(String.format(msg, args));
                }
            }
            return value;
        }

        public T getLazy(Supplier<T> supplier) {
            if (value == null) {
                value = Objects.requireNonNull(supplier.get());
            }
            return value;
        }
    }

}
