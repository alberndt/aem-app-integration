package com.alexanderberndt.appintegration.aem.engine;

import com.alexanderberndt.appintegration.aem.engine.model.SlingApplicationInstance;
import com.alexanderberndt.appintegration.engine.AppIntegrationFactory;
import com.alexanderberndt.appintegration.engine.Application;
import com.alexanderberndt.appintegration.engine.ContextProvider;
import com.alexanderberndt.appintegration.engine.ResourceLoader;
import com.alexanderberndt.appintegration.engine.resources.conversion.TextParser;
import com.alexanderberndt.appintegration.exceptions.AppIntegrationException;
import com.alexanderberndt.appintegration.pipeline.ProcessingPipeline;
import org.apache.commons.lang3.StringUtils;
import org.osgi.service.component.annotations.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.lang.invoke.MethodHandles;
import java.util.*;
import java.util.stream.Collectors;

import static org.osgi.service.component.ComponentConstants.COMPONENT_NAME;

@Component(service = AemAppIntegrationFactory.class)
public class AemAppIntegrationFactory implements AppIntegrationFactory<SlingApplicationInstance, AemGlobalContext> {

    private static final Logger LOG = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());

    private final Map<String, AemApplication> applicationMap = new HashMap<>();

    private final Map<String, ResourceLoader> resourceLoaderMap = new HashMap<>();

    private final Map<String, AemContextProvider> contextProviderMap = new HashMap<>();

    private final List<AemProcessingPipelineFactory> processingPipelineFactoryList = new ArrayList<>();

    private final List<TextParser> textParserList = new ArrayList<>();


    @Nonnull
    @Override
    public Map<String, Application> getAllApplications() {
        return Collections.unmodifiableMap(applicationMap);
    }

    @Nonnull
    @Override
    public Map<String, ResourceLoader> getAllResourceLoaders() {
        return Collections.unmodifiableMap(resourceLoaderMap);
    }

    @Nonnull
    @Override
    public Map<String, ContextProvider<SlingApplicationInstance>> getAllContextProvider() {
        return Collections.unmodifiableMap(contextProviderMap);
    }

    @Nonnull
    @Override
    public List<TextParser> getAllTextParsers() {
        return textParserList;
    }

    /**
     * Create a new instance of an processing pipeline, and updates the context with the default task configuration
     * and logging information.
     *
     * @param context Global Context
     * @param name    Name of the pipeline
     * @return A processing pipeline, and a initialized context
     * @throws AppIntegrationException In case the pipeline could not be created, an exception shall be thrown.
     *                                 Otherwise the method shall always create a valid pipeline.
     */
    @Nonnull
    @Override
    public ProcessingPipeline createProcessingPipeline(@Nonnull AemGlobalContext context, @Nonnull String name) {
        for (AemProcessingPipelineFactory factory : processingPipelineFactoryList) {
            if (factory.canProvidePipeline(context.getResourceResolver(), name)){
                return  factory.createProcessingPipeline(context.getResourceResolver(), name);
            }
        }
        throw new AppIntegrationException(String.format("Processing-pipeline %s is not defined!", name));
    }


    @Nullable
    @Override
    public Application getApplication(@Nonnull String id) {
        return applicationMap.get(id);
    }

    @Nullable
    @Override
    public ResourceLoader getResourceLoader(String id) {
        return resourceLoaderMap.get(id);
    }

    @Nullable
    @Override
    public ContextProvider<SlingApplicationInstance> getContextProvider(@Nonnull String providerName) {
        return contextProviderMap.get(providerName);
    }

    @Reference(name = "application", cardinality = ReferenceCardinality.MULTIPLE,
            policy = ReferencePolicy.DYNAMIC, policyOption = ReferencePolicyOption.GREEDY)
    protected void bindApplication(final AemApplication application, Map<String, ?> ref) {
        LOG.info("bindApplication {} with ref {}", application, ref);
        applicationMap.put(application.getApplicationId(), application);
    }

    @SuppressWarnings("unused")
    protected void unbindApplication(final AemApplication application) {
        applicationMap.remove(application.getApplicationId());
    }

    @Reference(name = "resourceLoader", cardinality = ReferenceCardinality.AT_LEAST_ONE,
            policy = ReferencePolicy.DYNAMIC, policyOption = ReferencePolicyOption.GREEDY)
    protected void bindResourceLoader(final ResourceLoader resourceLoader, final Map<String, Object> properties) {
        resourceLoaderMap.put(getKebabComponentName(resourceLoader, properties, "ResourceLoader"), resourceLoader);
    }

    @SuppressWarnings("unused")
    protected void unbindResourceLoader(final ResourceLoader resourceLoader) {
        removeValueFromMap(resourceLoaderMap, resourceLoader);
    }

    @Reference(name = "contextProvider", cardinality = ReferenceCardinality.MULTIPLE,
            policy = ReferencePolicy.DYNAMIC, policyOption = ReferencePolicyOption.GREEDY)
    protected void bindContextProvider(final AemContextProvider contextProvider, final Map<String, Object> properties) {
        contextProviderMap.put(getKebabComponentName(contextProvider, properties, "ContextProvider"), contextProvider);
    }

    @SuppressWarnings("unused")
    protected void unbindContextProvider(final AemContextProvider contextProvider) {
        removeValueFromMap(contextProviderMap, contextProvider);
    }

    @Reference(name = "aemProcessingPipelineFactory", cardinality = ReferenceCardinality.MULTIPLE,
            policy = ReferencePolicy.DYNAMIC, policyOption = ReferencePolicyOption.GREEDY)
    protected void bindAemProcessingPipelineFactory(final AemProcessingPipelineFactory pipelineFactory) {
        processingPipelineFactoryList.add(pipelineFactory);
    }

    @SuppressWarnings("unused")
    protected void unbindAemProcessingPipelineFactory(final AemProcessingPipelineFactory pipelineFactory) {
        processingPipelineFactoryList.remove(pipelineFactory);
    }

    @Reference(name = "textParser", cardinality = ReferenceCardinality.MULTIPLE,
            policy = ReferencePolicy.DYNAMIC, policyOption = ReferencePolicyOption.GREEDY)
    protected void bindTextParser(final TextParser textParser) {
        textParserList.add(textParser);
    }

    @SuppressWarnings("unused")
    protected void unbindTextParser(final TextParser textParser) {
        textParserList.remove(textParser);
    }

    @Nonnull
    private static String getKebabComponentName(@Nonnull Object component, @Nonnull Map<String, Object> properties, @Nullable String ignorableSuffix) {
        final Object nameObj = properties.get(COMPONENT_NAME);
        final String fullName = (nameObj != null) ? nameObj.toString() : component.getClass().getName();
        final String name = StringUtils.substringAfterLast(fullName, ".");
        final String shortedName = StringUtils.removeEnd(name, ignorableSuffix);
        return shortedName.replaceAll("([a-z])([A-Z])", "$1-$2").toLowerCase();
    }

    private static <T> void removeValueFromMap(Map<String, T> map, T value) {
        final List<String> removeKeys = map.entrySet().stream()
                .filter(entry -> entry.getValue() == value)
                .map(Map.Entry::getKey)
                .collect(Collectors.toList());
        removeKeys.forEach(map::remove);

    }
}
